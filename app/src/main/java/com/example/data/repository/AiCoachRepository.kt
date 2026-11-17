package com.example.data.repository

import android.util.Log
import com.example.data.model.AiCoachFeedback
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

class AiCoachRepository {

  private val feedbackSchema = Schema.obj(
    properties = mapOf(
      "fluencyScore" to Schema.integer(description = "Workplace English fluency and tone score from 0 to 100."),
      "feedback" to Schema.string(description = "Warm, concise coaching feedback in 1-2 sentences evaluating politeness, clarity, and workplace empathy."),
      "suggestedPhrase" to Schema.string(description = "A polished, natural workplace English response the learner can say next."),
      "pronunciationTip" to Schema.string(description = "One specific speaking rhythm, word stress, or articulation tip."),
    )
  )

  private val model by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash",
      generationConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = feedbackSchema
      },
    )
  }

  suspend fun evaluateWorkplaceResponse(
    careerName: String,
    cefrLevel: String,
    scenarioTitle: String,
    customerPrompt: String,
    userResponse: String,
  ): Result<AiCoachFeedback> {
    return try {
      val prompt = """
        You are an encouraging workplace English fluency coach for a $cefrLevel learner in "$careerName".
        Scenario: $scenarioTitle
        Colleague/Customer said: $customerPrompt
        Learner's reply: "$userResponse"

        Evaluate the learner's reply for workplace empathy, clarity, politeness, and natural phrasing.
        Return a fluencyScore (0-100), concise feedback, a polished suggestedPhrase, and a short pronunciationTip.
      """.trimIndent()

      val response = withTimeout(60_000L) {
        model.generateContent(prompt)
      }
      val jsonString = response.text ?: throw IllegalStateException("AI Coach returned an empty response.")
      val json = JSONObject(jsonString)

      Result.success(
        AiCoachFeedback(
          fluencyScore = json.optInt("fluencyScore", 80).coerceIn(0, 100),
          feedback = json.optString("feedback", "Good effort! Keep your tone warm and direct."),
          suggestedPhrase = json.optString("suggestedPhrase", ""),
          pronunciationTip = json.optString("pronunciationTip", "Pause briefly after polite openers."),
        )
      )
    } catch (e: TimeoutCancellationException) {
      Log.e(TAG, "AI Coach evaluation timed out", e)
      Result.failure(Exception("AI Coach timed out after 60 seconds. Please try again."))
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e(TAG, "AI Coach evaluation failed", e)
      Result.failure(e)
    }
  }

  private companion object {
    const val TAG = "AiCoachRepo"
  }
}
