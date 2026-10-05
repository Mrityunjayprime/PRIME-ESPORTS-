package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NotificationsScreen(
    viewModel: PrimeEsportsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val notifications by viewModel.notifications.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "NOTIFICATIONS",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notifications right now.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { notif ->
                    NotificationCard(
                        notif = notif,
                        onAction = {
                            when (notif.type) {
                                "LIVE" -> notif.targetId?.let { viewModel.openLiveStream(it) }
                                "FRIEND_REQUEST" -> notif.targetId?.let { viewModel.viewUserProfile(it) }
                                "FOLLOW" -> notif.targetId?.let { viewModel.viewUserProfile(it) }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notif: NotificationEntity,
    onAction: () -> Unit
) {
    val (icon, iconColor) = when (notif.type) {
        "LIVE" -> Pair(Icons.Default.LiveTv, LiveBadgeRed)
        "FRIEND_REQUEST", "FOLLOW" -> Pair(Icons.Default.PersonAdd, CyberCyan)
        "LIKE" -> Pair(Icons.Default.Favorite, FlameCrimson)
        "COMMENT" -> Pair(Icons.Default.Chat, ElectricViolet)
        else -> Pair(Icons.Default.Check, EmeraldNeon)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (notif.isRead) DarkSurfaceElevated.copy(alpha = 0.7f) else DarkSurfaceElevated)
            .border(
                0.8.dp,
                if (!notif.isRead) CyberCyan.copy(alpha = 0.6f) else DarkSurfaceBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onAction)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            EsportsAvatar(
                avatarIndex = notif.sourceAvatarIndex,
                name = notif.sourceName,
                size = 44.dp
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(iconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(11.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notif.title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
            Text(
                text = notif.body,
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = formatTimestamp(notif.timestamp),
                color = TextMuted,
                fontSize = 10.5.sp
            )
        }
    }
}
