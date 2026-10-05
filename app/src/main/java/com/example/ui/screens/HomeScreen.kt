package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.LiveStreamCard
import com.example.ui.components.PostCard
import com.example.ui.components.VideoCard
import com.example.ui.components.VoiceRoomCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    viewModel: PrimeEsportsViewModel,
    modifier: Modifier = Modifier
) {
    val posts by viewModel.allPosts.collectAsState()
    val videos by viewModel.allVideos.collectAsState()
    val liveStreams by viewModel.liveStreams.collectAsState()
    val creators by viewModel.allCreators.collectAsState()
    val voiceRooms by viewModel.voiceRooms.collectAsState()
    val connectedVoiceRoomId by viewModel.connectedVoiceRoomId.collectAsState()
    val selectedFilter by viewModel.selectedHomeFilter.collectAsState()

    var activeCommentTarget by remember { mutableStateOf<Triple<String, String, String>?>(null) } // type, id, title

    val categories = listOf("All", "Valorant", "Apex Legends", "CS2", "League of Legends", "Trending", "Tournaments")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Stories / Live Creators Bar
            item {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "LIVE CREATORS & PROS",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(creators, key = { it.id }) { creator ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { viewModel.viewUserProfile(creator.id) }
                                    .testTag("creator_story_${creator.id}")
                            ) {
                                EsportsAvatar(
                                    avatarIndex = creator.avatarIndex,
                                    name = creator.nickname,
                                    size = 56.dp,
                                    isLive = creator.status.contains("Live"),
                                    showOnlineIndicator = true,
                                    isOnline = true
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = creator.nickname.split(" ").firstOrNull() ?: creator.nickname,
                                    color = TextPrimary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // 2. Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedFilter == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) CyberCyan else DarkSurfaceElevated)
                                .border(
                                    0.8.dp,
                                    if (isSelected) CyberCyan else DarkSurfaceBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setHomeFilter(category) }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                                .testTag("filter_chip_$category")
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) DarkEsportsBg else TextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 3. Featured Live Stream (if available)
            val featuredStream = liveStreams.firstOrNull()
            if (featuredStream != null && (selectedFilter == "All" || selectedFilter == featuredStream.category || selectedFilter == "Trending")) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = LiveBadgeRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FEATURED TOURNAMENT STREAM",
                                color = TextPrimary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        LiveStreamCard(
                            stream = featuredStream,
                            onClick = { viewModel.openLiveStream(featuredStream.id) },
                            onCreatorClick = { viewModel.viewUserProfile(featuredStream.hostId) }
                        )
                    }
                }
            }

            // 4. Voice Lounge Quick Access
            val featuredVoice = voiceRooms.firstOrNull()
            if (featuredVoice != null) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "COMMUNITY VOICE CHANNELS",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        VoiceRoomCard(
                            room = featuredVoice,
                            isConnected = connectedVoiceRoomId == featuredVoice.id,
                            onJoinOrLeave = { viewModel.joinVoiceRoom(featuredVoice.id) }
                        )
                    }
                }
            }

            // 5. Feed Posts
            val filteredPosts = if (selectedFilter == "All" || selectedFilter == "Trending" || selectedFilter == "Tournaments") {
                posts
            } else {
                posts.filter { it.gameTag.equals(selectedFilter, ignoreCase = true) }
            }

            items(filteredPosts, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    PostCard(
                        post = post,
                        onLikeClick = { viewModel.togglePostLike(post) },
                        onCommentClick = {
                            activeCommentTarget = Triple("POST", post.id, "${post.authorName}'s Post")
                        },
                        onShareClick = {
                            viewModel.clearSnackbar()
                        },
                        onCreatorClick = { viewModel.viewUserProfile(post.authorId) },
                        onReportClick = { viewModel.reportContent("POST", post.id) }
                    )
                }
            }

            // 6. Highlighted Videos Section
            if (videos.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "TRENDING ESPORTS VODS & GUIDES",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                items(videos, key = { it.id }) { video ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        VideoCard(
                            video = video,
                            onClick = { viewModel.openVideo(video.id) },
                            onCreatorClick = { viewModel.viewUserProfile(video.authorId) }
                        )
                    }
                }
            }
        }

        // Active Comments Sheet
        activeCommentTarget?.let { (type, id, title) ->
            CommentsBottomSheet(
                targetType = type,
                targetId = id,
                title = title,
                viewModel = viewModel,
                onDismiss = { activeCommentTarget = null }
            )
        }
    }
}
