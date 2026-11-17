package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CurriculumData
import com.example.data.model.SavedVocab
import com.example.data.model.UserProgress
import com.example.data.model.VocabItem
import com.example.ui.components.VocabFlashCard
import com.example.ui.components.careerIconForId
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.AmberStreakBg
import com.example.ui.theme.OnPrimaryFixedVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixedDim
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.TertiaryFixed

@Composable
fun HomeDashboardScreen(
  progress: UserProgress,
  savedVocab: List<SavedVocab>,
  onSwitchCareer: () -> Unit,
  onStartLesson: () -> Unit,
  onOpenLearn: () -> Unit,
  onOpenPractice: () -> Unit,
  onOpenVocabulary: () -> Unit,
  onToggleSaveVocab: (String, VocabItem) -> Unit,
  onSpeak: (String) -> Unit,
  contentPadding: PaddingValues,
) {
  val career = CurriculumData.findCareer(progress.careerId)
  val level = CurriculumData.findLevel(progress.cefrLevel)
  val goalPct = ((progress.dailyMinutes.toFloat() / progress.dailyGoalMinutes.coerceAtLeast(1)) * 100f)
    .toInt()
    .coerceIn(0, 100)

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
      Spacer(modifier = Modifier.height(12.dp))

      // Active Track Switcher Bar
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = careerIconForId(career.id),
                contentDescription = career.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
              )
            }
            Column {
              Text(
                text = "ACTIVE TRACK",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Text(
                text = career.name,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
              .clip(CircleShape)
              .clickable(onClick = onSwitchCareer)
              .testTag("switch_career_button"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = "Switch",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
              )
              Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Greeting
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Good morning, ${progress.displayName.split(" ").firstOrNull() ?: "Learner"}!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Ready to sharpen your workplace dialogue today?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.Filled.WorkspacePremium,
            contentDescription = "Workplace badge",
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.size(24.dp),
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Quick Stats Bento Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        // Streak
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 2.dp,
          modifier = Modifier.weight(1f),
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = AmberStreak,
                modifier = Modifier.size(20.dp),
              )
              Surface(shape = CircleShape, color = AmberStreakBg) {
                Text(
                  text = "${progress.streakDays}d",
                  style = MaterialTheme.typography.labelSmall,
                  color = AmberStreak,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                )
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Streak", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              repeat(7) { idx ->
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (idx < 5) AmberStreak else MaterialTheme.colorScheme.surfaceContainerHigh),
                )
              }
            }
          }
        }

        // CEFR Level
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 2.dp,
          modifier = Modifier.weight(1f),
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Filled.MilitaryTech,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
              )
              Text(
                text = "72%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "${level.code} • ${level.name.take(5)}",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
              progress = { 0.72f },
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            )
          }
        }

        // Daily Goal Ring
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 2.dp,
          modifier = Modifier.weight(1f),
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp),
              )
              val trackClr = MaterialTheme.colorScheme.surfaceContainerHighest
              val activeClr = MaterialTheme.colorScheme.secondary
              Canvas(modifier = Modifier.size(20.dp)) {
                drawArc(
                  color = trackClr,
                  startAngle = 0f,
                  sweepAngle = 360f,
                  useCenter = false,
                  style = Stroke(width = 3.dp.toPx()),
                )
                drawArc(
                  color = activeClr,
                  startAngle = -90f,
                  sweepAngle = (goalPct / 100f) * 360f,
                  useCenter = false,
                  style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                )
              }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "${progress.dailyMinutes}/${progress.dailyGoalMinutes} m",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
            )
            Text(
              text = "$goalPct% done",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Hero Continue Learning Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(24.dp))
          .background(
            Brush.linearGradient(
              listOf(PrimaryContainer, Primary, OnPrimaryFixedVariant),
            )
          )
          .padding(20.dp),
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.2f),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SecondaryFixed))
                Text(
                  text = "TODAY'S PRIORITY LESSON",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White,
                )
              }
            }
            Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.2f)) {
              Text(
                text = "${progress.completedLessons.size.coerceAtMost(4)} of 4 done",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = career.heroModule.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = career.heroLesson,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
          )

          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.22f),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
              IconButton(
                onClick = { onSpeak(career.heroQuote) },
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(Color.White),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                  contentDescription = "Play quote",
                  tint = Primary,
                  modifier = Modifier.size(18.dp),
                )
              }
              Text(
                text = career.heroQuote,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.95f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          LinearProgressIndicator(
            progress = { 0.55f },
            color = SecondaryFixed,
            trackColor = Color.White.copy(alpha = 0.25f),
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(CircleShape),
          )
          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Button(
              onClick = onStartLesson,
              shape = CircleShape,
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = Primary,
              ),
              modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .testTag("continue_lesson_button"),
            ) {
              Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Continue Lesson (6 min)", style = MaterialTheme.typography.labelLarge)
            }
            IconButton(
              onClick = onOpenLearn,
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.16f)),
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = "Lesson Outline",
                tint = Color.White,
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Scenario Simulation Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(144.dp)
          .clip(RoundedCornerShape(18.dp))
          .clickable(onClick = onOpenPractice)
          .testTag("home_scenario_banner"),
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_practice_banner_1794745734922),
          contentDescription = "Workplace scenario simulation",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize(),
        )
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(Color.Transparent, Color(0xD8131B2E)),
              )
            )
            .padding(14.dp),
          contentAlignment = Alignment.BottomStart,
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "SCENARIO SIMULATION",
                style = MaterialTheme.typography.labelSmall,
                color = SecondaryFixed,
              )
              Text(
                text = career.scenarioTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
              )
            }
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.22f)) {
              Text(
                text = "Live Mode",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Recommended Vocabulary
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column {
          Text(
            text = "Recommended Vocabulary",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "Essential words for your ${career.name} shift",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onOpenVocabulary)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        ) {
          Text("View all", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
          Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        career.vocab.forEach { item ->
          val isSaved = savedVocab.any {
            it.word.equals(item.word, ignoreCase = true) && it.careerId == career.id
          }
          VocabFlashCard(
            item = item,
            arabicEnabled = progress.arabicEnabled,
            isSaved = isSaved,
            onToggleSave = { onToggleSaveVocab(career.id, item) },
            onSpeak = { onSpeak("${item.word}. ${item.example}") },
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Weekly Activity & Daily Challenge
      Surface(
        shape = RoundedCornerShape(24.dp),
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
            Column {
              Text("CONSISTENCY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Weekly Activity", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text("${85 + progress.dailyMinutes} min", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
              Text("+18% vs last week", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          val bars = listOf(
            Triple("M", "18m", 0.60f),
            Triple("T", "22m", 0.75f),
            Triple("W", "15m", 0.50f),
            Triple("T", "25m", 0.85f),
            Triple("F", "${progress.dailyMinutes}m", (progress.dailyMinutes / 30f).coerceIn(0.3f, 0.95f)),
            Triple("S", "-", 0.12f),
            Triple("S", "-", 0.12f),
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom,
          ) {
            bars.forEachIndexed { idx, (day, mins, frac) ->
              val barColor = when (idx) {
                3 -> Primary
                4 -> MaterialTheme.colorScheme.secondaryContainer
                5, 6 -> MaterialTheme.colorScheme.surfaceContainerHigh
                else -> PrimaryFixedDim
              }
              Column(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
              ) {
                Text(
                  text = mins,
                  style = MaterialTheme.typography.labelSmall,
                  color = if (idx == 4) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                  contentAlignment = Alignment.BottomCenter,
                ) {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .fillMaxHeight(frac)
                      .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                      .background(barColor),
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.secondary),
                  contentAlignment = Alignment.Center,
                ) {
                  Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                  Text("DAILY ROLEPLAY CHALLENGE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                  Text(
                    text = career.challengeTitle,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                  )
                }
              }
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "Practice calm, professional phrasing with Gemini AI Coach.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = onOpenPractice,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                  containerColor = MaterialTheme.colorScheme.secondary,
                  contentColor = Color.White,
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
              ) {
                Icon(Icons.Filled.GraphicEq, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Audio Practice (5 min)", style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Pro Workplace Tip
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = TertiaryFixed.copy(alpha = 0.35f),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.tertiary),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Filled.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
          }
          Column {
            Text("Pro Workplace Tip", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(career.proTip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
