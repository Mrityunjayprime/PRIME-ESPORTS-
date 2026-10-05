package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface EsportsDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE isCurrentUser = 0")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR nickname LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    // --- Posts ---
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPostsFlow(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY timestamp DESC")
    fun getPostsByAuthorFlow(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE content LIKE '%' || :query || '%' OR gameTag LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchPosts(query: String): Flow<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE posts SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :postId")
    suspend fun updatePostLike(postId: String, isLiked: Boolean, likesCount: Int)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    // --- Videos ---
    @Query("SELECT * FROM videos ORDER BY timestamp DESC")
    fun getAllVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE authorId = :authorId ORDER BY timestamp DESC")
    fun getVideosByAuthorFlow(authorId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    fun getVideoByIdFlow(videoId: String): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    suspend fun getVideoById(videoId: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :videoId")
    suspend fun updateVideoLike(videoId: String, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :videoId")
    suspend fun incrementVideoViews(videoId: String)

    // --- Reels ---
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    fun getAllReelsFlow(): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE authorId = :authorId ORDER BY timestamp DESC")
    fun getReelsByAuthorFlow(authorId: String): Flow<List<ReelEntity>>

    @Query("SELECT * FROM reels WHERE caption LIKE '%' || :query || '%' OR gameTag LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchReels(query: String): Flow<List<ReelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReel(reel: ReelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReels(reels: List<ReelEntity>)

    @Query("UPDATE reels SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :reelId")
    suspend fun updateReelLike(reelId: String, isLiked: Boolean, likesCount: Int)

    // --- Live Streams ---
    @Query("SELECT * FROM live_streams WHERE isLive = 1 ORDER BY viewerCount DESC")
    fun getLiveStreamsFlow(): Flow<List<LiveStreamEntity>>

    @Query("SELECT * FROM live_streams WHERE id = :streamId LIMIT 1")
    fun getLiveStreamByIdFlow(streamId: String): Flow<LiveStreamEntity?>

    @Query("SELECT * FROM live_streams WHERE id = :streamId LIMIT 1")
    suspend fun getLiveStreamById(streamId: String): LiveStreamEntity?

    @Query("SELECT * FROM live_streams WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY viewerCount DESC")
    fun searchLiveStreams(query: String): Flow<List<LiveStreamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStream(stream: LiveStreamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLiveStreams(streams: List<LiveStreamEntity>)

    @Query("UPDATE live_streams SET likesCount = likesCount + 1 WHERE id = :streamId")
    suspend fun incrementStreamLikes(streamId: String)

    @Query("UPDATE live_streams SET isLive = 0 WHERE id = :streamId")
    suspend fun endLiveStream(streamId: String)

    // --- Comments ---
    @Query("SELECT * FROM comments WHERE targetType = :targetType AND targetId = :targetId ORDER BY timestamp ASC")
    fun getCommentsForTarget(targetType: String, targetId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)

    @Query("UPDATE comments SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :commentId")
    suspend fun updateCommentLike(commentId: String, isLiked: Boolean, likesCount: Int)

    // --- Conversations & Messages ---
    @Query("SELECT * FROM conversations ORDER BY lastMessageTimestamp DESC")
    fun getAllConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :conversationId LIMIT 1")
    fun getConversationByIdFlow(conversationId: String): Flow<ConversationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conv: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(convs: List<ConversationEntity>)

    @Query("UPDATE conversations SET lastMessage = :lastMessage, lastMessageTimestamp = :timestamp, unreadCount = 0 WHERE id = :conversationId")
    suspend fun updateConversationLastMessage(conversationId: String, lastMessage: String, timestamp: Long)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversationFlow(conversationId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    // --- Friend Requests & Friends ---
    @Query("SELECT * FROM friend_requests WHERE receiverId = :userId ORDER BY timestamp DESC")
    fun getFriendRequestsFlow(userId: String): Flow<List<FriendRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequest(request: FriendRequestEntity)

    @Query("UPDATE friend_requests SET status = :status WHERE id = :requestId")
    suspend fun updateFriendRequestStatus(requestId: String, status: String)

    @Query("DELETE FROM friend_requests WHERE id = :requestId")
    suspend fun deleteFriendRequest(requestId: String)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getNotificationsFlow(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadNotificationsCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    // --- Voice Rooms ---
    @Query("SELECT * FROM voice_rooms")
    fun getAllVoiceRoomsFlow(): Flow<List<VoiceRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoiceRooms(rooms: List<VoiceRoomEntity>)

    @Query("UPDATE voice_rooms SET currentUsers = currentUsers + :delta WHERE id = :roomId")
    suspend fun updateVoiceRoomUsers(roomId: String, delta: Int)
}
