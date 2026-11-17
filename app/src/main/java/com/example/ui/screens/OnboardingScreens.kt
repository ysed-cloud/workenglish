package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CurriculumData
import com.example.ui.components.StackTopBar
import com.example.ui.components.careerIconForId
import com.example.ui.theme.OnPrimaryFixed
import com.example.ui.theme.OnTertiaryFixed
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.TertiaryFixed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CareerSelectionScreen(
  selectedCareerId: String,
  initials: String,
  onSelectCareer: (String) -> Unit,
  onContinue: () -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)

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
      StackTopBar(title = "Career Path", initials = initials, onBackClick = onBack)

      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp),
      ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(PrimaryContainer),
              contentAlignment = Alignment.Center,
            ) {
              Text("1", style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
            Text(
              text = "Step 1 of 3: Career Path",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
          TextButton(onClick = onContinue) {
            Text("Skip", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
          }
        }

        LinearProgressIndicator(
          progress = { 0.33f },
          color = MaterialTheme.colorScheme.secondary,
          trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        )

        Spacer(modifier = Modifier.height(18.dp))
        Surface(
          shape = CircleShape,
          color = PrimaryFixed,
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp),
            )
            Text(
              text = "PERSONALIZED CURRICULUM",
              style = MaterialTheme.typography.labelSmall,
              color = OnPrimaryFixed,
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "What is your current or target career?",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "We customize your everyday vocabulary, realistic dialogues, and listening exercises based on your role.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          CurriculumData.careers.forEach { career ->
            val selected = career.id == selectedCareerId
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surfaceContainerLowest,
              border = if (selected) BorderStroke(2.dp, PrimaryContainer) else null,
              shadowElevation = if (selected) 3.dp else 1.dp,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSelectCareer(career.id) }
                .testTag("career_card_${career.id}"),
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.Top,
                ) {
                  Box(
                    modifier = Modifier
                      .size(48.dp)
                      .clip(RoundedCornerShape(14.dp))
                      .background(if (selected) PrimaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                  ) {
                    Icon(
                      imageVector = careerIconForId(career.id),
                      contentDescription = career.name,
                      tint = if (selected) Color.White else MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(26.dp),
                    )
                  }

                  if (selected) {
                    Box(
                      modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary),
                      contentAlignment = Alignment.Center,
                    ) {
                      Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                      )
                    }
                  }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = career.name,
                  style = MaterialTheme.typography.headlineSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = career.description,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  career.tags.forEach { tag ->
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MaterialTheme.colorScheme.surfaceContainer,
                    ) {
                      Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      )
                    }
                  }
                }
              }
            }
          }
        }
        Spacer(modifier = Modifier.height(24.dp))
      }

      // Sticky Bottom Bar
      Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Button(
            onClick = onContinue,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
              containerColor = PrimaryContainer,
              contentColor = Color.White,
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("career_continue_button"),
          ) {
            Text("Continue to Level Selection", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "You can update your industry at any time in Profile Settings",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
          )
        }
      }
    }
  }
}

