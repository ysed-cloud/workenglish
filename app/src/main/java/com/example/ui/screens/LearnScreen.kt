package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.CurriculumData
import com.example.data.model.UserProgress
import com.example.ui.components.careerIconForId
import com.example.ui.theme.OnPrimaryFixed
import com.example.ui.theme.OnTertiaryFixedVariant
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.TertiaryFixed

private data class RoadmapModule(
  val num: String,
  val mins: String,
  val title: String,
  val desc: String,
  val status: String,
)

@Composable
fun LearnPathScreen(
  progress: UserProgress,
  onSwitchCareer: () -> Unit,
  onStartLesson: () -> Unit,
  contentPadding: PaddingValues,
) {
  val career = CurriculumData.findCareer(progress.careerId)
  val completedMods = listOf(
    RoadmapModule("01", "12 mins", "Basic ${career.tags.firstOrNull() ?: "Work"} English", "Clocking in, greetings, workplace etiquette & clarity.", "Review Done"),
    RoadmapModule("02", "10 mins", "Welcoming & First Impressions", "Polite reception, verifying details & active listening.", "100% Score"),
    RoadmapModule("03", "15 mins", "Asking Clarifying Questions", "Checking needs, timelines, and confirming requirements.", "Mastered"),
    RoadmapModule("04", "18 mins", career.heroLesson, "Step-by-step explanations and calm guidance.", "38 Vocab"),
    RoadmapModule("05", "16 mins", "Policies, Schedules & Quantities", "Clear instructions, timelines, and polite follow-ups.", "Mastered"),
  )
  val lockedMods = listOf(
    Triple("07", "Handling Urgent Situations", "Escalation protocols, triage terminology, alerting supervisors."),
    Triple("08", "Shift Handover & Reports", "Structured handovers, status updates, and log summaries."),
    Triple("09", "Career Job Interviews", "STAR technique answers tailored to your industry."),
  )
  val lessonDone = progress.completedLessons.contains("m6_l3")

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

      // Track Overview Card
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainerLowest,
              shadowElevation = 1.dp,
              modifier = Modifier
                .weight(1f, fill = false)
                .clip(CircleShape)
                .clickable(onClick = onSwitchCareer),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                Icon(
                  imageVector = careerIconForId(career.id),
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp),
                )
                Text(
                  text = career.name,
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f, fill = false),
                )
                Icon(Icons.Filled.ExpandMore, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
              Text(
                text = "${progress.cefrLevel} → B2",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text("CAREER TRACK PATH", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(career.trackTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(if (lessonDone) "70%" else "64%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.secondary)
              Text("Mastered", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          LinearProgressIndicator(
            progress = { if (lessonDone) 0.70f else 0.64f },
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            modifier = Modifier
              .fillMaxWidth()
              .height(10.dp)
              .clip(CircleShape),
          )
          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Completed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (lessonDone) "6 / 10 Mods" else "5 / 10 Mods", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Vocab Mastered", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("192 terms", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Practice Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("5.2 hrs", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Completed Modules
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        completedMods.forEach { mod ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
              contentAlignment = Alignment.Center,
            ) {
              Icon(Icons.Filled.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceContainerLowest,
              shadowElevation = 1.dp,
              modifier = Modifier.weight(1f),
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text("MODULE ${mod.num} • COMPLETE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(13.dp))
                    Text(mod.mins, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(mod.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(mod.desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainer) {
                    Text("4 / 4 Lessons", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                  }
                  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(15.dp))
                    Text(mod.status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                  }
                }
              }
            }
          }
        }

        // Active Module 06
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(PrimaryContainer),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Filled.Navigation, contentDescription = "Active module", tint = Color.White, modifier = Modifier.size(20.dp))
          }
          Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(2.dp, PrimaryContainer),
            shadowElevation = 6.dp,
            modifier = Modifier.weight(1f),
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)) {
                  Text("CURRENT MODULE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                }
                Text("~15 mins", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text("De-escalation & Clear Guidance", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
              Text("Empathetic responses, calming delays, and clear next-step explanations.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(12.dp))

              Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DoneSubLessonRow("1. Common Shift Inquiries")
                DoneSubLessonRow("2. Empathetic Openers")

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surfaceContainerHigh,
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically,
                    ) {
                      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(if (lessonDone) MaterialTheme.colorScheme.secondary else PrimaryContainer))
                        Text("3. Explaining Next Steps Clearly", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                      }
                      Surface(shape = RoundedCornerShape(6.dp), color = PrimaryFixed) {
                        Text(
                          text = if (lessonDone) "DONE" else "NEXT",
                          style = MaterialTheme.typography.labelSmall,
                          color = OnPrimaryFixed,
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                      }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Build natural, polite workplace replies using interactive word tiles.",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                      onClick = onStartLesson,
                      shape = CircleShape,
                      colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = Color.White,
                      ),
                      modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("learn_start_lesson_button"),
                    ) {
                      Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(if (lessonDone) "PRACTICE AGAIN" else "START LESSON NOW", style = MaterialTheme.typography.labelLarge)
                    }
                  }
                }

                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.surfaceContainerLow,
                  modifier = Modifier.fillMaxWidth(),
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                      Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(16.dp))
                      Text("4. Shift Wrap-Up & Follow-Through", style = MaterialTheme.typography.labelMedium)
                    }
                    Text("Locked", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                  }
                }
              }
            }
          }
        }

        // Locked Modules
        lockedMods.forEachIndexed { idx, (num, title, desc) ->
          Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer),
              contentAlignment = Alignment.Center,
            ) {
              Icon(Icons.Filled.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
            }
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.78f),
              shadowElevation = 1.dp,
              modifier = Modifier.weight(1f),
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text("MODULE $num • LOCKED", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  Text("4 Lessons", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Unlocks after completing Module 0${6 + idx}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
              }
            }
          }
        }

        // Capstone
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.Top,
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(TertiaryFixed),
            contentAlignment = Alignment.Center,
          ) {
            Icon(Icons.Filled.EmojiEvents, contentDescription = "Credential", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
          }
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            border = BorderStroke(1.dp, TertiaryFixed),
            modifier = Modifier.weight(1f),
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                  Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                  Text("OFFICIAL CREDENTIAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }
                Text("Exam + Certificate", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text("${career.name} Mastery Exam", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
              Text("Full AI oral assessment simulating a real shift under realistic conditions.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Spacer(modifier = Modifier.height(8.dp))
              Surface(shape = RoundedCornerShape(6.dp), color = TertiaryFixed.copy(alpha = 0.4f)) {
                Text("CEFR B2 Certification", style = MaterialTheme.typography.labelSmall, color = OnTertiaryFixedVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun DoneSubLessonRow(title: String) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.padding(10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(17.dp))
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textDecoration = TextDecoration.LineThrough,
        )
      }
      Text("Done", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
    }
  }
}
