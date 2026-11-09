package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun SettingsMobileScreen(
    settingsStack: List<SettingsPage>,
    currentPage: SettingsPage,
    appSettings: AppSettings,
    onPushPage: (SettingsPage) -> Unit,
    onPopPage: () -> Unit,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    onClearCache: () -> Unit,
    userProfile: UserProfile = UserProfile(),
    onEditProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val canGoBack = settingsStack.size > 1

    val headerTitle = when (currentPage) {
        SettingsPage.HOME -> "Pengaturan"
        SettingsPage.ACCOUNT -> "Akun"
        SettingsPage.PRIVACY -> "Privasi"
        SettingsPage.NOTIFICATIONS -> "Notifikasi"
        SettingsPage.APPEARANCE -> "Tampilan"
        SettingsPage.STORAGE -> "Penyimpanan"
        SettingsPage.STORAGE_MANAGE_MEDIA -> "Kelola Media"
        SettingsPage.CHAT -> "Pengaturan Chat"
        SettingsPage.CALLS -> "Pengaturan Panggilan"
        SettingsPage.AI -> "Asisten AI"
        SettingsPage.SECURITY -> "Keamanan & Privasi"
        SettingsPage.LANGUAGE -> "Bahasa"
        SettingsPage.ABOUT -> "Tentang & Info"
        SettingsPage.HELP -> "Bantuan & Dukungan"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("settings_mobile_screen")
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaGlassSurface)
                .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canGoBack) {
                    IconButton(
                        onClick = onPopPage,
                        modifier = Modifier.size(36.dp).testTag("settings_mobile_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Text(
                    text = headerTitle,
                    color = VerbaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // SCROLLABLE CONTENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (currentPage) {
                SettingsPage.HOME -> {
                    SettingsHomeScreen(
                        onNavigate = onPushPage,
                        appSettings = appSettings,
                        userProfile = userProfile,
                        onEditProfile = onEditProfileClick
                    )
                }
                SettingsPage.NOTIFICATIONS -> {
                    SettingsNotificationsScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.APPEARANCE -> {
                    SettingsAppearanceScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.STORAGE -> {
                    SettingsStorageScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings,
                        onNavigateManageMedia = { onPushPage(SettingsPage.STORAGE_MANAGE_MEDIA) },
                        onClearCache = onClearCache
                    )
                }
                SettingsPage.STORAGE_MANAGE_MEDIA -> {
                    SettingsManageMediaScreen(
                        settings = appSettings,
                        onClearCache = onClearCache
                    )
                }
                SettingsPage.CHAT -> {
                    SettingsChatScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.CALLS -> {
                    SettingsCallsScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.AI -> {
                    SettingsAiScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.SECURITY -> {
                    SettingsSecurityScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.LANGUAGE -> {
                    SettingsLanguageScreen(
                        settings = appSettings,
                        onUpdate = onUpdateSettings
                    )
                }
                SettingsPage.HELP,
                SettingsPage.ACCOUNT,
                SettingsPage.PRIVACY,
                SettingsPage.ABOUT -> {
                    SettingsHelpScreen()
                }
            }
        }

        // FOOTER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaGlassSurface)
                .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Element Messenger v2.0 • Android Mobile",
                color = VerbaTextMuted,
                fontSize = 11.sp
            )
            Text(
                text = "Enkripsi Aktif",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SettingsModal(
    isOpen: Boolean,
    settingsStack: List<SettingsPage>,
    currentPage: SettingsPage,
    appSettings: AppSettings,
    onPushPage: (SettingsPage) -> Unit,
    onPopPage: () -> Unit,
    onCloseSettings: () -> Unit,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    onClearCache: () -> Unit,
    isMobileCompact: Boolean
) {
    if (!isOpen) return

    val canGoBack = settingsStack.size > 1

    val headerTitle = when (currentPage) {
        SettingsPage.HOME -> "Pengaturan"
        SettingsPage.ACCOUNT -> "Akun"
        SettingsPage.PRIVACY -> "Privasi"
        SettingsPage.NOTIFICATIONS -> "Notifikasi"
        SettingsPage.APPEARANCE -> "Tampilan"
        SettingsPage.STORAGE -> "Penyimpanan"
        SettingsPage.STORAGE_MANAGE_MEDIA -> "Kelola Media"
        SettingsPage.CHAT -> "Pengaturan Chat"
        SettingsPage.CALLS -> "Pengaturan Panggilan"
        SettingsPage.AI -> "Asisten AI"
        SettingsPage.SECURITY -> "Keamanan & Privasi"
        SettingsPage.LANGUAGE -> "Bahasa"
        SettingsPage.ABOUT -> "Tentang & Info"
        SettingsPage.HELP -> "Bantuan & Dukungan"
    }

    Dialog(
        onDismissRequest = onCloseSettings,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = !isMobileCompact
        )
    ) {
        // Dark Overlay background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(enabled = !isMobileCompact, onClick = onCloseSettings)
                .testTag("settings_modal_overlay"),
            contentAlignment = Alignment.Center
        ) {
            // Centered Settings Panel (Desktop/Tablet) or Full-screen (Mobile)
            val panelModifier = if (isMobileCompact) {
                Modifier
                    .fillMaxSize()
                    .background(VerbaPanel)
            } else {
                Modifier
                    .width(480.dp)
                    .heightIn(min = 520.dp, max = 640.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VerbaPanel)
                    .border(1.dp, VerbaBorder, RoundedCornerShape(16.dp))
            }

            Column(
                modifier = panelModifier
                    .clickable(enabled = false, onClick = {}) // prevent click-through to overlay
                    .testTag("settings_panel")
            ) {
                // HEADER: ← / TITLE / X
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(VerbaSidebar)
                        .border(1.dp, VerbaBorder, RoundedCornerShape(0.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (canGoBack) {
                            IconButton(
                                onClick = onPopPage,
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("settings_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali ke Pengaturan Sebelumnya",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Text(
                            text = headerTitle,
                            color = VerbaTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = if (canGoBack) 4.dp else 0.dp)
                        )
                    }

                    // Tombol X menutup seluruh Settings
                    IconButton(
                        onClick = onCloseSettings,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("settings_close_x_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup Pengaturan",
                            tint = VerbaTextSecondary
                        )
                    }
                }

                // CONTENT: Scrollable Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (currentPage) {
                        SettingsPage.HOME -> {
                            SettingsHomeScreen(
                                onNavigate = onPushPage,
                                appSettings = appSettings
                            )
                        }
                        SettingsPage.NOTIFICATIONS -> {
                            SettingsNotificationsScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.APPEARANCE -> {
                            SettingsAppearanceScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.STORAGE -> {
                            SettingsStorageScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings,
                                onNavigateManageMedia = { onPushPage(SettingsPage.STORAGE_MANAGE_MEDIA) },
                                onClearCache = onClearCache
                            )
                        }
                        SettingsPage.STORAGE_MANAGE_MEDIA -> {
                            SettingsManageMediaScreen(
                                settings = appSettings,
                                onClearCache = onClearCache
                            )
                        }
                        SettingsPage.CHAT -> {
                            SettingsChatScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.CALLS -> {
                            SettingsCallsScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.AI -> {
                            SettingsAiScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.SECURITY -> {
                            SettingsSecurityScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.LANGUAGE -> {
                            SettingsLanguageScreen(
                                settings = appSettings,
                                onUpdate = onUpdateSettings
                            )
                        }
                        SettingsPage.HELP,
                        SettingsPage.ACCOUNT,
                        SettingsPage.PRIVACY,
                        SettingsPage.ABOUT -> {
                            SettingsHelpScreen()
                        }
                    }
                }

                // FOOTER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(VerbaSidebar)
                        .border(1.dp, VerbaBorder, RoundedCornerShape(0.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Element Messenger v2.0 • Android Mobile",
                        color = VerbaTextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Stack: ${settingsStack.size} level",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-SCREENS FOR NESTED NAVIGATION
// -------------------------------------------------------------

@Composable
private fun SettingsHomeScreen(
    onNavigate: (SettingsPage) -> Unit,
    appSettings: AppSettings,
    userProfile: UserProfile = UserProfile(),
    onEditProfile: () -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Kartu Profil Pengguna di Bagian Atas
        Surface(
            onClick = onEditProfile,
            shape = RoundedCornerShape(16.dp),
            color = VerbaGlassSurface,
            border = BorderStroke(1.dp, VerbaGlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_profile_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(ElementEmerald, Color(0xFF007A5A))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userProfile.avatar.ifEmpty { "EM" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.name.ifEmpty { "Pengguna VerbaChat" },
                        color = VerbaTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (userProfile.phone.isNotEmpty()) userProfile.phone else userProfile.bio.ifEmpty { "Ada untuk mengobrol" },
                        color = VerbaTextSecondary,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userProfile.username,
                        color = ElementEmerald,
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier.size(36.dp).testTag("settings_edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profil",
                        tint = ElementEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "PENGATURAN UMUM",
            color = VerbaTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 6.dp, bottom = 2.dp)
        )
        SettingsNavRow(
            icon = Icons.Default.Notifications,
            title = "Notifikasi",
            subtitle = if (appSettings.messageNotifications) "Aktif" else "Senyap",
            onClick = { onNavigate(SettingsPage.NOTIFICATIONS) }
        )
        SettingsNavRow(
            icon = Icons.Default.Palette,
            title = "Tampilan",
            subtitle = "${appSettings.accentTheme.displayName} • ${appSettings.themeMode.name}",
            onClick = { onNavigate(SettingsPage.APPEARANCE) }
        )
        SettingsNavRow(
            icon = Icons.Default.Storage,
            title = "Penyimpanan",
            subtitle = "14.2 MB / Cloud Storage",
            onClick = { onNavigate(SettingsPage.STORAGE) }
        )
        SettingsNavRow(
            icon = Icons.Default.Chat,
            title = "Chat",
            subtitle = if (appSettings.enterToSend) "Enter untuk mengirim" else "Enter baris baru",
            onClick = { onNavigate(SettingsPage.CHAT) }
        )
        SettingsNavRow(
            icon = Icons.Default.Call,
            title = "Panggilan",
            subtitle = "HD Voice & Video Call Aktif",
            onClick = { onNavigate(SettingsPage.CALLS) }
        )
        SettingsNavRow(
            icon = Icons.Default.AutoAwesome,
            title = "AI",
            subtitle = if (appSettings.aiAssistantEnabled) "VerbaAI Assistant Aktif" else "Nonaktif",
            onClick = { onNavigate(SettingsPage.AI) }
        )
        SettingsNavRow(
            icon = Icons.Default.Security,
            title = "Keamanan",
            subtitle = "${appSettings.activeSessionsCount} Sesi Aktif",
            onClick = { onNavigate(SettingsPage.SECURITY) }
        )
        SettingsNavRow(
            icon = Icons.Default.Language,
            title = "Bahasa",
            subtitle = if (appSettings.languageCode == "id") "Bahasa Indonesia" else "English",
            onClick = { onNavigate(SettingsPage.LANGUAGE) }
        )
        SettingsNavRow(
            icon = Icons.Default.HelpOutline,
            title = "Bantuan",
            subtitle = "FAQ & Informasi VerbaChat",
            onClick = { onNavigate(SettingsPage.HELP) }
        )
    }
}

@Composable
private fun SettingsNotificationsScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Pemberitahuan Pesan", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        SettingsToggleRow(
            title = "Notifikasi Pesan Pribadi",
            subtitle = "Terima pemberitahuan saat ada pesan baru masuk",
            checked = settings.messageNotifications,
            onCheckedChange = { check -> onUpdate { it.copy(messageNotifications = check) } }
        )
        SettingsToggleRow(
            title = "Notifikasi Grup",
            subtitle = "Terima pemberitahuan dari grup percakapan",
            checked = settings.groupNotifications,
            onCheckedChange = { check -> onUpdate { it.copy(groupNotifications = check) } }
        )
        SettingsToggleRow(
            title = "Notifikasi Panggilan",
            subtitle = "Tampilkan layar dering panggilan masuk",
            checked = settings.callNotifications,
            onCheckedChange = { check -> onUpdate { it.copy(callNotifications = check) } }
        )
        SettingsToggleRow(
            title = "Notifikasi Mention (@)",
            subtitle = "Beri tahu jika nama pengguna Anda di-mention",
            checked = settings.mentionNotifications,
            onCheckedChange = { check -> onUpdate { it.copy(mentionNotifications = check) } }
        )

        HorizontalDivider(color = VerbaBorder, modifier = Modifier.padding(vertical = 4.dp))
        Text("Suara & Getar", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        SettingsToggleRow(
            title = "Suara Notifikasi",
            subtitle = "Bunyikan nada saat ada pesan baru",
            checked = settings.notificationSound,
            onCheckedChange = { check -> onUpdate { it.copy(notificationSound = check) } }
        )
        SettingsToggleRow(
            title = "Getaran",
            subtitle = "Getarkan perangkat saat ada pesan",
            checked = settings.vibration,
            onCheckedChange = { check -> onUpdate { it.copy(vibration = check) } }
        )
        SettingsToggleRow(
            title = "Pratinjau Pesan",
            subtitle = "Tampilkan isi pesan dan nama pengirim di pop-up",
            checked = settings.previewMessage,
            onCheckedChange = { check -> onUpdate { it.copy(previewMessage = check) } }
        )
    }
}

@Composable
private fun SettingsAppearanceScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Mode Tema", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ThemeMode.values().forEach { mode ->
                val selected = settings.themeMode == mode
                val bg = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else VerbaCard
                val border = if (selected) MaterialTheme.colorScheme.primary else VerbaBorder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .border(1.dp, border, RoundedCornerShape(10.dp))
                        .clickable { onUpdate { it.copy(themeMode = mode) } }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (mode) {
                            ThemeMode.DARK -> "Mode Gelap"
                            ThemeMode.LIGHT -> "Mode Terang"
                            ThemeMode.SYSTEM -> "Sistem"
                        },
                        color = if (selected) MaterialTheme.colorScheme.primary else VerbaTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        HorizontalDivider(color = VerbaBorder)
        Text("Tema Aksen", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AccentTheme.values().forEach { accent ->
                val selected = settings.accentTheme == accent
                val accentColor = Color(accent.hex)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VerbaCard)
                        .border(1.dp, if (selected) accentColor else VerbaBorder, RoundedCornerShape(10.dp))
                        .clickable { onUpdate { it.copy(accentTheme = accent) } }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = accent.displayName.split(" ").last(),
                        color = VerbaTextPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        HorizontalDivider(color = VerbaBorder)
        Text("Ukuran Teks", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextScale.values().forEach { scale ->
                val selected = settings.textScale == scale
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else VerbaCard)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else VerbaBorder, RoundedCornerShape(10.dp))
                        .clickable { onUpdate { it.copy(textScale = scale) } }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = scale.label,
                        color = if (selected) MaterialTheme.colorScheme.primary else VerbaTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        HorizontalDivider(color = VerbaBorder)
        Text("Wallpaper Chat", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("default" to "Default", "dark" to "Pitch Dark", "emerald" to "Verba Emerald").forEach { (code, label) ->
                val selected = settings.chatWallpaper == code
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else VerbaCard)
                        .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else VerbaBorder, RoundedCornerShape(8.dp))
                        .clickable { onUpdate { it.copy(chatWallpaper = code) } }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (selected) MaterialTheme.colorScheme.primary else VerbaTextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsStorageScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit,
    onNavigateManageMedia: () -> Unit,
    onClearCache: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Storage Usage Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VerbaCard)
                .border(1.dp, VerbaBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(text = "Penggunaan Penyimpanan", color = VerbaTextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${settings.cloudStorageUsedMb} MB / Cloud Storage",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.28f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = VerbaSidebar
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Tersedia 5 GB penyimpanan cloud terenkripsi", color = VerbaTextMuted, fontSize = 11.sp)
            }
        }

        // Kelola media link row
        SettingsNavRow(
            icon = Icons.Default.PermMedia,
            title = "Kelola Media",
            subtitle = "Lihat dokumen, audio, dan foto tersimpan",
            onClick = onNavigateManageMedia
        )

        // Hapus Cache section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(VerbaCard)
                .border(1.dp, VerbaBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Hapus Cache", color = VerbaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "Cache lokal: ${settings.cacheSizeMb} MB", color = VerbaTextMuted, fontSize = 12.sp)
                }
                Button(
                    onClick = onClearCache,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Bersihkan Cache", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        HorizontalDivider(color = VerbaBorder)
        Text("Unduh Otomatis Media (Auto-download)", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        SettingsToggleRow(
            title = "Foto",
            subtitle = "Otomatis unduh foto saat terhubung ke Wi-Fi / Seluler",
            checked = settings.autoDownloadPhoto,
            onCheckedChange = { check -> onUpdate { it.copy(autoDownloadPhoto = check) } }
        )
        SettingsToggleRow(
            title = "Video",
            subtitle = "Otomatis unduh video saat terhubung ke Wi-Fi",
            checked = settings.autoDownloadVideo,
            onCheckedChange = { check -> onUpdate { it.copy(autoDownloadVideo = check) } }
        )
        SettingsToggleRow(
            title = "Dokumen",
            subtitle = "Otomatis unduh file berkas PDF, DOCX, ZIP",
            checked = settings.autoDownloadDocs,
            onCheckedChange = { check -> onUpdate { it.copy(autoDownloadDocs = check) } }
        )
    }
}

@Composable
private fun SettingsManageMediaScreen(
    settings: AppSettings,
    onClearCache: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Rincian Media Tersimpan", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        val mediaBreakdown = listOf(
            Triple("Foto & Gambar", "8.4 MB", Icons.Default.Image),
            Triple("Pesan Suara & Audio", "3.2 MB", Icons.Default.Audiotrack),
            Triple("Dokumen & Arsip", "2.6 MB", Icons.Default.InsertDriveFile)
        )

        mediaBreakdown.forEach { (label, size, icon) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VerbaCard)
                    .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = label, color = VerbaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(text = size, color = VerbaTextMuted, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onClearCache,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = VerbaDanger),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Hapus Semua Data Media Sementara", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingsChatScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingsToggleRow(
            title = "Enter untuk Mengirim",
            subtitle = "Menekan tombol Enter pada keyboard akan langsung mengirim pesan",
            checked = settings.enterToSend,
            onCheckedChange = { check -> onUpdate { it.copy(enterToSend = check) } }
        )
        SettingsToggleRow(
            title = "Pratinjau Tautan (Link Preview)",
            subtitle = "Tampilkan cuplikan judul dan gambar dari link yang dikirim",
            checked = settings.linkPreview,
            onCheckedChange = { check -> onUpdate { it.copy(linkPreview = check) } }
        )
        SettingsToggleRow(
            title = "Visibilitas Media di Galeri",
            subtitle = "Tampilkan media baru yang diunduh di galeri perangkat",
            checked = settings.mediaVisibilityInGallery,
            onCheckedChange = { check -> onUpdate { it.copy(mediaVisibilityInGallery = check) } }
        )
        SettingsToggleRow(
            title = "Arsipkan Chat saat Dibisukan",
            subtitle = "Otomatis sembunyikan obrolan yang tidak aktif",
            checked = settings.archiveMutedChats,
            onCheckedChange = { check -> onUpdate { it.copy(archiveMutedChats = check) } }
        )
    }
}

@Composable
private fun SettingsCallsScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingsToggleRow(
            title = "Panggilan Suara",
            subtitle = "Izinkan panggilan suara masuk dan keluar",
            checked = settings.voiceCallEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(voiceCallEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Panggilan Video HD",
            subtitle = "Gunakan kompresi VP9 untuk video call berkualitas tinggi",
            checked = settings.videoCallEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(videoCallEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Hemat Data Panggilan",
            subtitle = "Kurangi penggunaan kuota data seluler saat menelepon",
            checked = settings.useLessDataForCalls,
            onCheckedChange = { check -> onUpdate { it.copy(useLessDataForCalls = check) } }
        )
        SettingsToggleRow(
            title = "Speakerphone Standar",
            subtitle = "Gunakan speaker keras secara default untuk panggilan video",
            checked = settings.speakerphoneDefault,
            onCheckedChange = { check -> onUpdate { it.copy(speakerphoneDefault = check) } }
        )
    }
}

@Composable
private fun SettingsAiScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("VerbaAI On-Device Assistant", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Membantu Anda merespons cepat, merangkum percakapan, dan mengoreksi tulisan.", color = VerbaTextSecondary, fontSize = 11.sp)
                }
            }
        }

        SettingsToggleRow(
            title = "Aktifkan Asisten AI",
            subtitle = "Integrasikan fitur kecerdasan cerdas di seluruh percakapan",
            checked = settings.aiAssistantEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(aiAssistantEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Smart Reply",
            subtitle = "Saran balasan singkat otomatis di atas kolom input",
            checked = settings.smartReplyEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(smartReplyEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Ringkas Percakapan (Summarize)",
            subtitle = "Rangkum obrolan panjang menjadi poin penting dalam satu klik",
            checked = settings.autoSummarizeEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(autoSummarizeEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Terjemahkan Pesan Otomatis",
            subtitle = "Dukungan terjemahan pesan antar bahasa secara instan",
            checked = settings.translateEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(translateEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Perbaiki Tulisan (Improve Writing)",
            subtitle = "Koreksi tata bahasa dan tingkatkan gaya bahasa tulisan Anda",
            checked = settings.improveWritingEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(improveWritingEnabled = check) } }
        )
    }
}

@Composable
private fun SettingsSecurityScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SettingsToggleRow(
            title = "Kunci Aplikasi (App Lock)",
            subtitle = "Minta PIN saat membuka aplikasi VerbaChat",
            checked = settings.appLockEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(appLockEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Kunci Biometrik (Fingerprint/Face)",
            subtitle = "Buka aplikasi dengan sensor sidik jari perangkat",
            checked = settings.biometricEnabled,
            onCheckedChange = { check -> onUpdate { it.copy(biometricEnabled = check) } }
        )
        SettingsToggleRow(
            title = "Verifikasi Dua Langkah (2FA)",
            subtitle = "Tambahkan lapisan proteksi kata sandi tambahan",
            checked = settings.twoStepVerification,
            onCheckedChange = { check -> onUpdate { it.copy(twoStepVerification = check) } }
        )

        HorizontalDivider(color = VerbaBorder)
        Text("Sesi Perangkat Aktif", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(VerbaCard)
                .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column {
                Text("• Android 14 (Perangkat Ini) — Aktif sekarang", color = VerbaOnline, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• VerbaChat Desktop (MacBook Pro) — Terakhir aktif 2 jam lalu", color = VerbaTextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("• VerbaChat Web (Chrome) — Terakhir aktif kemarin", color = VerbaTextMuted, fontSize = 11.sp)
            }
        }

        Button(
            onClick = { onUpdate { it.copy(activeSessionsCount = 1) } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = VerbaCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, VerbaDanger),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Keluar dari Semua Perangkat Lain", color = VerbaDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SettingsLanguageScreen(
    settings: AppSettings,
    onUpdate: ((AppSettings) -> AppSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val languages = listOf(
            "id" to "Bahasa Indonesia (Indonesian)",
            "en" to "English (United States)"
        )
        languages.forEach { (code, name) ->
            val selected = settings.languageCode == code
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else VerbaCard)
                    .border(1.dp, if (selected) MaterialTheme.colorScheme.primary else VerbaBorder, RoundedCornerShape(10.dp))
                    .clickable { onUpdate { it.copy(languageCode = code) } }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, color = if (selected) MaterialTheme.colorScheme.primary else VerbaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                if (selected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun SettingsHelpScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Tentang VerbaChat", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(
            text = "VerbaChat adalah aplikasi perpesanan instan modern yang mengutamakan privasi, performa tinggi, dan pengalaman pengguna profesional.",
            color = VerbaTextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(VerbaCard)
                .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("• Versi: 1.2.0-stable", color = VerbaTextPrimary, fontSize = 12.sp)
                Text("• Enkripsi: End-to-End Cryptography", color = VerbaTextPrimary, fontSize = 12.sp)
                Text("• Status Server: Normal & Terhubung", color = VerbaOnline, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(VerbaCard)
            .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(VerbaPanel),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = VerbaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, color = VerbaTextMuted, fontSize = 11.sp)
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = "Buka",
            tint = VerbaTextMuted,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(VerbaCard)
            .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = VerbaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, color = VerbaTextMuted, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = VerbaTextSecondary,
                uncheckedTrackColor = VerbaSidebar
            )
        )
    }
}
