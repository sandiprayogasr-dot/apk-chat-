package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.CallDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.CommunityDao
import com.example.data.local.dao.ContactDao
import com.example.data.local.dao.EventDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.*

@Database(
    entities = [
        UserAccountEntity::class,
        ChatMessageEntity::class,
        ChatConversationEntity::class,
        ContactEntity::class,
        CallRecordEntity::class,
        CommunityEntity::class,
        EventEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class VerbaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun contactDao(): ContactDao
    abstract fun callDao(): CallDao
    abstract fun communityDao(): CommunityDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: VerbaDatabase? = null

        fun getInstance(context: Context): VerbaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VerbaDatabase::class.java,
                    "verbachat.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
