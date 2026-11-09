package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users ORDER BY displayName ASC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE username = :input OR email = :input LIMIT 1")
    suspend fun getUserByUsernameOrEmail(input: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserAccountEntity)

    @Query("UPDATE users SET isOnline = :isOnline, lastActiveTimestamp = :time WHERE username = :username")
    suspend fun setUserOnlineStatus(username: String, isOnline: Boolean, time: Long)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM conversations WHERE ownerUsername = :owner AND isArchived = 0 AND isRequest = 0 ORDER BY isPinned DESC, lastMessageTimestamp DESC")
    fun getActiveConversations(owner: String): Flow<List<ChatConversationEntity>>

    @Query("SELECT * FROM conversations WHERE ownerUsername = :owner AND isArchived = 1 ORDER BY lastMessageTimestamp DESC")
    fun getArchivedConversations(owner: String): Flow<List<ChatConversationEntity>>

    @Query("SELECT * FROM conversations WHERE ownerUsername = :owner AND isRequest = 1 ORDER BY lastMessageTimestamp DESC")
    fun getMessageRequests(owner: String): Flow<List<ChatConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :convId LIMIT 1")
    suspend fun getConversationById(convId: String): ChatConversationEntity?

    @Query("SELECT * FROM conversations WHERE ownerUsername = :owner AND participantUsername = :participant LIMIT 1")
    suspend fun getConversationByParticipant(owner: String, participant: String): ChatConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversations(conversations: List<ChatConversationEntity>)

    @Update
    suspend fun updateConversation(conversation: ChatConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :convId")
    suspend fun deleteConversation(convId: String)

    @Query("DELETE FROM conversations WHERE ownerUsername = :owner")
    suspend fun deleteAllConversationsForOwner(owner: String)

    // Messages
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Update
    suspend fun updateMessage(message: ChatMessageEntity)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

    @Query("UPDATE conversations SET isArchived = :archived WHERE id = :convId")
    suspend fun setArchived(convId: String, archived: Boolean)

    @Query("UPDATE conversations SET isRequest = 0 WHERE id = :convId")
    suspend fun acceptRequest(convId: String)
}

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts WHERE ownerUsername = :owner ORDER BY name ASC")
    fun getAllContacts(owner: String): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("UPDATE contacts SET isBlocked = :blocked WHERE id = :contactId")
    suspend fun setBlocked(contactId: String, blocked: Boolean)

    @Query("DELETE FROM contacts WHERE id = :contactId")
    suspend fun deleteContact(contactId: String)
}

@Dao
interface CallDao {
    @Query("SELECT * FROM calls WHERE ownerUsername = :owner ORDER BY timestampMillis DESC")
    fun getCallsForOwner(owner: String): Flow<List<CallRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: CallRecordEntity)

    @Query("DELETE FROM calls WHERE id = :callId")
    suspend fun deleteCall(callId: String)

    @Query("DELETE FROM calls WHERE ownerUsername = :owner")
    suspend fun deleteAllCallsForOwner(owner: String)
}

@Dao
interface CommunityDao {
    @Query("SELECT * FROM communities WHERE ownerUsername = :owner ORDER BY name ASC")
    fun getCommunitiesForOwner(owner: String): Flow<List<CommunityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunity(community: CommunityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommunities(communities: List<CommunityEntity>)

    @Query("UPDATE communities SET isJoined = :isJoined WHERE id = :id")
    suspend fun setJoined(id: String, isJoined: Boolean)

    @Query("UPDATE communities SET notificationMuted = :isMuted WHERE id = :id")
    suspend fun setMuted(id: String, isMuted: Boolean)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE ownerUsername = :owner ORDER BY createdAt DESC")
    fun getEventsForOwner(owner: String): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEvent(eventId: String)
}
