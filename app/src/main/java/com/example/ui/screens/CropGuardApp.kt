package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.models.AppSection
import com.example.ui.components.CropGuardTopBar
import com.example.ui.theme.AgriBackgroundLight
import com.example.ui.theme.AgriGreenPrimary
import com.example.ui.theme.AgriGreenSecondary
import com.example.ui.theme.AgriStatusDanger

@Composable
fun CropGuardApp() {
  var currentSection by remember { mutableStateOf(AppSection.HOME) }
  val activeAlertCount = 2

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val isExpandedScreen = maxWidth >= 600.dp

    if (isExpandedScreen) {
      // Tablet / Foldable / Desktop Layout with Navigation Rail
      Row(
        modifier = Modifier
          .fillMaxSize()
          .background(AgriBackgroundLight)
      ) {
        NavigationRail(
          modifier = Modifier.fillMaxHeight(),
          containerColor = Color.White,
          contentColor = AgriGreenPrimary,
          header = {
            Text(
              text = "🌾 CropGuard",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriGreenPrimary
              ),
              modifier = Modifier.padding(vertical = 16.dp)
            )
          }
        ) {
          AppSection.values().forEach { section ->
            val isSelected = currentSection == section
            val icon = getSectionIcon(section)
            NavigationRailItem(
              selected = isSelected,
              onClick = { currentSection = section },
              icon = {
                if (section == AppSection.ALERTS && activeAlertCount > 0) {
                  BadgedBox(
                    badge = {
                      Badge(containerColor = AgriStatusDanger) {
                        Text(activeAlertCount.toString(), color = Color.White)
                      }
                    }
                  ) {
                    Icon(icon, contentDescription = section.title)
                  }
                } else {
                  Icon(icon, contentDescription = section.title)
                }
              },
              label = {
                Text(
                  text = section.shortTitle,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationRailItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = AgriGreenPrimary,
                indicatorColor = AgriGreenPrimary
              ),
              modifier = Modifier.testTag("rail_item_${section.name.lowercase()}")
            )
          }
        }

        Scaffold(
          modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
          topBar = {
            CropGuardTopBar(
              currentSectionTitle = currentSection.title,
              alertCount = activeAlertCount,
              onAlertClick = { currentSection = AppSection.ALERTS }
            )
          },
          containerColor = AgriBackgroundLight,
          contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
          ) {
            Box(modifier = Modifier.widthIn(max = 900.dp)) {
              ScreenContent(
                section = currentSection,
                onNavigateTo = { currentSection = it }
              )
            }
          }
        }
      }
    } else {
      // Mobile Handheld Screen Layout with Bottom Navigation Bar
      Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
          CropGuardTopBar(
            currentSectionTitle = currentSection.title,
            alertCount = activeAlertCount,
            onAlertClick = { currentSection = AppSection.ALERTS }
          )
        },
        bottomBar = {
          NavigationBar(
            containerColor = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier.testTag("cropguard_bottom_navigation")
          ) {
            AppSection.values().forEach { section ->
              val isSelected = currentSection == section
              val icon = getSectionIcon(section)
              NavigationBarItem(
                selected = isSelected,
                onClick = { currentSection = section },
                icon = {
                  if (section == AppSection.ALERTS && activeAlertCount > 0) {
                    BadgedBox(
                      badge = {
                        Badge(containerColor = AgriStatusDanger) {
                          Text(activeAlertCount.toString(), fontSize = 10.sp, color = Color.White)
                        }
                      }
                    ) {
                      Icon(icon, contentDescription = section.title, modifier = Modifier.size(22.dp))
                    }
                  } else {
                    Icon(icon, contentDescription = section.title, modifier = Modifier.size(22.dp))
                  }
                },
                label = {
                  Text(
                    text = section.shortTitle,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1
                  )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = Color.White,
                  selectedTextColor = AgriGreenPrimary,
                  indicatorColor = AgriGreenPrimary,
                  unselectedIconColor = Color(0xFF757575),
                  unselectedTextColor = Color(0xFF757575)
                ),
                modifier = Modifier.testTag("nav_item_${section.name.lowercase()}")
              )
            }
          }
        },
        containerColor = AgriBackgroundLight,
        contentWindowInsets = WindowInsets.safeDrawing
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          ScreenContent(
            section = currentSection,
            onNavigateTo = { currentSection = it }
          )
        }
      }
    }
  }
}

@Composable
private fun ScreenContent(
  section: AppSection,
  onNavigateTo: (AppSection) -> Unit
) {
  when (section) {
    AppSection.HOME -> HomeScreen(onNavigateTo = onNavigateTo)
    AppSection.DETECTION -> DetectionScreen()
    AppSection.WEATHER -> WeatherScreen()
    AppSection.RISK_MAP -> RiskMapScreen()
    AppSection.ALERTS -> AlertsScreen()
    AppSection.HISTORY -> HistoryScreen()
  }
}

private fun getSectionIcon(section: AppSection): ImageVector {
  return when (section) {
    AppSection.HOME -> Icons.Default.Home
    AppSection.DETECTION -> Icons.Default.CameraAlt
    AppSection.WEATHER -> Icons.Default.Cloud
    AppSection.RISK_MAP -> Icons.Default.Map
    AppSection.ALERTS -> Icons.Default.Notifications
    AppSection.HISTORY -> Icons.Default.History
  }
}
