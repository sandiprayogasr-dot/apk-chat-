package com.example.model

enum class SettingsPage(val titleId: String, val titleEn: String) {
    HOME("Pengaturan", "Settings"),
    ACCOUNT("Akun", "Account"),
    SECURITY("Keamanan", "Security"),
    PRIVACY("Privasi", "Privacy"),
    NOTIFICATIONS("Notifikasi", "Notifications"),
    APPEARANCE("Tampilan", "Appearance"),
    STORAGE("Penyimpanan & Data", "Storage & Data"),
    STORAGE_MANAGE_MEDIA("Kelola Media", "Manage Media"),
    CHAT("Pengaturan Chat", "Chat Settings"),
    CALLS("Pengaturan Panggilan", "Call Settings"),
    AI("Asisten AI", "AI Assistant"),
    LANGUAGE("Bahasa", "Language"),
    ABOUT("Tentang & Info", "About & Info"),
    HELP("Bantuan", "Help")
}

enum class ThemeMode {
    DARK,
    LIGHT,
    SYSTEM
}

enum class AccentTheme(val hex: Long, val displayName: String) {
    BLUE(0xFF2878FF, "Electric Blue (#2878FF)"),
    EMERALD(0xFF00E676, "Teal Green"),
    PURPLE(0xFF9D4EDD, "Electric Purple"),
    ORANGE(0xFFFF9100, "Vibrant Orange")
}

enum class TextScale(val label: String, val scaleFactor: Float) {
    SMALL("Kecil", 0.85f),
    MEDIUM("Sedang", 1.0f),
    LARGE("Besar", 1.15f)
}

data class AppSettings(
    // Appearance
    val themeMode: ThemeMode = ThemeMode.DARK,
    val accentTheme: AccentTheme = AccentTheme.BLUE,
    val textScale: TextScale = TextScale.MEDIUM,
    val chatWallpaper: String = "dark",

    // Notifications
    val messageNotifications: Boolean = true,
    val groupNotifications: Boolean = true,
    val callNotifications: Boolean = true,
    val mentionNotifications: Boolean = true,
    val notificationSound: Boolean = true,
    val vibration: Boolean = true,
    val previewMessage: Boolean = true,

    // Storage
    val cloudStorageUsedMb: Float = 14.2f,
    val autoDownloadPhoto: Boolean = true,
    val autoDownloadVideo: Boolean = false,
    val autoDownloadDocs: Boolean = false,
    val cacheSizeMb: Float = 28.6f,

    // Chat
    val enterToSend: Boolean = true,
    val linkPreview: Boolean = true,
    val mediaVisibilityInGallery: Boolean = true,
    val archiveMutedChats: Boolean = false,
    val aiAssistantEnabled: Boolean = true,
    val smartReplyEnabled: Boolean = true,

    // Calls
    val voiceCallEnabled: Boolean = true,
    val videoCallEnabled: Boolean = true,
    val useLessDataForCalls: Boolean = false,
    val speakerphoneDefault: Boolean = true,

    // AI
    val autoSummarizeEnabled: Boolean = true,
    val translateEnabled: Boolean = true,
    val improveWritingEnabled: Boolean = true,

    // Privacy & Security
    val lastSeenPrivacy: String = "Semua Orang",
    val profilePhotoPrivacy: String = "Kontak Saya",
    val readReceipts: Boolean = true,
    val disappearingMessagesTimer: String = "Nonaktif",
    val appLockEnabled: Boolean = true,
    val biometricEnabled: Boolean = false,
    val isFingerprintEnabled: Boolean = false,
    val isPinLockEnabled: Boolean = true,
    val userPin: String = "123456",
    val twoFactorEnabled: Boolean = false,
    val twoStepVerification: Boolean = false,
    val activeSessionsCount: Int = 3,

    // Language
    val languageCode: String = "id"
)
