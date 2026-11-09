package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun VerbaChatPane(
    conversation: ChatConversation?,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onSendAttachment: (MessageType, String, String) -> Unit,
    onStartVoiceRecording: () -> Unit,
    onCancelVoiceRecording: () -> Unit,
    onFinishVoiceRecording: () -> Unit,
    isRecordingVoice: Boolean,
    recordingSeconds: Int,
    replyingTo: MessageReply?,
    onCancelReply: () -> Unit,
    editingMessage: ChatMessage?,
    onCancelEdit: () -> Unit,
    onSubmitEdit: (String) -> Unit,
    forwardingMessage: ChatMessage?,
    onCancelForward: () -> Unit,
    onForwardToContact: (String, String) -> Unit,
    contactsList: List<Contact>,
    onStartCall: (String, String, CallType) -> Unit,
    onReactionClick: (ChatMessage, String) -> Unit,
    onReplyMessage: (ChatMessage) -> Unit,
    onPinMessage: (ChatMessage) -> Unit,
    onStartEditMessage: (ChatMessage) -> Unit,
    onDeleteMessage: (String) -> Unit,
    onSetForwarding: (ChatMessage) -> Unit,
    onShowNotice: (String) -> Unit,
    isMobileCompact: Boolean,
    onBackToList: () -> Unit,
    onClearChat: () -> Unit = {},
    onDeleteChat: () -> Unit = {},
    enterToSend: Boolean = true,
    aiAssistantEnabled: Boolean = true,
    smartReplyEnabled: Boolean = true,
    chatWallpaper: String = "dark",
    modifier: Modifier = Modifier
) {
    if (conversation == null) {
        // Empty State when no chat is selected on large screen
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0B141A),
                            Color(0xFF091217),
                            Color(0xFF070E12)
                        )
                    )
                )
                .testTag("empty_chat_state"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(VerbaGlassSurface)
                        .border(1.dp, VerbaGlassBorderGlow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = WhatsAppEmerald,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "VerbaChat",
                    color = VerbaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pesan pribadi Anda terenkripsi secara menyeluruh",
                    color = VerbaTextSecondary,
                    fontSize = 13.sp
                )
            }
        }
        return
    }

    var inputText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showEmojiModal by remember { mutableStateOf(false) }
    var inChatSearchActive by remember { mutableStateOf(false) }
    var inChatSearchText by remember { mutableStateOf("") }
    var showChatDetailsDialog by remember { mutableStateOf(false) }
    var showHeaderMenu by remember { mutableStateOf(false) }
    var showClearChatDialog by remember { mutableStateOf(false) }
    var showDeleteChatDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // When editing message changes, populate input text
    LaunchedEffect(editingMessage) {
        if (editingMessage != null) {
            inputText = editingMessage.content
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Filter messages if search is active
    val displayedMessages = remember(messages, inChatSearchText, inChatSearchActive) {
        if (inChatSearchActive && inChatSearchText.isNotBlank()) {
            messages.filter { it.content.contains(inChatSearchText, ignoreCase = true) }
        } else {
            messages
        }
    }

    val pinnedMessage = remember(messages) {
        messages.lastOrNull { it.isPinned }
    }

    // Liquid Glass Chat Wallpaper with subtle emerald gradient orbs
    val wallpaperGradient = remember(chatWallpaper) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0B141A),
                Color(0xFF09161B),
                Color(0xFF071114)
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(wallpaperGradient)
            .testTag("chat_pane_${conversation.id}")
    ) {
        // WhatsApp Top Header Bar (Liquid Glass Frosted)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaGlassSurface)
                .border(width = 1.dp, color = VerbaGlassBorder, shape = RoundedCornerShape(0.dp))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showChatDetailsDialog = true }
                    .padding(vertical = 2.dp)
            ) {
                if (isMobileCompact) {
                    IconButton(
                        onClick = onBackToList,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("chat_mobile_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Daftar",
                            tint = VerbaTextPrimary
                        )
                    }
                }

                // WhatsApp Avatar with Status Glow
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (conversation.participantUsername == "@verba_ai") WhatsAppEmerald
                                else VerbaPanel
                            )
                            .border(1.dp, VerbaGlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conversation.avatar.ifBlank { "VC" },
                            color = if (conversation.participantUsername == "@verba_ai") Color.Black else VerbaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    if (conversation.isOnline) {
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(VerbaOnline)
                                .border(1.5.dp, VerbaGlassSurface, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = conversation.title,
                        color = VerbaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = if (conversation.isTyping) "sedang mengetik..."
                               else if (conversation.participantUsername == "@verba_ai") "VerbaAI Assistant"
                               else if (conversation.isOnline) "online" else "terakhir dilihat baru-baru ini",
                        color = if (conversation.isTyping || conversation.isOnline) WhatsAppEmerald else VerbaTextSecondary,
                        fontSize = 11.sp,
                        fontStyle = if (conversation.isTyping) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal
                    )
                }
            }

            // WhatsApp Header Actions: Video, Call, More
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        onStartCall(conversation.title, conversation.avatar, CallType.VIDEO)
                    },
                    modifier = Modifier.size(38.dp).testTag("chat_header_video_call")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Panggilan Video",
                        tint = VerbaTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = {
                        onStartCall(conversation.title, conversation.avatar, CallType.VOICE)
                    },
                    modifier = Modifier.size(38.dp).testTag("chat_header_voice_call")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Panggilan Suara",
                        tint = VerbaTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        inChatSearchActive = !inChatSearchActive
                        if (!inChatSearchActive) inChatSearchText = ""
                    },
                    modifier = Modifier.size(38.dp).testTag("chat_header_search")
                ) {
                    Icon(
                        imageVector = if (inChatSearchActive) Icons.Default.Close else Icons.Default.Search,
                        contentDescription = "Cari",
                        tint = if (inChatSearchActive) WhatsAppEmerald else VerbaTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showHeaderMenu = true },
                        modifier = Modifier.size(38.dp).testTag("chat_header_info")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu Obrolan",
                            tint = VerbaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showHeaderMenu,
                        onDismissRequest = { showHeaderMenu = false },
                        modifier = Modifier.background(VerbaPanel)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Lihat Kontak", color = VerbaTextPrimary, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = WhatsAppEmerald, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                showHeaderMenu = false
                                showChatDetailsDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cari Pesan", color = VerbaTextPrimary, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, tint = VerbaTextSecondary, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                showHeaderMenu = false
                                inChatSearchActive = true
                            }
                        )
                        HorizontalDivider(color = VerbaGlassBorder)
                        DropdownMenuItem(
                            text = { Text("Bersihkan Pesan", color = VerbaWarning, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.CleaningServices, null, tint = VerbaWarning, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                showHeaderMenu = false
                                showClearChatDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Obrolan", color = VerbaDanger, fontSize = 13.sp) },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = VerbaDanger, modifier = Modifier.size(18.dp)) },
                            onClick = {
                                showHeaderMenu = false
                                showDeleteChatDialog = true
                            }
                        )
                    }
                }
            }
        }

        // In-chat Search Bar (if active)
        AnimatedVisibility(visible = inChatSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerbaGlassCard)
                    .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = inChatSearchText,
                    onValueChange = { inChatSearchText = it },
                    placeholder = { Text("Cari pesan di obrolan ini...", fontSize = 13.sp, color = VerbaTextMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = VerbaTextPrimary,
                        unfocusedTextColor = VerbaTextPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Pinned Message Banner
        pinnedMessage?.let { pinned ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerbaGlassCard)
                    .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                    .clickable {
                        val index = messages.indexOfFirst { it.id == pinned.id }
                        if (index >= 0) {
                            coroutineScope.launch { listState.animateScrollToItem(index) }
                        }
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
                    .testTag("pinned_message_banner"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pesan Disematkan • ${pinned.senderName}",
                        color = WhatsAppEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pinned.content,
                        color = VerbaTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = { onPinMessage(pinned) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Lepas semat",
                        tint = VerbaTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // WhatsApp Encryption Security Tag
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF182229).copy(alpha = 0.85f))
                    .border(1.dp, VerbaGlassBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔒 Pesan dan panggilan terenkripsi secara end-to-end.",
                    color = Color(0xFFFFD279),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Messages LazyColumn
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
                .testTag("messages_list"),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(displayedMessages, key = { it.id }) { message ->
                MessageItem(
                    message = message,
                    onReactionClick = { emoji -> onReactionClick(message, emoji) },
                    onReplyClick = { onReplyMessage(message) },
                    onPinClick = { onPinMessage(message) },
                    onEditClick = { onStartEditMessage(message) },
                    onDeleteClick = { onDeleteMessage(message.id) },
                    onForwardClick = { onSetForwarding(message) },
                    onShowNotice = onShowNotice
                )
            }

            // Typing Indicator
            if (conversation.isTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(VerbaIncomingBubble)
                                .border(1.dp, VerbaIncomingBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "✍️ ${conversation.title} sedang mengetik...",
                                color = WhatsAppEmerald,
                                fontSize = 12.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }
        }

        // AI Smart Reply Chips Bar (if enabled)
        if (aiAssistantEnabled && smartReplyEnabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VerbaGlassSurface)
                    .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "✨ Balas Cepat:",
                    color = WhatsAppEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                val smartReplies = listOf(
                    "Baik, segera saya kabari!",
                    "Oke siap 👍",
                    "Terima kasih banyak!",
                    "Nanti kita teleponan ya"
                )
                smartReplies.forEach { reply ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(VerbaGlassCard)
                            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(16.dp))
                            .clickable { onSendMessage(reply) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = reply, color = VerbaTextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        // Quoted Reply Preview Bar
        AnimatedVisibility(
            visible = replyingTo != null,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            replyingTo?.let { reply ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerbaGlassSurface)
                        .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(32.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(WhatsAppEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Membalas ${reply.senderName}",
                                color = WhatsAppEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = reply.content,
                                color = VerbaTextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = onCancelReply, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Batal", tint = VerbaTextSecondary)
                    }
                }
            }
        }

        // Voice Recording Bar
        AnimatedVisibility(visible = isRecordingVoice) {
            VoiceRecorderBar(
                recordingSeconds = recordingSeconds,
                onCancel = onCancelVoiceRecording,
                onSend = onFinishVoiceRecording,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }

        // WhatsApp Iconic Floating Pill Input Area
        if (!isRecordingVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main Translucent Pill
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(VerbaGlassCard)
                        .border(1.dp, VerbaGlassBorder, RoundedCornerShape(24.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji Button
                    IconButton(
                        onClick = { showEmojiModal = true },
                        modifier = Modifier.size(38.dp).testTag("chat_emoji_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentSatisfiedAlt,
                            contentDescription = "Emoji",
                            tint = VerbaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Message Text Field inside Pill
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = if (editingMessage != null) "Edit pesan..." else "Pesan",
                                color = VerbaTextMuted,
                                fontSize = 15.sp
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            cursorColor = WhatsAppEmerald
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = if (enterToSend) ImeAction.Send else ImeAction.Default
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    if (editingMessage != null) onSubmitEdit(inputText)
                                    else onSendMessage(inputText)
                                    inputText = ""
                                }
                            }
                        ),
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input")
                    )

                    // Paperclip Attachment Button
                    IconButton(
                        onClick = { showAttachmentSheet = true },
                        modifier = Modifier.size(38.dp).testTag("chat_attachment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Lampiran",
                            tint = VerbaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Quick Camera Button
                    IconButton(
                        onClick = {
                            onSendAttachment(MessageType.IMAGE, "camera_capture.jpg", "1.8 MB")
                            onShowNotice("Foto diambil & dikirim")
                        },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Kamera",
                            tint = VerbaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // WhatsApp Round Send / Mic Floating Action Button
                if (inputText.isBlank() && editingMessage == null) {
                    IconButton(
                        onClick = onStartVoiceRecording,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .testTag("chat_voice_record_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Rekam Pesan Suara",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                if (editingMessage != null) onSubmitEdit(inputText)
                                else onSendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = if (editingMessage != null) Icons.Default.Check else Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Attachment Bottom Sheet
    if (showAttachmentSheet) {
        AttachmentBottomSheet(
            onDismiss = { showAttachmentSheet = false },
            onSelectAttachment = onSendAttachment
        )
    }

    // Emoji Picker Modal
    if (showEmojiModal) {
        EmojiPickerModal(
            onDismiss = { showEmojiModal = false },
            onEmojiSelected = { emoji -> inputText += emoji }
        )
    }

    // Forward Dialog
    if (forwardingMessage != null) {
        AlertDialog(
            onDismissRequest = onCancelForward,
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Teruskan Pesan", color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "\"${forwardingMessage.content}\"",
                        color = VerbaTextMuted,
                        fontSize = 12.sp,
                        maxLines = 2,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(contactsList) { contact ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onForwardToContact(contact.name, contact.username) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(34.dp).clip(CircleShape).background(VerbaCard),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(contact.avatar, color = WhatsAppEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(contact.name, color = VerbaTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text(contact.username, color = VerbaTextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = onCancelForward) {
                    Text("Batal", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Chat Info Dialog
    if (showChatDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showChatDetailsDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(conversation.avatar, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(conversation.title, color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Username: ${conversation.participantUsername}", color = WhatsAppEmerald, fontSize = 13.sp)
                    Text(text = "Keamanan: Enkripsi End-to-End VerbaChat P2P", color = VerbaTextSecondary, fontSize = 12.sp)
                    Text(text = "Jumlah pesan tersimpan: ${messages.size}", color = VerbaTextMuted, fontSize = 12.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showChatDetailsDialog = false }) {
                    Text("Tutup", color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }

    // Clear Chat Confirmation
    if (showClearChatDialog) {
        AlertDialog(
            onDismissRequest = { showClearChatDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CleaningServices, null, tint = VerbaWarning)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Bersihkan Semua Pesan?", color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Semua pesan di obrolan dengan ${conversation.title} akan dihapus secara permanen.",
                    color = VerbaTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearChat()
                        showClearChatDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerbaWarning)
                ) {
                    Text("Bersihkan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearChatDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }

    // Delete Chat Confirmation
    if (showDeleteChatDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteChatDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, null, tint = VerbaDanger)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Obrolan Ini?", color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Percakapan dengan ${conversation.title} dan seluruh riwayat pesannya akan dihapus dari daftar chat Anda.",
                    color = VerbaTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteChat()
                        showDeleteChatDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerbaDanger)
                ) {
                    Text("Hapus Obrolan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteChatDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }
}
