package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val nickname: String,
    val email: String,
    val bio: String,
    val status: String,
    val avatarIndex: Int,
    val followersCount: Int,
    val followingCount: Int,
    val friendsCount: Int,
    val isVerified: Boolean = false,
    val isFollowedByMe: Boolean = false,
    val isFriend: Boolean = false,
    val hasPendingFriendRequest: Boolean = false,
    val twitchUrl: String = "",
    val discordTag: String = "",
    val youtubeUrl: String = "",
    val twitterHandle: String = "",
    val tiktokHandle: String = "",
    val customWebsite: String = "",
    val isCurrentUser: Boolean = false
)

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarIndex: Int,
    val isVerified: Boolean = false,
    val content: String,
    val mediaType: String = "NONE", // NONE, IMAGE, CLIP
    val mediaResId: Int? = null,
    val gameTag: String = "All",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarIndex: Int,
    val isVerified: Boolean = false,
    val title: String,
    val description: String,
    val category: String,
    val tags: String,
    val viewsCount: Long = 0,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val durationSeconds: Int = 180,
    val isLiked: Boolean = false,
    val allowComments: Boolean = true,
    val visibility: String = "PUBLIC",
    val mediaResId: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarIndex: Int,
    val isVerified: Boolean = false,
    val caption: String,
    val audioTrackTitle: String,
    val gameTag: String,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val mediaResId: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "live_streams")
data class LiveStreamEntity(
    @PrimaryKey val id: String,
    val hostId: String,
    val hostName: String,
    val hostHandle: String,
    val hostAvatarIndex: Int,
    val isVerified: Boolean = false,
    val title: String,
    val description: String,
    val category: String,
    val viewerCount: Int = 120,
    val isLive: Boolean = true,
    val mediaResId: Int? = null,
    val likesCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val targetType: String, // POST, VIDEO, REEL, STREAM
    val targetId: String,
    val authorId: String,
    val authorName: String,
    val authorHandle: String,
    val authorAvatarIndex: Int,
    val content: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val parentCommentId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderId: String,
    val recipientId: String,
    val content: String,
    val isFromMe: Boolean,
    val isRead: Boolean = true,
    val mediaType: String = "TEXT", // TEXT, IMAGE, AUDIO
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserHandle: String,
    val otherUserAvatarIndex: Int,
    val lastMessage: String,
    val lastMessageTimestamp: Long,
    val unreadCount: Int = 0,
    val isOnline: Boolean = true,
    val isTyping: Boolean = false
)

@Entity(tableName = "friend_requests")
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val senderId: String,
    val senderName: String,
    val senderHandle: String,
    val senderAvatarIndex: Int,
    val receiverId: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val type: String, // FRIEND_REQUEST, REQUEST_ACCEPTED, FOLLOW, LIKE, COMMENT, LIVE, SYSTEM
    val title: String,
    val body: String,
    val sourceName: String,
    val sourceAvatarIndex: Int,
    val targetId: String? = null,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "voice_rooms")
data class VoiceRoomEntity(
    @PrimaryKey val id: String,
    val name: String,
    val gameTitle: String,
    val maxUsers: Int = 8,
    val currentUsers: Int = 3,
    val isLocked: Boolean = false,
    val pingMs: Int = 18,
    val hostName: String
)
