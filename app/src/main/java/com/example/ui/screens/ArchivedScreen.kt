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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatConversation
import com.example.ui.theme.*

@Composable
fun ArchivedScreen(
    archivedChats: List<ChatConversation>,
    onOpenChat: (String) -> Unit,
    onUnarchive: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("archived_screen")
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaSidebar)
                .border(1.dp, VerbaBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Arsip Chat",
                    color = VerbaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${archivedChats.size} percakapan diarsipkan",
                    color = VerbaTextMuted,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.Archive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(archivedChats, key = { it.id }) { chat ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VerbaCard)
                        .border(1.dp, VerbaBorder, RoundedCornerShape(10.dp))
                        .clickable { onOpenChat(chat.id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(VerbaPanel),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(chat.avatar, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(chat.title, color = VerbaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text(chat.lastMessage, color = VerbaTextSecondary, fontSize = 12.sp, maxLines = 1)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onUnarchive(chat.id) }) {
                            Icon(Icons.Default.Unarchive, contentDescription = "Kembalikan dari Arsip", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { onDelete(chat.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus Percakapan", tint = VerbaDanger)
                        }
                    }
                }
            }

            if (archivedChats.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada obrolan yang diarsipkan",
                            color = VerbaTextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
