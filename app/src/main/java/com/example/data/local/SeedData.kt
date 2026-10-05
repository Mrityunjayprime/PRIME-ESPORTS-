package com.example.data.local

import com.example.R
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

object SeedData {

    suspend fun populate(dao: EsportsDao) {
        val now = System.currentTimeMillis()

        // 1. Current User
        val currentUser = UserEntity(
            id = "user_me",
            username = "apex_prime",
            nickname = "Apex Prime",
            email = "pro@primeesports.gg",
            bio = "Competitive Apex Legends & Valorant IGL. Streaming nightly tournaments & scrims. 🏆 2x Major Finalist.",
            status = "Grinding Radiant in Valorant 🔴 Live",
            avatarIndex = 0,
            followersCount = 48200,
            followingCount = 312,
            friendsCount = 84,
            isVerified = true,
            isFollowedByMe = false,
            isFriend = false,
            twitchUrl = "twitch.tv/apexprime",
            discordTag = "ApexPrime#0001",
            youtubeUrl = "youtube.com/@apexprimeesports",
            twitterHandle = "@ApexPrimeGG",
            tiktokHandle = "@primeapex",
            customWebsite = "https://primeesports.gg/roster",
            isCurrentUser = true
        )

        // 2. Notable Creators & Esports Players
        val creators = listOf(
            UserEntity(
                id = "user_neon_viper",
                username = "neon_viper",
                nickname = "Neon Viper",
                email = "viper@sentinels.gg",
                bio = "Professional Duelist & Initiator. World Champion 2025. VCT Americas MVP.",
                status = "Aim training warmup | VCT Scrims",
                avatarIndex = 1,
                followersCount = 184500,
                followingCount = 142,
                friendsCount = 39,
                isVerified = true,
                isFollowedByMe = true,
                isFriend = true,
                twitchUrl = "twitch.tv/neonviper",
                discordTag = "NeonViper#7777",
                twitterHandle = "@NeonViperVAL",
                isCurrentUser = false
            ),
            UserEntity(
                id = "user_shadow_sniper",
                username = "shadow_sniper",
                nickname = "Shadow Sniper",
                email = "shadow@navi.gg",
                bio = "CS2 Main AWPer. 1.34 Rating in Premier. Precision flickshots & grenade lineups.",
                status = "Practicing Mirage smokes",
                avatarIndex = 2,
                followersCount = 92300,
                followingCount = 89,
                friendsCount = 22,
                isVerified = true,
                isFollowedByMe = true,
                isFriend = false,
                youtubeUrl = "youtube.com/@shadowsniper",
                twitterHandle = "@ShadowCS2",
                isCurrentUser = false
            ),
            UserEntity(
                id = "user_cyber_kitsune",
                username = "cyber_kitsune",
                nickname = "Cyber Kitsune",
                email = "kitsune@cloud9.gg",
                bio = "Variety streamer, League of Legends Mid-laner & mechanical demon. Prime Esports Partner.",
                status = "Duo with viewers tonight! ✨",
                avatarIndex = 3,
                followersCount = 230400,
                followingCount = 410,
                friendsCount = 115,
                isVerified = true,
                isFollowedByMe = false,
                isFriend = false,
                hasPendingFriendRequest = true,
                twitchUrl = "twitch.tv/cyberkitsune",
                tiktokHandle = "@kitsunegaming",
                isCurrentUser = false
            ),
            UserEntity(
                id = "user_valkyrie",
                username = "valkyrie_tactics",
                nickname = "Valkyrie",
                email = "valk@fnatic.com",
                bio = "Esports analyst, coach & tournament caster. Breaking down pro plays frame by frame.",
                status = "Analyzing Grand Finals VODs",
                avatarIndex = 4,
                followersCount = 67800,
                followingCount = 230,
                friendsCount = 48,
                isVerified = true,
                isFollowedByMe = true,
                isFriend = true,
                youtubeUrl = "youtube.com/@valkyrietactics",
                twitterHandle = "@ValkTactics",
                isCurrentUser = false
            )
        )

        dao.insertUser(currentUser)
        dao.insertUsers(creators)

        // 3. Posts
        val posts = listOf(
            PostEntity(
                id = "post_1",
                authorId = "user_neon_viper",
                authorName = "Neon Viper",
                authorHandle = "@neon_viper",
                authorAvatarIndex = 1,
                isVerified = true,
                content = "JUST QUALIFIED FOR THE PRIME ESPORTS WORLD CHAMPIONSHIP GRAND FINALS! 🏆🔥 GGs to all teams, the 1v4 clutch on Ascent map 3 was pure adrenaline! Thank you for the insane energy in the chat today!",
                mediaType = "IMAGE",
                mediaResId = R.drawable.esports_hero_banner_1791090922894,
                gameTag = "Valorant",
                likesCount = 4820,
                commentsCount = 342,
                sharesCount = 612,
                isLiked = true,
                timestamp = now - 1000 * 60 * 45
            ),
            PostEntity(
                id = "post_2",
                authorId = "user_shadow_sniper",
                authorName = "Shadow Sniper",
                authorHandle = "@shadow_sniper",
                authorAvatarIndex = 2,
                isVerified = true,
                content = "New 128-tick flick training routine dropped. Hitting 98% accuracy on AWP reaction benchmarks today. Never let your crosshair placement get lazy. Who wants a full config breakdown?",
                mediaType = "IMAGE",
                mediaResId = R.drawable.stream_thumb_gameplay_1791090935862,
                gameTag = "CS2",
                likesCount = 1940,
                commentsCount = 128,
                sharesCount = 89,
                isLiked = false,
                timestamp = now - 1000 * 60 * 180
            ),
            PostEntity(
                id = "post_3",
                authorId = "user_cyber_kitsune",
                authorName = "Cyber Kitsune",
                authorHandle = "@cyber_kitsune",
                authorAvatarIndex = 3,
                isVerified = true,
                content = "Late night ranked grind starts in 20 minutes! We are 42 LP away from Challenger rank. Joining the Prime voice room if anyone wants to listen in to callouts! 🦊⚡",
                mediaType = "NONE",
                mediaResId = null,
                gameTag = "League of Legends",
                likesCount = 3120,
                commentsCount = 215,
                sharesCount = 140,
                isLiked = false,
                timestamp = now - 1000 * 60 * 360
            )
        )
        dao.insertPosts(posts)

        // 4. Videos
        val videos = listOf(
            VideoEntity(
                id = "vid_1",
                authorId = "user_neon_viper",
                authorName = "Neon Viper",
                authorHandle = "@neon_viper",
                authorAvatarIndex = 1,
                isVerified = true,
                title = "1v4 Against World Champions - Ascent Grand Finals Tactical Breakdown",
                description = "Complete in-depth round breakdown of the tournament-winning 1v4 clutch on Ascent. Analyzing utility timing, sound baiting, and isolation of duels under pressure.",
                category = "Valorant",
                tags = "vct, esports, clutch, breakdown, guide",
                viewsCount = 124500,
                likesCount = 8920,
                commentsCount = 412,
                durationSeconds = 642,
                isLiked = true,
                mediaResId = R.drawable.esports_hero_banner_1791090922894,
                timestamp = now - 1000 * 60 * 60 * 5
            ),
            VideoEntity(
                id = "vid_2",
                authorId = "user_shadow_sniper",
                authorName = "Shadow Sniper",
                authorHandle = "@shadow_sniper",
                authorAvatarIndex = 2,
                isVerified = true,
                title = "CS2 Pro Settings, Crosshair & Peeking Guide 2026",
                description = "Every setting you need for maximum FPS, zero input latency, and pixel-perfect AWP flicks in Counter-Strike 2. Sensitivity conversion sheet in bio.",
                category = "CS2",
                tags = "cs2, awp, settings, crosshair, aim",
                viewsCount = 88700,
                likesCount = 6310,
                commentsCount = 189,
                durationSeconds = 480,
                isLiked = false,
                mediaResId = R.drawable.stream_thumb_gameplay_1791090935862,
                timestamp = now - 1000 * 60 * 60 * 12
            ),
            VideoEntity(
                id = "vid_3",
                authorId = "user_valkyrie",
                authorName = "Valkyrie",
                authorHandle = "@valkyrie_tactics",
                authorAvatarIndex = 4,
                isVerified = true,
                title = "Why Korean Teams Are Dominating The Current Meta",
                description = "Comprehensive macroeconomic analysis of team rotations, objective priority, and resource allocation in competitive League of Legends.",
                category = "Analysis",
                tags = "lck, esports, meta, macro, tactics",
                viewsCount = 54200,
                likesCount = 3780,
                commentsCount = 164,
                durationSeconds = 890,
                isLiked = false,
                mediaResId = R.drawable.esports_hero_banner_1791090922894,
                timestamp = now - 1000 * 60 * 60 * 24
            )
        )
        dao.insertVideos(videos)

        // 5. Reels (TikTok-style short video clips)
        val reels = listOf(
            ReelEntity(
                id = "reel_1",
                authorId = "user_neon_viper",
                authorName = "Neon Viper",
                authorHandle = "@neon_viper",
                authorAvatarIndex = 1,
                isVerified = true,
                caption = "Instant 4-kill Marshall headshot collateral! 🎯⚡ They called hacks in all-chat lmao",
                audioTrackTitle = "Cyberpunk Phonk - Prime Bass Mix #4",
                gameTag = "Valorant",
                likesCount = 34500,
                commentsCount = 890,
                sharesCount = 4210,
                isLiked = true,
                isSaved = true,
                mediaResId = R.drawable.stream_thumb_gameplay_1791090935862,
                timestamp = now - 1000 * 60 * 30
            ),
            ReelEntity(
                id = "reel_2",
                authorId = "user_cyber_kitsune",
                authorName = "Cyber Kitsune",
                authorHandle = "@cyber_kitsune",
                authorAvatarIndex = 3,
                isVerified = true,
                caption = "When your duo flashes you right before the teamfight starts 💀😭 Why do we do this?",
                audioTrackTitle = "Kitsune Lo-Fi Chill Beats Vol. 2",
                gameTag = "League of Legends",
                likesCount = 61200,
                commentsCount = 1430,
                sharesCount = 8920,
                isLiked = true,
                isSaved = false,
                mediaResId = R.drawable.esports_hero_banner_1791090922894,
                timestamp = now - 1000 * 60 * 90
            ),
            ReelEntity(
                id = "reel_3",
                authorId = "user_shadow_sniper",
                authorName = "Shadow Sniper",
                authorHandle = "@shadow_sniper",
                authorAvatarIndex = 2,
                isVerified = true,
                caption = "Blind no-scope through smoke on de_inferno Banana push! 💣 0.1s left on bomb timer!",
                audioTrackTitle = "Adrenaline Hardstyle Drop - DJ Shadow",
                gameTag = "CS2",
                likesCount = 28900,
                commentsCount = 460,
                sharesCount = 1890,
                isLiked = false,
                isSaved = false,
                mediaResId = R.drawable.stream_thumb_gameplay_1791090935862,
                timestamp = now - 1000 * 60 * 240
            )
        )
        dao.insertReels(reels)

        // 6. Live Streams
        val liveStreams = listOf(
            LiveStreamEntity(
                id = "stream_1",
                hostId = "user_neon_viper",
                hostName = "Neon Viper",
                hostHandle = "@neon_viper",
                hostAvatarIndex = 1,
                isVerified = true,
                title = "VCT MASTERS SCRIMS & RANKED 1 GRIND !sens !gear",
                description = "Warmup deathmatches then high ELO 5-stack tournament practice with the squad. Drops enabled!",
                category = "Valorant",
                viewerCount = 14820,
                isLive = true,
                mediaResId = R.drawable.esports_hero_banner_1791090922894,
                likesCount = 5940,
                timestamp = now - 1000 * 60 * 55
            ),
            LiveStreamEntity(
                id = "stream_2",
                hostId = "user_cyber_kitsune",
                hostName = "Cyber Kitsune",
                hostHandle = "@cyber_kitsune",
                hostAvatarIndex = 3,
                isVerified = true,
                title = "PRO LEAGUE MID-LANE CHALLENGER ROAD | Chat Plays Next Game",
                description = "Challenger promo series game 5! Positive vibes, giveaways every hour, and voice call in Prime Esports lobby.",
                category = "League of Legends",
                viewerCount = 9430,
                isLive = true,
                mediaResId = R.drawable.stream_thumb_gameplay_1791090935862,
                likesCount = 3810,
                timestamp = now - 1000 * 60 * 120
            ),
            LiveStreamEntity(
                id = "stream_3",
                hostId = "user_shadow_sniper",
                hostName = "Shadow Sniper",
                hostHandle = "@shadow_sniper",
                hostAvatarIndex = 2,
                isVerified = true,
                title = "CS2 MAJOR OPEN QUALIFIERS | NAVI WATCH PARTY",
                description = "Watching European qualifiers with live analysis and tactical commentary.",
                category = "CS2",
                viewerCount = 7610,
                isLive = true,
                mediaResId = R.drawable.esports_hero_banner_1791090922894,
                likesCount = 2190,
                timestamp = now - 1000 * 60 * 200
            )
        )
        dao.insertLiveStreams(liveStreams)

        // 7. Initial Comments
        val comments = listOf(
            CommentEntity(
                id = "comment_1",
                targetType = "POST",
                targetId = "post_1",
                authorId = "user_shadow_sniper",
                authorName = "Shadow Sniper",
                authorHandle = "@shadow_sniper",
                authorAvatarIndex = 2,
                content = "That spray transfer on site was legendary bro. Best clutch of the season hands down!",
                likesCount = 142,
                isLiked = true,
                timestamp = now - 1000 * 60 * 35
            ),
            CommentEntity(
                id = "comment_2",
                targetType = "POST",
                targetId = "post_1",
                authorId = "user_cyber_kitsune",
                authorName = "Cyber Kitsune",
                authorHandle = "@cyber_kitsune",
                authorAvatarIndex = 3,
                content = "CONGRATS VIPER!! 🏆✨ Bringing the trophy home!!",
                likesCount = 88,
                isLiked = false,
                timestamp = now - 1000 * 60 * 25
            ),
            CommentEntity(
                id = "comment_3",
                targetType = "STREAM",
                targetId = "stream_1",
                authorId = "user_valkyrie",
                authorName = "Valkyrie",
                authorHandle = "@valkyrie_tactics",
                authorAvatarIndex = 4,
                content = "Notice how Viper clears close left before checking heaven. Perfect crosshair placement.",
                likesCount = 45,
                timestamp = now - 1000 * 60 * 5
            ),
            CommentEntity(
                id = "comment_4",
                targetType = "STREAM",
                targetId = "stream_1",
                authorId = "user_shadow_sniper",
                authorName = "Shadow Sniper",
                authorHandle = "@shadow_sniper",
                authorAvatarIndex = 2,
                content = "POGGERS THAT FLICK WAS DISGUSTING 🔥🔥🔥",
                likesCount = 67,
                timestamp = now - 1000 * 60 * 2
            )
        )
        dao.insertComments(comments)

        // 8. Conversations & Messages
        val convs = listOf(
            ConversationEntity(
                id = "conv_viper",
                otherUserId = "user_neon_viper",
                otherUserName = "Neon Viper",
                otherUserHandle = "@neon_viper",
                otherUserAvatarIndex = 1,
                lastMessage = "Bro are you ready for tonight's tournament finals? Hop on voice!",
                lastMessageTimestamp = now - 1000 * 60 * 12,
                unreadCount = 2,
                isOnline = true,
                isTyping = false
            ),
            ConversationEntity(
                id = "conv_valk",
                otherUserId = "user_valkyrie",
                otherUserName = "Valkyrie",
                otherUserHandle = "@valkyrie_tactics",
                otherUserAvatarIndex = 4,
                lastMessage = "Sent you the VOD timestamps for the Ascent defensive setups.",
                lastMessageTimestamp = now - 1000 * 60 * 120,
                unreadCount = 0,
                isOnline = false,
                isTyping = false
            )
        )
        dao.insertConversations(convs)

        val messages = listOf(
            MessageEntity(
                id = "msg_1",
                conversationId = "conv_viper",
                senderId = "user_neon_viper",
                recipientId = "user_me",
                content = "Yo Apex! Did you see the tournament brackets for tomorrow?",
                isFromMe = false,
                isRead = true,
                timestamp = now - 1000 * 60 * 45
            ),
            MessageEntity(
                id = "msg_2",
                conversationId = "conv_viper",
                senderId = "user_me",
                recipientId = "user_neon_viper",
                content = "Yeah! We are facing Cloud9 in round 2. Our site holds need to be rock solid.",
                isFromMe = true,
                isRead = true,
                timestamp = now - 1000 * 60 * 30
            ),
            MessageEntity(
                id = "msg_3",
                conversationId = "conv_viper",
                senderId = "user_neon_viper",
                recipientId = "user_me",
                content = "Bro are you ready for tonight's tournament finals? Hop on voice!",
                isFromMe = false,
                isRead = false,
                timestamp = now - 1000 * 60 * 12
            )
        )
        dao.insertMessages(messages)

        // 9. Friend Requests
        val friendRequests = listOf(
            FriendRequestEntity(
                id = "freq_1",
                senderId = "user_cyber_kitsune",
                senderName = "Cyber Kitsune",
                senderHandle = "@cyber_kitsune",
                senderAvatarIndex = 3,
                receiverId = "user_me",
                status = "PENDING",
                timestamp = now - 1000 * 60 * 60 * 2
            )
        )
        dao.insertFriendRequest(friendRequests[0])

        // 10. Notifications
        val notifications = listOf(
            NotificationEntity(
                id = "notif_1",
                type = "LIVE",
                title = "Neon Viper is LIVE!",
                body = "VCT MASTERS SCRIMS & RANKED 1 GRIND !sens !gear",
                sourceName = "Neon Viper",
                sourceAvatarIndex = 1,
                targetId = "stream_1",
                isRead = false,
                timestamp = now - 1000 * 60 * 50
            ),
            NotificationEntity(
                id = "notif_2",
                type = "FRIEND_REQUEST",
                title = "Friend Request Received",
                body = "Cyber Kitsune sent you a friend request",
                sourceName = "Cyber Kitsune",
                sourceAvatarIndex = 3,
                targetId = "user_cyber_kitsune",
                isRead = false,
                timestamp = now - 1000 * 60 * 120
            ),
            NotificationEntity(
                id = "notif_3",
                type = "LIKE",
                title = "New Likes",
                body = "Neon Viper and 48 others liked your tournament clip",
                sourceName = "Neon Viper",
                sourceAvatarIndex = 1,
                targetId = "post_1",
                isRead = true,
                timestamp = now - 1000 * 60 * 360
            )
        )
        dao.insertNotifications(notifications)

        // 11. Voice Rooms
        val voiceRooms = listOf(
            VoiceRoomEntity(
                id = "voice_1",
                name = "Apex Ranked Squad #1 [Predator]",
                gameTitle = "Apex Legends",
                maxUsers = 3,
                currentUsers = 2,
                isLocked = false,
                pingMs = 12,
                hostName = "Apex Prime"
            ),
            VoiceRoomEntity(
                id = "voice_2",
                name = "Valorant VCT Scrims & Strategy",
                gameTitle = "Valorant",
                maxUsers = 5,
                currentUsers = 4,
                isLocked = false,
                pingMs = 15,
                hostName = "Neon Viper"
            ),
            VoiceRoomEntity(
                id = "voice_3",
                name = "CS2 Premier 5v5 Stack",
                gameTitle = "Counter-Strike 2",
                maxUsers = 5,
                currentUsers = 3,
                isLocked = false,
                pingMs = 19,
                hostName = "Shadow Sniper"
            ),
            VoiceRoomEntity(
                id = "voice_4",
                name = "Prime Esports Chill Community Lounge",
                gameTitle = "All Games",
                maxUsers = 20,
                currentUsers = 9,
                isLocked = false,
                pingMs = 8,
                hostName = "Cyber Kitsune"
            )
        )
        dao.insertVoiceRooms(voiceRooms)
    }
}
