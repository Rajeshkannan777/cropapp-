package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriStatusDanger
import com.example.ui.theme.AgriStatusHealthy
import com.example.ui.theme.AgriStatusInfo
import com.example.ui.theme.AgriStatusWarning

/**
 * Pure white card with clean border and subtle elevation, tailored for agricultural sunlight visibility.
 */
@Composable
fun AgriCard(
  modifier: Modifier = Modifier,
  borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  if (onClick != null) {
    Card(
      onClick = onClick,
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color.White
      ),
      border = BorderStroke(1.dp, borderColor),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      content()
    }
  } else {
    Card(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color.White
      ),
      border = BorderStroke(1.dp, borderColor),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      content()
    }
  }
}

/**
 * Large, high-contrast button specifically designed for farmers' outdoor usage with gloves or working hands.
 * Minimum height 56dp for high accessibility touch targets.
 */
@Composable
fun FarmerButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  containerColor: Color = AgriGreenPrimary,
  contentColor: Color = Color.White,
  testTag: String = "farmer_button"
) {
  Button(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(56.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = containerColor,
      contentColor = contentColor,
      disabledContainerColor = Color(0xFFE0E0E0),
      disabledContentColor = Color(0xFF9E9E9E)
    ),
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
    enabled = enabled
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      )
    }
  }
}

/**
 * Secondary outlined action button with large hit area.
 */
@Composable
fun FarmerOutlinedButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  borderColor: Color = AgriGreenPrimary,
  textColor: Color = AgriGreenPrimary,
  testTag: String = "farmer_outlined_button"
) {
  OutlinedButton(
    onClick = onClick,
    modifier = modifier
      .fillMaxWidth()
      .height(54.dp)
      .testTag(testTag),
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(2.dp, if (enabled) borderColor else Color.LightGray),
    colors = ButtonDefaults.outlinedButtonColors(
      contentColor = textColor,
      disabledContentColor = Color.Gray
    ),
    enabled = enabled
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
          fontSize = 15.sp,
          fontWeight = FontWeight.SemiBold
        )
      )
    }
  }
}

/**
 * Status indicator badge for farm health, risks, and diagnosis tags.
 */
@Composable
fun StatusBadge(
  label: String,
  statusColor: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = statusColor.copy(alpha = 0.12f),
    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(statusColor)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        color = statusColor,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      )
    }
  }
}

/**
 * Section Header for Agricultural modules.
 */
@Composable
fun SectionHeader(
  title: String,
  subtitle: String? = null,
  actionText: String? = null,
  onActionClick: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
          color = Color(0xFF1E281E),
          fontWeight = FontWeight.Bold
        )
      )
      if (!subtitle.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = Color(0xFF5A6658)
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
    if (!actionText.isNullOrBlank() && onActionClick != null) {
      OutlinedButton(
        onClick = onActionClick,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, AgriGreenPrimary),
        modifier = Modifier.height(36.dp)
      ) {
        Text(
          text = actionText,
          style = MaterialTheme.typography.labelMedium.copy(
            color = AgriGreenPrimary,
            fontWeight = FontWeight.Bold
          )
        )
      }
    }
  }
}

/**
 * Top App Bar with Farm Station info and Alerts counter.
 */
@Composable
fun CropGuardTopBar(
  currentSectionTitle: String,
  alertCount: Int = 2,
  onAlertClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = Color.White,
    shadowElevation = 2.dp,
    border = BorderStroke(0.dp, Color.Transparent)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = AgriGreenPrimary,
          modifier = Modifier.size(42.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Park,
              contentDescription = "CropGuard AI Logo",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "CropGuard AI",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFE8F5E9)
            ) {
              Text(
                text = "Agri-v1.0",
                color = AgriGreenPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Green Valley Agro • Field #4",
            style = MaterialTheme.typography.bodySmall.copy(
              color = Color(0xFF6B7280)
            )
          )
        }
      }

      IconButton(
        onClick = onAlertClick,
        modifier = Modifier.testTag("top_bar_alert_button")
      ) {
        BadgedBox(
          badge = {
            if (alertCount > 0) {
              Badge(
                containerColor = AgriStatusDanger,
                contentColor = Color.White
              ) {
                Text(text = alertCount.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Farm Alerts",
            tint = Color(0xFF374151),
            modifier = Modifier.size(26.dp)
          )
        }
      }
    }
  }
}
