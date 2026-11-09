package com.example.model

data class Contact(
    val id: String,
    val name: String,
    val username: String,
    val avatar: String,
    val bio: String,
    val phone: String,
    val isOnline: Boolean = false,
    val isBlocked: Boolean = false
)

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}

data class CallRecord(
    val id: String,
    val contactName: String,
    val contactAvatar: String,
    val type: CallType,
    val direction: CallDirection,
    val timestamp: String,
    val durationText: String
)

data class Community(
    val id: String,
    val name: String,
    val description: String,
    val avatar: String,
    val memberCount: Int,
    val activeCount: Int,
    val isJoined: Boolean = true,
    val notificationMuted: Boolean = false
)

data class MarketItem(
    val id: String,
    val title: String,
    val price: String,
    val category: String,
    val sellerName: String,
    val sellerAvatar: String,
    val rating: Float,
    val description: String,
    val imageType: String
)

data class UserProfile(
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "Ada, menggunakan Element Messenger ✨",
    val phone: String = "",
    val avatar: String = "",
    val photoUri: String = "",
    val status: String = "Online",
    val pin: String = "123456",
    val isFingerprintEnabled: Boolean = false,
    val isLoggedIn: Boolean = false
)

data class CalendarEvent(
    val id: String,
    val title: String,
    val dateTime: String,
    val description: String,
    val invitedMembers: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class UserStatusStory(
    val id: String,
    val username: String,
    val authorName: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isSeen: Boolean = false,
    val colorHex: Long = 0xFF005C4B
)
