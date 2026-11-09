package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.UserProfile
import com.example.model.UserStatusStory
import com.example.ui.theme.*

@Composable
fun StatusScreen(
    userProfile: UserProfile,
    statusList: List<UserStatusStory>,
    myStatus: UserStatusStory?,
    onAddStatus: (String, Long) -> Unit,
    onReplyToStatus: (authorUsername: String, authorName: String, replyText: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddStatusDialog by remember { mutableStateOf(false) }
    var activeViewingStatus by remember { mutableStateOf<UserStatusStory?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("status_screen")
    ) {
        // WhatsApp Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaGlassSurface)
                .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Pembaruan",
                    color = VerbaTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Status cerita & saluran terkini",
                    color = VerbaTextSecondary,
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { showAddStatusDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .testTag("add_status_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Buat Status",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // "Status Saya" (My Status) Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VerbaGlassCard)
                        .border(1.dp, VerbaGlassBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            if (myStatus != null) activeViewingStatus = myStatus
                            else showAddStatusDialog = true
                        }
                        .padding(12.dp)
                        .testTag("my_status_item"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(2.dp, if (myStatus != null) WhatsAppEmerald else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.avatar.ifBlank { "VC" },
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        // Green Plus Badge if no status or to add more
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(WhatsAppEmerald)
                                .border(1.5.dp, VerbaCard, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Status",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Status saya",
                            color = VerbaTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = myStatus?.let { "Diposting ${it.timestamp}" } ?: "Ketuk untuk menambahkan pembaruan status",
                            color = VerbaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Divider & Section Header
            item {
                Text(
                    text = "PEMBARUAN TERKINI",
                    color = VerbaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (statusList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.DonutLarge,
                                contentDescription = null,
                                tint = VerbaTextMuted,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Tidak ada pembaruan status terkini",
                                color = VerbaTextSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pembaruan status dari kontak Anda akan muncul di sini",
                                color = VerbaTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            items(statusList, key = { it.id }) { story ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VerbaGlassCard)
                        .border(1.dp, VerbaGlassBorder, RoundedCornerShape(12.dp))
                        .clickable { activeViewingStatus = story }
                        .padding(12.dp)
                        .testTag("status_item_${story.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with WhatsApp Status Ring
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, if (!story.isSeen) WhatsAppEmerald else VerbaTextMuted.copy(alpha = 0.5f), CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color(story.colorHex).copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = story.authorAvatar,
                            color = VerbaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = story.authorName,
                            color = VerbaTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = story.timestamp,
                            color = VerbaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // Add Status Dialog
    if (showAddStatusDialog) {
        var statusText by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf(0xFF005C4B) }
        val colors = listOf(0xFF005C4B, 0xFF128C7E, 0xFF1E3A8A, 0xFF581C87, 0xFF831843)

        AlertDialog(
            onDismissRequest = { showAddStatusDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Buat Status VerbaChat", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Preview Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(selectedColor))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = statusText.ifBlank { "Ketik status Anda di sini..." },
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = statusText,
                        onValueChange = { statusText = it },
                        placeholder = { Text("Tuliskan status atau pikiran Anda...", color = VerbaTextMuted, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = VerbaInputBg,
                            unfocusedContainerColor = VerbaInputBg,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = VerbaGlassBorder,
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Color Palette Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        colors.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(hex))
                                    .border(2.dp, if (selectedColor == hex) Color.White else Color.Transparent, CircleShape)
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (statusText.isNotBlank()) {
                            onAddStatus(statusText.trim(), selectedColor)
                            showAddStatusDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Kirim Status", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddStatusDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }

    // Fullscreen WhatsApp Status Viewer Dialog
    activeViewingStatus?.let { story ->
        StatusViewerDialog(
            story = story,
            onDismiss = { activeViewingStatus = null },
            onReply = { reply ->
                onReplyToStatus(story.username, story.authorName, reply)
                activeViewingStatus = null
            }
        )
    }
}

@Composable
fun StatusViewerDialog(
    story: UserStatusStory,
    onDismiss: () -> Unit,
    onReply: (String) -> Unit
) {
    val progress = remember { Animatable(0f) }
    var replyText by remember { mutableStateOf("") }

    LaunchedEffect(story.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 6000, easing = LinearEasing)
        )
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(story.colorHex))
                .testTag("status_viewer_fullscreen")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Meta: Progress Bar + Author Header
                Column {
                    // Animated Story Progress Bar
                    LinearProgressIndicator(
                        progress = { progress.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(story.authorAvatar, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(story.authorName, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(story.timestamp, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                        }
                    }
                }

                // Middle: Big Story Text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = story.content,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 34.sp
                    )
                }

                // Bottom: WhatsApp Status Reply Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Balas status ini...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Black.copy(alpha = 0.35f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.35f),
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onReply(replyText.trim())
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim Balasan",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
