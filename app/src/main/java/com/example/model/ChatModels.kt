package com.example.model

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}

enum class MessageType {
    TEXT,
    IMAGE,
    VOICE,
    FILE
}

data class MessageReaction(
    val emoji: String,
    val count: Int,
    val reactedByMe: Boolean = false
)

data class MessageReply(
    val id: String,
    val senderName: String,
    val content: String
)

data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val messageType: MessageType = MessageType.TEXT,
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp: Long = System.currentTimeMillis(),
    val isOutgoing: Boolean = false,
    val replyTo: MessageReply? = null,
    val isPinned: Boolean = false,
    val isEdited: Boolean = false,
    val reactions: List<MessageReaction> = emptyList(),
    val durationSeconds: Int? = null,
    val fileName: String? = null,
    val fileSize: String? = null
)

data class ChatConversation(
    val id: String,
    val ownerUsername: String = "",
    val participantUsername: String = "",
    val title: String,
    val avatar: String,
    val isGroup: Boolean = false,
    val unreadCount: Int = 0,
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isRequest: Boolean = false,
    val isOnline: Boolean = false,
    val memberCount: Int = 0,
    val isTyping: Boolean = false
)
