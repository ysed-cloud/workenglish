package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.data.model.CurriculumData
import com.example.data.model.PracticeSession
import com.example.data.model.UserProgress
import com.example.ui.AiCoachUiState
import com.example.ui.components.careerIconForId
import com.example.ui.theme.OnPrimaryFixed
import com.example.ui.theme.OnSecondaryFixed
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.TertiaryFixed
import com.example.util.VoiceInputController

@Composable
fun PracticeScreen(
  progress: UserProgress,
  aiCoachState: AiCoachUiState,
  recentSessions: List<PracticeSession>,
  onEvaluateReply: (String) -> Unit,
  onCompleteSimulation: () -> Unit,
  onBackHome: () -> Unit,
  onSpeak: (String) -> Unit,
  onShowMessage: (String) -> Unit,
  contentPadding: PaddingValues,
) {
  val context = LocalContext.current
  val career = CurriculumData.findCareer(progress.careerId)

  var practiceStep by remember { mutableIntStateOf(1) }
  var selectedOptionIdx by remember { mutableIntStateOf(0) }
  var showArabic by remember(progress.arabicEnabled) { mutableStateOf(progress.arabicEnabled) }
  var customReply by remember { mutableStateOf("") }
  var isListening by remember { mutableStateOf(false) }

  val voiceController = remember(context) {
    VoiceInputController(
      context = context,
      onPartialOrFinal = { spoken -> customReply = spoken },
      onListeningChanged = { active -> isListening = active },
      onError = { msg -> onShowMessage(msg) },
    )
  }

  DisposableEffect(voiceController) {
    onDispose { voiceController.stopListening() }
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) { granted ->
    if (granted) {
      voiceController.startListening()
    } else {
      onShowMessage("Microphone permission denied. You can still type your reply below for AI feedback.")
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(contentPadding)
        .padding(horizontal = 16.dp),
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f),
        ) {
          IconButton(
            onClick = onBackHome,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainerHigh),
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp))
          }
          Column(modifier = Modifier.weight(1f)) {
            Text("LIVE SIMULATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(
              text = career.scenarioTitle,
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHighest) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary))
            Text(progress.cefrLevel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Situation Context Card
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(PrimaryFixed),
            contentAlignment = Alignment.Center,
          ) {
            Icon(careerIconForId(career.id), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
          }
          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(career.scenarioPerson, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
              Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainer) {
                Text(career.scenarioLocation, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Situation: ${career.scenarioDesc}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Previous message (dimmed)
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier.alpha(0.65f),
      ) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer),
          contentAlignment = Alignment.Center,
        ) {
          Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }
        Surface(
          shape = RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLow,
        ) {
          Text(
            text = career.scenarioPrev,
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(10.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Active Prompt Bubble
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PrimaryFixed),
          contentAlignment = Alignment.Center,
        ) {
          Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
        }
        Surface(
          shape = RoundedCornerShape(topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 2.dp,
          modifier = Modifier.weight(1f),
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(career.scenarioPerson, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
              Text("Just now", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(career.scenarioMsg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Surface(
                shape = CircleShape,
                color = PrimaryFixed,
                modifier = Modifier
                  .clip(CircleShape)
                  .clickable { onSpeak(career.scenarioMsg) }
                  .testTag("practice_listen_button"),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                  Text("Listen", style = MaterialTheme.typography.labelSmall, color = OnPrimaryFixed)
                }
              }
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                  .clip(CircleShape)
                  .clickable { showArabic = !showArabic },
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Icon(Icons.Filled.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                  Text("Translation", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
            if (showArabic) {
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = career.scenarioArabic,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.End,
                  modifier = Modifier.padding(10.dp),
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Response Card + AI Fluency Coach
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(PrimaryContainer))
              Text("Your Response", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Surface(shape = CircleShape, color = SecondaryFixed) {
              Text(
                text = "Service Etiquette",
                style = MaterialTheme.typography.labelSmall,
                color = OnSecondaryFixed,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "How would you respond professionally to maintain high workplace standards?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )

          Spacer(modifier = Modifier.height(12.dp))
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CurriculumData.practiceChoiceOptions.forEachIndexed { idx, opt ->
              val selected = selectedOptionIdx == idx
              val bgColor = when {
                selected && opt.isOptimal -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.28f)
                selected && !opt.isOptimal -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f)
                else -> MaterialTheme.colorScheme.surfaceContainerLow
              }
              val border = when {
                selected && opt.isOptimal -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.secondary)
                selected && !opt.isOptimal -> BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
                else -> null
              }

              Surface(
                shape = RoundedCornerShape(14.dp),
                color = bgColor,
                border = border,
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(14.dp))
                  .clickable { selectedOptionIdx = idx },
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                          if (selected && opt.isOptimal) MaterialTheme.colorScheme.secondary
                          else MaterialTheme.colorScheme.surfaceContainerHighest
                        ),
                      contentAlignment = Alignment.Center,
                    ) {
                      if (selected && opt.isOptimal) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                      } else {
                        Text(opt.label, style = MaterialTheme.typography.labelSmall)
                      }
                    }
                    Text(
                      text = opt.text,
                      style = MaterialTheme.typography.bodyMedium,
                      fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                      modifier = Modifier.weight(1f),
                    )
                  }
                  if (selected) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val fbBg = if (opt.isOptimal) SecondaryFixed.copy(alpha = 0.45f)
                    else if (idx == 1) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
                    else TertiaryFixed.copy(alpha = 0.6f)
                    val fbIcon = if (opt.isOptimal) Icons.Filled.Stars else if (idx == 1) Icons.Filled.Warning else Icons.Filled.Info
                    val fbTint = if (opt.isOptimal) MaterialTheme.colorScheme.secondary else if (idx == 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary

                    Surface(shape = RoundedCornerShape(10.dp), color = fbBg, modifier = Modifier.fillMaxWidth()) {
                      Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                      ) {
                        Icon(fbIcon, contentDescription = null, tint = fbTint, modifier = Modifier.size(18.dp))
                        Text(
                          text = "${opt.feedbackTitle} ${opt.feedbackText}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurface,
                        )
                      }
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Gemini AI Fluency & Speech Coach
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text("Or practice with Gemini AI Coach:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
              Text("AI Fluency Coach", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
              IconButton(
                onClick = {
                  if (isListening) {
                    voiceController.stopListening()
                  } else {
                    val granted = ContextCompat.checkSelfPermission(
                      context,
                      Manifest.permission.RECORD_AUDIO,
                    ) == PackageManager.PERMISSION_GRANTED
                    if (granted) {
                      voiceController.startListening()
                    } else {
                      permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                  }
                },
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(if (isListening) MaterialTheme.colorScheme.error else PrimaryContainer)
                  .testTag("practice_mic_button"),
              ) {
                Icon(
                  imageVector = if (isListening) Icons.Filled.Stop else Icons.Filled.Mic,
                  contentDescription = if (isListening) "Stop listening" else "Speak reply",
                  tint = Color.White,
                  modifier = Modifier.size(26.dp),
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = if (isListening) "Listening... Speak clearly" else "Tap mic or type your own reply",
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                )
                Text(
                  text = "Gemini scores your tone, clarity & phrasing.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = customReply,
            onValueChange = { customReply = it },
            label = { Text("Your spoken or typed response") },
            placeholder = { Text("e.g., I understand the delay is frustrating; let me check right away.") },
            singleLine = false,
            maxLines = 3,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("ai_coach_reply_input"),
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { onEvaluateReply(customReply) },
            enabled = aiCoachState !is AiCoachUiState.Loading,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.secondary,
              contentColor = Color.White,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("ai_coach_submit_button"),
          ) {
            if (aiCoachState is AiCoachUiState.Loading) {
              CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("Evaluating with Gemini AI...", style = MaterialTheme.typography.labelMedium)
            } else {
              Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Get Gemini AI Fluency Feedback", style = MaterialTheme.typography.labelMedium)
            }
          }

          // AI Coach Output
          when (aiCoachState) {
            is AiCoachUiState.Success -> {
              val fb = aiCoachState.feedback
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                modifier = Modifier.fillMaxWidth(),
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Text("Gemini AI Coaching", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondary) {
                      Text("${fb.fluencyScore}% Fluency", style = MaterialTheme.typography.labelSmall, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(fb.feedback, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                  if (fb.suggestedPhrase.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = MaterialTheme.colorScheme.surfaceContainerLowest,
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSpeak(fb.suggestedPhrase) },
                    ) {
                      Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                      ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Column(modifier = Modifier.weight(1f)) {
                          Text("TRY SAYING:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                          Text("\"${fb.suggestedPhrase}\"", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                      }
                    }
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text("Tip: ${fb.pronunciationTip}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
            }
            is AiCoachUiState.Error -> {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = aiCoachState.message,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onErrorContainer,
                  modifier = Modifier.padding(12.dp),
                )
              }
            }
            else -> Unit
          }
        }
      }

      if (recentSessions.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))
        Text("Recent AI Coaching Logs", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
        Spacer(modifier = Modifier.height(6.dp))
        recentSessions.take(2).forEach { sess ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 6.dp),
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("\"${sess.userResponse}\"", style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(sess.aiFeedback, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
              }
              Spacer(modifier = Modifier.width(8.dp))
              Surface(shape = CircleShape, color = PrimaryFixed) {
                Text("${sess.fluencyScore}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Simulation Progress Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Text("SIMULATION PROGRESS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Step $practiceStep of 3", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
      }
      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { practiceStep / 3f },
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(CircleShape),
      )
      Spacer(modifier = Modifier.height(12.dp))
      Button(
        onClick = {
          if (practiceStep < 3) {
            practiceStep += 1
            selectedOptionIdx = 0
          } else {
            practiceStep = 1
            onCompleteSimulation()
            onBackHome()
          }
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("practice_continue_button"),
      ) {
        Text(
          text = if (practiceStep < 3) "Continue Conversation (Step $practiceStep of 3)" else "Complete Simulation (+25 XP)",
          style = MaterialTheme.typography.labelLarge,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
      }
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
