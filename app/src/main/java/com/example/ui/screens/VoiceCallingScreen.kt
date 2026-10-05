package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.EsportsAvatar
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
import java.util.Locale

@Composable
fun VoiceCallingScreen(
    viewModel: PrimeEsportsViewModel,
    modifier: Modifier = Modifier
) {
    val callState by viewModel.activeCallState.collectAsState()
    var isSpeakerOn by remember { mutableStateOf(true) }

    BackHandler {
        viewModel.endCall()
    }

    val minutes = callState.durationSeconds / 60
    val seconds = callState.durationSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    // Animated voice wave effect
    val infiniteTransition = rememberInfiniteTransition(label = "VoiceWave")
    val waveAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveAnim"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Background Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height * 0.4f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ElectricViolet.copy(alpha = 0.25f),
                        CyberCyan.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.width * 0.7f
                ),
                center = center,
                radius = size.width * 0.7f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Status Bar
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                    .border(0.8.dp, DarkSurfaceBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FiberManualRecord,
                    contentDescription = null,
                    tint = EmeraldNeon,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (callState.isVideo) "ENCRYPTED VIDEO CALL • 18ms" else "ENCRYPTED VOICE CALL • 12ms",
                    color = EmeraldNeon,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }

            // Center Call Avatar / Video Mockup
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (callState.isVideo && callState.isCameraOn) {
                    // Simulated Video Feed Card
                    Box(
                        modifier = Modifier
                            .size(240.dp, 320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.5.dp, CyberCyan, RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            EsportsAvatar(
                                avatarIndex = callState.callerAvatarIndex,
                                name = callState.callerName,
                                size = 80.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Camera Active (720p 60FPS)",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else {
                    // Pulsing Avatar with Wave Rings
                    Box(contentAlignment = Alignment.Center) {
                        // Outer Pulsing Ring
                        Box(
                            modifier = Modifier
                                .size(170.dp * waveAnim)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.15f * (1f - waveAnim * 0.5f)))
                        )
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(ElectricViolet.copy(alpha = 0.25f))
                        )
                        EsportsAvatar(
                            avatarIndex = callState.callerAvatarIndex,
                            name = callState.callerName,
                            size = 110.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = callState.callerName,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = callState.callerHandle,
                    color = CyberCyan,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = timeFormatted,
                    color = TextMuted,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom Controls Bar (Mute, Camera, Speaker, End Call)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(DarkSurfaceElevated.copy(alpha = 0.95f))
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(28.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Mute Toggle
                IconButton(
                    onClick = { viewModel.toggleCallMute() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (callState.isMuted) FlameCrimson.copy(alpha = 0.25f) else DarkSurface)
                        .border(1.dp, if (callState.isMuted) FlameCrimson else DarkSurfaceBorder, CircleShape)
                        .testTag("call_mute_toggle")
                ) {
                    Icon(
                        imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = if (callState.isMuted) FlameCrimson else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Video Camera Toggle
                IconButton(
                    onClick = { viewModel.toggleCallCamera() },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (!callState.isCameraOn) FlameCrimson.copy(alpha = 0.25f) else DarkSurface)
                        .border(1.dp, if (!callState.isCameraOn) FlameCrimson else DarkSurfaceBorder, CircleShape)
                        .testTag("call_camera_toggle")
                ) {
                    Icon(
                        imageVector = if (callState.isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        contentDescription = "Camera",
                        tint = if (!callState.isCameraOn) FlameCrimson else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Speaker Output
                IconButton(
                    onClick = { isSpeakerOn = !isSpeakerOn },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isSpeakerOn) CyberCyan.copy(alpha = 0.25f) else DarkSurface)
                        .border(1.dp, if (isSpeakerOn) CyberCyan else DarkSurfaceBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Speaker",
                        tint = if (isSpeakerOn) CyberCyan else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // End Call Button
                IconButton(
                    onClick = { viewModel.endCall() },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(FlameCrimson)
                        .testTag("call_end_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
