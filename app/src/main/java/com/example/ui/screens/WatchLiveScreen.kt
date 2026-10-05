package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.CommentEntity
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.EsportsAvatar
import com.example.ui.components.formatCount
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.ModBadgeGreen
import com.example.ui.theme.PartnerBadgeBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VipBadgeGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class LiveChatMessage(
    val id: String,
    val senderName: String,
    val badge: String, // HOST, VIP, MOD, SUB, GAMER
    val badgeColor: Color,
    val text: String
)

@Composable
fun WatchLiveScreen(
    streamId: String,
    viewModel: PrimeEsportsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val liveStreams by viewModel.liveStreams.collectAsState()
    val stream = liveStreams.find { it.id == streamId }
    val currentUser by viewModel.currentUser.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            LiveChatMessage("1", "ViperFan", "SUB", PartnerBadgeBlue, "THAT FLICK WAS INCREDIBLE!!"),
            LiveChatMessage("2", "KitsunePro", "VIP", VipBadgeGold, "PogChamp! Best stream on Prime!"),
            LiveChatMessage("3", "Mod_Shield", "MOD", ModBadgeGreen, "Please keep chat friendly everyone!"),
            LiveChatMessage("4", "GamerX", "GAMER", TextMuted, "What's the sensitivity DPI?"),
            LiveChatMessage("5", stream?.hostName ?: "Host", "HOST", LiveBadgeRed, "Welcome in chat! Let's win game 3!")
        )
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var floatingReaction by remember { mutableStateOf<String?>(null) }

    // Floating reaction dismiss timer
    LaunchedEffect(floatingReaction) {
        if (floatingReaction != null) {
            delay(1200)
            floatingReaction = null
        }
    }

    if (stream == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkEsportsBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Stream ended or not found", color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Back") }
            }
        }
        return
    }

    val isHost = stream.hostId == currentUser?.id

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .imePadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Live Video Player Header Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color.Black)
            ) {
                if (stream.mediaResId != null) {
                    Image(
                        painter = painterResource(id = stream.mediaResId),
                        contentDescription = stream.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(DarkSurfaceElevated, Color.Black)
                                )
                            )
                    )
                }

                // Top Controls Bar (Back, Viewer Count, Live Badge)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .testTag("stream_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // LIVE Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LiveBadgeRed)
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Viewers Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatCount(stream.viewerCount),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Reaction burst overlay
                if (floatingReaction != null) {
                    Text(
                        text = floatingReaction ?: "",
                        fontSize = 52.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // 2. Streamer Info Bar & Title
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.viewUserProfile(stream.hostId) }
                    ) {
                        EsportsAvatar(
                            avatarIndex = stream.hostAvatarIndex,
                            name = stream.hostName,
                            size = 40.dp,
                            isLive = true
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stream.hostName,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                            Text(
                                text = "${stream.category} • Prime Partner",
                                color = CyberCyan,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (isHost) {
                        Button(
                            onClick = { viewModel.endLiveStream(stream.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameCrimson),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("End Stream", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.followUser(stream.hostId, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Follow", color = DarkEsportsBg, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stream.title,
                    color = TextPrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2
                )
            }

            // 3. Live Chat Header & Quick Reactions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STREAM CHAT",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Quick Reactions Row
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("🔥", "GG", "Pog", "❤️", "🏆").forEach { reaction ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .border(0.6.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    floatingReaction = reaction
                                    viewModel.incrementStreamLikes(stream.id)
                                }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(text = reaction, fontSize = 12.sp)
                        }
                    }
                }
            }

            Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

            // 4. Live Chat Stream Messages
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(msg.badgeColor.copy(alpha = 0.25f))
                                .border(0.6.dp, msg.badgeColor, RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = msg.badge,
                                color = msg.badgeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = msg.senderName,
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = msg.text,
                            color = TextPrimary,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }

            Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

            // 5. Chat Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    placeholder = { Text("Send a message to stream chat...", color = TextMuted, fontSize = 12.5.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("stream_chat_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = CyberCyan,
                        unfocusedIndicatorColor = DarkSurfaceBorder
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (chatInput.isNotBlank()) {
                            chatMessages.add(
                                LiveChatMessage(
                                    id = System.currentTimeMillis().toString(),
                                    senderName = currentUser?.nickname ?: "Me",
                                    badge = "SUB",
                                    badgeColor = PartnerBadgeBlue,
                                    text = chatInput.trim()
                                )
                            )
                            chatInput = ""
                            coroutineScope.launch {
                                listState.animateScrollToItem(chatMessages.size - 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyberCyan)
                        .testTag("send_stream_chat_button"),
                    enabled = chatInput.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = DarkEsportsBg,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
