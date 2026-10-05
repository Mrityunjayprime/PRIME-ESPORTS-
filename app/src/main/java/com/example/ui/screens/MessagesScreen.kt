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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun MessagesScreen(
    viewModel: PrimeEsportsViewModel,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()
    val activeChatUserId by viewModel.activeChatUserId.collectAsState()

    if (activeConversationId != null && activeChatUserId != null) {
        val conv = conversations.find { it.id == activeConversationId }
        ChatThreadView(
            conversation = conv,
            conversationId = activeConversationId!!,
            otherUserId = activeChatUserId!!,
            viewModel = viewModel,
            onBack = { viewModel.closeConversation() },
            modifier = modifier
        )
    } else {
        ConversationListView(
            conversations = conversations,
            onSelectConversation = { conv ->
                viewModel.openConversation(conv.id, conv.otherUserId)
            },
            onUserClick = { userId ->
                viewModel.viewUserProfile(userId)
            },
            modifier = modifier
        )
    }
}

@Composable
private fun ConversationListView(
    conversations: List<ConversationEntity>,
    onSelectConversation: (ConversationEntity) -> Unit,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .statusBarsPadding()
            .padding(bottom = 70.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DIRECT MESSAGES",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Encrypted esports chat & calls",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

        if (conversations.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active conversations yet.", color = TextMuted)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(conversations, key = { it.id }) { conv ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                            .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(14.dp))
                            .clickable { onSelectConversation(conv) }
                            .padding(12.dp)
                            .testTag("conversation_row_${conv.id}"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        EsportsAvatar(
                            avatarIndex = conv.otherUserAvatarIndex,
                            name = conv.otherUserName,
                            size = 46.dp,
                            showOnlineIndicator = true,
                            isOnline = conv.isOnline,
                            onClick = { onUserClick(conv.otherUserId) }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = conv.otherUserName,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                                Text(
                                    text = formatTimestamp(conv.lastMessageTimestamp),
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (conv.isTyping) "typing..." else conv.lastMessage,
                                color = if (conv.isTyping) CyberCyan else TextSecondary,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (conv.unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = CyberCyan, contentColor = DarkEsportsBg) {
                                Text(
                                    text = conv.unreadCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatThreadView(
    conversation: ConversationEntity?,
    conversationId: String,
    otherUserId: String,
    viewModel: PrimeEsportsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val messages by viewModel.getConversationMessages(conversationId).collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val otherName = conversation?.otherUserName ?: "Gamer"
    val otherHandle = conversation?.otherUserHandle ?: "@player"
    val otherAvatarIndex = conversation?.otherUserAvatarIndex ?: 1

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Chat Header with Voice / Video Call buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                EsportsAvatar(
                    avatarIndex = otherAvatarIndex,
                    name = otherName,
                    size = 38.dp,
                    showOnlineIndicator = true,
                    isOnline = conversation?.isOnline ?: true,
                    onClick = { viewModel.viewUserProfile(otherUserId) }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = otherName,
                        color = TextPrimary,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (conversation?.isOnline == true) "Online • In Ranked" else "Offline",
                        color = if (conversation?.isOnline == true) EmeraldNeon else TextMuted,
                        fontSize = 11.5.sp
                    )
                }
            }

            // Voice & Video Call Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Voice Call Button
                IconButton(
                    onClick = {
                        viewModel.startCall(
                            callerName = otherName,
                            callerHandle = otherHandle,
                            callerAvatarIndex = otherAvatarIndex,
                            isVideo = false
                        )
                    },
                    modifier = Modifier.size(36.dp).testTag("start_voice_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice Call",
                        tint = CyberCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Video Call Button
                IconButton(
                    onClick = {
                        viewModel.startCall(
                            callerName = otherName,
                            callerHandle = otherHandle,
                            callerAvatarIndex = otherAvatarIndex,
                            isVideo = true
                        )
                    },
                    modifier = Modifier.size(36.dp).testTag("start_video_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = ElectricViolet,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isMe = msg.isFromMe
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    if (!isMe) {
                        EsportsAvatar(
                            avatarIndex = otherAvatarIndex,
                            name = otherName,
                            size = 28.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth(0.78f)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMe) 16.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 16.dp
                                    )
                                )
                                .background(
                                    if (isMe) {
                                        Brush.linearGradient(listOf(CyberCyan.copy(alpha = 0.85f), ElectricViolet))
                                    } else {
                                        Brush.linearGradient(listOf(DarkSurfaceElevated, DarkSurface))
                                    }
                                )
                                .border(
                                    0.6.dp,
                                    if (isMe) CyberCyan else DarkSurfaceBorder,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = if (isMe) Color.White else TextPrimary,
                                fontSize = 13.5.sp,
                                lineHeight = 19.sp
                            )
                        }

                        Text(
                            text = formatTimestamp(msg.timestamp),
                            color = TextMuted,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }
            }
        }

        // Quick Emoji Picker Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf("🔥", "🎮", "🏆", "GG", "💀", "❤️").forEach { emoji ->
                Text(
                    text = emoji,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { messageText += emoji }
                        .padding(4.dp)
                )
            }
        }

        Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Type an esports message...", color = TextMuted, fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_message_input"),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedIndicatorColor = CyberCyan,
                    unfocusedIndicatorColor = DarkSurfaceBorder
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(conversationId, otherUserId, messageText.trim())
                        messageText = ""
                        scope.launch {
                            if (messages.isNotEmpty()) {
                                listState.animateScrollToItem(messages.size)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CyberCyan)
                    .testTag("send_chat_message_button"),
                enabled = messageText.isNotBlank()
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
