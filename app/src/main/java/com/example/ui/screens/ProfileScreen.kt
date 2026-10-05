package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.R
import com.example.data.model.UserEntity
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.PostCard
import com.example.ui.components.VideoCard
import com.example.ui.components.formatCount
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    targetUserId: String?,
    viewModel: PrimeEsportsViewModel,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (onBack != null) {
        BackHandler { onBack() }
    }

    val currentUser by viewModel.currentUser.collectAsState()
    val allCreators by viewModel.allCreators.collectAsState()
    val posts by viewModel.allPosts.collectAsState()
    val videos by viewModel.allVideos.collectAsState()
    val reels by viewModel.allReels.collectAsState()

    val profileUser: UserEntity? = if (targetUserId == null || targetUserId == currentUser?.id) {
        currentUser
    } else {
        allCreators.find { it.id == targetUserId }
    }

    val isMe = profileUser?.id == currentUser?.id
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Posts", "Videos", "Reels", "Links")

    if (profileUser == null) {
        Box(modifier = modifier.fillMaxSize().background(DarkEsportsBg), contentAlignment = Alignment.Center) {
            Text("User profile not found", color = TextPrimary)
        }
        return
    }

    val userPosts = posts.filter { it.authorId == profileUser.id }
    val userVideos = videos.filter { it.authorId == profileUser.id }
    val userReels = reels.filter { it.authorId == profileUser.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .padding(bottom = 70.dp)
    ) {
        // 1. Hero Banner Area & Avatar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Background Banner
                Image(
                    painter = painterResource(id = R.drawable.esports_hero_banner_1791090922894),
                    contentDescription = "Profile Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                // Vignette
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent, DarkEsportsBg)
                            )
                        )
                )

                // Top Back Button (if viewing other user)
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                // Avatar overlapping banner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp)
                ) {
                    EsportsAvatar(
                        avatarIndex = profileUser.avatarIndex,
                        name = profileUser.nickname,
                        size = 78.dp,
                        showOnlineIndicator = true,
                        isOnline = true
                    )
                }
            }
        }

        // 2. Profile Details & Action Buttons
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profileUser.nickname,
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (profileUser.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "@${profileUser.username}",
                            color = TextMuted,
                            fontSize = 13.5.sp
                        )
                    }

                    // Action Button (Edit Profile or Follow/Message)
                    if (isMe) {
                        Button(
                            onClick = { viewModel.openEditProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = CyberCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", color = TextPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.followUser(profileUser.id, !profileUser.isFollowedByMe) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (profileUser.isFollowedByMe) DarkSurfaceElevated else CyberCyan
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (profileUser.isFollowedByMe) "Following" else "Follow",
                                    color = if (profileUser.isFollowedByMe) TextSecondary else DarkEsportsBg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    viewModel.openConversation("conv_${profileUser.id}", profileUser.id)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Custom Status Tag
                if (profileUser.status.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceElevated)
                            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🎮 ${profileUser.status}",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Bio
                Text(
                    text = profileUser.bio,
                    color = TextSecondary,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row: Followers, Following, Friends
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatItem(count = formatCount(profileUser.followersCount), label = "Followers")
                    StatItem(count = profileUser.followingCount.toString(), label = "Following")
                    StatItem(count = profileUser.friendsCount.toString(), label = "Friends")
                }
            }
        }

        // 3. Tab Row: Posts, Videos, Reels, Links
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = CyberCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyberCyan
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) CyberCyan else TextMuted,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        // 4. Tab Content
        when (selectedTab) {
            0 -> {
                // Posts
                if (userPosts.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No posts published yet.", color = TextMuted)
                        }
                    }
                } else {
                    items(userPosts, key = { it.id }) { post ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            PostCard(
                                post = post,
                                onLikeClick = { viewModel.togglePostLike(post) },
                                onCommentClick = { },
                                onShareClick = { },
                                onCreatorClick = { },
                                onReportClick = { viewModel.reportContent("POST", post.id) }
                            )
                        }
                    }
                }
            }
            1 -> {
                // Videos
                if (userVideos.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No videos uploaded yet.", color = TextMuted)
                        }
                    }
                } else {
                    items(userVideos, key = { it.id }) { video ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            VideoCard(
                                video = video,
                                onClick = { viewModel.openVideo(video.id) },
                                onCreatorClick = { }
                            )
                        }
                    }
                }
            }
            2 -> {
                // Reels
                if (userReels.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No reels posted yet.", color = TextMuted)
                        }
                    }
                } else {
                    items(userReels, key = { it.id }) { reel ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .clickable { viewModel.selectTab(com.example.ui.MainNavTab.REELS) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(text = reel.caption, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${formatCount(reel.likesCount)} likes • ${reel.gameTag}", color = CyberCyan, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            3 -> {
                // Links Section (Twitch, Discord, YouTube, Twitter, TikTok, Website)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "OFFICIAL SOCIAL & GAMING LINKS",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        SocialLinkCard("Twitch", profileUser.twitchUrl.ifEmpty { "Not linked" }, CyberCyan)
                        SocialLinkCard("Discord", profileUser.discordTag.ifEmpty { "Not linked" }, ElectricViolet)
                        SocialLinkCard("YouTube", profileUser.youtubeUrl.ifEmpty { "Not linked" }, FlameCrimson)
                        SocialLinkCard("X / Twitter", profileUser.twitterHandle.ifEmpty { "Not linked" }, Color(0xFF1DA1F2))
                        SocialLinkCard("TikTok", profileUser.tiktokHandle.ifEmpty { "Not linked" }, Color(0xFFFF0050))
                        SocialLinkCard("Website", profileUser.customWebsite.ifEmpty { "Not linked" }, EmeraldNeon)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(count: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = count,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun SocialLinkCard(platform: String, handleOrUrl: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = platform, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                Text(text = handleOrUrl, color = if (handleOrUrl == "Not linked") TextMuted else CyberCyan, fontSize = 12.sp)
            }
        }
    }
}
