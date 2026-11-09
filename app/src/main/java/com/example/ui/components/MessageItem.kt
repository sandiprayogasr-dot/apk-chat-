package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    message: ChatMessage,
    onReactionClick: (String) -> Unit,
    onReplyClick: () -> Unit,
    onPinClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onForwardClick: () -> Unit,
    onShowNotice: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showContextMenu by remember { mutableStateOf(false) }
    var showReactionPicker by remember { mutableStateOf(false) }

    val isOutgoing = message.isOutgoing
    val alignment = if (isOutgoing) Alignment.End else Alignment.Start

    val bubbleBg = if (isOutgoing) VerbaOutgoingBubble else VerbaIncomingBubble
    val bubbleBorder = if (isOutgoing) VerbaOutgoingBorder else VerbaIncomingBorder

    // WhatsApp Message Bubble shape with tail corner
    val shape = if (isOutgoing) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 3.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 3.dp, bottomEnd = 16.dp)
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 3.dp),
        horizontalAlignment = alignment
    ) {
        // Message Container with Long Press Context Menu & Liquid Glass Specular Edge
        Box(
            modifier = Modifier
                .widthIn(min = 84.dp, max = 340.dp)
                .clip(shape)
                .background(bubbleBg)
                .border(1.dp, bubbleBorder, shape)
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showContextMenu = true }
                )
                .padding(horizontal = 11.dp, vertical = 7.dp)
                .testTag("message_bubble_${message.id}")
        ) {
            Column {
                // Sender name if incoming
                if (!isOutgoing && message.senderName.isNotBlank()) {
                    Text(
                        text = message.senderName,
                        color = WhatsAppEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                // Quoted Reply preview (WhatsApp styled)
                message.replyTo?.let { reply ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(WhatsAppEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = reply.senderName,
                                color = WhatsAppEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = reply.content,
                                color = VerbaTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Message Body content according to type
                when (message.messageType) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.content,
                            color = VerbaTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    MessageType.VOICE -> {
                        AudioVoicePlayer(
                            durationSeconds = message.durationSeconds ?: 8,
                            isOutgoing = isOutgoing
                        )
                    }
                    MessageType.IMAGE -> {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(VerbaPanel)
                                    .border(1.dp, VerbaGlassBorder, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Gambar",
                                    tint = WhatsAppEmerald,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            if (message.content.isNotBlank() && message.content != "Foto dikirim") {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = message.content,
                                    color = VerbaTextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    MessageType.FILE -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(VerbaGlassCard)
                                .border(1.dp, VerbaGlassBorder, RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.InsertDriveFile,
                                contentDescription = "File",
                                tint = WhatsAppEmerald,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.fileName ?: "Dokumen.pdf",
                                    color = VerbaTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = message.fileSize ?: "2.4 MB",
                                    color = VerbaTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                // Bottom Meta: Edited badge, Pinned icon, Timestamp, WhatsApp Checkmarks
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (message.isEdited) {
                        Text(
                            text = "diedit",
                            color = VerbaTextMuted,
                            fontSize = 10.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                    if (message.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = VerbaTextMuted,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                    Text(
                        text = formattedTime,
                        color = VerbaTextMuted,
                        fontSize = 11.sp
                    )

                    if (isOutgoing) {
                        StatusIndicator(status = message.status)
                    }
                }
            }

            // Context Menu Dropdown
            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                modifier = Modifier.background(VerbaPanel)
            ) {
                DropdownMenuItem(
                    text = { Text("Balas", color = VerbaTextPrimary) },
                    leadingIcon = { Icon(Icons.Default.Reply, null, tint = WhatsAppEmerald) },
                    onClick = {
                        showContextMenu = false
                        onReplyClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Reaksi Emoji", color = VerbaTextPrimary) },
                    leadingIcon = { Icon(Icons.Default.AddReaction, null, tint = WhatsAppEmerald) },
                    onClick = {
                        showContextMenu = false
                        showReactionPicker = true
                    }
                )
                DropdownMenuItem(
                    text = { Text("Salin Teks", color = VerbaTextPrimary) },
                    leadingIcon = { Icon(Icons.Default.ContentCopy, null, tint = VerbaTextSecondary) },
                    onClick = {
                        showContextMenu = false
                        clipboardManager.setText(AnnotatedString(message.content))
                        onShowNotice("Teks disalin")
                    }
                )
                DropdownMenuItem(
                    text = { Text(if (message.isPinned) "Lepas Semat" else "Sematkan Pesan", color = VerbaTextPrimary) },
                    leadingIcon = { Icon(Icons.Default.PushPin, null, tint = VerbaTextSecondary) },
                    onClick = {
                        showContextMenu = false
                        onPinClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Teruskan Pesan", color = VerbaTextPrimary) },
                    leadingIcon = { Icon(Icons.Default.Forward, null, tint = VerbaTextSecondary) },
                    onClick = {
                        showContextMenu = false
                        onForwardClick()
                    }
                )
                if (isOutgoing && message.messageType == MessageType.TEXT) {
                    DropdownMenuItem(
                        text = { Text("Edit Pesan", color = VerbaTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = VerbaTextSecondary) },
                        onClick = {
                            showContextMenu = false
                            onEditClick()
                        }
                    )
                }
                DropdownMenuItem(
                    text = { Text("Hapus Pesan", color = VerbaDanger) },
                    leadingIcon = { Icon(Icons.Default.Delete, null, tint = VerbaDanger) },
                    onClick = {
                        showContextMenu = false
                        onDeleteClick()
                    }
                )
            }
        }

        // Reaction chips below bubble
        if (message.reactions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { reaction ->
                    val isMine = reaction.reactedByMe
                    val chipBg = if (isMine) WhatsAppEmerald.copy(alpha = 0.25f) else VerbaGlassCard
                    val border = if (isMine) WhatsAppEmerald.copy(alpha = 0.6f) else VerbaGlassBorder

                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(chipBg)
                            .border(1.dp, border, CircleShape)
                            .clickable { onReactionClick(reaction.emoji) }
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = reaction.emoji, fontSize = 12.sp)
                        if (reaction.count > 1) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = reaction.count.toString(),
                                color = if (isMine) WhatsAppEmerald else VerbaTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Reaction Picker Popup dialog
        if (showReactionPicker) {
            AlertDialog(
                onDismissRequest = { showReactionPicker = false },
                containerColor = VerbaPanel,
                shape = RoundedCornerShape(16.dp),
                title = { Text("Reaksi Emoji", color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("👍", "❤️", "😂", "😮", "😢", "🙏", "🔥", "✨").forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(VerbaGlassCard)
                                    .clickable {
                                        showReactionPicker = false
                                        onReactionClick(emoji)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showReactionPicker = false }) {
                        Text("Tutup", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    }
}

@Composable
private fun StatusIndicator(status: MessageStatus) {
    val blueCheck = WhatsAppBlueCheck // Iconic WhatsApp Blue Double Checkmark
    val muted = VerbaTextMuted

    when (status) {
        MessageStatus.SENDING -> {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Mengirim",
                tint = muted,
                modifier = Modifier.size(12.dp)
            )
        }
        MessageStatus.SENT -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Terkirim",
                tint = muted,
                modifier = Modifier.size(13.dp)
            )
        }
        MessageStatus.DELIVERED -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Tersampaikan",
                tint = muted,
                modifier = Modifier.size(14.dp)
            )
        }
        MessageStatus.READ -> {
            Icon(
                imageVector = Icons.Default.DoneAll,
                contentDescription = "Dibaca",
                tint = blueCheck,
                modifier = Modifier.size(14.dp)
            )
        }
        MessageStatus.FAILED -> {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Gagal",
                tint = VerbaDanger,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}
