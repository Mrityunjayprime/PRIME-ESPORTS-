package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CommentEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.LiveStreamEntity
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PostEntity
import com.example.data.model.ReelEntity
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import com.example.data.model.VoiceRoomEntity
import com.example.data.repository.EsportsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavTab {
    HOME,
    REELS,
    STREAMS,
    MESSAGES,
    PROFILE
}

enum class CreateMode {
    POST,
    VIDEO,
    REEL,
    LIVE
}

enum class SearchCategory {
    ALL,
    PEOPLE,
    VIDEOS,
    REELS,
    LIVE,
    POSTS
}

data class ActiveCallState(
    val inCall: Boolean = false,
    val isVideo: Boolean = false,
    val callerName: String = "",
    val callerHandle: String = "",
    val callerAvatarIndex: Int = 1,
    val isMuted: Boolean = false,
    val isCameraOn: Boolean = true,
    val durationSeconds: Int = 0,
    val isIncomingRinging: Boolean = false
)

class PrimeEsportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EsportsRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = EsportsRepository(db.esportsDao())
    }

    // Repository Flows
    val currentUser: StateFlow<UserEntity?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allPosts: StateFlow<List<PostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReels: StateFlow<List<ReelEntity>> = repository.allReels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveStreams: StateFlow<List<LiveStreamEntity>> = repository.liveStreams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val conversations: StateFlow<List<ConversationEntity>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotifCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val voiceRooms: StateFlow<List<VoiceRoomEntity>> = repository.voiceRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCreators: StateFlow<List<UserEntity>> = repository.allCreators
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation State
    private val _currentTab = MutableStateFlow(MainNavTab.HOME)
    val currentTab: StateFlow<MainNavTab> = _currentTab.asStateFlow()

    private val _selectedHomeFilter = MutableStateFlow("All")
    val selectedHomeFilter: StateFlow<String> = _selectedHomeFilter.asStateFlow()

    // Overlay Screen States
    private val _selectedVideoId = MutableStateFlow<String?>(null)
    val selectedVideoId: StateFlow<String?> = _selectedVideoId.asStateFlow()

    private val _selectedStreamId = MutableStateFlow<String?>(null)
    val selectedStreamId: StateFlow<String?> = _selectedStreamId.asStateFlow()

    private val _activeConversationId = MutableStateFlow<String?>(null)
    val activeConversationId: StateFlow<String?> = _activeConversationId.asStateFlow()
    private val _activeChatUserId = MutableStateFlow<String?>(null)
    val activeChatUserId: StateFlow<String?> = _activeChatUserId.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchFilter = MutableStateFlow(SearchCategory.ALL)
    val searchFilter: StateFlow<SearchCategory> = _searchFilter.asStateFlow()

    private val _isNotificationsOpen = MutableStateFlow(false)
    val isNotificationsOpen: StateFlow<Boolean> = _isNotificationsOpen.asStateFlow()

    private val _isFriendsListOpen = MutableStateFlow(false)
    val isFriendsListOpen: StateFlow<Boolean> = _isFriendsListOpen.asStateFlow()

    private val _isEditProfileOpen = MutableStateFlow(false)
    val isEditProfileOpen: StateFlow<Boolean> = _isEditProfileOpen.asStateFlow()

    private val _isCreateModalOpen = MutableStateFlow(false)
    val isCreateModalOpen: StateFlow<Boolean> = _isCreateModalOpen.asStateFlow()

    private val _createMode = MutableStateFlow(CreateMode.POST)
    val createMode: StateFlow<CreateMode> = _createMode.asStateFlow()

    private val _viewingProfileUserId = MutableStateFlow<String?>(null)
    val viewingProfileUserId: StateFlow<String?> = _viewingProfileUserId.asStateFlow()

    // Active Call / Voice Chat State
    private val _activeCallState = MutableStateFlow(ActiveCallState())
    val activeCallState: StateFlow<ActiveCallState> = _activeCallState.asStateFlow()
    private var callTimerJob: Job? = null

    // Voice Room connected
    private val _connectedVoiceRoomId = MutableStateFlow<String?>(null)
    val connectedVoiceRoomId: StateFlow<String?> = _connectedVoiceRoomId.asStateFlow()

    private val _isVoiceMuted = MutableStateFlow(false)
    val isVoiceMuted: StateFlow<Boolean> = _isVoiceMuted.asStateFlow()

    // Moderation dialog
    private val _reportTarget = MutableStateFlow<Pair<String, String>?>(null) // Pair(type, id)
    val reportTarget: StateFlow<Pair<String, String>?> = _reportTarget.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // --- Navigation Actions ---
    fun selectTab(tab: MainNavTab) {
        _currentTab.value = tab
        // Close overlay screens when switching primary tabs
        _selectedVideoId.value = null
        _selectedStreamId.value = null
        _activeConversationId.value = null
        _viewingProfileUserId.value = null
        _isSearchOpen.value = false
        _isNotificationsOpen.value = false
        _isFriendsListOpen.value = false
        _isEditProfileOpen.value = false
    }

    fun setHomeFilter(filter: String) {
        _selectedHomeFilter.value = filter
    }

    fun openVideo(videoId: String) {
        _selectedVideoId.value = videoId
        viewModelScope.launch {
            repository.incrementVideoViews(videoId)
        }
    }

    fun closeVideo() {
        _selectedVideoId.value = null
    }

    fun openLiveStream(streamId: String) {
        _selectedStreamId.value = streamId
    }

    fun closeLiveStream() {
        _selectedStreamId.value = null
    }

    fun openConversation(conversationId: String, otherUserId: String) {
        _activeConversationId.value = conversationId
        _activeChatUserId.value = otherUserId
    }

    fun closeConversation() {
        _activeConversationId.value = null
        _activeChatUserId.value = null
    }

    fun openSearch(initialQuery: String = "") {
        _searchQuery.value = initialQuery
        _isSearchOpen.value = true
    }

    fun closeSearch() {
        _isSearchOpen.value = false
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSearchFilter(category: SearchCategory) {
        _searchFilter.value = category
    }

    fun openNotifications() {
        _isNotificationsOpen.value = true
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
        }
    }

    fun closeNotifications() {
        _isNotificationsOpen.value = false
    }

    fun openFriendsList() {
        _isFriendsListOpen.value = true
    }

    fun closeFriendsList() {
        _isFriendsListOpen.value = false
    }

    fun openEditProfile() {
        _isEditProfileOpen.value = true
    }

    fun closeEditProfile() {
        _isEditProfileOpen.value = false
    }

    fun openCreateModal(mode: CreateMode = CreateMode.POST) {
        _createMode.value = mode
        _isCreateModalOpen.value = true
    }

    fun closeCreateModal() {
        _isCreateModalOpen.value = false
    }

    fun viewUserProfile(userId: String) {
        _viewingProfileUserId.value = userId
    }

    fun closeUserProfile() {
        _viewingProfileUserId.value = null
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    // --- Content Interactions ---
    fun togglePostLike(post: PostEntity) {
        viewModelScope.launch {
            repository.togglePostLike(post)
        }
    }

    fun toggleVideoLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleVideoLike(video)
        }
    }

    fun toggleReelLike(reel: ReelEntity) {
        viewModelScope.launch {
            repository.toggleReelLike(reel)
        }
    }

    fun incrementStreamLikes(streamId: String) {
        viewModelScope.launch {
            repository.incrementStreamLikes(streamId)
        }
    }

    fun followUser(userId: String, shouldFollow: Boolean) {
        viewModelScope.launch {
            repository.followUser(userId, shouldFollow)
            _snackbarMessage.value = if (shouldFollow) "Followed creator!" else "Unfollowed"
        }
    }

    fun toggleFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            repository.toggleFriendRequest(targetUserId)
            _snackbarMessage.value = "Friend status updated"
        }
    }

    fun respondToFriendRequest(requestId: String, senderId: String, accept: Boolean) {
        viewModelScope.launch {
            repository.respondToFriendRequest(requestId, senderId, accept)
            _snackbarMessage.value = if (accept) "Friend request accepted!" else "Request rejected"
        }
    }

    fun createPost(content: String, gameTag: String, mediaResId: Int?) {
        viewModelScope.launch {
            repository.createPost(content, gameTag, mediaResId)
            _isCreateModalOpen.value = false
            _snackbarMessage.value = "Post published to Prime Esports feed!"
        }
    }

    fun uploadVideo(title: String, desc: String, category: String, tags: String, allowComments: Boolean, visibility: String, mediaResId: Int?) {
        viewModelScope.launch {
            repository.uploadVideo(title, desc, category, tags, allowComments, visibility, mediaResId)
            _isCreateModalOpen.value = false
            _snackbarMessage.value = "Video uploaded successfully!"
        }
    }

    fun uploadReel(caption: String, soundTitle: String, gameTag: String, mediaResId: Int?) {
        viewModelScope.launch {
            repository.uploadReel(caption, soundTitle, gameTag, mediaResId)
            _isCreateModalOpen.value = false
            _snackbarMessage.value = "Reel posted to short feed!"
        }
    }

    fun goLive(title: String, desc: String, category: String, mediaResId: Int?) {
        viewModelScope.launch {
            val streamId = repository.startLiveStream(title, desc, category, mediaResId)
            _isCreateModalOpen.value = false
            _selectedStreamId.value = streamId
            _snackbarMessage.value = "You are now LIVE on Prime Esports! 🔴"
        }
    }

    fun endLiveStream(streamId: String) {
        viewModelScope.launch {
            repository.endLiveStream(streamId)
            _selectedStreamId.value = null
            _snackbarMessage.value = "Live stream ended & saved to VODs."
        }
    }

    fun getCommentsFor(targetType: String, targetId: String): StateFlow<List<CommentEntity>> {
        return repository.getComments(targetType, targetId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addComment(targetType: String, targetId: String, content: String, parentId: String? = null) {
        viewModelScope.launch {
            repository.addComment(targetType, targetId, content, parentId)
        }
    }

    fun toggleCommentLike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentLike(comment)
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            _snackbarMessage.value = "Comment deleted"
        }
    }

    fun getConversationMessages(conversationId: String): StateFlow<List<MessageEntity>> {
        return repository.getMessages(conversationId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun sendMessage(conversationId: String, otherUserId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(conversationId, otherUserId, text)
        }
    }

    // --- Voice & Video Calls ---
    fun startCall(callerName: String, callerHandle: String, callerAvatarIndex: Int, isVideo: Boolean) {
        callTimerJob?.cancel()
        _activeCallState.value = ActiveCallState(
            inCall = true,
            isVideo = isVideo,
            callerName = callerName,
            callerHandle = callerHandle,
            callerAvatarIndex = callerAvatarIndex,
            isMuted = false,
            isCameraOn = isVideo,
            durationSeconds = 0,
            isIncomingRinging = false
        )
        // Start duration counter
        callTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _activeCallState.value = _activeCallState.value.copy(
                    durationSeconds = _activeCallState.value.durationSeconds + 1
                )
            }
        }
    }

    fun toggleCallMute() {
        _activeCallState.value = _activeCallState.value.copy(
            isMuted = !_activeCallState.value.isMuted
        )
    }

    fun toggleCallCamera() {
        _activeCallState.value = _activeCallState.value.copy(
            isCameraOn = !_activeCallState.value.isCameraOn
        )
    }

    fun endCall() {
        callTimerJob?.cancel()
        _activeCallState.value = ActiveCallState()
        _snackbarMessage.value = "Call ended"
    }

    // --- Voice Rooms ---
    fun joinVoiceRoom(roomId: String) {
        if (_connectedVoiceRoomId.value == roomId) {
            leaveVoiceRoom()
            return
        }
        _connectedVoiceRoomId.value = roomId
        viewModelScope.launch {
            repository.updateVoiceRoomUsers(roomId, 1)
            _snackbarMessage.value = "Connected to voice room"
        }
    }

    fun leaveVoiceRoom() {
        val current = _connectedVoiceRoomId.value ?: return
        _connectedVoiceRoomId.value = null
        viewModelScope.launch {
            repository.updateVoiceRoomUsers(current, -1)
            _snackbarMessage.value = "Disconnected from voice room"
        }
    }

    fun toggleVoiceMute() {
        _isVoiceMuted.value = !_isVoiceMuted.value
    }

    // --- Profile Editing ---
    fun saveProfile(
        nickname: String,
        username: String,
        bio: String,
        status: String,
        twitch: String,
        discord: String,
        youtube: String,
        twitter: String,
        tiktok: String,
        website: String,
        avatarIndex: Int
    ) {
        viewModelScope.launch {
            repository.updateProfile(
                nickname = nickname,
                username = username,
                bio = bio,
                status = status,
                twitchUrl = twitch,
                discordTag = discord,
                youtubeUrl = youtube,
                twitterHandle = twitter,
                tiktokHandle = tiktok,
                customWebsite = website,
                avatarIndex = avatarIndex
            )
            _isEditProfileOpen.value = false
            _snackbarMessage.value = "Profile updated successfully!"
        }
    }

    fun login(email: String, username: String) {
        viewModelScope.launch {
            repository.loginWithEmail(email, username)
            _snackbarMessage.value = "Signed in to Prime Esports as $username"
        }
    }

    // --- Moderation / Reporting ---
    fun reportContent(targetType: String, targetId: String) {
        _reportTarget.value = Pair(targetType, targetId)
    }

    fun submitReport(reason: String) {
        _reportTarget.value = null
        _snackbarMessage.value = "Report submitted. Thank you for keeping Prime Esports safe!"
    }

    fun cancelReport() {
        _reportTarget.value = null
    }
}
