package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.model.CallDirection
import com.example.model.CallRecord
import com.example.model.CallType
import com.example.ui.theme.*

@Composable
fun CallsScreen(
    callHistory: List<CallRecord>,
    onStartCall: (String, String, CallType) -> Unit,
    onClearCallHistory: () -> Unit = {},
    onDeleteCall: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCallLinkDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("calls_screen")
    ) {
        // WhatsApp Calls Top Header
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
                    text = "Panggilan",
                    color = VerbaTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Panggilan suara & video terenkripsi P2P",
                    color = VerbaTextSecondary,
                    fontSize = 12.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (callHistory.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("clear_call_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Bersihkan Riwayat Panggilan",
                            tint = VerbaTextSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.PhoneInTalk,
                    contentDescription = null,
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // WhatsApp "Buat tautan panggilan" (Create Call Link)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(VerbaGlassCard)
                        .border(1.dp, VerbaGlassBorder, RoundedCornerShape(12.dp))
                        .clickable { showCallLinkDialog = true }
                        .padding(12.dp)
                        .testTag("create_call_link_item"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(WhatsAppEmerald),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Buat tautan",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buat tautan panggilan",
                            color = VerbaTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Bagikan tautan untuk panggilan Element Messenger Anda",
                            color = VerbaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "TERBARU",
                    color = VerbaTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Real Call Logs
            items(callHistory, key = { it.id }) { call ->
                CallItemRow(
                    record = call,
                    onVoiceCall = { onStartCall(call.contactName, call.contactAvatar, CallType.VOICE) },
                    onVideoCall = { onStartCall(call.contactName, call.contactAvatar, CallType.VIDEO) },
                    onDelete = { onDeleteCall(call.id) }
                )
            }

            if (callHistory.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(VerbaGlassCard)
                                .border(1.dp, VerbaGlassBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhoneCallback, null, tint = VerbaTextMuted, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Belum ada riwayat panggilan",
                            color = VerbaTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Panggilan suara atau video Anda akan muncul di sini",
                            color = VerbaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    if (showCallLinkDialog) {
        AlertDialog(
            onDismissRequest = { showCallLinkDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Link, null, tint = WhatsAppEmerald)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tautan Panggilan Element Messenger", color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Siapa pun dengan tautan ini dapat bergabung ke panggilan video/suara terenkripsi:",
                        color = VerbaTextSecondary,
                        fontSize = 13.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VerbaGlassCard)
                            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "https://call.whatsapp.com/v/w72-${(1000..9999).random()}",
                            color = WhatsAppEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCallLinkDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppEmerald)
                ) {
                    Text("Salin Tautan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCallLinkDialog = false }) {
                    Text("Tutup", color = VerbaTextSecondary)
                }
            }
        )
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DeleteSweep, null, tint = VerbaDanger)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Riwayat Panggilan?", color = VerbaTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus semua bekas log panggilan untuk akun ini? Tindakan ini tidak dapat dibatalkan.",
                    color = VerbaTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearCallHistory()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerbaDanger)
                ) {
                    Text("Hapus Semua", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun CallItemRow(
    record: CallRecord,
    onVoiceCall: () -> Unit,
    onVideoCall: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VerbaGlassCard)
            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(VerbaPanel)
                    .border(1.dp, VerbaGlassBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = record.contactAvatar,
                    color = WhatsAppEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = record.contactName,
                    color = VerbaTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (icon, tint) = when (record.direction) {
                        CallDirection.INCOMING -> Icons.Default.CallReceived to VerbaOnline
                        CallDirection.OUTGOING -> Icons.Default.CallMade to WhatsAppEmerald
                        CallDirection.MISSED -> Icons.Default.CallMissed to VerbaDanger
                    }
                    Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${record.timestamp} • ${record.durationText}",
                        color = VerbaTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = if (record.type == CallType.VIDEO) onVideoCall else onVoiceCall,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (record.type == CallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = "Panggil",
                    tint = WhatsAppEmerald,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opsi",
                        tint = VerbaTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(VerbaPanel)
                ) {
                    DropdownMenuItem(
                        text = { Text("Hapus dari log", color = VerbaDanger, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = VerbaDanger, modifier = Modifier.size(18.dp)) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
