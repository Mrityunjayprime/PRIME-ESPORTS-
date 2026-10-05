package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.CreateMode
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContentModal(
    initialMode: CreateMode,
    viewModel: PrimeEsportsViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember {
        mutableIntStateOf(
            when (initialMode) {
                CreateMode.POST -> 0
                CreateMode.VIDEO -> 1
                CreateMode.REEL -> 2
                CreateMode.LIVE -> 3
            }
        )
    }

    val tabTitles = listOf("Post", "Video", "Reel", "Go Live")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurfaceElevated,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextMuted)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRIME ESPORTS CREATOR STUDIO",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            // Tab Row
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

            Spacer(modifier = Modifier.height(14.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> PostCreationForm(viewModel)
                    1 -> VideoUploadForm(viewModel)
                    2 -> ReelUploadForm(viewModel)
                    3 -> GoLiveForm(viewModel)
                }
            }
        }
    }
}

@Composable
private fun PostCreationForm(viewModel: PrimeEsportsViewModel) {
    var content by remember { mutableStateOf("") }
    var gameTag by remember { mutableStateOf("Valorant") }
    var attachImage by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("What's your latest gaming play or tournament update?", color = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .testTag("post_content_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = gameTag,
            onValueChange = { gameTag = it },
            label = { Text("Game Title / Tag") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Attach match screenshot / highlight banner", color = TextSecondary, fontSize = 13.sp)
            Switch(
                checked = attachImage,
                onCheckedChange = { attachImage = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan)
            )
        }

        if (attachImage) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.esports_hero_banner_1791090922894),
                    contentDescription = "Attachment Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                if (content.isNotBlank()) {
                    viewModel.createPost(
                        content = content.trim(),
                        gameTag = gameTag.trim(),
                        mediaResId = if (attachImage) R.drawable.esports_hero_banner_1791090922894 else null
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("publish_post_button"),
            enabled = content.isNotBlank()
        ) {
            Text("Publish Post", color = DarkEsportsBg, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun VideoUploadForm(viewModel: PrimeEsportsViewModel) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Valorant") }
    var tags by remember { mutableStateOf("esports, guide, clutch") }
    var allowComments by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isUploading) {
        if (isUploading) {
            while (uploadProgress < 1f) {
                delay(150)
                uploadProgress += 0.2f
            }
            viewModel.uploadVideo(
                title = title.trim(),
                desc = desc.trim(),
                category = category.trim(),
                tags = tags.trim(),
                allowComments = allowComments,
                visibility = "PUBLIC",
                mediaResId = R.drawable.stream_thumb_gameplay_1791090935862
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Video Title") },
            modifier = Modifier.fillMaxWidth().testTag("video_title_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().height(90.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Game Category") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Allow viewer comments", color = TextSecondary)
            Switch(
                checked = allowComments,
                onCheckedChange = { allowComments = it },
                colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan)
            )
        }

        if (isUploading) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Encoding & Uploading to Prime Storage: ${(uploadProgress * 100).toInt()}%",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { uploadProgress },
                    color = CyberCyan,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    isUploading = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("upload_video_button"),
            enabled = title.isNotBlank() && !isUploading
        ) {
            Text(if (isUploading) "Processing..." else "Upload Video", color = DarkEsportsBg, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun ReelUploadForm(viewModel: PrimeEsportsViewModel) {
    var caption by remember { mutableStateOf("") }
    var soundTitle by remember { mutableStateOf("Cyber Bass - Prime Phonk") }
    var gameTag by remember { mutableStateOf("Valorant") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            label = { Text("Reel Caption") },
            placeholder = { Text("Add caption and #esports hashtags") },
            modifier = Modifier.fillMaxWidth().testTag("reel_caption_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = soundTitle,
            onValueChange = { soundTitle = it },
            label = { Text("Sound Track / Audio") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = gameTag,
            onValueChange = { gameTag = it },
            label = { Text("Game Title") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        Button(
            onClick = {
                if (caption.isNotBlank()) {
                    viewModel.uploadReel(
                        caption = caption.trim(),
                        soundTitle = soundTitle.trim(),
                        gameTag = gameTag.trim(),
                        mediaResId = R.drawable.stream_thumb_gameplay_1791090935862
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("publish_reel_button"),
            enabled = caption.isNotBlank()
        ) {
            Text("Publish Reel", color = DarkEsportsBg, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun GoLiveForm(viewModel: PrimeEsportsViewModel) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Valorant") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Stream Title") },
            placeholder = { Text("e.g. VCT QUALIFIERS & RANKED 1 GRIND") },
            modifier = Modifier.fillMaxWidth().testTag("stream_title_input"),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = LiveBadgeRed,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = LiveBadgeRed,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Description & Stream Rules") },
            modifier = Modifier.fillMaxWidth().height(90.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = LiveBadgeRed,
                unfocusedIndicatorColor = DarkSurfaceBorder
            )
        )

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.goLive(
                        title = title.trim(),
                        desc = desc.trim(),
                        category = category.trim(),
                        mediaResId = R.drawable.esports_hero_banner_1791090922894
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = LiveBadgeRed),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("start_live_stream_button"),
            enabled = title.isNotBlank()
        ) {
            Text("🔴 Go Live Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
