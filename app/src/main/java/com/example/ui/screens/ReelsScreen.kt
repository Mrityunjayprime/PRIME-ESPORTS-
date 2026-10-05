package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReelEntity
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.formatCount
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun ReelsScreen(
    viewModel: PrimeEsportsViewModel,
    modifier: Modifier = Modifier
) {
    val reels by viewModel.allReels.collectAsState()
    var activeCommentReel by remember { mutableStateOf<ReelEntity?>(null) }
    var isMuted by remember { mutableStateOf(false) }

    if (reels.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkEsportsBg),
            contentAlignment = Alignment.Center
        ) {
            Text("No reels available yet. Upload the first gaming short!", color = TextMuted)
        }
        return
    }

    val pagerState = rememberPagerState(pageCount = { reels.size })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val reel = reels[page]
            ReelItemPage(
                reel = reel,
                isMuted = isMuted,
                onToggleMute = { isMuted = !isMuted },
                onLikeClick = { viewModel.toggleReelLike(reel) },
                onCommentClick = { activeCommentReel = reel },
                onCreatorClick = { viewModel.viewUserProfile(reel.authorId) },
                onFollowCreator = { viewModel.followUser(reel.authorId, true) }
            )
        }

        // Top Reel Bar indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PRIME REELS",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            IconButton(
                onClick = { isMuted = !isMuted },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Mute",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Active Comments Sheet
        activeCommentReel?.let { reel ->
            CommentsBottomSheet(
                targetType = "REEL",
                targetId = reel.id,
                title = reel.caption,
                viewModel = viewModel,
                onDismiss = { activeCommentReel = null }
            )
        }
    }
}

@Composable
fun ReelItemPage(
    reel: ReelEntity,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onCreatorClick: () -> Unit,
    onFollowCreator: () -> Unit
) {
    var showHeartBurst by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }

    // Simulating smooth video playback progress loop
    LaunchedEffect(reel.id) {
        playbackProgress = 0f
        while (true) {
            delay(100)
            playbackProgress = (playbackProgress + 0.015f) % 1f
        }
    }

    // Rotating vinyl disc animation
    val infiniteTransition = rememberInfiniteTransition(label = "VinylDisc")
    val discRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiscRotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (!reel.isLiked) {
                            onLikeClick()
                        }
                        showHeartBurst = true
                    },
                    onTap = {
                        onToggleMute()
                    }
                )
            }
    ) {
        // Video Background Mockup (Image / Banner)
        if (reel.mediaResId != null) {
            Image(
                painter = painterResource(id = reel.mediaResId),
                contentDescription = reel.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DarkSurfaceElevated, DarkEsportsBg)
                        )
                    )
            )
        }

        // Dark Vignette Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Double-Tap Heart Burst Animation
        LaunchedEffect(showHeartBurst) {
            if (showHeartBurst) {
                delay(800)
                showHeartBurst = false
            }
        }
        AnimatedVisibility(
            visible = showHeartBurst,
            enter = scaleIn(tween(250)) + fadeIn(),
            exit = scaleOut(tween(250)) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Liked",
                tint = FlameCrimson,
                modifier = Modifier.size(100.dp)
            )
        }

        // Right-Side Action Column (Avatar, Like, Comment, Share, Sound Disc)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Creator Avatar with "+" Follow badge
            Box(contentAlignment = Alignment.Center) {
                EsportsAvatar(
                    avatarIndex = reel.authorAvatarIndex,
                    name = reel.authorName,
                    size = 46.dp,
                    onClick = onCreatorClick
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 8.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(CyberCyan)
                        .clickable(onClick = onFollowCreator),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow",
                        tint = DarkEsportsBg,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Like Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onLikeClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("reel_like_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Like",
                        tint = if (reel.isLiked) FlameCrimson else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    text = formatCount(reel.likesCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comment Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = onCommentClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("reel_comment_${reel.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(29.dp)
                    )
                }
                Text(
                    text = formatCount(reel.commentsCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Share Button
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = { /* Share */ },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Text(
                    text = formatCount(reel.sharesCount),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Rotating Vinyl Sound Disc
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .border(1.5.dp, CyberCyan, CircleShape)
                    .rotate(discRotation),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Audio Track",
                    tint = CyberCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bottom Left Info: Creator handle, Caption, Game Tag, Audio Track Marquee
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.78f)
                .padding(start = 16.dp, bottom = 80.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCreatorClick() }
            ) {
                Text(
                    text = reel.authorHandle,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberCyan)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = reel.gameTag,
                        color = DarkEsportsBg,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = reel.caption,
                color = TextPrimary,
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Audio Track Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = reel.audioTrackTitle,
                    color = Color.White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }

        // Simulated Playback Progress Bar at the very bottom
        LinearProgressIndicator(
            progress = { playbackProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp)
                .align(Alignment.BottomCenter),
            color = CyberCyan,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}
