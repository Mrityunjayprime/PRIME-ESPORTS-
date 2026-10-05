package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SlowMotionVideo
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainNavTab
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun MobileBottomBar(
    currentTab: MainNavTab,
    onTabSelected: (MainNavTab) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkEsportsBg.copy(alpha = 0.96f))
            .border(
                width = 0.8.dp,
                color = DarkSurfaceBorder.copy(alpha = 0.6f)
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Tab
            BottomNavItem(
                iconSelected = Icons.Filled.Home,
                iconUnselected = Icons.Outlined.Home,
                label = "Home",
                isSelected = currentTab == MainNavTab.HOME,
                testTag = "nav_home",
                onClick = { onTabSelected(MainNavTab.HOME) }
            )

            // Reels Tab
            BottomNavItem(
                iconSelected = Icons.Filled.SlowMotionVideo,
                iconUnselected = Icons.Outlined.SlowMotionVideo,
                label = "Reels",
                isSelected = currentTab == MainNavTab.REELS,
                testTag = "nav_reels",
                onClick = { onTabSelected(MainNavTab.REELS) }
            )

            // Center Floating Create/Plus Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .shadow(elevation = 12.dp, shape = CircleShape, spotColor = CyberCyan)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(CyberCyan, ElectricViolet)
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = androidx.compose.material3.ripple(bounded = true),
                        onClick = onCreateClick
                    )
                    .testTag("nav_create_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Content",
                    tint = DarkEsportsBg,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Live Streams Tab
            BottomNavItem(
                iconSelected = Icons.Filled.LiveTv,
                iconUnselected = Icons.Outlined.LiveTv,
                label = "Live",
                isSelected = currentTab == MainNavTab.STREAMS,
                testTag = "nav_streams",
                onClick = { onTabSelected(MainNavTab.STREAMS) }
            )

            // Profile Tab
            BottomNavItem(
                iconSelected = Icons.Filled.Person,
                iconUnselected = Icons.Outlined.Person,
                label = "Profile",
                isSelected = currentTab == MainNavTab.PROFILE,
                testTag = "nav_profile",
                onClick = { onTabSelected(MainNavTab.PROFILE) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    iconSelected: ImageVector,
    iconUnselected: ImageVector,
    label: String,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(bounded = true, radius = 24.dp),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else iconUnselected,
            contentDescription = label,
            tint = if (isSelected) CyberCyan else TextMuted,
            modifier = Modifier.size(23.dp)
        )
        Text(
            text = label,
            color = if (isSelected) TextPrimary else TextMuted,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
