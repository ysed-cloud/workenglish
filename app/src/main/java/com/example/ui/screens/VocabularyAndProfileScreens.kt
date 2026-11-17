package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.data.model.CurriculumData
import com.example.data.model.SavedVocab
import com.example.data.model.UserProgress
import com.example.data.model.VocabItem
import com.example.ui.auth.signOutUser
import com.example.ui.components.VocabFlashCard
import com.example.ui.components.careerIconForId
import com.example.ui.theme.PrimaryContainer

@Composable
fun VocabularyScreen(
  progress: UserProgress,
  savedVocab: List<SavedVocab>,
  onToggleSaveVocab: (String, VocabItem) -> Unit,
  onToggleMastered: (SavedVocab) -> Unit,
  onSpeak: (String) -> Unit,
  contentPadding: PaddingValues,
) {
  val career = CurriculumData.findCareer(progress.careerId)
  var showSavedOnly by remember { mutableStateOf(false) }

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
      Spacer(modifier = Modifier.height(14.dp))

      Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("${career.name.uppercase()} DECK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text("Saved & Shift Terms", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
          }
          Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
            Text(
              text = "${savedVocab.size} Saved",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
          selected = !showSavedOnly,
          onClick = { showSavedOnly = false },
          label = { Text("Shift Essentials") },
        )
        FilterChip(
          selected = showSavedOnly,
          onClick = { showSavedOnly = true },
          label = { Text("Bookmarked (${savedVocab.size})") },
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (!showSavedOnly) {
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
      } else if (savedVocab.isEmpty()) {
        Surface(
          shape = RoundedCornerShape(18.dp),
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Icon(Icons.Filled.Bookmark, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("No saved words yet", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tap the bookmark icon on any shift term to save it to your cloud flashcard deck.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }
        }
      } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          savedVocab.forEach { saved ->
            val asItem = VocabItem(
              word = saved.word,
              ipa = saved.ipa,
              arabic = saved.arabic,
              example = saved.example,
              tip = saved.tip,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              VocabFlashCard(
                item = asItem,
                arabicEnabled = progress.arabicEnabled,
                isSaved = true,
                onToggleSave = { onToggleSaveVocab(saved.careerId, asItem) },
                onSpeak = { onSpeak("${saved.word}. ${saved.example}") },
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(10.dp))
                  .clickable { onToggleMastered(saved) }
                  .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                  imageVector = Icons.Filled.CheckCircle,
                  contentDescription = null,
                  tint = if (saved.mastered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (saved.mastered) "Mastered" else "Mark as mastered",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (saved.mastered) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                )
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
fun ProfileScreen(
  progress: UserProgress,
  initials: String,
  onChangeCareer: () -> Unit,
  onChangeLevel: () -> Unit,
  onToggleArabic: () -> Unit,
  onSignedOut: () -> Unit,
  contentPadding: PaddingValues,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  val career = CurriculumData.findCareer(progress.careerId)
  val level = CurriculumData.findLevel(progress.cefrLevel)

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
      Spacer(modifier = Modifier.height(14.dp))

      // User Card
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          horizontalArrangement = Arrangement.spacedBy(14.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(PrimaryContainer),
            contentAlignment = Alignment.Center,
          ) {
            Text(
              text = initials,
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(progress.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("${career.name} • ${level.code} ${level.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
              ) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(14.dp))
                Text("${progress.xp} Total XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Preferences Card
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("LEARNING PREFERENCES", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)

          PreferenceActionRow(
            icon = careerIconForId(career.id),
            title = "Career Path",
            subtitle = career.name,
            onClick = onChangeCareer,
            testTag = "profile_career_row",
          )

          PreferenceActionRow(
            icon = Icons.Filled.MilitaryTech,
            title = "CEFR Level",
            subtitle = "${level.code} • ${level.name}",
            onClick = onChangeLevel,
            testTag = "profile_level_row",
          )

          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .clickable(onClick = onToggleArabic)
              .testTag("profile_arabic_toggle"),
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f),
              ) {
                Icon(Icons.Filled.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column {
                  Text("Arabic Translation Support", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                  Text("Show bilingual hints & translations", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }
              Surface(
                shape = CircleShape,
                color = if (progress.arabicEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceContainerHighest,
              ) {
                Text(
                  text = if (progress.arabicEnabled) "ON" else "OFF",
                  style = MaterialTheme.typography.labelSmall,
                  color = if (progress.arabicEnabled) Color.White else MaterialTheme.colorScheme.outline,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          signOutUser(
            credentialManager = credentialManager,
            scope = scope,
            onSignOutComplete = onSignedOut,
          )
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
          contentColor = MaterialTheme.colorScheme.onSurface,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("sign_out_button"),
      ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign Out", style = MaterialTheme.typography.labelLarge)
      }
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun PreferenceActionRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick)
      .testTag(testTag),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f),
      ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column {
          Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
          Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
      Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
  }
}
