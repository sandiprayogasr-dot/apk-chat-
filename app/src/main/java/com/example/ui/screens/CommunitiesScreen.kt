package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.model.Community
import com.example.ui.theme.*

@Composable
fun CommunitiesScreen(
    communities: List<Community>,
    onToggleJoin: (Community) -> Unit,
    onToggleMute: (Community) -> Unit,
    onOpenCommunityChat: (Community) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCommunities = remember(communities, searchQuery) {
        if (searchQuery.isBlank()) communities
        else communities.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("communities_screen")
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
                    text = "Komunitas Verba",
                    color = VerbaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Temukan ruang diskusi publik & kolaborasi topik",
                    color = VerbaTextMuted,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari komunitas berdasarkan nama atau topik...", color = VerbaTextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = VerbaTextSecondary, modifier = Modifier.size(18.dp)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = VerbaInputBg,
                unfocusedContainerColor = VerbaInputBg,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = VerbaBorder,
                focusedTextColor = VerbaTextPrimary,
                unfocusedTextColor = VerbaTextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .height(48.dp)
        )

        // Community List or Empty State
        if (filteredCommunities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = VerbaTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Belum Ada Grup",
                        color = VerbaTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Grup atau komunitas yang baru dibuat akan muncul di sini.",
                        color = VerbaTextMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCommunities, key = { it.id }) { community ->
                    CommunityCard(
                        community = community,
                        onJoinToggle = { onToggleJoin(community) },
                        onMuteToggle = { onToggleMute(community) },
                        onOpenChat = { onOpenCommunityChat(community) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityCard(
    community: Community,
    onJoinToggle: () -> Unit,
    onMuteToggle: () -> Unit,
    onOpenChat: () -> Unit
) {
    Column(
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = community.avatar,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = community.name,
                        color = VerbaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${community.memberCount} anggota",
                            color = VerbaTextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(VerbaTextMuted))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${community.activeCount} online",
                            color = VerbaOnline,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            IconButton(onClick = onMuteToggle, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = if (community.notificationMuted) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                    contentDescription = "Mute",
                    tint = if (community.notificationMuted) VerbaTextMuted else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = community.description,
            color = VerbaTextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onJoinToggle,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (community.isJoined) VerbaBorder else MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (community.isJoined) "Keluar Komunitas" else "Gabung",
                    color = if (community.isJoined) VerbaTextSecondary else MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onOpenChat,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Buka Saluran", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
