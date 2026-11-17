package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.CurriculumData
import com.example.data.model.UserProgress
import com.example.ui.components.StackTopBar
import com.example.ui.components.careerIconForId
import com.example.ui.theme.OnPrimaryFixed
import com.example.ui.theme.OnTertiaryFixed
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.TertiaryFixed

private enum class LessonStatus { IDLE, CORRECT, WRONG }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveLessonScreen(
  progress: UserProgress,
  initials: String,
  onDeductHeart: () -> Unit,
  onLessonCompleted: () -> Unit,
  onSpeak: (String) -> Unit,
  onShowMessage: (String) -> Unit,
  onExit: () -> Unit,
) {
  BackHandler(onBack = onExit)

  val career = CurriculumData.findCareer(progress.careerId)
  val lp = career.lessonPrompt
  val wordPool = remember(career.id) {
    (lp.expectedWords + lp.distractors).sortedBy { it.lowercase() }
  }
  val selectedWords = remember(career.id) { mutableStateListOf<String>() }
  var status by remember(career.id) { mutableStateOf(LessonStatus.IDLE) }

  // Compute remaining available occurrences of each word in the pool
  val usedCounts = mutableMapOf<String, Int>()
  selectedWords.forEach { w -> usedCounts[w] = (usedCounts[w] ?: 0) + 1 }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxSize(),
    ) {
      StackTopBar(title = "Lesson Detail", initials = initials, onBackClick = onExit)

      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp)
          .navigationBarsPadding(),
      ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Lesson Header Controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
              onClick = onExit,
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            ) {
              Icon(Icons.Filled.Close, contentDescription = "Exit lesson", modifier = Modifier.size(20.dp))
            }
            IconButton(
              onClick = { onSpeak(lp.quote) },
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
              Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Play audio", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
          }

          LinearProgressIndicator(
            progress = { if (status == LessonStatus.CORRECT) 1.0f else 0.6f },
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 12.dp)
              .height(10.dp)
              .clip(CircleShape),
          )

          Surface(shape = CircleShape, color = MaterialTheme.colorScheme.errorContainer) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              Icon(Icons.Filled.Favorite, contentDescription = "Hearts", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
              Text("${progress.hearts}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(shape = CircleShape, color = PrimaryFixed) {
            Text(
              text = "MODULE 6 • LESSON 3",
              style = MaterialTheme.typography.labelSmall,
              color = OnPrimaryFixed,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
          Text(
            text = "${career.name.split(" ").first()} ${progress.cefrLevel}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scenario Context Card
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 2.dp,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              verticalAlignment = Alignment.Top,
            ) {
              Box(
                modifier = Modifier
                  .size(52.dp)
                  .clip(CircleShape)
                  .background(PrimaryFixed),
                contentAlignment = Alignment.Center,
              ) {
                Icon(careerIconForId(career.id), contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(26.dp))
              }
              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Text(lp.person, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                  IconButton(
                    onClick = { onSpeak(lp.quote) },
                    modifier = Modifier
                      .size(32.dp)
                      .clip(CircleShape)
                      .background(PrimaryFixed),
                  ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surfaceContainerLow,
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Text(
                    text = lp.quote,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(12.dp),
                  )
                }
              }
            }

            if (progress.arabicEnabled) {
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth(),
              ) {
                Text(
                  text = lp.arabic,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface,
                  textAlign = TextAlign.End,
                  modifier = Modifier.padding(10.dp),
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
          Text(lp.instruction, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Text(
          text = "Tap the words in the correct professional order.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Answer Construction Zone
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.surfaceContainer,
          modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 104.dp),
        ) {
          if (selectedWords.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center,
            ) {
              Text(
                text = "Select words below to craft your reply...",
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.outline,
              )
            }
          } else {
            FlowRow(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              selectedWords.forEachIndexed { idx, word ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.primary,
                  shadowElevation = 2.dp,
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                      selectedWords.removeAt(idx)
                      status = LessonStatus.IDLE
                    },
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                  ) {
                    Text(word, style = MaterialTheme.typography.labelLarge, color = Color.White)
                    Icon(Icons.Filled.Close, contentDescription = "Remove word", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text("WORD OPTIONS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .clip(CircleShape)
              .clickable {
                selectedWords.clear()
                status = LessonStatus.IDLE
              }
              .padding(horizontal = 8.dp, vertical = 4.dp),
          ) {
            Icon(Icons.Filled.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
            Text("Reset", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Word Bank Pool
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          wordPool.forEach { w ->
            val used = (usedCounts[w] ?: 0) > 0
            if (used) usedCounts[w] = usedCounts[w]!! - 1
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceContainerLowest,
              shadowElevation = if (used) 0.dp else 2.dp,
              modifier = Modifier
                .alpha(if (used) 0.3f else 1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = !used) {
                  selectedWords.add(w)
                  status = LessonStatus.IDLE
                }
                .testTag("word_tile_$w"),
            ) {
              Text(
                text = w,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Workplace Tip
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLow,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
                  .background(TertiaryFixed),
                contentAlignment = Alignment.Center,
              ) {
                Icon(Icons.Filled.TipsAndUpdates, contentDescription = null, tint = OnTertiaryFixed, modifier = Modifier.size(16.dp))
              }
              Text("Workplace Tip", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = lp.tip,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(start = 36.dp),
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feedback Banner
        when (status) {
          LessonStatus.CORRECT -> {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.secondaryContainer,
              shadowElevation = 4.dp,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp),
                  modifier = Modifier.weight(1f),
                ) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.secondary),
                    contentAlignment = Alignment.Center,
                  ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                  }
                  Column {
                    Text("Great job!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text("Polite, clear, and perfectly structured.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                  }
                }
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondary) {
                  Text("+10 XP", style = MaterialTheme.typography.labelLarge, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }
          LessonStatus.WRONG -> {
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.errorContainer,
              shadowElevation = 4.dp,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color.White)
                }
                Column {
                  Text("Not quite there", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onErrorContainer)
                  Text(
                    text = "Hint: Starts with \"${lp.expectedWords.take(3).joinToString(" ")}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(12.dp))
          }
          LessonStatus.IDLE -> Unit
        }

        // Bottom Action Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          IconButton(
            onClick = { onShowMessage("Feedback noted — thank you!") },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceContainer),
          ) {
            Icon(Icons.Filled.Flag, contentDescription = "Report issue", tint = MaterialTheme.colorScheme.outline)
          }

          if (status == LessonStatus.CORRECT) {
            Button(
              onClick = {
                onLessonCompleted()
                onExit()
              },
              shape = CircleShape,
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = Color.White),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("lesson_finish_button"),
            ) {
              Text("Continue", style = MaterialTheme.typography.labelLarge)
              Spacer(modifier = Modifier.width(8.dp))
              Icon(Icons.Filled.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
            }
          } else {
            Button(
              onClick = {
                if (selectedWords.isEmpty()) {
                  onShowMessage("Tap words below to build your reply")
                } else if (selectedWords.toList() == lp.expectedWords) {
                  status = LessonStatus.CORRECT
                  onSpeak(lp.expectedWords.joinToString(" "))
                } else {
                  status = LessonStatus.WRONG
                  onDeductHeart()
                }
              },
              shape = CircleShape,
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainer, contentColor = Color.White),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("lesson_check_button"),
            ) {
              Text("Check Answer", style = MaterialTheme.typography.labelLarge)
              Spacer(modifier = Modifier.width(8.dp))
              Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
