package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.ui.auth.attemptAutoSignIn
import com.example.ui.auth.onGoogleSignInClicked
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.TertiaryFixed

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthWelcomeScreen(
  onSpeakPhrase: (String) -> Unit,
  onAuthSuccess: () -> Unit,
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val credentialManager = remember { CredentialManager.create(context) }

  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var bilingualEnabled by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    attemptAutoSignIn(
      context = context,
      credentialManager = credentialManager,
      onAuthSuccess = onAuthSuccess,
      onUnauthenticated = {},
      scope = scope,
    )
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface)
      .statusBarsPadding()
      .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier
        .widthIn(max = 600.dp)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top Pill Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerHigh,
          shadowElevation = 1.dp,
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center,
            ) {
              Icon(
                imageVector = Icons.Filled.Work,
                contentDescription = "WorkEnglish icon",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp),
              )
            }
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            )
            Text(
              text = "CAREER-FIRST ENGLISH",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
            )
          }
        }

        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.surfaceContainerLowest,
          shadowElevation = 2.dp,
          modifier = Modifier
            .clip(CircleShape)
            .clickable { bilingualEnabled = !bilingualEnabled }
            .testTag("welcome_lang_toggle"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.Translate,
              contentDescription = "Toggle language mode",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = if (bilingualEnabled) "EN / العربية" else "EN Only",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Hero Visual Cluster with Floating Playable Speech Bubbles
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(280.dp),
        contentAlignment = Alignment.Center,
      ) {
        Box(
          modifier = Modifier
            .size(width = 190.dp, height = 224.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow),
        ) {
          Image(
            painter = painterResource(id = R.drawable.img_welcome_hero_1794745735939),
            contentDescription = "Confident workplace professional",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(Color.Transparent, Color(0xCC131B2E)),
                )
              )
              .padding(12.dp),
            contentAlignment = Alignment.BottomStart,
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.95f),
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
              ) {
                Icon(
                  imageVector = Icons.Filled.Verified,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.secondary,
                  modifier = Modifier.size(14.dp),
                )
                Text(
                  text = "CEFR A1–B2+ Workplace",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface,
                )
              }
            }
          }
        }

        // Top-left floating bubble
        FloatingSpeechChip(
          icon = Icons.Filled.MedicalServices,
          iconBg = SecondaryFixed.copy(alpha = 0.5f),
          iconTint = MaterialTheme.colorScheme.secondary,
          category = "Healthcare",
          phrase = "\"Can I assist you?\"",
          modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = 4.dp, y = 12.dp)
            .rotate(-3f),
          onClick = { onSpeakPhrase("Can I assist you?") },
        )

        // Mid-right floating bubble
        FloatingSpeechChip(
          icon = Icons.Filled.BusinessCenter,
          iconBg = PrimaryFixed,
          iconTint = MaterialTheme.colorScheme.primary,
          category = "Office & Tech",
          phrase = "\"Here's the summary\"",
          modifier = Modifier
            .align(Alignment.CenterEnd)
            .offset(x = (-2).dp, y = (-8).dp)
            .rotate(2f),
          onClick = { onSpeakPhrase("Here is the summary.") },
        )

        // Bottom-left floating bubble
        FloatingSpeechChip(
          icon = Icons.Filled.Storefront,
          iconBg = TertiaryFixed,
          iconTint = MaterialTheme.colorScheme.tertiary,
          category = "Retail",
          phrase = "\"Available in aisle 4\"",
          modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = 10.dp, y = (-4).dp)
            .rotate(-1f),
          onClick = { onSpeakPhrase("Available in aisle 4.") },
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Headline & Subhead
      Text(
        text = "Learn English for\nthe Job You Want.",
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Real English. Real Situations. Real Confidence. Tailored for healthcare, retail, hospitality, tech, and office roles.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
      )

      Spacer(modifier = Modifier.height(14.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        listOf("Healthcare", "Retail", "Tech & IT", "Hospitality", "Administration").forEach { tag ->
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainer,
          ) {
            Text(
              text = tag,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Value Proposition Cards
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ValuePropRow(
          icon = Icons.Filled.BusinessCenter,
          iconBg = MaterialTheme.colorScheme.surfaceContainer,
          iconTint = MaterialTheme.colorScheme.primary,
          title = "Role-Specific Dialogues",
          subtitle = "Interview prep, shift handovers & client calls",
        )
        ValuePropRow(
          icon = Icons.Filled.Mic,
          iconBg = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
          iconTint = MaterialTheme.colorScheme.secondary,
          title = "Gemini AI Fluency Coach",
          subtitle = "Instant phrasing, tone & pronunciation coaching",
        )
        ValuePropRow(
          icon = Icons.Filled.Translate,
          iconBg = TertiaryFixed.copy(alpha = 0.5f),
          iconTint = MaterialTheme.colorScheme.tertiary,
          title = "Arabic & Bilingual Support",
          subtitle = "Clear bilingual context & IPA sound tips",
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (errorMessage != null) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.errorContainer,
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        ) {
          Text(
            text = errorMessage ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp),
          )
        }
      }

      // Google Sign-In CTA
      Button(
        onClick = {
          errorMessage = null
          isLoading = true
          onGoogleSignInClicked(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = {
              isLoading = false
              onAuthSuccess()
            },
            onAuthError = { msg ->
              isLoading = false
              errorMessage = msg
            },
            scope = scope,
            onAuthCancelled = { isLoading = false },
          )
        },
        enabled = !isLoading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
          containerColor = PrimaryContainer,
          contentColor = Color.White,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("google_sign_in_button"),
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = Color.White,
            strokeWidth = 2.5.dp,
          )
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.CheckCircle,
              contentDescription = null,
              modifier = Modifier.size(20.dp),
            )
            Text(
              text = "Sign in with Google to Start",
              style = MaterialTheme.typography.labelLarge,
            )
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Syncs your career track, daily streaks, XP & saved vocabulary securely.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
      )
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
private fun FloatingSpeechChip(
  icon: ImageVector,
  iconBg: Color,
  iconTint: Color,
  category: String,
  phrase: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.96f),
    shadowElevation = 6.dp,
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = category,
          tint = iconTint,
          modifier = Modifier.size(16.dp),
        )
      }
      Column {
        Text(
          text = category,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline,
        )
        Text(
          text = phrase,
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  }
}

@Composable
private fun ValuePropRow(
  icon: ImageVector,
  iconBg: Color,
  iconTint: Color,
  title: String,
  subtitle: String,
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    shadowElevation = 1.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(iconBg),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(20.dp),
          )
        }
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          )
        }
      }
      Icon(
        imageVector = Icons.Filled.ChevronRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.outline,
      )
    }
  }
}
