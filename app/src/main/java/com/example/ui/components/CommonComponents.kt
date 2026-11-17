package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.VocabItem
import com.example.ui.theme.AmberStreak
import com.example.ui.theme.AmberStreakBg
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryFixed

fun careerIconForId(careerId: String): ImageVector = when (careerId) {
  "retail" -> Icons.Filled.Storefront
  "cs" -> Icons.Filled.HeadsetMic
  "hospitality" -> Icons.Filled.Restaurant
  "office" -> Icons.Filled.Apartment
  "sales" -> Icons.AutoMirrored.Filled.TrendingUp
  "logistics" -> Icons.Filled.LocalShipping
  "healthcare" -> Icons.Filled.MedicalServices
  "tech" -> Icons.Filled.Terminal
  else -> Icons.Filled.Explore
}

@Composable
fun WorkEnglishLogoMark(size: Dp = 32.dp) {
  Canvas(modifier = Modifier.size(size)) {
    drawRoundRect(
      brush = Brush.linearGradient(listOf(Primary, PrimaryContainer)),
      cornerRadius = CornerRadius(this.size.width * 0.3f, this.size.width * 0.3f),
    )
    val w = this.size.width
    val h = this.size.height
    val path = Path().apply {
      moveTo(w * 0.25f, h * 0.35f)
      lineTo(w * 0.36f, h * 0.65f)
      lineTo(w * 0.50f, h * 0.42f)
      lineTo(w * 0.64f, h * 0.65f)
      lineTo(w * 0.75f, h * 0.35f)
    }
    drawPath(
      path = path,
      color = Color.White,
      style = Stroke(width = w * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round),
    )
    drawCircle(
      color = SecondaryFixed,
      radius = w * 0.075f,
      center = Offset(w * 0.75f, h * 0.27f),
    )
  }
}

@Composable
fun AvatarBadge(
  initials: String,
  onClick: (() -> Unit)? = null,
) {
  Box(
    modifier = Modifier
      .size(36.dp)
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    contentAlignment = Alignment.Center,
  ) {
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(PrimaryFixed),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = initials,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Primary,
      )
    }
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 2.dp, bottom = 2.dp)
        .size(10.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surface)
        .padding(1.5.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.secondary),
    )
  }
}

@Composable
fun WorkEnglishTopBar(
  tabLabel: String,
  streakDays: Long,
  initials: String,
  onAvatarClick: () -> Unit,
) {
  Surface(
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(60.dp)
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        WorkEnglishLogoMark(32.dp)
        Text(
          text = "WorkEnglish",
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
        )
        Text(
          text = tabLabel,
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Surface(
          shape = CircleShape,
          color = AmberStreakBg,
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Icon(
              imageVector = Icons.Filled.LocalFireDepartment,
              contentDescription = "Streak",
              tint = AmberStreak,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = "$streakDays Days",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = AmberStreak,
            )
          }
        }
        AvatarBadge(initials = initials, onClick = onAvatarClick)
      }
    }
  }
}

@Composable
fun StackTopBar(
  title: String,
  initials: String,
  onBackClick: () -> Unit,
) {
  Surface(
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .height(60.dp)
        .padding(horizontal = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        IconButton(onClick = onBackClick) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp),
          )
        }
        WorkEnglishLogoMark(30.dp)
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }
      AvatarBadge(initials = initials)
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VocabFlashCard(
  item: VocabItem,
  arabicEnabled: Boolean,
  isSaved: Boolean,
  onToggleSave: () -> Unit,
  onSpeak: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    shape = RoundedCornerShape(18.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLowest,
    shadowElevation = 2.dp,
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
          ) {
            Text(
              text = item.word,
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = if (item.isSecondaryAccent) {
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
              } else {
                PrimaryFixed.copy(alpha = 0.65f)
              },
            ) {
              Text(
                text = item.ipa,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (item.isSecondaryAccent) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              )
            }
            if (arabicEnabled && item.arabic.isNotBlank()) {
              Text(
                text = item.arabic,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically),
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = item.example,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
          )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onToggleSave,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(
                if (isSaved) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.surfaceContainer
              ),
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
              contentDescription = "Save word",
              tint = if (isSaved) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(18.dp),
            )
          }

          IconButton(
            onClick = onSpeak,
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "Listen to pronunciation",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp),
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = item.tip,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
          )
          Icon(
            imageVector = Icons.Filled.Lightbulb,
            contentDescription = null,
            tint = AmberStreak,
            modifier = Modifier.size(16.dp),
          )
        }
      }
    }
  }
}
