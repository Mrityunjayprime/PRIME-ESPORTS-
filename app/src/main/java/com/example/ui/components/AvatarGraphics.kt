package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.NeonPurple

@Composable
fun EsportsAvatar(
    avatarIndex: Int,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    showOnlineIndicator: Boolean = false,
    isOnline: Boolean = true,
    isLive: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val (gradientColors, iconVector) = when (avatarIndex % 5) {
        0 -> Pair(listOf(CyberCyan, ElectricViolet), Icons.Default.SportsEsports)
        1 -> Pair(listOf(EmeraldNeon, Color(0xFF00B0FF)), Icons.Default.FlashOn)
        2 -> Pair(listOf(FlameCrimson, Color(0xFFFF9100)), Icons.Default.Shield)
        3 -> Pair(listOf(NeonPurple, Color(0xFFFF4081)), Icons.Default.Star)
        else -> Pair(listOf(Color(0xFF00E5FF), Color(0xFF7C4DFF)), Icons.Default.Security)
    }

    Box(
        modifier = modifier
            .size(size)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glowing Outer Ring if Live
        if (isLive) {
            Box(
                modifier = Modifier
                    .size(size + 4.dp)
                    .clip(CircleShape)
                    .border(2.dp, LiveBadgeRed, CircleShape)
            )
        }

        // Inner Avatar Circle
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(gradientColors),
                    shape = CircleShape
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            gradientColors[0].copy(alpha = 0.35f),
                            gradientColors[1].copy(alpha = 0.65f),
                            DarkEsportsBg
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            val initial = name.firstOrNull()?.uppercase() ?: "P"
            if (size > 36.dp) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(size * 0.52f)
                )
            } else {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.42f).sp
                )
            }
        }

        // Online or Live Indicator Dot
        if (showOnlineIndicator) {
            val dotColor = if (isLive) LiveBadgeRed else (if (isOnline) EmeraldNeon else Color(0xFF64748B))
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
                    .size(size * 0.28f)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(1.5.dp, DarkEsportsBg, CircleShape)
            )
        }
    }
}
