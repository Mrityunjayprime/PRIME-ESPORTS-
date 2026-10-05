package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        VideoEntity::class,
        ReelEntity::class,
        LiveStreamEntity::class,
        CommentEntity::class,
        MessageEntity::class,
        ConversationEntity::class,
        FriendRequestEntity::class,
        NotificationEntity::class,
        VoiceRoomEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun esportsDao(): EsportsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "prime_esports_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate demo seed data on creation
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    SeedData.populate(database.esportsDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
