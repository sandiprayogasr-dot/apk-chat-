package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.*
import com.example.ui.*
import com.example.ui.components.*
import com.example.ui.theme.*

/**
 * Element Messenger — Android Mobile Only Main Screen
 * Premium Emerald Glassmorphism Mobile Experience
 */
@Composable
fun VerbaMainScreen(
    viewModel: VerbaViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    // If user is not logged in, show Mobile Login Screen
    if (!isLoggedIn) {
        LoginScreen(
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    val currentNavTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val sidebarFilter by viewModel.sidebarFilter.collectAsStateWithLifecycle()
    val selectedChatId by viewModel.selectedChatId.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val settingsStack by viewModel.settingsStack.collectAsStateWithLifecycle()
    val currentSettingsPage by viewModel.currentSettingsPage.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val activeCall by viewModel.activeCall.collectAsStateWithLifecycle()
    val replyingTo by viewModel.replyingTo.collectAsStateWithLifecycle()
    val editingMessage by viewModel.editingMessage.collectAsStateWithLifecycle()
    val forwardingMessage by viewModel.forwardingMessage.collectAsStateWithLifecycle()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingSeconds.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val showNewChatDialog by viewModel.showNewChatDialog.collectAsStateWithLifecycle()

    val activeConversations by viewModel.activeConversations.collectAsStateWithLifecycle()
    val archivedConversations by viewModel.archivedConversations.collectAsStateWithLifecycle()
    val messageRequests by viewModel.messageRequests.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val callHistory by viewModel.callHistory.collectAsStateWithLifecycle()
    val communities by viewModel.communities.collectAsStateWithLifecycle()
    val marketItems by viewModel.marketItems.collectAsStateWithLifecycle()
    val currentChatConversation by viewModel.currentChatConversation.collectAsStateWithLifecycle()
    val activeMessages by viewModel.activeChatMessages.collectAsStateWithLifecycle()

    val statusStories by viewModel.statusStories.collectAsStateWithLifecycle()
    val myStatus by viewModel.myStatus.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showTopMenu by remember { mutableStateOf(false) }

    // Status snackbar trigger
    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearNotice()
        }
    }

    // Android Mobile BackHandler Navigation
    val canPopNestedSettings = (currentNavTab == MainNavTab.SETTINGS && settingsStack.size > 1) || isSettingsOpen
    BackHandler(
        enabled = isRecordingVoice || editingMessage != null || replyingTo != null ||
                selectedChatId != null || canPopNestedSettings || currentNavTab != MainNavTab.CHATS
    ) {
        when {
            isRecordingVoice -> viewModel.cancelVoiceRecording()
            editingMessage != null -> viewModel.cancelEdit()
            replyingTo != null -> viewModel.cancelReply()
            selectedChatId != null -> viewModel.selectChat(null)
            canPopNestedSettings -> viewModel.popSettings()
            currentNavTab != MainNavTab.CHATS -> viewModel.setNavTab(MainNavTab.CHATS)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(VerbaDarkBackground)) {
        if (selectedChatId != null && currentNavTab == MainNavTab.CHATS && sidebarFilter == SidebarFilter.CHATS) {
            // ==========================================
            // FULL SCREEN MOBILE CHAT PANE
            // ==========================================
            VerbaChatPane(
                conversation = currentChatConversation,
                messages = activeMessages,
                onSendMessage = { viewModel.sendMessage(it) },
                onSendAttachment = { type, name, size -> viewModel.sendAttachment(type, name, size) },
                onStartVoiceRecording = { viewModel.startVoiceRecording() },
                onCancelVoiceRecording = { viewModel.cancelVoiceRecording() },
                onFinishVoiceRecording = { viewModel.finishAndSendVoiceRecording() },
                isRecordingVoice = isRecordingVoice,
                recordingSeconds = recordingSeconds,
                replyingTo = replyingTo,
                onCancelReply = { viewModel.cancelReply() },
                editingMessage = editingMessage,
                onCancelEdit = { viewModel.cancelEdit() },
                onSubmitEdit = { viewModel.submitEditMessage(it) },
                forwardingMessage = forwardingMessage,
                onCancelForward = { viewModel.setForwarding(null) },
                onForwardToContact = { name, targetUsername -> viewModel.forwardMessageTo(targetUsername, name) },
                contactsList = contacts,
                onStartCall = { name, avatar, type -> viewModel.startCall(name, avatar, type) },
                onReactionClick = { msg, emoji -> viewModel.toggleReaction(msg, emoji) },
                onReplyMessage = { viewModel.setReplyTo(it) },
                onPinMessage = { viewModel.togglePinMessage(it) },
                onStartEditMessage = { viewModel.startEditMessage(it) },
                onDeleteMessage = { viewModel.deleteMessage(it) },
                onSetForwarding = { viewModel.setForwarding(it) },
                onShowNotice = { viewModel.showNotice(it) },
                isMobileCompact = true,
                onBackToList = { viewModel.selectChat(null) },
                onClearChat = { currentChatConversation?.let { viewModel.clearChatMessages(it.id) } },
                onDeleteChat = {
                    currentChatConversation?.let {
                        viewModel.deleteConversation(it.id)
                        viewModel.selectChat(null)
                    }
                },
                enterToSend = appSettings.enterToSend,
                aiAssistantEnabled = appSettings.aiAssistantEnabled,
                smartReplyEnabled = appSettings.smartReplyEnabled,
                chatWallpaper = appSettings.chatWallpaper
            )
        } else {
            // ==========================================
            // ANDROID MOBILE 5-TAB SCREEN
            // ==========================================
            Scaffold(
                floatingActionButton = {
                    // Show FAB on primary content tabs (hide on Settings)
                    if (currentNavTab != MainNavTab.SETTINGS) {
                        FloatingActionButton(
                            onClick = {
                                when (currentNavTab) {
                                    MainNavTab.CHATS -> viewModel.showNewChatDialog()
                                    MainNavTab.CALLS -> viewModel.showNewChatDialog()
                                    MainNavTab.COMMUNITIES -> viewModel.showNewChatDialog()
                                    MainNavTab.UPDATES -> viewModel.showNotice("Tambah pembaruan status baru")
                                    else -> {}
                                }
                            },
                            containerColor = ElementEmerald,
                            contentColor = Color.Black,
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(bottom = 60.dp)
                                .size(56.dp)
                                .testTag("mobile_fab")
                        ) {
                            Icon(
                                imageVector = when (currentNavTab) {
                                    MainNavTab.CHATS -> Icons.AutoMirrored.Filled.Chat
                                    MainNavTab.CALLS -> Icons.Default.AddIcCall
                                    MainNavTab.COMMUNITIES -> Icons.Default.GroupAdd
                                    MainNavTab.UPDATES -> Icons.Default.CameraAlt
                                    else -> Icons.Default.Add
                                },
                                contentDescription = "Aksi Cepat",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                bottomBar = {
                    // 5-Tab Element Messenger Mobile Bottom Navigation
                    // 1. Chats | 2. Calls | 3. Communities | 4. Updates | 5. Settings
                    NavigationBar(
                        containerColor = VerbaGlassSurface,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                            .testTag("mobile_bottom_nav")
                    ) {
                        // 1. Chats
                        val unreadTotal = activeConversations.sumOf { it.unreadCount }
                        NavigationBarItem(
                            selected = currentNavTab == MainNavTab.CHATS,
                            onClick = { viewModel.setNavTab(MainNavTab.CHATS) },
                            icon = {
                                BadgedBox(badge = {
                                    if (unreadTotal > 0) {
                                        Badge(
                                            containerColor = ElementEmerald,
                                            contentColor = Color.Black
                                        ) {
                                            Text(unreadTotal.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (currentNavTab == MainNavTab.CHATS) Icons.AutoMirrored.Filled.Chat else Icons.AutoMirrored.Outlined.Chat,
                                        contentDescription = "Chats"
                                    )
                                }
                            },
                            label = { Text("Chats", fontSize = 11.sp, fontWeight = if (currentNavTab == MainNavTab.CHATS) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElementEmerald,
                                indicatorColor = ElementEmerald.copy(alpha = 0.18f),
                                unselectedIconColor = VerbaTextSecondary,
                                selectedTextColor = ElementEmerald,
                                unselectedTextColor = VerbaTextMuted
                            )
                        )

                        // 2. Calls
                        NavigationBarItem(
                            selected = currentNavTab == MainNavTab.CALLS,
                            onClick = { viewModel.setNavTab(MainNavTab.CALLS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentNavTab == MainNavTab.CALLS) Icons.Default.Call else Icons.Outlined.Call,
                                    contentDescription = "Calls"
                                )
                            },
                            label = { Text("Calls", fontSize = 11.sp, fontWeight = if (currentNavTab == MainNavTab.CALLS) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElementEmerald,
                                indicatorColor = ElementEmerald.copy(alpha = 0.18f),
                                unselectedIconColor = VerbaTextSecondary,
                                selectedTextColor = ElementEmerald,
                                unselectedTextColor = VerbaTextMuted
                            )
                        )

                        // 3. Communities
                        NavigationBarItem(
                            selected = currentNavTab == MainNavTab.COMMUNITIES,
                            onClick = { viewModel.setNavTab(MainNavTab.COMMUNITIES) },
                            icon = {
                                Icon(
                                    imageVector = if (currentNavTab == MainNavTab.COMMUNITIES) Icons.Default.Groups else Icons.Outlined.Groups,
                                    contentDescription = "Communities"
                                )
                            },
                            label = { Text("Communities", fontSize = 10.sp, fontWeight = if (currentNavTab == MainNavTab.COMMUNITIES) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElementEmerald,
                                indicatorColor = ElementEmerald.copy(alpha = 0.18f),
                                unselectedIconColor = VerbaTextSecondary,
                                selectedTextColor = ElementEmerald,
                                unselectedTextColor = VerbaTextMuted
                            )
                        )

                        // 4. Updates
                        NavigationBarItem(
                            selected = currentNavTab == MainNavTab.UPDATES,
                            onClick = { viewModel.setNavTab(MainNavTab.UPDATES) },
                            icon = {
                                Icon(
                                    imageVector = if (currentNavTab == MainNavTab.UPDATES) Icons.Filled.DonutLarge else Icons.Outlined.DonutLarge,
                                    contentDescription = "Updates"
                                )
                            },
                            label = { Text("Updates", fontSize = 11.sp, fontWeight = if (currentNavTab == MainNavTab.UPDATES) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElementEmerald,
                                indicatorColor = ElementEmerald.copy(alpha = 0.18f),
                                unselectedIconColor = VerbaTextSecondary,
                                selectedTextColor = ElementEmerald,
                                unselectedTextColor = VerbaTextMuted
                            )
                        )

                        // 5. Settings
                        NavigationBarItem(
                            selected = currentNavTab == MainNavTab.SETTINGS,
                            onClick = { viewModel.setNavTab(MainNavTab.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (currentNavTab == MainNavTab.SETTINGS) Icons.Default.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings", fontSize = 11.sp, fontWeight = if (currentNavTab == MainNavTab.SETTINGS) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElementEmerald,
                                indicatorColor = ElementEmerald.copy(alpha = 0.18f),
                                unselectedIconColor = VerbaTextSecondary,
                                selectedTextColor = ElementEmerald,
                                unselectedTextColor = VerbaTextMuted
                            )
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Mobile Top Bar (Only on non-settings tabs; Settings has its own header)
                    if (currentNavTab != MainNavTab.SETTINGS) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(VerbaGlassSurface)
                                .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                                .statusBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Element Messenger",
                                color = ElementEmerald,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.showNotice("Kamera siap mengambil foto/video") },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = "Kamera", tint = VerbaTextSecondary)
                                }
                                IconButton(
                                    onClick = { viewModel.showNewChatDialog() },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = "Cari", tint = VerbaTextSecondary)
                                }
                                Box {
                                    IconButton(
                                        onClick = { showTopMenu = true },
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = VerbaTextSecondary)
                                    }
                                    DropdownMenu(
                                        expanded = showTopMenu,
                                        onDismissRequest = { showTopMenu = false },
                                        modifier = Modifier.background(VerbaPanel)
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Grup baru", color = VerbaTextPrimary) },
                                            leadingIcon = { Icon(Icons.Default.GroupAdd, null, tint = ElementEmerald) },
                                            onClick = {
                                                showTopMenu = false
                                                viewModel.showNewChatDialog()
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Siaran baru", color = VerbaTextPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Podcasts, null, tint = ElementEmerald) },
                                            onClick = {
                                                showTopMenu = false
                                                viewModel.showNotice("Fitur Siaran Baru")
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Pesan berbintang", color = VerbaTextPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Star, null, tint = ElementEmerald) },
                                            onClick = {
                                                showTopMenu = false
                                                viewModel.showNotice("Pesan Berbintang")
                                            }
                                        )
                                        HorizontalDivider(color = VerbaGlassBorder)
                                        DropdownMenuItem(
                                            text = { Text("Pengaturan", color = VerbaTextPrimary) },
                                            leadingIcon = { Icon(Icons.Default.Settings, null, tint = ElementEmerald) },
                                            onClick = {
                                                showTopMenu = false
                                                viewModel.setNavTab(MainNavTab.SETTINGS)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Keluar Akun", color = VerbaDanger) },
                                            leadingIcon = { Icon(Icons.Default.ExitToApp, null, tint = VerbaDanger) },
                                            onClick = {
                                                showTopMenu = false
                                                showLogoutConfirmDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Content Area (Native Mobile Screens for each tab)
                    Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                        when (currentNavTab) {
                            MainNavTab.CHATS -> {
                                when (sidebarFilter) {
                                    SidebarFilter.CHATS -> {
                                        VerbaSidebar(
                                            userProfile = userProfile,
                                            currentFilter = sidebarFilter,
                                            onFilterSelected = { viewModel.setSidebarFilter(it) },
                                            conversations = activeConversations,
                                            selectedChatId = selectedChatId,
                                            onSelectChat = { id -> viewModel.selectChat(id) },
                                            communities = communities,
                                            onSelectCommunity = { viewModel.setNavTab(MainNavTab.COMMUNITIES) },
                                            searchQuery = searchQuery,
                                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                            messageRequestCount = messageRequests.size,
                                            archiveCount = archivedConversations.size,
                                            onEditProfileClick = { showEditProfileDialog = true },
                                            onNewChatClick = { viewModel.showNewChatDialog() },
                                            onLogoutClick = { showLogoutConfirmDialog = true },
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    SidebarFilter.MARKETPLACE -> {
                                        MarketplaceScreen(
                                            items = marketItems,
                                            onChatWithSeller = { item ->
                                                val sellerUser = "@${item.sellerName.lowercase().replace(" ", "_")}"
                                                viewModel.startNewChat(sellerUser, item.sellerName, item.sellerAvatar)
                                                viewModel.sendMessage("Halo ${item.sellerName}, saya ingin pesan ${item.title}")
                                            }
                                        )
                                    }
                                    SidebarFilter.REQUESTS -> {
                                        MessageRequestsScreen(
                                            requests = messageRequests,
                                            onAccept = { viewModel.acceptRequest(it) },
                                            onDelete = { viewModel.deleteConversation(it) },
                                            onBlock = { viewModel.deleteConversation(it) }
                                        )
                                    }
                                    SidebarFilter.ARCHIVE -> {
                                        ArchivedScreen(
                                            archivedChats = archivedConversations,
                                            onOpenChat = { id ->
                                                viewModel.setSidebarFilter(SidebarFilter.CHATS)
                                                viewModel.selectChat(id)
                                            },
                                            onUnarchive = { viewModel.setArchived(it, false) },
                                            onDelete = { viewModel.deleteConversation(it) }
                                        )
                                    }
                                }
                            }
                            MainNavTab.CALLS -> {
                                CallsScreen(
                                    callHistory = callHistory,
                                    onStartCall = { name, avatar, type -> viewModel.startCall(name, avatar, type) },
                                    onClearCallHistory = { viewModel.clearCallHistory() },
                                    onDeleteCall = { viewModel.deleteCallRecord(it) }
                                )
                            }
                            MainNavTab.COMMUNITIES -> {
                                CommunitiesScreen(
                                    communities = communities,
                                    onToggleJoin = { viewModel.toggleCommunityJoin(it) },
                                    onToggleMute = { viewModel.toggleCommunityMute(it) },
                                    onOpenCommunityChat = { comm ->
                                        viewModel.startNewChat(comm.id, comm.name, comm.avatar)
                                    }
                                )
                            }
                            MainNavTab.UPDATES -> {
                                StatusScreen(
                                    userProfile = userProfile,
                                    statusList = statusStories,
                                    myStatus = myStatus,
                                    onAddStatus = { text, color -> viewModel.addStatus(text, color) },
                                    onReplyToStatus = { user, name, reply -> viewModel.replyToStatus(user, name, reply) }
                                )
                            }
                            MainNavTab.SETTINGS -> {
                                SettingsMobileScreen(
                                    settingsStack = settingsStack,
                                    currentPage = currentSettingsPage,
                                    appSettings = appSettings,
                                    userProfile = userProfile,
                                    onPushPage = { viewModel.pushSettings(it) },
                                    onPopPage = { viewModel.popSettings() },
                                    onUpdateSettings = { viewModel.updateSettings(it) },
                                    onClearCache = { viewModel.clearCache() },
                                    onEditProfileClick = { showEditProfileDialog = true },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp-Style New Chat Menu Dialog
        NewChatDialog(
            isOpen = showNewChatDialog,
            onDismiss = { viewModel.dismissNewChatDialog() },
            contacts = contacts,
            onSelectContact = { username, name, avatar ->
                viewModel.startNewChat(username, name, avatar)
            },
            onStartChatByUsername = { username ->
                viewModel.startNewChat(username, username.removePrefix("@"), "EM")
            },
            onCreateGroup = { name, avatar, members ->
                viewModel.createGroup(name, avatar, members)
            },
            onAddContact = { name, phone, username ->
                viewModel.addContact(name, phone, username)
            },
            onCreateCommunity = { name, desc, avatar ->
                viewModel.createCommunity(name, desc, avatar)
            },
            onCreateEvent = { title, dt, desc, members ->
                viewModel.createEvent(title, dt, desc, members)
            }
        )

        // Active In-Call Simulation Dialog
        activeCall?.let { call ->
            ActiveCallDialog(
                callState = call,
                onMuteToggle = { viewModel.toggleCallMute() },
                onSpeakerToggle = { viewModel.toggleCallSpeaker() },
                onEndCall = { viewModel.endCall() }
            )
        }

        // Complete Verified Edit Profile Dialog
        EditProfileDialog(
            isOpen = showEditProfileDialog,
            userProfile = userProfile,
            onDismiss = { showEditProfileDialog = false },
            onSaveProfile = { name, username, bio, avatar, phone ->
                viewModel.updateProfileDetailed(name, username, bio, avatar, phone)
            },
            onRequestPhoneOtp = { phone, onOtpSent ->
                viewModel.requestPhoneOtp(phone)
                val code = viewModel.dispatchedOtpNotice.value ?: "123456"
                onOtpSent(code)
            }
        )

        // Logout Confirmation Dialog
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                containerColor = VerbaPanel,
                shape = RoundedCornerShape(16.dp),
                title = { Text("Keluar dari Akun?", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Text(
                        text = "Anda akan keluar dari ${userProfile.username}. Obrolan Anda tersimpan aman dan Anda dapat masuk kembali kapan saja.",
                        color = VerbaTextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showLogoutConfirmDialog = false
                            viewModel.logout()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VerbaDanger)
                    ) {
                        Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text("Batal", color = VerbaTextSecondary)
                    }
                }
            )
        }

        // Snackbar Host for notices
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp)
        )
    }
}
