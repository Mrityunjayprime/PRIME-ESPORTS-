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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CreateMode
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.LiveStreamCard
import com.example.ui.components.VoiceRoomCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun StreamsScreen(
    viewModel: PrimeEsportsViewModel,
    modifier: Modifier = Modifier
) {
    val liveStreams by viewModel.liveStreams.collectAsState()
    val voiceRooms by viewModel.voiceRooms.collectAsState()
    val connectedVoiceRoomId by viewModel.connectedVoiceRoomId.collectAsState()

    var selectedGameCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Valorant", "CS2", "Apex Legends", "League of Legends", "Tekken 8", "Overwatch 2")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Go Live CTA
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FiberManualRecord,
                                contentDescription = null,
                                tint = LiveBadgeRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE ARENA",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Watch pros & stream your matches",
                            color = TextMuted,
                            fontSize = 12.5.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.openCreateModal(CreateMode.LIVE) },
                        colors = ButtonDefaults.buttonColors(containerColor = LiveBadgeRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("go_live_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GO LIVE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                    }
                }
            }

            // Category Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedGameCategory == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) CyberCyan else DarkSurfaceElevated)
                                .border(0.8.dp, if (isSelected) CyberCyan else DarkSurfaceBorder, RoundedCornerShape(18.dp))
                                .clickable { selectedGameCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) DarkEsportsBg else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Live Streams List
            val filtered = if (selectedGameCategory == "All") {
                liveStreams
            } else {
                liveStreams.filter { it.category.equals(selectedGameCategory, ignoreCase = true) }
            }

            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No active live streams in this category. Be the first to go live!", color = TextMuted)
                    }
                }
            } else {
                items(filtered, key = { it.id }) { stream ->
                    LiveStreamCard(
                        stream = stream,
                        onClick = { viewModel.openLiveStream(stream.id) },
                        onCreatorClick = { viewModel.viewUserProfile(stream.hostId) }
                    )
                }
            }

            // Voice Channels Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DISCORD-STYLE VOICE CHANNELS",
                        color = TextPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            items(voiceRooms, key = { it.id }) { room ->
                VoiceRoomCard(
                    room = room,
                    isConnected = connectedVoiceRoomId == room.id,
                    onJoinOrLeave = { viewModel.joinVoiceRoom(room.id) }
                )
            }
        }
    }
}
