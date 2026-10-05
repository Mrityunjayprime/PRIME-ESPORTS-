package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.SearchCategory
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.LiveStreamCard
import com.example.ui.components.PostCard
import com.example.ui.components.VideoCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SearchScreen(
    viewModel: PrimeEsportsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val query by viewModel.searchQuery.collectAsState()
    val searchFilter by viewModel.searchFilter.collectAsState()

    val creators by viewModel.allCreators.collectAsState()
    val videos by viewModel.allVideos.collectAsState()
    val reels by viewModel.allReels.collectAsState()
    val streams by viewModel.liveStreams.collectAsState()
    val posts by viewModel.allPosts.collectAsState()

    val filteredCreators = creators.filter {
        query.isEmpty() || it.username.contains(query, ignoreCase = true) || it.nickname.contains(query, ignoreCase = true) || it.bio.contains(query, ignoreCase = true)
    }

    val filteredVideos = videos.filter {
        query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) || it.tags.contains(query, ignoreCase = true)
    }

    val filteredReels = reels.filter {
        query.isEmpty() || it.caption.contains(query, ignoreCase = true) || it.gameTag.contains(query, ignoreCase = true)
    }

    val filteredStreams = streams.filter {
        query.isEmpty() || it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) || it.hostName.contains(query, ignoreCase = true)
    }

    val filteredPosts = posts.filter {
        query.isEmpty() || it.content.contains(query, ignoreCase = true) || it.gameTag.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .statusBarsPadding()
    ) {
        // Search Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search players, videos, reels, streams...", color = TextMuted, fontSize = 13.5.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("global_search_input"),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = CyberCyan,
                    unfocusedIndicatorColor = DarkSurfaceBorder
                ),
                singleLine = true
            )
        }

        // Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchCategory.values().forEach { cat ->
                val isSelected = searchFilter == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) CyberCyan else DarkSurfaceElevated)
                        .border(0.8.dp, if (isSelected) CyberCyan else DarkSurfaceBorder, RoundedCornerShape(16.dp))
                        .clickable { viewModel.setSearchFilter(cat) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("search_filter_${cat.name}")
                ) {
                    Text(
                        text = cat.name.replaceFirstChar { it.uppercase() },
                        color = if (isSelected) DarkEsportsBg else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Divider(color = DarkSurfaceBorder, thickness = 0.8.dp, modifier = Modifier.padding(top = 4.dp))

        // Search Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // People Results
            if (searchFilter == SearchCategory.ALL || searchFilter == SearchCategory.PEOPLE) {
                if (filteredCreators.isNotEmpty()) {
                    item {
                        Text(
                            text = "PLAYERS & CREATORS (${filteredCreators.size})",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    items(filteredCreators, key = { it.id }) { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                                .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.viewUserProfile(user.id) }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                EsportsAvatar(
                                    avatarIndex = user.avatarIndex,
                                    name = user.nickname,
                                    size = 42.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = user.nickname,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "@${user.username} • ${user.status}",
                                        color = TextMuted,
                                        fontSize = 11.5.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberCyan.copy(alpha = 0.2f))
                                    .border(0.8.dp, CyberCyan, RoundedCornerShape(8.dp))
                                    .clickable { viewModel.viewUserProfile(user.id) }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("View", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Live Streams Results
            if (searchFilter == SearchCategory.ALL || searchFilter == SearchCategory.LIVE) {
                if (filteredStreams.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "LIVE STREAMS (${filteredStreams.size})",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    items(filteredStreams, key = { it.id }) { stream ->
                        LiveStreamCard(
                            stream = stream,
                            onClick = { viewModel.openLiveStream(stream.id) },
                            onCreatorClick = { viewModel.viewUserProfile(stream.hostId) }
                        )
                    }
                }
            }

            // Videos Results
            if (searchFilter == SearchCategory.ALL || searchFilter == SearchCategory.VIDEOS) {
                if (filteredVideos.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "VIDEOS (${filteredVideos.size})",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    items(filteredVideos, key = { it.id }) { video ->
                        VideoCard(
                            video = video,
                            onClick = { viewModel.openVideo(video.id) },
                            onCreatorClick = { viewModel.viewUserProfile(video.authorId) }
                        )
                    }
                }
            }

            // Posts Results
            if (searchFilter == SearchCategory.ALL || searchFilter == SearchCategory.POSTS) {
                if (filteredPosts.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "POSTS (${filteredPosts.size})",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    items(filteredPosts, key = { it.id }) { post ->
                        PostCard(
                            post = post,
                            onLikeClick = { viewModel.togglePostLike(post) },
                            onCommentClick = { },
                            onShareClick = { },
                            onCreatorClick = { viewModel.viewUserProfile(post.authorId) },
                            onReportClick = { viewModel.reportContent("POST", post.id) }
                        )
                    }
                }
            }
        }
    }
}
