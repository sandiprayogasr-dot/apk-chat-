package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.VerbaRepository
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppRootScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    CREATE_ACCOUNT,
    FORGOT_PASSWORD,
    PROFILE_SETUP,
    CREATE_PIN,
    FINGERPRINT,
    HOME
}

enum class HomeOverlayScreen {
    NONE,
    SEARCH,
    CONTACTS,
    CONTACT_PROFILE,
    MEDIA_LINKS_DOCS,
    SETTINGS
}

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val content: String,
    val timestamp: String = "Sekarang"
)

enum class MainNavTab {
    CHATS,
    CALLS,
    COMMUNITIES,
    UPDATES,
    SETTINGS;

    companion object {
        val CHAT get() = CHATS
        val STATUS get() = UPDATES
        val GROUPS get() = COMMUNITIES
        val AI get() = CHATS
    }
}

enum class SidebarFilter {
    CHATS,
    MARKETPLACE,
    REQUESTS,
    ARCHIVE
}

data class ActiveCallState(
    val contactName: String,
    val contactAvatar: String,
    val type: CallType,
    val durationSeconds: Int = 0,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = true,
    val isCameraOn: Boolean = true,
    val isVideoFront: Boolean = true,
    val isConnected: Boolean = true
)

enum class PhoneAuthStep {
    INPUT_PHONE,
    OTP_VERIFY,
    PROFILE_SETUP
}

class VerbaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VerbaRepository(application)

    // User Profile & Auth
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val isLoggedIn: StateFlow<Boolean> = repository.userProfile.map { it.isLoggedIn }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Events State
    val events: StateFlow<List<CalendarEvent>> = repository.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Phone Auth States
    private val _phoneAuthStep = MutableStateFlow(PhoneAuthStep.INPUT_PHONE)
    val phoneAuthStep: StateFlow<PhoneAuthStep> = _phoneAuthStep.asStateFlow()

    private val _phoneNumberInput = MutableStateFlow("")
    val phoneNumberInput: StateFlow<String> = _phoneNumberInput.asStateFlow()

    private val _countryCode = MutableStateFlow("+62")
    val countryCode: StateFlow<String> = _countryCode.asStateFlow()

    private val _otpInput = MutableStateFlow("")
    val otpInput: StateFlow<String> = _otpInput.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _dispatchedOtpNotice = MutableStateFlow<String?>(null)
    val dispatchedOtpNotice: StateFlow<String?> = _dispatchedOtpNotice.asStateFlow()

    private val _otpTimerSeconds = MutableStateFlow(60)
    val otpTimerSeconds: StateFlow<Int> = _otpTimerSeconds.asStateFlow()
    private var countdownJob: kotlinx.coroutines.Job? = null

    // Selected Contact for Inspection / Profile Details
    private val _inspectedContact = MutableStateFlow<Contact?>(null)
    val inspectedContact: StateFlow<Contact?> = _inspectedContact.asStateFlow()

    // Root Navigation State (Starts at SPLASH)
    private val _rootScreen = MutableStateFlow(AppRootScreen.SPLASH)
    val rootScreen: StateFlow<AppRootScreen> = _rootScreen.asStateFlow()

    // Home Sub-Screen Overlays
    private val _homeOverlay = MutableStateFlow(HomeOverlayScreen.NONE)
    val homeOverlay: StateFlow<HomeOverlayScreen> = _homeOverlay.asStateFlow()

    // Selected Contact for Profile View
    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    // Selected Title for Media/Links/Docs Screen
    private val _mediaScreenTitle = MutableStateFlow("Jenny Wilson")
    val mediaScreenTitle: StateFlow<String> = _mediaScreenTitle.asStateFlow()

    // Main Navigation Tab
    private val _currentNavTab = MutableStateFlow(MainNavTab.CHAT)
    val currentNavTab: StateFlow<MainNavTab> = _currentNavTab.asStateFlow()

    private val _sidebarFilter = MutableStateFlow(SidebarFilter.CHATS)
    val sidebarFilter: StateFlow<SidebarFilter> = _sidebarFilter.asStateFlow()

    // Active Selected Chat Room ID
    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // New Chat Dialog
    private val _showNewChatDialog = MutableStateFlow(false)
    val showNewChatDialog: StateFlow<Boolean> = _showNewChatDialog.asStateFlow()

    // Settings Navigation Stack
    private val _settingsStack = MutableStateFlow<List<SettingsPage>>(listOf(SettingsPage.HOME))
    val settingsStack: StateFlow<List<SettingsPage>> = _settingsStack.asStateFlow()

    val currentSettingsPage: StateFlow<SettingsPage> = _settingsStack.map { stack ->
        stack.lastOrNull() ?: SettingsPage.HOME
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsPage.HOME)

    val isSettingsOpen: StateFlow<Boolean> = _homeOverlay.map { it == HomeOverlayScreen.SETTINGS }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // AI Assistant Messages
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                id = "ai_intro",
                sender = "ai",
                content = "Halo! Saya Element AI Assistant. Saya siap membantu Anda membuat draf pesan, merangkum chat, menerjemahkan bahasa, atau menjawab pertanyaan.",
                timestamp = "Sekarang"
            )
        )
    )
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // App Preferences State
    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    // Active Call
    private val _activeCall = MutableStateFlow<ActiveCallState?>(null)
    val activeCall: StateFlow<ActiveCallState?> = _activeCall.asStateFlow()
    private var callTimerJob: Job? = null

    // Message Input Enhancements
    private val _replyingTo = MutableStateFlow<MessageReply?>(null)
    val replyingTo: StateFlow<MessageReply?> = _replyingTo.asStateFlow()

    private val _editingMessage = MutableStateFlow<ChatMessage?>(null)
    val editingMessage: StateFlow<ChatMessage?> = _editingMessage.asStateFlow()

    private val _forwardingMessage = MutableStateFlow<ChatMessage?>(null)
    val forwardingMessage: StateFlow<ChatMessage?> = _forwardingMessage.asStateFlow()

    private val _inChatSearchQuery = MutableStateFlow<String?>(null)
    val inChatSearchQuery: StateFlow<String?> = _inChatSearchQuery.asStateFlow()

    // Voice recording
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()
    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()
    private var recordingJob: Job? = null

    // Status snackbar / notice
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Data streams from repository scoped to active user
    val activeConversations: StateFlow<List<ChatConversation>> = repository.activeConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedConversations: StateFlow<List<ChatConversation>> = repository.archivedConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messageRequests: StateFlow<List<ChatConversation>> = repository.messageRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contacts: StateFlow<List<Contact>> = repository.contacts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val callHistory: StateFlow<List<CallRecord>> = repository.callHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val communities: StateFlow<List<Community>> = repository.communities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val marketItems: StateFlow<List<MarketItem>> = repository.marketItems
    val typingChats: StateFlow<Map<String, Boolean>> = repository.typingChats

    // VerbaChat Status Stories (Starts empty, populated when user or contacts add status)
    private val _statusStories = MutableStateFlow<List<UserStatusStory>>(emptyList())
    val statusStories: StateFlow<List<UserStatusStory>> = _statusStories.asStateFlow()

    private val _myStatus = MutableStateFlow<UserStatusStory?>(null)
    val myStatus: StateFlow<UserStatusStory?> = _myStatus.asStateFlow()

    init {
        // Splash Screen Timer: checks login status after 1200ms
        viewModelScope.launch {
            delay(1200)
            if (_rootScreen.value == AppRootScreen.SPLASH) {
                if (repository.userProfile.value.isLoggedIn) {
                    _rootScreen.value = AppRootScreen.HOME
                } else {
                    _rootScreen.value = AppRootScreen.ONBOARDING
                }
            }
        }
    }

    // ==========================================
    // NAVIGATION METHODS
    // ==========================================
    fun navigateTo(screen: AppRootScreen) {
        _rootScreen.value = screen
    }

    fun openOverlay(overlay: HomeOverlayScreen) {
        _homeOverlay.value = overlay
    }

    fun closeOverlay() {
        _homeOverlay.value = HomeOverlayScreen.NONE
    }

    fun openContactProfile(contact: Contact) {
        _selectedContact.value = contact
        _inspectedContact.value = contact
        _homeOverlay.value = HomeOverlayScreen.CONTACT_PROFILE
    }

    fun openMediaLinksDocs(title: String) {
        _mediaScreenTitle.value = title
        _homeOverlay.value = HomeOverlayScreen.MEDIA_LINKS_DOCS
    }

    fun openSearch() {
        _homeOverlay.value = HomeOverlayScreen.SEARCH
    }

    fun openContacts() {
        _homeOverlay.value = HomeOverlayScreen.CONTACTS
    }

    fun openSettings(page: SettingsPage = SettingsPage.HOME) {
        _settingsStack.value = if (page == SettingsPage.HOME) listOf(SettingsPage.HOME) else listOf(SettingsPage.HOME, page)
        _currentNavTab.value = MainNavTab.SETTINGS
        _homeOverlay.value = HomeOverlayScreen.SETTINGS
    }

    fun pushSettingsPage(page: SettingsPage) {
        _settingsStack.value = _settingsStack.value + page
    }

    fun pushSettings(page: SettingsPage) {
        pushSettingsPage(page)
    }

    fun popSettings(): Boolean {
        val stack = _settingsStack.value
        return if (stack.size > 1) {
            _settingsStack.value = stack.dropLast(1)
            true
        } else {
            _homeOverlay.value = HomeOverlayScreen.NONE
            if (_currentNavTab.value == MainNavTab.SETTINGS) {
                _currentNavTab.value = MainNavTab.CHATS
            }
            false
        }
    }

    fun closeSettings() {
        _settingsStack.value = listOf(SettingsPage.HOME)
        _homeOverlay.value = HomeOverlayScreen.NONE
        if (_currentNavTab.value == MainNavTab.SETTINGS) {
            _currentNavTab.value = MainNavTab.CHATS
        }
    }

    fun login(username: String, displayName: String, bio: String) {
        viewModelScope.launch {
            repository.login(username, displayName, bio)
            _rootScreen.value = AppRootScreen.HOME
            showNotice("Selamat datang di Element Messenger, $displayName!")
        }
    }

    // ==========================================
    // AI ASSISTANT ACTIONS
    // ==========================================
    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = AiMessage(sender = "user", content = prompt)
        _aiMessages.value = _aiMessages.value + userMsg

        viewModelScope.launch {
            _isAiThinking.value = true
            delay(1200)
            val replyText = generateAiResponse(prompt)
            _aiMessages.value = _aiMessages.value + AiMessage(sender = "ai", content = replyText)
            _isAiThinking.value = false
        }
    }

    fun clearAiChat() {
        _aiMessages.value = listOf(
            AiMessage(
                id = "ai_welcome_fresh",
                sender = "ai",
                content = "Percakapan dengan Element AI dibersihkan. Apa yang ingin Anda diskusikan?",
                timestamp = "Sekarang"
            )
        )
    }

    private fun generateAiResponse(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("halo") || lower.contains("hai") || lower.contains("pagi") || lower.contains("siang") || lower.contains("malam") ->
                "Halo! Ada yang bisa saya bantu terkait komunikasi, rangkuman chat, atau pesan Anda hari ini?"
            lower.contains("rangkum") || lower.contains("ringkas") ->
                "📌 **Rangkuman Percakapan:**\n1. Element Messenger hadir dengan visual Emerald Frosted Glass yang elegan.\n2. Navigasi utama mobile memiliki 5 menu: Chats, Calls, Communities, Updates, dan Settings.\n3. Fitur terintegrasi dengan performa lancar, enkripsi aman, dan responsif."
            lower.contains("terjemah") || lower.contains("translate") || lower.contains("inggris") ->
                "🌐 **Terjemahan:**\n\"Element Messenger is a modern, high-speed, secure mobile messaging application with a beautiful emerald glass interface.\""
            lower.contains("perbaiki") || lower.contains("formal") || lower.contains("tulis") ->
                "✍️ **Saran Draf Pesan Formal:**\n\"Yth. Rekan Tim, saya ingin mengonfirmasikan bahwa pembaruan sistem dan antarmuka Element Messenger telah siap untuk ditinjau. Terima kasih atas kerja samanya.\""
            lower.contains("bantu") || lower.contains("fitur") ->
                "💡 **Fitur Element Messenger:**\n• Obrolan Real-Time & Enkripsi\n• Panggilan Suara & Video HD\n• Story / Status Updates\n• Komunitas & Grup Publik\n• Element AI Assistant"
            else ->
                "Saya memahami permintaan Anda mengenai \"$input\". Di Element Messenger, Anda dapat menggunakan AI untuk menyempurnakan tulisan pesan, membuat ringkasan rapat, menerjemahkan bahasa, atau menghasilkan respons cepat."
        }
    }

    // ==========================================
    // AUTH ACTIONS (EMAIL / USERNAME + PASSWORD)
    // ==========================================
    fun loginWithCredentials(
        identifier: String,
        password: String,
        rememberMe: Boolean,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.loginWithCredentials(identifier, password, rememberMe)
            result.onSuccess {
                _selectedChatId.value = null
                _rootScreen.value = AppRootScreen.HOME
                showNotice("Selamat datang kembali, ${it.name}!")
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Email/username atau password salah.")
            }
        }
    }

    fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(fullName, username, email, password)
            result.onSuccess {
                // Register sequence: Register -> Profile Setup -> Create PIN -> Fingerprint -> Home
                _rootScreen.value = AppRootScreen.PROFILE_SETUP
                showNotice("Akun berhasil dibuat. Silakan lengkapi profil Anda.")
                onSuccess()
            }.onFailure {
                onError(it.message ?: "Gagal membuat akun.")
            }
        }
    }

    fun resetPassword(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.resetPassword(email)
            result.onSuccess {
                onResult(true, "Password reset instructions have been sent.")
            }.onFailure {
                onResult(false, it.message ?: "Gagal mengirim instruksi reset password.")
            }
        }
    }

    fun saveProfileSetup(displayName: String, bio: String, avatar: String) {
        viewModelScope.launch {
            repository.updateProfile(displayName, bio, "Online")
            _rootScreen.value = AppRootScreen.CREATE_PIN
            showNotice("Profil diperbarui. Silakan buat PIN keamanan.")
        }
    }

    fun savePin(pin: String) {
        viewModelScope.launch {
            repository.updatePin(pin)
            _rootScreen.value = AppRootScreen.FINGERPRINT
            showNotice("PIN 6-digit berhasil disimpan.")
        }
    }

    fun enableFingerprint() {
        viewModelScope.launch {
            repository.setFingerprintEnabled(true)
            _rootScreen.value = AppRootScreen.HOME
            showNotice("Fingerprint aktif! Selamat datang di Element Messenger.")
        }
    }

    fun skipFingerprint() {
        _rootScreen.value = AppRootScreen.HOME
        showNotice("Selamat datang di Element Messenger!")
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _selectedChatId.value = null
            _homeOverlay.value = HomeOverlayScreen.NONE
            _rootScreen.value = AppRootScreen.LOGIN
            showNotice("Anda telah keluar dari akun.")
        }
    }

    // ==========================================
    // CHAT & MESSAGING
    // ==========================================
    fun setNavTab(tab: MainNavTab) {
        _currentNavTab.value = tab
        if (tab != MainNavTab.SETTINGS) {
            _homeOverlay.value = HomeOverlayScreen.NONE
        } else {
            if (_settingsStack.value.isEmpty()) {
                _settingsStack.value = listOf(SettingsPage.HOME)
            }
            _homeOverlay.value = HomeOverlayScreen.SETTINGS
        }
    }

    fun setSidebarFilter(filter: SidebarFilter) {
        _sidebarFilter.value = filter
    }

    fun selectChat(chatId: String?) {
        _selectedChatId.value = chatId
        _replyingTo.value = null
        _editingMessage.value = null
        _inChatSearchQuery.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setInChatSearch(query: String?) {
        _inChatSearchQuery.value = query
    }

    fun showNewChatDialog() {
        _showNewChatDialog.value = true
    }

    fun dismissNewChatDialog() {
        _showNewChatDialog.value = false
    }

    fun startNewChat(targetUsername: String, targetName: String, targetAvatar: String) {
        viewModelScope.launch {
            val chatId = repository.startOrGetConversation(targetUsername, targetName, targetAvatar)
            _selectedChatId.value = chatId
            _showNewChatDialog.value = false
            _homeOverlay.value = HomeOverlayScreen.NONE
            _currentNavTab.value = MainNavTab.CHATS
        }
    }

    // Message stream for currently selected chat
    val activeChatMessages: StateFlow<List<ChatMessage>> = _selectedChatId
        .flatMapLatest { chatId ->
            if (chatId == null) flowOf(emptyList())
            else repository.getMessagesForChat(chatId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentChatConversation: StateFlow<ChatConversation?> = combine(
        _selectedChatId,
        activeConversations,
        archivedConversations,
        messageRequests
    ) { chatId, active, archived, requests ->
        if (chatId == null) null
        else active.find { it.id == chatId || generateChatId(it.ownerUsername, it.participantUsername) == chatId }
            ?: archived.find { it.id == chatId || generateChatId(it.ownerUsername, it.participantUsername) == chatId }
            ?: requests.find { it.id == chatId || generateChatId(it.ownerUsername, it.participantUsername) == chatId }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun sendMessage(content: String) {
        val chatId = _selectedChatId.value ?: return
        if (content.isBlank()) return
        val reply = _replyingTo.value
        _replyingTo.value = null

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                content = content.trim(),
                messageType = MessageType.TEXT,
                replyTo = reply
            )
        }
    }

    fun sendAttachment(type: MessageType, fileName: String, fileSize: String) {
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            val content = when (type) {
                MessageType.IMAGE -> "Foto dikirim"
                MessageType.FILE -> fileName
                MessageType.VOICE -> "Pesan suara"
                else -> fileName
            }
            repository.sendMessage(
                chatId = chatId,
                content = content,
                messageType = type,
                fileName = fileName,
                fileSize = fileSize
            )
            showNotice("Lampiran ${type.name.lowercase()} terkirim")
        }
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingSeconds.value = 0
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordingSeconds.value += 1
            }
        }
    }

    fun cancelVoiceRecording() {
        recordingJob?.cancel()
        _isRecordingVoice.value = false
        _recordingSeconds.value = 0
    }

    fun finishAndSendVoiceRecording() {
        val duration = _recordingSeconds.value
        cancelVoiceRecording()
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                content = "Pesan suara",
                messageType = MessageType.VOICE,
                durationSeconds = if (duration > 0) duration else 3
            )
            showNotice("Pesan suara (${duration}s) terkirim")
        }
    }

    fun setReplyTo(message: ChatMessage) {
        _replyingTo.value = MessageReply(
            id = message.id,
            senderName = message.senderName,
            content = message.content
        )
    }

    fun cancelReply() {
        _replyingTo.value = null
    }

    fun startEditMessage(message: ChatMessage) {
        _editingMessage.value = message
    }

    fun cancelEdit() {
        _editingMessage.value = null
    }

    fun submitEditMessage(newContent: String) {
        val editing = _editingMessage.value ?: return
        if (newContent.isBlank()) return
        _editingMessage.value = null
        viewModelScope.launch {
            repository.editMessage(editing.id, newContent.trim(), editing)
            showNotice("Pesan berhasil diedit")
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            repository.deleteMessage(messageId)
            showNotice("Pesan telah dihapus")
        }
    }

    fun clearChatMessages(chatId: String) {
        viewModelScope.launch {
            repository.clearMessagesForChat(chatId)
            showNotice("Semua pesan obrolan telah dibersihkan")
        }
    }

    fun toggleReaction(message: ChatMessage, emoji: String) {
        viewModelScope.launch {
            repository.toggleReaction(message, emoji)
        }
    }

    fun togglePinMessage(message: ChatMessage) {
        viewModelScope.launch {
            repository.togglePinMessage(message)
            showNotice(if (!message.isPinned) "Pesan disematkan ke atas" else "Semat pesan dilepas")
        }
    }

    fun setForwarding(message: ChatMessage?) {
        _forwardingMessage.value = message
    }

    fun forwardMessageTo(contactUsername: String, contactName: String) {
        val forward = _forwardingMessage.value ?: return
        _forwardingMessage.value = null
        viewModelScope.launch {
            val targetChatId = repository.startOrGetConversation(contactUsername, contactName, "")
            repository.sendMessage(
                chatId = targetChatId,
                content = "[Diteruskan] ${forward.content}",
                messageType = forward.messageType,
                durationSeconds = forward.durationSeconds,
                fileName = forward.fileName,
                fileSize = forward.fileSize
            )
            showNotice("Pesan diteruskan ke $contactName")
        }
    }

    // Call controls
    fun startCall(contactName: String, contactAvatar: String, type: CallType) {
        _activeCall.value = ActiveCallState(
            contactName = contactName,
            contactAvatar = contactAvatar,
            type = type,
            durationSeconds = 0,
            isConnected = true
        )
        callTimerJob?.cancel()
        callTimerJob = viewModelScope.launch {
            while (_activeCall.value != null) {
                delay(1000)
                _activeCall.value = _activeCall.value?.let {
                    it.copy(durationSeconds = it.durationSeconds + 1)
                }
            }
        }
    }

    fun toggleCallMute() {
        _activeCall.value = _activeCall.value?.let { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleCallSpeaker() {
        _activeCall.value = _activeCall.value?.let { it.copy(isSpeakerOn = !it.isSpeakerOn) }
    }

    fun toggleCallCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isCameraOn = !it.isCameraOn) }
    }

    fun switchCallCamera() {
        _activeCall.value = _activeCall.value?.let { it.copy(isVideoFront = !it.isVideoFront) }
        showNotice("Kamera dialihkan")
    }

    fun endCall() {
        val current = _activeCall.value
        callTimerJob?.cancel()
        _activeCall.value = null

        if (current != null) {
            val minutes = current.durationSeconds / 60
            val seconds = current.durationSeconds % 60
            val durationText = String.format("%02d:%02d", minutes, seconds)
            viewModelScope.launch {
                repository.addCallRecord(
                    contactName = current.contactName,
                    contactAvatar = current.contactAvatar,
                    type = current.type,
                    direction = CallDirection.OUTGOING,
                    durationText = durationText
                )
            }
            showNotice("Panggilan berakhir ($durationText)")
        }
    }

    fun clearCallHistory() {
        viewModelScope.launch {
            repository.clearAllCalls()
            showNotice("Riwayat panggilan telah dibersihkan")
        }
    }

    fun deleteCallRecord(callId: String) {
        viewModelScope.launch {
            repository.deleteCall(callId)
            showNotice("Panggilan dihapus")
        }
    }

    // Status Stories
    fun addStatus(content: String, colorHex: Long) {
        val user = userProfile.value
        val newStory = UserStatusStory(
            id = "status_me_${System.currentTimeMillis()}",
            username = user.username,
            authorName = user.name,
            authorAvatar = user.avatar,
            content = content,
            timestamp = "Baru saja",
            colorHex = colorHex
        )
        _myStatus.value = newStory
        showNotice("Status berhasil dibagikan")
    }

    fun replyToStatus(authorUsername: String, authorName: String, replyText: String) {
        viewModelScope.launch {
            val chatId = repository.startOrGetConversation(authorUsername, authorName, "")
            _selectedChatId.value = chatId
            _currentNavTab.value = MainNavTab.CHATS
            repository.sendMessage(
                chatId = chatId,
                content = "Membalas status: \"$replyText\""
            )
            showNotice("Balasan status terkirim ke $authorName")
        }
    }

    // Contacts
    fun addContact(name: String, phone: String, username: String) {
        viewModelScope.launch {
            repository.addContact(name, phone, username)
            showNotice("Kontak $name berhasil ditambahkan")
        }
    }

    fun toggleBlockContact(contact: Contact) {
        viewModelScope.launch {
            repository.toggleBlockContact(contact.id, contact.isBlocked)
            showNotice(if (contact.isBlocked) "Blokir ${contact.name} dibuka" else "${contact.name} diblokir")
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            repository.deleteContact(contact.id)
            showNotice("Kontak ${contact.name} dihapus")
        }
    }

    // Communities
    fun toggleCommunityJoin(community: Community) {
        viewModelScope.launch {
            repository.toggleCommunityJoin(community.id)
            showNotice(if (community.isJoined) "Keluar dari ${community.name}" else "Bergabung ke ${community.name}")
        }
    }

    fun toggleCommunityMute(community: Community) {
        viewModelScope.launch {
            repository.toggleCommunityMute(community.id)
            showNotice(if (community.notificationMuted) "Notifikasi diaktifkan" else "Notifikasi dibisukan")
        }
    }

    // Requests & Archive
    fun acceptRequest(convId: String) {
        viewModelScope.launch {
            repository.acceptMessageRequest(convId)
            _selectedChatId.value = convId
            setNavTab(MainNavTab.CHATS)
            setSidebarFilter(SidebarFilter.CHATS)
            showNotice("Permintaan pesan disetujui")
        }
    }

    fun deleteConversation(convId: String) {
        viewModelScope.launch {
            repository.deleteConversation(convId)
            if (_selectedChatId.value == convId) {
                _selectedChatId.value = null
            }
            showNotice("Percakapan telah dihapus")
        }
    }

    fun setArchived(convId: String, archived: Boolean) {
        viewModelScope.launch {
            repository.setArchived(convId, archived)
            showNotice(if (archived) "Chat diarsipkan" else "Chat dikembalikan dari arsip")
        }
    }

    fun updateProfile(name: String, bio: String, status: String) {
        viewModelScope.launch {
            repository.updateProfile(name, bio, status)
            showNotice("Profil berhasil diperbarui")
        }
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        _appSettings.value = transform(_appSettings.value)
    }

    fun clearCache() {
        _appSettings.value = _appSettings.value.copy(cacheSizeMb = 0.0f)
        showNotice("Cache aplikasi berhasil dibersihkan (0 MB)")
    }

    fun showNotice(text: String) {
        _statusMessage.value = text
    }

    // Phone Auth Methods
    fun setPhoneNumber(number: String) {
        _phoneNumberInput.value = number
        _authError.value = null
    }

    fun setCountryCode(code: String) {
        _countryCode.value = code
    }

    fun setOtp(otp: String) {
        _otpInput.value = otp
        _authError.value = null
        if (otp.length == 6) {
            verifyPhoneOtp()
        }
    }

    fun requestPhoneOtp(fullNumber: String? = null) {
        val phone = fullNumber ?: "${_countryCode.value}${_phoneNumberInput.value.trim()}"
        if (_phoneNumberInput.value.trim().length < 6) {
            _authError.value = "Nomor telepon terlalu pendek. Masukkan nomor yang valid."
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.requestPhoneOtp(phone)
            _isAuthLoading.value = false
            result.onSuccess { otp ->
                _phoneAuthStep.value = PhoneAuthStep.OTP_VERIFY
                _dispatchedOtpNotice.value = otp
                _otpInput.value = ""
                startOtpCountdown()
                showNotice("Kode OTP 6-digit: $otp")
            }.onFailure { err ->
                _authError.value = err.message ?: "Gagal mengirim OTP."
            }
        }
    }

    private fun startOtpCountdown() {
        countdownJob?.cancel()
        _otpTimerSeconds.value = 60
        countdownJob = viewModelScope.launch {
            while (_otpTimerSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _otpTimerSeconds.value -= 1
            }
        }
    }

    fun resendPhoneOtp() {
        if (_otpTimerSeconds.value > 0) return
        val phone = "${_countryCode.value}${_phoneNumberInput.value.trim()}"
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.requestPhoneOtp(phone)
            _isAuthLoading.value = false
            result.onSuccess { otp ->
                _dispatchedOtpNotice.value = otp
                startOtpCountdown()
                showNotice("Kode OTP baru: $otp")
            }.onFailure { err ->
                _authError.value = err.message ?: "Gagal mengirim ulang OTP."
            }
        }
    }

    fun verifyPhoneOtp() {
        val phone = "${_countryCode.value}${_phoneNumberInput.value.trim()}"
        val otp = _otpInput.value.trim()
        if (otp.length != 6) {
            _authError.value = "Masukkan 6 digit kode OTP secara lengkap."
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.verifyPhoneOtp(phone, otp)
            _isAuthLoading.value = false
            result.onSuccess { isExistingUser ->
                if (isExistingUser) {
                    _phoneAuthStep.value = PhoneAuthStep.INPUT_PHONE
                    _dispatchedOtpNotice.value = null
                    showNotice("Login berhasil!")
                } else {
                    _phoneAuthStep.value = PhoneAuthStep.PROFILE_SETUP
                    showNotice("Nomor terverifikasi! Lengkapi profil Anda.")
                }
            }.onFailure { err ->
                _authError.value = err.message ?: "Verifikasi gagal."
            }
        }
    }

    fun completePhoneRegistration(displayName: String, username: String, bio: String, avatar: String) {
        val phone = "${_countryCode.value}${_phoneNumberInput.value.trim()}"
        if (displayName.isBlank()) {
            _authError.value = "Nama tampilan tidak boleh kosong."
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = repository.completePhoneRegistration(phone, displayName, username, bio, avatar)
            _isAuthLoading.value = false
            result.onSuccess { profile ->
                _phoneAuthStep.value = PhoneAuthStep.INPUT_PHONE
                _dispatchedOtpNotice.value = null
                showNotice("Selamat datang di Element Messenger, ${profile.name}!")
            }.onFailure { err ->
                _authError.value = err.message ?: "Pendaftaran gagal."
            }
        }
    }

    fun backToPhoneInput() {
        _phoneAuthStep.value = PhoneAuthStep.INPUT_PHONE
        _authError.value = null
        _otpInput.value = ""
    }

    fun updateProfileDetailed(name: String, username: String, bio: String, avatar: String, newPhone: String? = null) {
        viewModelScope.launch {
            val result = repository.updateProfileDetailed(name, username, bio, avatar, newPhone)
            result.onSuccess {
                showNotice("Perubahan profil berhasil disimpan!")
            }.onFailure { err ->
                showNotice("Gagal menyimpan profil: ${err.message}")
            }
        }
    }

    fun inspectContact(contact: Contact?) {
        _inspectedContact.value = contact
    }

    fun createGroup(groupName: String, avatar: String, memberContacts: List<Contact>) {
        if (groupName.isBlank()) {
            showNotice("Nama grup tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            val groupId = repository.createGroup(groupName, avatar, memberContacts)
            selectChat(groupId)
            setNavTab(MainNavTab.CHATS)
            showNotice("Grup \"$groupName\" berhasil dibuat!")
        }
    }

    fun createCommunity(name: String, description: String, avatar: String) {
        if (name.isBlank()) {
            showNotice("Nama komunitas tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            repository.createCommunity(name, description, avatar)
            showNotice("Komunitas \"$name\" berhasil dibuat!")
        }
    }

    fun createEvent(title: String, dateTime: String, description: String, invitedMembers: List<String>) {
        if (title.isBlank()) {
            showNotice("Judul acara tidak boleh kosong")
            return
        }
        viewModelScope.launch {
            repository.createEvent(title, dateTime, description, invitedMembers)
            showNotice("Acara \"$title\" berhasil dibuat!")
        }
    }

    fun clearNotice() {
        _statusMessage.value = null
    }

    private fun generateChatId(u1: String, u2: String): String {
        val sorted = listOf(u1.lowercase(), u2.lowercase()).sorted()
        return "chat_${sorted[0]}_${sorted[1]}"
    }
}
