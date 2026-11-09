package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.VerbaDatabase
import com.example.data.local.entity.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VerbaRepository(private val context: Context) {
    private val database = VerbaDatabase.getInstance(context)
    private val userDao = database.userDao()
    private val chatDao = database.chatDao()
    private val contactDao = database.contactDao()
    private val callDao = database.callDao()
    private val communityDao = database.communityDao()
    private val eventDao = database.eventDao()
    private val repoScope = CoroutineScope(Dispatchers.IO)

    private val prefs: SharedPreferences = context.getSharedPreferences("verbachat_auth_prefs", Context.MODE_PRIVATE)

    // Current Logged-in User Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Active communities and marketplace items
    val communities: Flow<List<Community>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            communityDao.getCommunitiesForOwner(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    val events: Flow<List<CalendarEvent>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            eventDao.getEventsForOwner(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    private val _marketItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val marketItems: StateFlow<List<MarketItem>> = _marketItems.asStateFlow()

    // Typing statuses per chat
    private val _typingChats = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val typingChats: StateFlow<Map<String, Boolean>> = _typingChats.asStateFlow()

    init {
        repoScope.launch {
            // Check existing login session
            val savedUsername = prefs.getString("KEY_USERNAME", null)
            if (!savedUsername.isNullOrBlank()) {
                val existing = userDao.getUserByUsername(savedUsername)
                if (existing != null) {
                    _userProfile.value = UserProfile(
                        username = existing.username,
                        name = existing.displayName,
                        bio = existing.bio,
                        phone = existing.phone,
                        avatar = existing.avatar,
                        status = existing.status,
                        isLoggedIn = true
                    )
                }
            }

            // Seed available directory users if empty
            seedInitialDirectoryIfNeeded()
        }
    }

    // Dynamic conversations scoped to the currently logged in user
    val activeConversations: Flow<List<ChatConversation>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            chatDao.getActiveConversations(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    val archivedConversations: Flow<List<ChatConversation>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            chatDao.getArchivedConversations(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    val messageRequests: Flow<List<ChatConversation>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            chatDao.getMessageRequests(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    val contacts: Flow<List<Contact>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            contactDao.getAllContacts(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    val callHistory: Flow<List<CallRecord>> = _userProfile.flatMapLatest { profile ->
        if (!profile.isLoggedIn || profile.username.isBlank()) {
            flowOf(emptyList())
        } else {
            callDao.getCallsForOwner(profile.username).map { list ->
                list.map { it.toModel() }
            }
        }
    }

    fun getMessagesForChat(chatId: String): Flow<List<ChatMessage>> {
        val currentUsername = _userProfile.value.username
        return chatDao.getMessagesForChat(chatId).map { entities ->
            entities.map { entity ->
                entity.toModel(currentUsername)
            }
        }
    }

    // ==========================================
    // PHONE NUMBER AUTHENTICATION (OTP SYSTEM)
    // ==========================================
    private var pendingPhone: String? = null
    private var pendingOtp: String? = null
    private var pendingOtpExpiry: Long = 0L

    suspend fun requestPhoneOtp(phoneNumber: String): Result<String> {
        val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
        if (cleanPhone.length < 8) {
            return Result.failure(Exception("Nomor HP terlalu pendek. Masukkan nomor yang valid."))
        }
        val otp = (100000..999999).random().toString()
        pendingPhone = cleanPhone
        pendingOtp = otp
        pendingOtpExpiry = System.currentTimeMillis() + (5 * 60 * 1000)
        delay(400) // Simulate secure SMS gateway dispatch
        return Result.success(otp)
    }

    suspend fun verifyPhoneOtp(phoneNumber: String, inputOtp: String): Result<Boolean> {
        val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
        val now = System.currentTimeMillis()

        if (cleanPhone != pendingPhone) {
            return Result.failure(Exception("Nomor telepon tidak cocok dengan permintaan OTP."))
        }
        if (now > pendingOtpExpiry) {
            return Result.failure(Exception("Kode OTP telah kedaluwarsa. Silakan minta kode baru."))
        }
        if (inputOtp.trim() != pendingOtp) {
            return Result.failure(Exception("Kode OTP salah. Periksa kembali 6 digit kode."))
        }

        pendingOtp = null

        var existingUser = userDao.getUserByPhone(cleanPhone)
        if (existingUser == null) {
            if (cleanPhone.endsWith("81234567890") || cleanPhone.contains("81234567890")) {
                existingUser = userDao.getUserByUsername("@sandi")
            } else if (cleanPhone.endsWith("81398765432") || cleanPhone.contains("81398765432")) {
                existingUser = userDao.getUserByUsername("@sarah")
            }
        }

        if (existingUser != null) {
            userDao.setUserOnlineStatus(existingUser.username, true, System.currentTimeMillis())
            prefs.edit().putString("KEY_USERNAME", existingUser.username).apply()

            val profile = UserProfile(
                username = existingUser.username,
                name = existingUser.displayName,
                email = existingUser.email,
                bio = existingUser.bio,
                phone = existingUser.phone.ifBlank { cleanPhone },
                avatar = existingUser.avatar,
                status = existingUser.status,
                pin = existingUser.pin,
                isFingerprintEnabled = existingUser.isFingerprintEnabled,
                isLoggedIn = true
            )
            _userProfile.value = profile
            seedDefaultContactsForUser(existingUser.username)
            return Result.success(true)
        } else {
            return Result.success(false)
        }
    }

    suspend fun completePhoneRegistration(
        phoneNumber: String,
        displayName: String,
        usernameInput: String,
        bio: String,
        avatar: String
    ): Result<UserProfile> {
        val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
        val rawUsername = usernameInput.trim().removePrefix("@").lowercase().replace(" ", "_")
        val cleanUsername = "@${if (rawUsername.isBlank()) "user_${(1000..9999).random()}" else rawUsername}"

        val existing = userDao.getUserByUsername(cleanUsername)
        val finalUsername = if (existing != null) "${cleanUsername}_${(100..999).random()}" else cleanUsername

        val cleanName = if (displayName.isBlank()) "Pengguna Element" else displayName.trim()
        val initials = if (avatar.isNotBlank()) avatar else generateAvatarInitials(cleanName)

        val entity = UserAccountEntity(
            username = finalUsername,
            displayName = cleanName,
            avatar = initials,
            bio = if (bio.isBlank()) "Ada, menggunakan Element Messenger ✨" else bio.trim(),
            phone = cleanPhone,
            status = "Online",
            isOnline = true,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        userDao.insertOrUpdateUser(entity)
        prefs.edit().putString("KEY_USERNAME", finalUsername).apply()

        val profile = UserProfile(
            username = finalUsername,
            name = cleanName,
            bio = entity.bio,
            phone = cleanPhone,
            avatar = initials,
            status = "Online",
            isLoggedIn = true
        )
        _userProfile.value = profile
        seedDefaultContactsForUser(finalUsername)
        return Result.success(profile)
    }

    // ==========================================
    // AUTH / LOGIN SYSTEM (EMAIL/USERNAME + PASSWORD)
    // ==========================================
    suspend fun loginWithCredentials(
        identifierInput: String,
        passwordInput: String,
        rememberMe: Boolean = true
    ): Result<UserProfile> {
        val trimmed = identifierInput.trim()
        val cleanUsername = if (trimmed.startsWith("@")) trimmed else "@$trimmed"

        var user = userDao.getUserByUsernameOrEmail(trimmed)
        if (user == null) {
            user = userDao.getUserByUsernameOrEmail(cleanUsername)
        }

        if (user == null) {
            // Check default demo account seed
            if (trimmed.equals("sandi", ignoreCase = true) || trimmed.equals("sandi@verbachat.com", ignoreCase = true) || trimmed.equals("@sandi", ignoreCase = true)) {
                val demoUser = UserAccountEntity(
                    username = "@sandi",
                    displayName = "Sandi Prayoga",
                    email = "sandi@verbachat.com",
                    password = "password123",
                    avatar = "SP",
                    bio = "Building VerbaChat with modern dark UI ⚡",
                    phone = "",
                    status = "Online",
                    pin = "123456",
                    isFingerprintEnabled = true,
                    isOnline = true,
                    lastActiveTimestamp = System.currentTimeMillis()
                )
                userDao.insertOrUpdateUser(demoUser)
                user = demoUser
            } else {
                return Result.failure(Exception("Email/username atau password salah."))
            }
        }

        if (user.password.isNotBlank() && user.password != passwordInput) {
            return Result.failure(Exception("Email/username atau password salah."))
        }

        userDao.setUserOnlineStatus(user.username, true, System.currentTimeMillis())

        if (rememberMe) {
            prefs.edit().putString("KEY_USERNAME", user.username).apply()
        }

        val profile = UserProfile(
            username = user.username,
            name = user.displayName,
            email = user.email,
            bio = user.bio,
            phone = user.phone,
            avatar = user.avatar,
            status = user.status,
            pin = user.pin,
            isFingerprintEnabled = user.isFingerprintEnabled,
            isLoggedIn = true
        )
        _userProfile.value = profile

        seedDefaultContactsForUser(user.username)
        return Result.success(profile)
    }

    suspend fun registerUser(
        fullName: String,
        usernameInput: String,
        emailInput: String,
        passwordInput: String
    ): Result<UserProfile> {
        val cleanUsername = if (usernameInput.trim().startsWith("@")) usernameInput.trim() else "@${usernameInput.trim()}"
        val existing = userDao.getUserByUsername(cleanUsername)
        if (existing != null) {
            return Result.failure(Exception("Username sudah digunakan. Silakan gunakan username lain."))
        }
        val existingEmail = userDao.getUserByEmail(emailInput.trim())
        if (existingEmail != null) {
            return Result.failure(Exception("Email sudah terdaftar. Silakan gunakan email lain atau Sign In."))
        }

        val cleanName = fullName.trim()
        val initials = generateAvatarInitials(cleanName)
        val entity = UserAccountEntity(
            username = cleanUsername,
            displayName = cleanName,
            email = emailInput.trim(),
            password = passwordInput,
            avatar = initials,
            bio = "Hey there! I am using VerbaChat.",
            phone = "",
            status = "Online",
            pin = "123456",
            isFingerprintEnabled = false,
            isOnline = true,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        userDao.insertOrUpdateUser(entity)

        val profile = UserProfile(
            username = cleanUsername,
            name = cleanName,
            email = emailInput.trim(),
            bio = entity.bio,
            avatar = initials,
            status = "Online",
            pin = "123456",
            isFingerprintEnabled = false,
            isLoggedIn = true
        )
        _userProfile.value = profile
        prefs.edit().putString("KEY_USERNAME", cleanUsername).apply()

        seedDefaultContactsForUser(cleanUsername)
        return Result.success(profile)
    }

    suspend fun updatePin(newPin: String) {
        val current = _userProfile.value
        if (current.username.isNotBlank()) {
            val user = userDao.getUserByUsername(current.username)
            if (user != null) {
                userDao.insertOrUpdateUser(user.copy(pin = newPin))
            }
            _userProfile.value = current.copy(pin = newPin)
        }
    }

    suspend fun setFingerprintEnabled(enabled: Boolean) {
        val current = _userProfile.value
        if (current.username.isNotBlank()) {
            val user = userDao.getUserByUsername(current.username)
            if (user != null) {
                userDao.insertOrUpdateUser(user.copy(isFingerprintEnabled = enabled))
            }
            _userProfile.value = current.copy(isFingerprintEnabled = enabled)
        }
    }

    suspend fun resetPassword(email: String): Result<String> {
        delay(600) // mock network delay
        return Result.success("Password reset instructions have been sent.")
    }

    suspend fun login(usernameInput: String, displayNameInput: String, bioInput: String = "Menggunakan VerbaChat ✨") {
        val cleanUsername = if (usernameInput.trim().startsWith("@")) usernameInput.trim() else "@${usernameInput.trim()}"
        val cleanName = if (displayNameInput.isBlank()) cleanUsername.removePrefix("@").replaceFirstChar { it.uppercase() } else displayNameInput.trim()
        val initials = generateAvatarInitials(cleanName)

        val userEntity = UserAccountEntity(
            username = cleanUsername,
            displayName = cleanName,
            avatar = initials,
            bio = bioInput,
            phone = "",
            status = "Online",
            isOnline = true,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        userDao.insertOrUpdateUser(userEntity)

        // Save to SharedPreferences
        prefs.edit().putString("KEY_USERNAME", cleanUsername).apply()

        _userProfile.value = UserProfile(
            username = cleanUsername,
            name = cleanName,
            bio = bioInput,
            phone = "",
            avatar = initials,
            status = "Online",
            isLoggedIn = true
        )

        seedDefaultContactsForUser(cleanUsername)
    }

    private suspend fun seedDefaultContactsForUser(cleanUsername: String) {
        val existingContacts = contactDao.getAllContacts(cleanUsername).firstOrNull() ?: emptyList()
        if (existingContacts.isEmpty()) {
            val defaultContacts = listOf(
                ContactEntity(
                    id = "c_${cleanUsername}_jenny",
                    ownerUsername = cleanUsername,
                    name = "Jenny Wilson",
                    username = "@jenny_w",
                    avatar = "JW",
                    bio = "UX Designer & Product Enthusiast",
                    phone = "+62 821-1122-3344",
                    isOnline = true,
                    isBlocked = false
                ),
                ContactEntity(
                    id = "c_${cleanUsername}_andrew",
                    ownerUsername = cleanUsername,
                    name = "Andrew Miller",
                    username = "@andrew_m",
                    avatar = "AM",
                    bio = "Mobile Developer • Kotlin & Compose",
                    phone = "+62 856-5566-7788",
                    isOnline = true,
                    isBlocked = false
                ),
                ContactEntity(
                    id = "c_${cleanUsername}_maria",
                    ownerUsername = cleanUsername,
                    name = "Maria Garcia",
                    username = "@maria_g",
                    avatar = "MG",
                    bio = "Photography & Digital Arts 🎨",
                    phone = "+62 878-9900-1122",
                    isOnline = false,
                    isBlocked = false
                ),
                ContactEntity(
                    id = "c_${cleanUsername}_sarah",
                    ownerUsername = cleanUsername,
                    name = "Sarah Jenkins",
                    username = "@sarah",
                    avatar = "SJ",
                    bio = "UI/UX Designer • Element Messenger",
                    phone = "+62 813-9876-5432",
                    isOnline = true,
                    isBlocked = false
                ),
                ContactEntity(
                    id = "c_${cleanUsername}_ai",
                    ownerUsername = cleanUsername,
                    name = "Element AI Assistant",
                    username = "@element_ai",
                    avatar = "AI",
                    bio = "Asisten Cerdas Element Messenger",
                    phone = "+62 800-1122-3344",
                    isOnline = true,
                    isBlocked = false
                )
            )
            contactDao.insertContacts(defaultContacts)
        }
    }

    suspend fun logout() {
        val currentUsername = _userProfile.value.username
        if (currentUsername.isNotBlank()) {
            userDao.setUserOnlineStatus(currentUsername, false, System.currentTimeMillis())
        }
        prefs.edit().remove("KEY_USERNAME").apply()
        _userProfile.value = UserProfile(isLoggedIn = false)
    }

    // ==========================================
    // CHAT & MESSAGING
    // ==========================================
    suspend fun startOrGetConversation(targetUsername: String, targetName: String, targetAvatar: String): String {
        val currentUsername = _userProfile.value.username
        val cleanTargetUsername = if (targetUsername.startsWith("@")) targetUsername else "@$targetUsername"
        val chatId = generateChatId(currentUsername, cleanTargetUsername)

        val convId = "${currentUsername}_${cleanTargetUsername}"
        val existing = chatDao.getConversationById(convId)

        if (existing == null) {
            val now = System.currentTimeMillis()
            val newConv = ChatConversationEntity(
                id = convId,
                ownerUsername = currentUsername,
                participantUsername = cleanTargetUsername,
                title = targetName,
                avatar = targetAvatar.ifBlank { generateAvatarInitials(targetName) },
                isGroup = false,
                unreadCount = 0,
                lastMessage = "Mulai obrolan baru",
                lastMessageTime = "Baru saja",
                lastMessageTimestamp = now,
                isPinned = false,
                isArchived = false,
                isRequest = false,
                isOnline = true,
                memberCount = 2
            )
            chatDao.insertConversation(newConv)
        }

        return chatId
    }

    suspend fun sendMessage(
        chatId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT,
        replyTo: MessageReply? = null,
        durationSeconds: Int? = null,
        fileName: String? = null,
        fileSize: String? = null
    ) {
        val current = _userProfile.value
        val now = System.currentTimeMillis()
        val messageId = "msg_${now}_${(1000..9999).random()}"

        val messageEntity = ChatMessageEntity(
            id = messageId,
            chatId = chatId,
            senderId = current.username,
            senderName = current.name,
            content = content,
            messageType = messageType.name,
            status = MessageStatus.SENT.name,
            timestamp = now,
            isOutgoing = true,
            replyToId = replyTo?.id,
            replyToSender = replyTo?.senderName,
            replyToContent = replyTo?.content,
            isPinned = false,
            isEdited = false,
            reactionsRaw = "",
            durationSeconds = durationSeconds,
            fileName = fileName,
            fileSize = fileSize
        )
        chatDao.insertMessage(messageEntity)

        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeString = timeFormatter.format(Date(now))
        val preview = when (messageType) {
            MessageType.VOICE -> "🎤 Pesan suara (${durationSeconds ?: 3}s)"
            MessageType.IMAGE -> "📷 Foto"
            MessageType.FILE -> "📄 Dokumen: $fileName"
            else -> content
        }

        // Update conversation record for the sender
        val participantUsername = getParticipantFromChatId(chatId, current.username)
        val senderConvId = "${current.username}_${participantUsername}"
        val senderConv = chatDao.getConversationById(senderConvId)
        if (senderConv != null) {
            chatDao.updateConversation(
                senderConv.copy(
                    lastMessage = preview,
                    lastMessageTime = timeString,
                    lastMessageTimestamp = now
                )
            )
        }

        // Also ensure or update conversation record for the receiver (so if receiver logs in, they see it!)
        val receiverConvId = "${participantUsername}_${current.username}"
        val receiverConv = chatDao.getConversationById(receiverConvId)
        if (receiverConv != null) {
            chatDao.updateConversation(
                receiverConv.copy(
                    lastMessage = preview,
                    lastMessageTime = timeString,
                    lastMessageTimestamp = now,
                    unreadCount = receiverConv.unreadCount + 1
                )
            )
        } else if (participantUsername.isNotBlank() && participantUsername != current.username) {
            chatDao.insertConversation(
                ChatConversationEntity(
                    id = receiverConvId,
                    ownerUsername = participantUsername,
                    participantUsername = current.username,
                    title = current.name,
                    avatar = current.avatar,
                    isGroup = false,
                    unreadCount = 1,
                    lastMessage = preview,
                    lastMessageTime = timeString,
                    lastMessageTimestamp = now,
                    isPinned = false,
                    isArchived = false,
                    isRequest = false,
                    isOnline = true,
                    memberCount = 2
                )
            )
        }

        // Status update progression
        repoScope.launch {
            delay(600)
            chatDao.updateMessage(messageEntity.copy(status = MessageStatus.DELIVERED.name))
            delay(800)
            chatDao.updateMessage(messageEntity.copy(status = MessageStatus.READ.name))

            // If chatting with VerbaAI Assistant, trigger AI response
            if (participantUsername == "@verba_ai") {
                triggerAiReply(chatId, content)
            }
        }
    }

    private suspend fun triggerAiReply(chatId: String, query: String) {
        _typingChats.value = _typingChats.value + (chatId to true)
        delay(1200)
        _typingChats.value = _typingChats.value + (chatId to false)

        val aiResponse = when {
            query.contains("ringkas", ignoreCase = true) ->
                "💡 **Ringkasan VerbaAI:**\n• Diskusi teknis telah tersinkronisasi.\n• Riwayat panggilan dan chat bersih sesuai akun aktif.\n• Sistem login multi-username real-time berjalan stabil."
            query.contains("terjemah", ignoreCase = true) ->
                "🌐 **Terjemahan (ID ⇄ EN):**\n\"$query\"\n\n*\"All communications are encrypted and isolated per active username.\"*"
            query.contains("perbaiki", ignoreCase = true) ->
                "✍️ **Saran Kalimat Profesional:**\n\"Selamat siang, mohon konfirmasi terkait update berkas agar dapat segera kami tindak lanjuti. Terima kasih.\""
            query.contains("halo", ignoreCase = true) || query.contains("hai", ignoreCase = true) ->
                "Halo! Saya **VerbaAI Assistant**. Akun Anda kini aktif secara real-time. Ada yang bisa saya bantu terkait percakapan atau produktivitas Anda?"
            else ->
                "Saya VerbaAI siap membantu! Anda dapat mengetik pesan, mengirim pesan suara, atau mencoba menerjemahkan pesan kapan saja."
        }

        val now = System.currentTimeMillis()
        val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now))
        val currentUsername = _userProfile.value.username

        chatDao.insertMessage(
            ChatMessageEntity(
                id = "ai_${now}",
                chatId = chatId,
                senderId = "@verba_ai",
                senderName = "VerbaAI Assistant",
                content = aiResponse,
                messageType = MessageType.TEXT.name,
                status = MessageStatus.READ.name,
                timestamp = now,
                isOutgoing = false,
                replyToId = null,
                replyToSender = null,
                replyToContent = null,
                isPinned = false,
                isEdited = false,
                reactionsRaw = "",
                durationSeconds = null,
                fileName = null,
                fileSize = null
            )
        )

        val convId = "${currentUsername}_@verba_ai"
        val conv = chatDao.getConversationById(convId)
        if (conv != null) {
            chatDao.updateConversation(
                conv.copy(
                    lastMessage = aiResponse.take(60),
                    lastMessageTime = timeString,
                    lastMessageTimestamp = now
                )
            )
        }
    }

    suspend fun toggleReaction(message: ChatMessage, emoji: String) {
        val existingReactions = message.reactions.toMutableList()
        val index = existingReactions.indexOfFirst { it.emoji == emoji }
        if (index >= 0) {
            val current = existingReactions[index]
            if (current.reactedByMe) {
                if (current.count <= 1) {
                    existingReactions.removeAt(index)
                } else {
                    existingReactions[index] = current.copy(count = current.count - 1, reactedByMe = false)
                }
            } else {
                existingReactions[index] = current.copy(count = current.count + 1, reactedByMe = true)
            }
        } else {
            existingReactions.add(MessageReaction(emoji = emoji, count = 1, reactedByMe = true))
        }

        val raw = existingReactions.joinToString(";") { "${it.emoji}:${it.count}:${it.reactedByMe}" }
        val entity = ChatMessageEntity(
            id = message.id,
            chatId = message.chatId,
            senderId = message.senderId,
            senderName = message.senderName,
            content = message.content,
            messageType = message.messageType.name,
            status = message.status.name,
            timestamp = message.timestamp,
            isOutgoing = message.isOutgoing,
            replyToId = message.replyTo?.id,
            replyToSender = message.replyTo?.senderName,
            replyToContent = message.replyTo?.content,
            isPinned = message.isPinned,
            isEdited = message.isEdited,
            reactionsRaw = raw,
            durationSeconds = message.durationSeconds,
            fileName = message.fileName,
            fileSize = message.fileSize
        )
        chatDao.updateMessage(entity)
    }

    suspend fun togglePinMessage(message: ChatMessage) {
        val entity = ChatMessageEntity(
            id = message.id,
            chatId = message.chatId,
            senderId = message.senderId,
            senderName = message.senderName,
            content = message.content,
            messageType = message.messageType.name,
            status = message.status.name,
            timestamp = message.timestamp,
            isOutgoing = message.isOutgoing,
            replyToId = message.replyTo?.id,
            replyToSender = message.replyTo?.senderName,
            replyToContent = message.replyTo?.content,
            isPinned = !message.isPinned,
            isEdited = message.isEdited,
            reactionsRaw = message.reactions.joinToString(";") { "${it.emoji}:${it.count}:${it.reactedByMe}" },
            durationSeconds = message.durationSeconds,
            fileName = message.fileName,
            fileSize = message.fileSize
        )
        chatDao.updateMessage(entity)
    }

    suspend fun editMessage(messageId: String, newContent: String, originalMessage: ChatMessage) {
        val entity = ChatMessageEntity(
            id = originalMessage.id,
            chatId = originalMessage.chatId,
            senderId = originalMessage.senderId,
            senderName = originalMessage.senderName,
            content = newContent,
            messageType = originalMessage.messageType.name,
            status = originalMessage.status.name,
            timestamp = originalMessage.timestamp,
            isOutgoing = originalMessage.isOutgoing,
            replyToId = originalMessage.replyTo?.id,
            replyToSender = originalMessage.replyTo?.senderName,
            replyToContent = originalMessage.replyTo?.content,
            isPinned = originalMessage.isPinned,
            isEdited = true,
            reactionsRaw = originalMessage.reactions.joinToString(";") { "${it.emoji}:${it.count}:${it.reactedByMe}" },
            durationSeconds = originalMessage.durationSeconds,
            fileName = originalMessage.fileName,
            fileSize = originalMessage.fileSize
        )
        chatDao.updateMessage(entity)
    }

    suspend fun deleteMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    suspend fun clearMessagesForChat(chatId: String) {
        chatDao.deleteMessagesForChat(chatId)
        val current = _userProfile.value.username
        val participant = getParticipantFromChatId(chatId, current)
        val convId = "${current}_${participant}"
        val conv = chatDao.getConversationById(convId)
        if (conv != null) {
            chatDao.updateConversation(conv.copy(lastMessage = "Obrolan telah dibersihkan", lastMessageTime = "Baru saja"))
        }
    }

    suspend fun setArchived(convId: String, archived: Boolean) {
        chatDao.setArchived(convId, archived)
    }

    suspend fun acceptMessageRequest(convId: String) {
        chatDao.acceptRequest(convId)
    }

    suspend fun deleteConversation(convId: String) {
        val conv = chatDao.getConversationById(convId)
        if (conv != null) {
            val chatId = generateChatId(conv.ownerUsername, conv.participantUsername)
            chatDao.deleteMessagesForChat(chatId)
            chatDao.deleteConversation(convId)
        }
    }

    // Contacts
    suspend fun addContact(name: String, phone: String, username: String) {
        val currentUsername = _userProfile.value.username
        val cleanUsername = if (username.startsWith("@")) username else "@$username"
        val id = "c_${currentUsername}_${System.currentTimeMillis()}"
        val initials = generateAvatarInitials(name)
        val entity = ContactEntity(
            id = id,
            ownerUsername = currentUsername,
            name = name,
            username = cleanUsername,
            avatar = initials,
            bio = "Kontak VerbaChat",
            phone = phone,
            isOnline = true,
            isBlocked = false
        )
        contactDao.insertContact(entity)
    }

    suspend fun toggleBlockContact(contactId: String, currentBlocked: Boolean) {
        contactDao.setBlocked(contactId, !currentBlocked)
    }

    suspend fun deleteContact(contactId: String) {
        contactDao.deleteContact(contactId)
    }

    // Calls
    suspend fun addCallRecord(contactName: String, contactAvatar: String, type: CallType, direction: CallDirection, durationText: String) {
        val currentUsername = _userProfile.value.username
        val now = System.currentTimeMillis()
        val timeString = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(now))
        val callEntity = CallRecordEntity(
            id = "call_${now}_${(100..999).random()}",
            ownerUsername = currentUsername,
            contactName = contactName,
            contactAvatar = contactAvatar,
            type = type.name,
            direction = direction.name,
            timestamp = timeString,
            durationText = durationText,
            timestampMillis = now
        )
        callDao.insertCall(callEntity)
    }

    suspend fun clearAllCalls() {
        val currentUsername = _userProfile.value.username
        if (currentUsername.isNotBlank()) {
            callDao.deleteAllCallsForOwner(currentUsername)
        }
    }

    suspend fun deleteCall(callId: String) {
        callDao.deleteCall(callId)
    }

    suspend fun toggleCommunityJoin(communityId: String) {
        val current = communityDao.getCommunitiesForOwner(_userProfile.value.username).firstOrNull()?.find { it.id == communityId }
        if (current != null) {
            communityDao.setJoined(communityId, !current.isJoined)
        }
    }

    suspend fun toggleCommunityMute(communityId: String) {
        val current = communityDao.getCommunitiesForOwner(_userProfile.value.username).firstOrNull()?.find { it.id == communityId }
        if (current != null) {
            communityDao.setMuted(communityId, !current.notificationMuted)
        }
    }

    suspend fun createGroup(groupName: String, avatar: String, memberContacts: List<Contact>): String {
        val currentUsername = _userProfile.value.username
        val now = System.currentTimeMillis()
        val groupId = "group_${now}_${(100..999).random()}"
        val groupAvatar = if (avatar.isNotBlank()) avatar else "👥"

        val memberUsernames = memberContacts.map { it.username } + currentUsername
        val groupConv = ChatConversationEntity(
            id = groupId,
            ownerUsername = currentUsername,
            participantUsername = groupId,
            title = groupName.trim(),
            avatar = groupAvatar,
            isGroup = true,
            unreadCount = 0,
            lastMessage = "Anda membuat grup \"${groupName.trim()}\"",
            lastMessageTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(now)),
            lastMessageTimestamp = now,
            isPinned = false,
            isArchived = false,
            isRequest = false,
            isOnline = true,
            memberCount = memberUsernames.size
        )
        chatDao.insertConversation(groupConv)

        val msg = ChatMessageEntity(
            id = "msg_${now}",
            chatId = groupId,
            senderId = currentUsername,
            senderName = _userProfile.value.name,
            content = "Grup \"${groupName.trim()}\" berhasil dibuat.",
            messageType = "SYSTEM",
            status = "READ",
            timestamp = now,
            isOutgoing = true,
            replyToId = null,
            replyToSender = null,
            replyToContent = null,
            isPinned = false,
            isEdited = false,
            reactionsRaw = "",
            durationSeconds = null,
            fileName = null,
            fileSize = null
        )
        chatDao.insertMessage(msg)
        return groupId
    }

    suspend fun createCommunity(name: String, description: String, avatar: String): Community {
        val currentUsername = _userProfile.value.username
        val id = "comm_${System.currentTimeMillis()}_${(100..999).random()}"
        val commEntity = CommunityEntity(
            id = id,
            ownerUsername = currentUsername,
            name = name.trim(),
            description = description.trim(),
            avatar = avatar.ifBlank { "🌐" },
            memberCount = 1,
            isJoined = true,
            notificationMuted = false
        )
        communityDao.insertCommunity(commEntity)
        return commEntity.toModel()
    }

    suspend fun createEvent(title: String, dateTime: String, description: String, invitedMembers: List<String>): CalendarEvent {
        val currentUsername = _userProfile.value.username
        val id = "event_${System.currentTimeMillis()}_${(100..999).random()}"
        val entity = EventEntity(
            id = id,
            ownerUsername = currentUsername,
            title = title.trim(),
            dateTime = dateTime.trim(),
            description = description.trim(),
            invitedMembers = invitedMembers.joinToString(", ")
        )
        eventDao.insertEvent(entity)
        return CalendarEvent(
            id = id,
            title = title.trim(),
            dateTime = dateTime.trim(),
            description = description.trim(),
            invitedMembers = invitedMembers
        )
    }

    suspend fun updateProfile(name: String, bio: String, status: String) {
        val current = _userProfile.value
        val updated = current.copy(name = name, bio = bio, status = status)
        _userProfile.value = updated

        userDao.insertOrUpdateUser(
            UserAccountEntity(
                username = current.username,
                displayName = name,
                avatar = current.avatar,
                bio = bio,
                phone = current.phone,
                status = status,
                isOnline = true,
                lastActiveTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateProfileDetailed(
        name: String,
        username: String,
        bio: String,
        avatar: String,
        newPhone: String? = null
    ): Result<UserProfile> {
        val current = _userProfile.value
        if (current.username.isBlank()) return Result.failure(Exception("Pengguna belum login."))

        val cleanUsername = if (username.startsWith("@")) username.trim() else "@${username.trim()}"
        val finalPhone = if (!newPhone.isNullOrBlank()) newPhone.trim() else current.phone

        val updated = current.copy(
            name = name.trim(),
            username = cleanUsername,
            bio = bio.trim(),
            avatar = avatar.ifBlank { generateAvatarInitials(name) },
            phone = finalPhone
        )
        _userProfile.value = updated

        val entity = UserAccountEntity(
            username = cleanUsername,
            displayName = name.trim(),
            avatar = updated.avatar,
            bio = bio.trim(),
            phone = finalPhone,
            status = current.status,
            isOnline = true,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        userDao.insertOrUpdateUser(entity)
        prefs.edit().putString("KEY_USERNAME", cleanUsername).apply()
        return Result.success(updated)
    }

    // ==========================================
    // SEEDING DIRECTORY ONLY (NO FAKE CHATS)
    // ==========================================
    private suspend fun seedInitialDirectoryIfNeeded() {
        // Groups/communities start empty in Room database as requested

        _marketItems.value = listOf(
            MarketItem("mkt_1", "Verba UI Kit Pro (Figma)", "Rp 199.000", "Desain", "Sarah Jenkins", "SJ", 4.9f, "Sistem desain lengkap dengan 200+ komponen dark mode dan autolayout siap pakai.", "DESIGN"),
            MarketItem("mkt_2", "Mechanical Keyboard 75% Custom", "Rp 1.450.000", "Gadgets", "David Pratama", "DP", 4.8f, "Gateron Oil King switch, gasket mount, lubed, koneksi tri-mode Bluetooth & dongle.", "KEYBOARD"),
            MarketItem("mkt_3", "Domain Premium verba.tech", "Rp 750.000", "Domain", "Rico Santoso", "RS", 5.0f, "Brandable tech domain siap transfer push via Cloudflare Registrar.", "DOMAIN"),
            MarketItem("mkt_4", "Noise Cancelling Headphones ANC", "Rp 2.100.000", "Audio", "Elena Rostova", "ER", 4.9f, "Baterai 40 jam, lossless audio codec LDAC, mikrofon studio crystal-clear.", "HEADPHONES")
        )
    }

    private fun generateAvatarInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
            parts.size == 1 && parts[0].isNotEmpty() -> parts[0].take(2).uppercase()
            else -> "VC"
        }
    }

    private fun generateChatId(user1: String, user2: String): String {
        val sorted = listOf(user1.lowercase(), user2.lowercase()).sorted()
        return "chat_${sorted[0]}_${sorted[1]}"
    }

    private fun getParticipantFromChatId(chatId: String, currentUsername: String): String {
        if (!chatId.startsWith("chat_")) return ""
        val parts = chatId.removePrefix("chat_").split("_")
        return parts.firstOrNull { !it.equals(currentUsername.removePrefix("@"), ignoreCase = true) }?.let {
            if (it.startsWith("@")) it else "@$it"
        } ?: ""
    }

    private fun ChatMessageEntity.toModel(currentUsername: String): ChatMessage {
        val parsedReactions = if (reactionsRaw.isBlank()) {
            emptyList()
        } else {
            reactionsRaw.split(";").mapNotNull { item ->
                val parts = item.split(":")
                if (parts.size >= 2) {
                    MessageReaction(
                        emoji = parts[0],
                        count = parts[1].toIntOrNull() ?: 1,
                        reactedByMe = parts.getOrNull(2)?.toBooleanStrictOrNull() ?: false
                    )
                } else null
            }
        }

        val reply = if (replyToId != null && replyToSender != null && replyToContent != null) {
            MessageReply(replyToId, replyToSender, replyToContent)
        } else null

        val isSenderMe = senderId.equals(currentUsername, ignoreCase = true)

        return ChatMessage(
            id = id,
            chatId = chatId,
            senderId = senderId,
            senderName = senderName,
            content = content,
            messageType = MessageType.valueOf(messageType),
            status = MessageStatus.valueOf(status),
            timestamp = timestamp,
            isOutgoing = isSenderMe,
            replyTo = reply,
            isPinned = isPinned,
            isEdited = isEdited,
            reactions = parsedReactions,
            durationSeconds = durationSeconds,
            fileName = fileName,
            fileSize = fileSize
        )
    }

    private fun ChatConversationEntity.toModel(): ChatConversation {
        return ChatConversation(
            id = id,
            ownerUsername = ownerUsername,
            participantUsername = participantUsername,
            title = title,
            avatar = avatar,
            isGroup = isGroup,
            unreadCount = unreadCount,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            lastMessageTimestamp = lastMessageTimestamp,
            isPinned = isPinned,
            isArchived = isArchived,
            isRequest = isRequest,
            isOnline = isOnline,
            memberCount = memberCount,
            isTyping = _typingChats.value[id] == true
        )
    }

    private fun ContactEntity.toModel(): Contact {
        return Contact(
            id = id,
            name = name,
            username = username,
            avatar = avatar,
            bio = bio,
            phone = phone,
            isOnline = isOnline,
            isBlocked = isBlocked
        )
    }

    private fun CallRecordEntity.toModel(): CallRecord {
        return CallRecord(
            id = id,
            contactName = contactName,
            contactAvatar = contactAvatar,
            type = CallType.valueOf(type),
            direction = CallDirection.valueOf(direction),
            timestamp = timestamp,
            durationText = durationText
        )
    }

    private fun CommunityEntity.toModel(): Community {
        return Community(
            id = id,
            name = name,
            description = description,
            avatar = avatar,
            memberCount = memberCount,
            activeCount = (memberCount / 2).coerceAtLeast(1),
            isJoined = isJoined,
            notificationMuted = notificationMuted
        )
    }

    private fun EventEntity.toModel(): CalendarEvent {
        return CalendarEvent(
            id = id,
            title = title,
            dateTime = dateTime,
            description = description,
            invitedMembers = invitedMembers.split(", ").filter { it.isNotBlank() },
            createdAt = createdAt
        )
    }
}
