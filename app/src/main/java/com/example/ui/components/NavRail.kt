package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainNavTab
import com.example.ui.theme.*

@Composable
fun VerbaNavRail(
    currentTab: MainNavTab,
    onTabSelected: (MainNavTab) -> Unit,
    onOpenSettings: () -> Unit,
    onLogoutClick: () -> Unit,
    unreadMessagesTotal: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(78.dp)
            .fillMaxHeight()
            .background(VerbaGlassSurface)
            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section: WhatsApp Liquid Glass Logo + Primary Navigation Icons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // WhatsApp-style Liquid Emblem
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LiquidGlassEmeraldGradient)
                    .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                    .testTag("app_brand_logo"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "V",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Chat
            RailIconItem(
                selected = currentTab == MainNavTab.CHAT,
                activeIcon = Icons.AutoMirrored.Filled.Chat,
                inactiveIcon = Icons.AutoMirrored.Outlined.Chat,
                label = "Chat",
                badgeCount = unreadMessagesTotal,
                testTag = "nav_rail_chat",
                onClick = { onTabSelected(MainNavTab.CHAT) }
            )

            // 2. Status
            RailIconItem(
                selected = currentTab == MainNavTab.STATUS,
                activeIcon = Icons.Filled.DonutLarge,
                inactiveIcon = Icons.Outlined.DonutLarge,
                label = "Status",
                testTag = "nav_rail_status",
                onClick = { onTabSelected(MainNavTab.STATUS) }
            )

            // 3. Panggilan
            RailIconItem(
                selected = currentTab == MainNavTab.CALLS,
                activeIcon = Icons.Filled.Call,
                inactiveIcon = Icons.Outlined.Call,
                label = "Panggilan",
                testTag = "nav_rail_calls",
                onClick = { onTabSelected(MainNavTab.CALLS) }
            )

            // 4. Grup
            RailIconItem(
                selected = currentTab == MainNavTab.GROUPS,
                activeIcon = Icons.Filled.Groups,
                inactiveIcon = Icons.Outlined.Groups,
                label = "Grup",
                testTag = "nav_rail_groups",
                onClick = { onTabSelected(MainNavTab.GROUPS) }
            )

            // 5. AI Assistant
            RailIconItem(
                selected = currentTab == MainNavTab.AI,
                activeIcon = Icons.Filled.AutoAwesome,
                inactiveIcon = Icons.Outlined.AutoAwesome,
                label = "AI Assistant",
                testTag = "nav_rail_ai",
                onClick = { onTabSelected(MainNavTab.AI) }
            )
        }

        // Bottom section: Settings & Sign out
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(VerbaGlassBorder)
            )

            RailIconItem(
                selected = false,
                activeIcon = Icons.Filled.Settings,
                inactiveIcon = Icons.Outlined.Settings,
                label = "Pengaturan",
                testTag = "nav_rail_settings",
                onClick = onOpenSettings
            )

            RailIconItem(
                selected = false,
                activeIcon = Icons.AutoMirrored.Outlined.ExitToApp,
                inactiveIcon = Icons.AutoMirrored.Outlined.ExitToApp,
                label = "Keluar",
                testTag = "nav_rail_logout",
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun RailIconItem(
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    label: String,
    badgeCount: Int = 0,
    testTag: String,
    onClick: () -> Unit
) {
    val activeColor = WhatsAppEmerald
    val iconColor = if (selected) activeColor else VerbaTextSecondary
    val bgColor = if (selected) activeColor.copy(alpha = 0.16f) else Color.Transparent
    val border = if (selected) activeColor.copy(alpha = 0.35f) else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = if (selected) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(WhatsAppEmerald),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (selected) VerbaTextPrimary else VerbaTextMuted,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
