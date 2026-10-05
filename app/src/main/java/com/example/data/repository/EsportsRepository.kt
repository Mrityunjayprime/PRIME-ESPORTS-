package com.example.data.repository

import com.example.data.local.EsportsDao
import com.example.data.model.CommentEntity
import com.example.data.model.ConversationEntity
import com.example.data.model.FriendRequestEntity
import com.example.data.model.LiveStreamEntity
import com.example.data.model.MessageEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.PostEntity
import com.example.data.model.ReelEntity
import com.example.data.model.UserEntity
import com.example.data.model.VideoEntity
import com.example.data.model.VoiceRoomEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class EsportsRepository(private val dao: EsportsDao) {

    // --- User & Auth ---
    val currentUser: Flow<UserEntity?> = dao.getCurrentUserFlow()
    val allCreators: Flow<List<UserEntity>> = dao.getAllUsersFlow()

    fun getUserById(userId: String): Flow<UserEntity?> = dao.getUserByIdFlow(userId)

    suspend fun updateProfile(
        nickname: String,
        username: String,
        bio: String,
        status: String,
        twitchUrl: String,
        discordTag: String,
        youtubeUrl: String,
        twitterHandle: String,
        tiktokHandle: String,
        customWebsite: String,
        avatarIndex: Int
    ) {
        val current = dao.getCurrentUser() ?: return
        val updated = current.copy(
            nickname = nickname,
            username = username,
            bio = bio,
            status = status,
            twitchUrl = twitchUrl,
            discordTag = discordTag,
            youtubeUrl = youtubeUrl,
            twitterHandle = twitterHandle,
            tiktokHandle = tiktokHandle,
            customWebsite = customWebsite,
            avatarIndex = avatarIndex
        )
        dao.updateUser(updated)
    }

    suspend fun loginWithEmail(email: String, username: String) {
        // Authenticate or update current active session
        val current = dao.getCurrentUser()
        if (current != null) {
            dao.updateUser(current.copy(email = email, username = username.ifEmpty { current.username }))
        } else {
            val newUser = UserEntity(
                id = "user_me",
                username = username.ifEmpty { "player_" + (1000..9999).random() },
                nickname = username.replaceFirstChar { it.uppercase() }.ifEmpty { "Prime Gamer" },
                email = email,
                bio = "Competitive gamer on Prime Esports. 🎮 Ready to dominate!",
                status = "Online | Looking for squad",
                avatarIndex = 0,
                followersCount = 120,
                followingCount = 45,
                friendsCount = 18,
                isCurrentUser = true
            )
            dao.insertUser(newUser)
        }
    }

    suspend fun followUser(userId: String, shouldFollow: Boolean) {
        val user = dao.getUserById(userId) ?: return
        val updated = user.copy(
            isFollowedByMe = shouldFollow,
            followersCount = if (shouldFollow) user.followersCount + 1 else (user.followersCount - 1).coerceAtLeast(0)
        )
        dao.updateUser(updated)

        // If newly followed, create a notification
        if (shouldFollow) {
            val current = dao.getCurrentUser()
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    type = "FOLLOW",
                    title = "New Follower",
                    body = "${current?.nickname ?: "Someone"} started following you",
                    sourceName = current?.nickname ?: "Gamer",
                    sourceAvatarIndex = current?.avatarIndex ?: 0,
                    targetId = current?.id,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun toggleFriendRequest(targetUserId: String) {
        val user = dao.getUserById(targetUserId) ?: return
        val current = dao.getCurrentUser() ?: return

        if (user.isFriend) {
            // Unfriend
            dao.updateUser(user.copy(isFriend = false, friendsCount = (user.friendsCount - 1).coerceAtLeast(0)))
            dao.updateUser(current.copy(friendsCount = (current.friendsCount - 1).coerceAtLeast(0)))
        } else if (user.hasPendingFriendRequest) {
            // Cancel request
            dao.updateUser(user.copy(hasPendingFriendRequest = false))
        } else {
            // Send friend request
            dao.updateUser(user.copy(hasPendingFriendRequest = true))
            dao.insertFriendRequest(
                FriendRequestEntity(
                    id = UUID.randomUUID().toString(),
                    senderId = current.id,
                    senderName = current.nickname,
                    senderHandle = "@${current.username}",
                    senderAvatarIndex = current.avatarIndex,
                    receiverId = targetUserId,
                    status = "PENDING",
                    timestamp = System.currentTimeMillis()
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    type = "FRIEND_REQUEST",
                    title = "Friend Request",
                    body = "${current.nickname} sent you a friend request",
                    sourceName = current.nickname,
                    sourceAvatarIndex = current.avatarIndex,
                    targetId = current.id,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun respondToFriendRequest(requestId: String, senderId: String, accept: Boolean) {
        val current = dao.getCurrentUser() ?: return
        val sender = dao.getUserById(senderId)

        if (accept) {
            dao.updateFriendRequestStatus(requestId, "ACCEPTED")
            if (sender != null) {
                dao.updateUser(sender.copy(isFriend = true, friendsCount = sender.friendsCount + 1, hasPendingFriendRequest = false))
            }
            dao.updateUser(current.copy(friendsCount = current.friendsCount + 1))
            dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    type = "REQUEST_ACCEPTED",
                    title = "Friend Request Accepted",
                    body = "${current.nickname} accepted your friend request",
                    sourceName = current.nickname,
                    sourceAvatarIndex = current.avatarIndex,
                    targetId = current.id,
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            dao.updateFriendRequestStatus(requestId, "REJECTED")
            if (sender != null) {
                dao.updateUser(sender.copy(hasPendingFriendRequest = false))
            }
        }
    }

    // --- Posts ---
    val allPosts: Flow<List<PostEntity>> = dao.getAllPostsFlow()

    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>> = dao.getPostsByAuthorFlow(authorId)

    suspend fun createPost(content: String, gameTag: String, mediaResId: Int?) {
        val current = dao.getCurrentUser() ?: return
        val newPost = PostEntity(
            id = UUID.randomUUID().toString(),
            authorId = current.id,
            authorName = current.nickname,
            authorHandle = "@${current.username}",
            authorAvatarIndex = current.avatarIndex,
            isVerified = current.isVerified,
            content = content,
            mediaType = if (mediaResId != null) "IMAGE" else "NONE",
            mediaResId = mediaResId,
            gameTag = gameTag.ifEmpty { "General" },
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertPost(newPost)
    }

    suspend fun togglePostLike(post: PostEntity) {
        val newLiked = !post.isLiked
        val newCount = if (newLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        dao.updatePostLike(post.id, newLiked, newCount)
    }

    suspend fun deletePost(postId: String) {
        dao.deletePost(postId)
    }

    // --- Videos ---
    val allVideos: Flow<List<VideoEntity>> = dao.getAllVideosFlow()

    fun getVideoById(videoId: String): Flow<VideoEntity?> = dao.getVideoByIdFlow(videoId)

    suspend fun uploadVideo(
        title: String,
        description: String,
        category: String,
        tags: String,
        allowComments: Boolean,
        visibility: String,
        mediaResId: Int?
    ) {
        val current = dao.getCurrentUser() ?: return
        val newVideo = VideoEntity(
            id = UUID.randomUUID().toString(),
            authorId = current.id,
            authorName = current.nickname,
            authorHandle = "@${current.username}",
            authorAvatarIndex = current.avatarIndex,
            isVerified = current.isVerified,
            title = title,
            description = description,
            category = category.ifEmpty { "Gaming" },
            tags = tags,
            viewsCount = 1,
            likesCount = 0,
            commentsCount = 0,
            durationSeconds = (120..600).random(),
            isLiked = false,
            allowComments = allowComments,
            visibility = visibility,
            mediaResId = mediaResId,
            timestamp = System.currentTimeMillis()
        )
        dao.insertVideo(newVideo)
    }

    suspend fun toggleVideoLike(video: VideoEntity) {
        val newLiked = !video.isLiked
        val newCount = if (newLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        dao.updateVideoLike(video.id, newLiked, newCount)
    }

    suspend fun incrementVideoViews(videoId: String) {
        dao.incrementVideoViews(videoId)
    }

    // --- Reels ---
    val allReels: Flow<List<ReelEntity>> = dao.getAllReelsFlow()

    suspend fun uploadReel(caption: String, soundTitle: String, gameTag: String, mediaResId: Int?) {
        val current = dao.getCurrentUser() ?: return
        val newReel = ReelEntity(
            id = UUID.randomUUID().toString(),
            authorId = current.id,
            authorName = current.nickname,
            authorHandle = "@${current.username}",
            authorAvatarIndex = current.avatarIndex,
            isVerified = current.isVerified,
            caption = caption,
            audioTrackTitle = soundTitle.ifEmpty { "Original Sound - ${current.nickname}" },
            gameTag = gameTag.ifEmpty { "Gaming" },
            likesCount = 0,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            mediaResId = mediaResId,
            timestamp = System.currentTimeMillis()
        )
        dao.insertReel(newReel)
    }

    suspend fun toggleReelLike(reel: ReelEntity) {
        val newLiked = !reel.isLiked
        val newCount = if (newLiked) reel.likesCount + 1 else (reel.likesCount - 1).coerceAtLeast(0)
        dao.updateReelLike(reel.id, newLiked, newCount)
    }

    // --- Live Streams ---
    val liveStreams: Flow<List<LiveStreamEntity>> = dao.getLiveStreamsFlow()

    fun getLiveStreamById(streamId: String): Flow<LiveStreamEntity?> = dao.getLiveStreamByIdFlow(streamId)

    suspend fun startLiveStream(title: String, description: String, category: String, mediaResId: Int?): String {
        val current = dao.getCurrentUser() ?: return ""
        val streamId = UUID.randomUUID().toString()
        val newStream = LiveStreamEntity(
            id = streamId,
            hostId = current.id,
            hostName = current.nickname,
            hostHandle = "@${current.username}",
            hostAvatarIndex = current.avatarIndex,
            isVerified = current.isVerified,
            title = title,
            description = description,
            category = category.ifEmpty { "Gaming" },
            viewerCount = 1,
            isLive = true,
            mediaResId = mediaResId,
            likesCount = 0,
            timestamp = System.currentTimeMillis()
        )
        dao.insertLiveStream(newStream)

        // Notify followers
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                type = "LIVE",
                title = "${current.nickname} is LIVE!",
                body = title,
                sourceName = current.nickname,
                sourceAvatarIndex = current.avatarIndex,
                targetId = streamId,
                timestamp = System.currentTimeMillis()
            )
        )
        return streamId
    }

    suspend fun incrementStreamLikes(streamId: String) {
        dao.incrementStreamLikes(streamId)
    }

    suspend fun endLiveStream(streamId: String) {
        dao.endLiveStream(streamId)
    }

    // --- Comments ---
    fun getComments(targetType: String, targetId: String): Flow<List<CommentEntity>> =
        dao.getCommentsForTarget(targetType, targetId)

    suspend fun addComment(targetType: String, targetId: String, content: String, parentCommentId: String? = null) {
        val current = dao.getCurrentUser() ?: return
        val newComment = CommentEntity(
            id = UUID.randomUUID().toString(),
            targetType = targetType,
            targetId = targetId,
            authorId = current.id,
            authorName = current.nickname,
            authorHandle = "@${current.username}",
            authorAvatarIndex = current.avatarIndex,
            content = content,
            likesCount = 0,
            isLiked = false,
            parentCommentId = parentCommentId,
            timestamp = System.currentTimeMillis()
        )
        dao.insertComment(newComment)
    }

    suspend fun deleteComment(commentId: String) {
        dao.deleteComment(commentId)
    }

    suspend fun toggleCommentLike(comment: CommentEntity) {
        val newLiked = !comment.isLiked
        val newCount = if (newLiked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
        dao.updateCommentLike(comment.id, newLiked, newCount)
    }

    // --- Messages & Conversations ---
    val allConversations: Flow<List<ConversationEntity>> = dao.getAllConversationsFlow()

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        dao.getMessagesForConversationFlow(conversationId)

    suspend fun sendMessage(conversationId: String, otherUserId: String, text: String) {
        val current = dao.getCurrentUser() ?: return
        val otherUser = dao.getUserById(otherUserId)
        val now = System.currentTimeMillis()

        val message = MessageEntity(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = current.id,
            recipientId = otherUserId,
            content = text,
            isFromMe = true,
            isRead = true,
            timestamp = now
        )
        dao.insertMessage(message)

        val conv = ConversationEntity(
            id = conversationId,
            otherUserId = otherUserId,
            otherUserName = otherUser?.nickname ?: "Gamer",
            otherUserHandle = "@${otherUser?.username ?: "user"}",
            otherUserAvatarIndex = otherUser?.avatarIndex ?: 1,
            lastMessage = text,
            lastMessageTimestamp = now,
            unreadCount = 0,
            isOnline = otherUser?.status?.contains("Live") == true || true,
            isTyping = false
        )
        dao.insertConversation(conv)
    }

    // --- Notifications ---
    val notifications: Flow<List<NotificationEntity>> = dao.getNotificationsFlow()
    val unreadNotificationsCount: Flow<Int> = dao.getUnreadNotificationsCountFlow()

    suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }

    // --- Voice Rooms ---
    val voiceRooms: Flow<List<VoiceRoomEntity>> = dao.getAllVoiceRoomsFlow()

    suspend fun updateVoiceRoomUsers(roomId: String, delta: Int) {
        dao.updateVoiceRoomUsers(roomId, delta)
    }

    // --- Search ---
    fun searchUsers(query: String): Flow<List<UserEntity>> = dao.searchUsers(query)
    fun searchVideos(query: String): Flow<List<VideoEntity>> = dao.searchVideos(query)
    fun searchReels(query: String): Flow<List<ReelEntity>> = dao.searchReels(query)
    fun searchLiveStreams(query: String): Flow<List<LiveStreamEntity>> = dao.searchLiveStreams(query)
    fun searchPosts(query: String): Flow<List<PostEntity>> = dao.searchPosts(query)
}
