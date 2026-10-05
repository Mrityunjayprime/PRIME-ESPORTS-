package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.VideoCard
import com.example.ui.components.formatCount
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun WatchVideoScreen(
    videoId: String,
    viewModel: PrimeEsportsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val allVideos by viewModel.allVideos.collectAsState()
    val video = allVideos.find { it.id == videoId }
    val creators by viewModel.allCreators.collectAsState()
    val creator = creators.find { it.id == video?.authorId }

    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableFloatStateOf(0.15f) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var isCommentsOpen by remember { mutableStateOf(false) }

    // Playback loop simulation
    LaunchedEffect(isPlaying, videoId) {
        while (isPlaying) {
            delay(1000)
            currentProgress = if (currentProgress >= 1f) 0f else currentProgress + 0.01f
        }
    }

    if (video == null) {
        Box(modifier = modifier.fillMaxSize().background(DarkEsportsBg), contentAlignment = Alignment.Center) {
            Button(onClick = onBack) { Text("Video not found - Go Back") }
        }
        return
    }

    val currentSeconds = (video.durationSeconds * currentProgress).toInt()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
            // 1. Video Player Area
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .background(Color.Black)
                ) {
                    if (video.mediaResId != null) {
                        Image(
                            painter = painterResource(id = video.mediaResId),
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Dark overlay for controls
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                    )

                    // Back Button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .align(Alignment.TopStart)
                            .testTag("video_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Center Play/Pause button
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.65f))
                            .align(Alignment.Center)
                            .testTag("video_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    // Bottom Seekbar & Timestamps
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = String.format(Locale.getDefault(), "%d:%02d", currentSeconds / 60, currentSeconds % 60),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = String.format(Locale.getDefault(), "%d:%02d", video.durationSeconds / 60, video.durationSeconds % 60),
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Slider(
                            value = currentProgress,
                            onValueChange = { currentProgress = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(18.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = CyberCyan,
                                activeTrackColor = CyberCyan,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // 2. Video Title, Views, Timestamp
            item {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = video.title,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${formatCount(video.viewsCount.toInt())} views • ${formatTimestamp(video.timestamp)} • #${video.category}",
                        color = TextMuted,
                        fontSize = 12.5.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row: Like, Comments, Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.toggleVideoLike(video) }
                                .padding(8.dp)
                                .testTag("video_like_button")
                        ) {
                            Icon(
                                imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (video.isLiked) FlameCrimson else TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatCount(video.likesCount),
                                color = if (video.isLiked) FlameCrimson else TextSecondary,
                                fontSize = 11.5.sp
                            )
                        }

                        // Comments Drawer trigger
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { isCommentsOpen = true }
                                .padding(8.dp)
                                .testTag("video_comments_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = "Comments",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatCount(video.commentsCount),
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                        }

                        // Share
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Share",
                                color = TextSecondary,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Creator Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.viewUserProfile(video.authorId) }
                        ) {
                            EsportsAvatar(
                                avatarIndex = video.authorAvatarIndex,
                                name = video.authorName,
                                size = 44.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = video.authorName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                                Text(
                                    text = "${formatCount(creator?.followersCount ?: 48000)} followers",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        val isFollowed = creator?.isFollowedByMe ?: false
                        Button(
                            onClick = { viewModel.followUser(video.authorId, !isFollowed) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowed) DarkSurfaceElevated else CyberCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (isFollowed) "Subscribed" else "Subscribe",
                                color = if (isFollowed) TextSecondary else DarkEsportsBg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceElevated)
                            .clickable { isDescriptionExpanded = !isDescriptionExpanded }
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = video.description,
                                color = TextSecondary,
                                fontSize = 13.sp,
                                maxLines = if (isDescriptionExpanded) 15 else 2,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isDescriptionExpanded) "Show less" else "...more",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Recommended Videos Section
            item {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    Text(
                        text = "RECOMMENDED ESPORTS MATCHES",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }

            val related = allVideos.filter { it.id != videoId }
            items(related, key = { it.id }) { item ->
                Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
                    VideoCard(
                        video = item,
                        onClick = { viewModel.openVideo(item.id) },
                        onCreatorClick = { viewModel.viewUserProfile(item.authorId) }
                    )
                }
            }
        }

        // Active Comments Sheet
        if (isCommentsOpen) {
            CommentsBottomSheet(
                targetType = "VIDEO",
                targetId = video.id,
                title = video.title,
                viewModel = viewModel,
                onDismiss = { isCommentsOpen = false }
            )
        }
    }
}
