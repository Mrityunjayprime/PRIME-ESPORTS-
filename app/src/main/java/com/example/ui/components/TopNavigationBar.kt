package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun TopNavigationBar(
    currentUser: UserEntity?,
    unreadNotificationsCount: Int,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkEsportsBg.copy(alpha = 0.92f))
            .border(
                width = 0.8.dp,
                color = DarkSurfaceBorder.copy(alpha = 0.5f)
            )
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left & Center/Left: Prime Esports Logo & Brand Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onProfileClick() }
                    .padding(4.dp)
            ) {
                // Glowing Cyber Esports Crest Emblem
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(CyberCyan, ElectricViolet)
                            )
                        )
                        .padding(1.2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(7.dp))
                            .background(DarkEsportsBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Prime Esports Logo",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(9.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PRIME",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ESPORTS",
                            color = CyberCyan,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        text = "GLOBAL ARENA",
                        color = TextMuted,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }

            // Right: Search, Notifications, Messages, Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Search Icon Button
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Notification Icon Button with Badge
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("top_notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotificationsCount > 0) {
                                Badge(
                                    containerColor = FlameCrimson,
                                    contentColor = Color.White,
                                    modifier = Modifier.offset(x = (-3).dp, y = 3.dp)
                                ) {
                                    Text(
                                        text = if (unreadNotificationsCount > 9) "9+" else unreadNotificationsCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Messages Icon Button
                IconButton(
                    onClick = onMessagesClick,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("top_messages_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Messages",
                        tint = TextPrimary,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Profile/Avatar Menu
                EsportsAvatar(
                    avatarIndex = currentUser?.avatarIndex ?: 0,
                    name = currentUser?.nickname ?: "Prime",
                    size = 34.dp,
                    showOnlineIndicator = true,
                    isOnline = true,
                    onClick = onProfileClick,
                    modifier = Modifier.testTag("top_profile_button")
                )
            }
        }
    }
}
