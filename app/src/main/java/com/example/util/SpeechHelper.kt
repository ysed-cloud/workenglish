package com.example.util

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun rememberSimpleTts(): (String) -> Unit {
  val context = LocalContext.current.applicationContext
  val state = remember {
    var isReady = false
    var engine: TextToSpeech? = null
    engine = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        engine?.language = Locale.US
        engine?.setSpeechRate(0.95f)
        isReady = true
      }
    }
    Pair({ text: String ->
      val clean = text.replace("“", "").replace("”", "").replace("\"", "").trim()
      if (isReady && clean.isNotEmpty()) {
        engine?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "we_${clean.hashCode()}")
      }
    }, engine)
  }

  DisposableEffect(state) {
    onDispose {
      state.second?.stop()
      state.second?.shutdown()
    }
  }

  return state.first
}

class VoiceInputController(
  private val context: android.content.Context,
  private val onPartialOrFinal: (String) -> Unit,
  private val onListeningChanged: (Boolean) -> Unit,
  private val onError: (String) -> Unit,
) {
  private var recognizer: SpeechRecognizer? = null

  fun startListening() {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      onError("On-device speech recognition is unavailable here. Type your response below for AI coaching.")
      return
    }
    stopListening()
    val sr = SpeechRecognizer.createSpeechRecognizer(context)
    recognizer = sr
    sr.setRecognitionListener(object : RecognitionListener {
      override fun onReadyForSpeech(params: Bundle?) = onListeningChanged(true)
      override fun onBeginningOfSpeech() {}
      override fun onRmsChanged(rmsdB: Float) {}
      override fun onBufferReceived(buffer: ByteArray?) {}
      override fun onEndOfSpeech() = onListeningChanged(false)
      override fun onError(error: Int) {
        onListeningChanged(false)
        onError("Could not capture clear speech (code $error). Try speaking closer or typing your reply.")
      }
      override fun onResults(results: Bundle?) {
        onListeningChanged(false)
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull().orEmpty()
        if (text.isNotBlank()) onPartialOrFinal(text)
      }
      override fun onPartialResults(partialResults: Bundle?) {
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val text = matches?.firstOrNull().orEmpty()
        if (text.isNotBlank()) onPartialOrFinal(text)
      }
      override fun onEvent(eventType: Int, params: Bundle?) {}
    })
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
      putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
      putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
      putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
    }
    sr.startListening(intent)
  }

  fun stopListening() {
    recognizer?.stopListening()
    recognizer?.destroy()
    recognizer = null
    onListeningChanged(false)
  }
}