@Composable
fun LevelSelectionScreen(
  selectedLevel: String,
  arabicEnabled: Boolean,
  initials: String,
  onSelectLevel: (String) -> Unit,
  onConfirm: () -> Unit,
  onBack: () -> Unit,
) {
  BackHandler(onBack = onBack)
  var showQuizModal by remember { mutableStateOf(false) }

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
      StackTopBar(title = "Level Assessment", initials = initials, onBackClick = onBack)

      Column(
        modifier = Modifier
          .weight(1f)
          .verticalScroll(rememberScrollState())
          .padding(horizontal = 16.dp)
          .navigationBarsPadding(),
      ) {
        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "STEP 2 OF 3",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
          )
          Text(
            text = "Proficiency Level",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
          Text(
            text = "66%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
          progress = { 0.66f },
          color = MaterialTheme.colorScheme.secondary,
          trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
          modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape),
        )

        Spacer(modifier = Modifier.height(18.dp))
        Text(
          text = "Select your current English level",
          style = MaterialTheme.typography.headlineMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Start where you feel comfortable. You will unlock advanced workplace simulations as you advance.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CurriculumData.cefrLevels.forEach { lvl ->
            val selected = lvl.code == selectedLevel
            val (badgeBg, badgeText, dotColor) = when (lvl.code) {
              "A1" -> Triple(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondary)
              "A2" -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary, SecondaryFixed)
              "B1" -> Triple(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary)
              else -> Triple(TertiaryFixed, OnTertiaryFixed, MaterialTheme.colorScheme.tertiary)
            }

            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (selected) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerLowest,
              border = if (selected) BorderStroke(2.dp, PrimaryContainer) else null,
              shadowElevation = if (selected) 4.dp else 1.dp,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onSelectLevel(lvl.code) }
                .testTag("level_card_${lvl.code}"),
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Surface(shape = CircleShape, color = badgeBg) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                      Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                      Text(
                        text = "${lvl.code} • ${lvl.name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeText,
                      )
                    }
                  }
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(if (selected) PrimaryContainer else MaterialTheme.colorScheme.surfaceContainer),
                    contentAlignment = Alignment.Center,
                  ) {
                    if (selected) {
                      Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(15.dp),
                      )
                    }
                  }
                }

                if (arabicEnabled) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Text(
                    text = lvl.arabic,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = lvl.description,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Medium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (selected) MaterialTheme.colorScheme.surfaceContainer else MaterialTheme.colorScheme.surfaceContainerLow,
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Filled.Flag,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp),
                    )
                    Text(
                      text = "Focus: ${lvl.focus}",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Placement Test Banner
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          shadowElevation = 1.dp,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier
              .background(
                Brush.horizontalGradient(
                  listOf(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    MaterialTheme.colorScheme.surfaceContainerLow,
                  )
                )
              )
              .padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PrimaryFixed),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Filled.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
              )
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Not sure about your level?",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "Take our 3-minute Workplace Placement Test to find your exact match.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 2.dp,
                modifier = Modifier
                  .clip(CircleShape)
                  .clickable { showQuizModal = true }
                  .testTag("start_placement_quiz_button"),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                  Text(
                    text = "Start Quick Test",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                  )
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
          onClick = onConfirm,
          shape = CircleShape,
          colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryContainer,
            contentColor = Color.White,
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("confirm_setup_button"),
        ) {
          Text("Confirm & Setup Dashboard", style = MaterialTheme.typography.labelLarge)
          Spacer(modifier = Modifier.width(8.dp))
          Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  if (showQuizModal) {
    PlacementQuizDialog(
      onDismiss = { showQuizModal = false },
      onLevelRecommended = { recommended ->
        onSelectLevel(recommended)
        showQuizModal = false
      },
    )
  }
}

@Composable
private fun PlacementQuizDialog(
  onDismiss: () -> Unit,
  onLevelRecommended: (String) -> Unit,
) {
  var step by remember { mutableIntStateOf(0) }
  var totalScore by remember { mutableIntStateOf(0) }
  val questions = CurriculumData.placementQuestions
  val current = questions[step]

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLowest,
      shadowElevation = 12.dp,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Surface(shape = CircleShape, color = PrimaryFixed) {
            Text(
              text = "QUESTION ${step + 1} OF ${questions.size}",
              style = MaterialTheme.typography.labelSmall,
              color = OnPrimaryFixed,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            )
          }
          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close quiz")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = current.question,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = current.context,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          current.options.forEachIndexed { idx, opt ->
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surfaceContainerLow,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                  val nextScore = totalScore + opt.score
                  if (step < questions.lastIndex) {
                    totalScore = nextScore
                    step += 1
                  } else {
                    val rec = when {
                      nextScore >= 8 -> "B2"
                      nextScore >= 6 -> "B1"
                      nextScore >= 4 -> "A2"
                      else -> "A1"
                    }
                    onLevelRecommended(rec)
                  }
                },
            ) {
              Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                  text = "${('A' + idx)}.",
                  style = MaterialTheme.typography.labelLarge,
                  color = MaterialTheme.colorScheme.primary,
                )
                Text(
                  text = opt.text,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }
        }
      }
    }
  }
}
