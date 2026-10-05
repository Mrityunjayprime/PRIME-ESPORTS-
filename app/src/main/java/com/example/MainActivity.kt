package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.CreateMode
import com.example.ui.MainNavTab
import com.example.ui.PrimeEsportsViewModel
import com.example.ui.components.AtmosphericBackground
import com.example.ui.components.AuthModalDialog
import com.example.ui.components.MobileBottomBar
import com.example.ui.components.ReportContentModal
import com.example.ui.components.TopNavigationBar
import com.example.ui.screens.CreateContentModal
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReelsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.StreamsScreen
import com.example.ui.screens.VoiceCallingScreen
import com.example.ui.screens.WatchLiveScreen
import com.example.ui.screens.WatchVideoScreen
import com.example.ui.theme.DarkEsportsBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: PrimeEsportsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PrimeEsportsApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PrimeEsportsApp(viewModel: PrimeEsportsViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val unreadNotifs by viewModel.unreadNotifCount.collectAsState()

    // Overlay screen states
    val selectedVideoId by viewModel.selectedVideoId.collectAsState()
    val selectedStreamId by viewModel.selectedStreamId.collectAsState()
    val activeCallState by viewModel.activeCallState.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsState()
    val isEditProfileOpen by viewModel.isEditProfileOpen.collectAsState()
    val viewingProfileUserId by viewModel.viewingProfileUserId.collectAsState()
    val isCreateModalOpen by viewModel.isCreateModalOpen.collectAsState()
    val createMode by viewModel.createMode.collectAsState()
    val reportTarget by viewModel.reportTarget.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()

    var isAuthDialogOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkEsportsBg)
    ) {
        // Atmospheric Animated Background
        AtmosphericBackground()

        // Responsive Box for phones & tablets (up to 700dp max on tablet for phone-like ergonomics or full width)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .align(Alignment.Center)
        ) {
            Scaffold(
                containerColor = androidx.compose.ui.graphics.Color.Transparent,
                topBar = {
                    // Hide top bar when fullscreen overlays are active (e.g. video watch, live watch, in-call, reels)
                    if (selectedVideoId == null && selectedStreamId == null && !activeCallState.inCall &&
                        !isSearchOpen && !isNotificationsOpen && !isEditProfileOpen && viewingProfileUserId == null &&
                        currentTab != MainNavTab.REELS
                    ) {
                        TopNavigationBar(
                            currentUser = currentUser,
                            unreadNotificationsCount = unreadNotifs,
                            onSearchClick = { viewModel.openSearch() },
                            onNotificationsClick = { viewModel.openNotifications() },
                            onMessagesClick = { viewModel.selectTab(MainNavTab.MESSAGES) },
                            onProfileClick = { viewModel.selectTab(MainNavTab.PROFILE) }
                        )
                    }
                },
                bottomBar = {
                    // Show fixed mobile bottom nav when on main tabs
                    if (selectedVideoId == null && selectedStreamId == null && !activeCallState.inCall &&
                        !isSearchOpen && !isNotificationsOpen && !isEditProfileOpen && viewingProfileUserId == null
                    ) {
                        MobileBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { viewModel.selectTab(it) },
                            onCreateClick = { viewModel.openCreateModal(CreateMode.POST) }
                        )
                    }
                },
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Primary Tabs
                    when (currentTab) {
                        MainNavTab.HOME -> HomeScreen(viewModel = viewModel)
                        MainNavTab.REELS -> ReelsScreen(viewModel = viewModel)
                        MainNavTab.STREAMS -> StreamsScreen(viewModel = viewModel)
                        MainNavTab.MESSAGES -> MessagesScreen(viewModel = viewModel)
                        MainNavTab.PROFILE -> ProfileScreen(
                            targetUserId = currentUser?.id,
                            viewModel = viewModel
                        )
                    }
                }
            }

            // --- Fullscreen and Modal Overlays ---

            // 1. Video Watch Screen
            selectedVideoId?.let { videoId ->
                WatchVideoScreen(
                    videoId = videoId,
                    viewModel = viewModel,
                    onBack = { viewModel.closeVideo() }
                )
            }

            // 2. Live Stream Watch Screen
            selectedStreamId?.let { streamId ->
                WatchLiveScreen(
                    streamId = streamId,
                    viewModel = viewModel,
                    onBack = { viewModel.closeLiveStream() }
                )
            }

            // 3. Active Voice / Video Call Screen
            if (activeCallState.inCall) {
                VoiceCallingScreen(viewModel = viewModel)
            }

            // 4. Global Search Screen
            if (isSearchOpen) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeSearch() }
                )
            }

            // 5. Notifications Screen
            if (isNotificationsOpen) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeNotifications() }
                )
            }

            // 6. Edit Profile Screen
            if (isEditProfileOpen) {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.closeEditProfile() }
                )
            }

            // 7. Viewing Other User Profile
            viewingProfileUserId?.let { userId ->
                ProfileScreen(
                    targetUserId = userId,
                    viewModel = viewModel,
                    onBack = { viewModel.closeUserProfile() }
                )
            }

            // 8. Create Content / Upload Modal
            if (isCreateModalOpen) {
                CreateContentModal(
                    initialMode = createMode,
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeCreateModal() }
                )
            }

            // 9. Auth Dialog (Sign Up / Sign In)
            AuthModalDialog(
                isOpen = isAuthDialogOpen,
                onDismiss = { isAuthDialogOpen = false },
                viewModel = viewModel
            )

            // 10. Moderation & Report Content Dialog
            ReportContentModal(
                reportTarget = reportTarget,
                onDismiss = { viewModel.cancelReport() },
                onSubmit = { reason -> viewModel.submitReport(reason) }
            )
        }
    }
}
