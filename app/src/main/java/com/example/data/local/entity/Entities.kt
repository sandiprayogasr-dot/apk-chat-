package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserAccountEntity(
    @PrimaryKey val username: String,
    val displayName: String,
    val email: String = "",
    val password: String = "",
    val avatar: String,
    val bio: String,
    val phone: String = "",
    val status: String = "Online",
    val pin: String = "123456",
    val isFingerprintEnabled: Boolean = false,
    val isOnline: Boolean = true,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String, // username of sender
    val senderName: String,
    val content: String,
    val messageType: String,
    val status: String,
    val timestamp: Long,
    val isOutgoing: Boolean,
    val replyToId: String?,
    val replyToSender: String?,
    val replyToContent: String?,
    val isPinned: Boolean,
    val isEdited: Boolean,
    val reactionsRaw: String, // serialized emoji:count:reactedBy
    val durationSeconds: Int?,
    val fileName: String?,
    val fileSize: String?
)

@Entity(tableName = "conversations")
data class ChatConversationEntity(
    @PrimaryKey val id: String,
    val ownerUsername: String,
    val participantUsername: String,
    val title: String,
    val avatar: String,
    val isGroup: Boolean,
    val unreadCount: Int,
    val lastMessage: String,
    val lastMessageTime: String,
    val lastMessageTimestamp: Long,
    val isPinned: Boolean,
    val isArchived: Boolean,
    val isRequest: Boolean,
    val isOnline: Boolean,
    val memberCount: Int
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val ownerUsername: String,
    val name: String,
    val username: String,
    val avatar: String,
    val bio: String,
    val phone: String,
    val isOnline: Boolean,
    val isBlocked: Boolean
)

@Entity(tableName = "calls")
data class CallRecordEntity(
    @PrimaryKey val id: String,
    val ownerUsername: String,
    val contactName: String,
    val contactAvatar: String,
    val type: String, // VOICE, VIDEO
    val direction: String, // INCOMING, OUTGOING, MISSED
    val timestamp: String,
    val durationText: String,
    val timestampMillis: Long
)

@Entity(tableName = "communities")
data class CommunityEntity(
    @PrimaryKey val id: String,
    val ownerUsername: String,
    val name: String,
    val description: String,
    val avatar: String,
    val memberCount: Int,
    val isJoined: Boolean = true,
    val notificationMuted: Boolean = false
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val ownerUsername: String,
    val title: String,
    val dateTime: String,
    val description: String,
    val invitedMembers: String, // Comma separated names
    val createdAt: Long = System.currentTimeMillis()
)
