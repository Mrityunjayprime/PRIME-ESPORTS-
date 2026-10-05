package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.SeedData
import com.example.data.model.PostEntity
import com.example.data.model.UserEntity
import com.example.data.repository.EsportsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: EsportsRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = EsportsRepository(db.esportsDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Prime Esports", appName)
    }

    @Test
    fun `database seed data and repository flow test`() = runBlocking {
        SeedData.populate(db.esportsDao())

        val currentUser = repository.currentUser.first()
        assertNotNull(currentUser)
        assertEquals("apex_prime", currentUser?.username)

        val posts = repository.allPosts.first()
        assertTrue(posts.isNotEmpty())

        val videos = repository.allVideos.first()
        assertTrue(videos.isNotEmpty())

        val reels = repository.allReels.first()
        assertTrue(reels.isNotEmpty())

        val streams = repository.liveStreams.first()
        assertTrue(streams.isNotEmpty())
    }

    @Test
    fun `post creation and like toggle test`() = runBlocking {
        val testUser = UserEntity(
            id = "test_user",
            username = "pro_player",
            nickname = "Pro Player",
            email = "pro@primeesports.gg",
            bio = "Competitive player",
            status = "Online",
            avatarIndex = 0,
            followersCount = 10,
            followingCount = 5,
            friendsCount = 2,
            isCurrentUser = true
        )
        db.esportsDao().insertUser(testUser)

        repository.createPost("Clutched 1v3 in Grand Finals!", "Valorant", null)
        val posts = repository.allPosts.first()
        assertEquals(1, posts.size)
        assertEquals("Clutched 1v3 in Grand Finals!", posts[0].content)
        assertEquals(0, posts[0].likesCount)

        repository.togglePostLike(posts[0])
        val updatedPosts = repository.allPosts.first()
        assertEquals(1, updatedPosts[0].likesCount)
        assertTrue(updatedPosts[0].isLiked)
    }
}
