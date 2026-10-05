package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.FlameCrimson
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EditProfileScreen(
    viewModel: PrimeEsportsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val currentUser by viewModel.currentUser.collectAsState()

    var nickname by remember(currentUser) { mutableStateOf(currentUser?.nickname ?: "") }
    var username by remember(currentUser) { mutableStateOf(currentUser?.username ?: "") }
    var bio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }
    var status by remember(currentUser) { mutableStateOf(currentUser?.status ?: "") }
    var avatarIndex by remember(currentUser) { mutableIntStateOf(currentUser?.avatarIndex ?: 0) }

    var twitchUrl by remember(currentUser) { mutableStateOf(currentUser?.twitchUrl ?: "") }
    var discordTag by remember(currentUser) { mutableStateOf(currentUser?.discordTag ?: "") }
    var youtubeUrl by remember(currentUser) { mutableStateOf(currentUser?.youtubeUrl ?: "") }
    var twitterHandle by remember(currentUser) { mutableStateOf(currentUser?.twitterHandle ?: "") }
    var tiktokHandle by remember(currentUser) { mutableStateOf(currentUser?.tiktokHandle ?: "") }
    var customWebsite by remember(currentUser) { mutableStateOf(currentUser?.customWebsite ?: "") }

    var usernameError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
            .statusBarsPadding()
            .imePadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "EDIT ESPORTS PROFILE",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    val cleanUsername = username.trim().lowercase().replace(" ", "_")
                    if (cleanUsername.length < 3) {
                        usernameError = "Username must be at least 3 characters"
                        return@Button
                    }
                    usernameError = null
                    viewModel.saveProfile(
                        nickname = nickname.trim(),
                        username = cleanUsername,
                        bio = bio.trim(),
                        status = status.trim(),
                        twitch = twitchUrl.trim(),
                        discord = discordTag.trim(),
                        youtube = youtubeUrl.trim(),
                        twitter = twitterHandle.trim(),
                        tiktok = tiktokHandle.trim(),
                        website = customWebsite.trim(),
                        avatarIndex = avatarIndex
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("Save", color = DarkEsportsBg, fontWeight = FontWeight.Bold)
            }
        }

        Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar Selector
            Text(
                text = "CHOOSE AVATAR CREST",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(5) { idx ->
                    val isSelected = avatarIndex == idx
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) CyberCyan else DarkSurfaceBorder,
                                shape = CircleShape
                            )
                            .clickable { avatarIndex = idx }
                            .padding(4.dp)
                    ) {
                        EsportsAvatar(
                            avatarIndex = idx,
                            name = nickname.ifEmpty { "Gamer" },
                            size = 56.dp
                        )
                    }
                }
            }

            Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

            // Basic Info
            EditTextField(
                label = "Display Nickname",
                value = nickname,
                onValueChange = { nickname = it },
                testTag = "input_nickname"
            )

            Column {
                EditTextField(
                    label = "Username Handle",
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = null
                    },
                    prefix = "@",
                    testTag = "input_username"
                )
                if (usernameError != null) {
                    Text(
                        text = usernameError!!,
                        color = FlameCrimson,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )
                }
            }

            EditTextField(
                label = "Custom Status (e.g. Grinding Valorant Radiant)",
                value = status,
                onValueChange = { status = it },
                testTag = "input_status"
            )

            EditTextField(
                label = "Bio",
                value = bio,
                onValueChange = { bio = it },
                maxLines = 4,
                testTag = "input_bio"
            )

            Divider(color = DarkSurfaceBorder, thickness = 0.8.dp)

            // External Social Links
            Text(
                text = "CREATOR & STREAMING LINKS",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            EditTextField(label = "Twitch Channel", value = twitchUrl, onValueChange = { twitchUrl = it })
            EditTextField(label = "Discord Tag", value = discordTag, onValueChange = { discordTag = it })
            EditTextField(label = "YouTube Channel", value = youtubeUrl, onValueChange = { youtubeUrl = it })
            EditTextField(label = "X / Twitter Handle", value = twitterHandle, onValueChange = { twitterHandle = it })
            EditTextField(label = "TikTok Handle", value = tiktokHandle, onValueChange = { tiktokHandle = it })
            EditTextField(label = "Custom Portfolio / Website", value = customWebsite, onValueChange = { customWebsite = it })

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EditTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String? = null,
    maxLines: Int = 1,
    testTag: String = ""
) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            prefix = if (prefix != null) { { Text(prefix, color = CyberCyan) } } else null,
            modifier = Modifier
                .fillMaxWidth()
                .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = DarkSurfaceElevated,
                unfocusedContainerColor = DarkSurfaceElevated,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedIndicatorColor = CyberCyan,
                unfocusedIndicatorColor = DarkSurfaceBorder
            ),
            maxLines = maxLines
        )
    }
}
