package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatConversation
import com.example.model.Community
import com.example.model.UserProfile
import com.example.ui.SidebarFilter
import com.example.ui.theme.*

@Composable
fun VerbaSidebar(
    userProfile: UserProfile,
    currentFilter: SidebarFilter,
    onFilterSelected: (SidebarFilter) -> Unit,
    conversations: List<ChatConversation>,
    selectedChatId: String?,
    onSelectChat: (String) -> Unit,
    communities: List<Community>,
    onSelectCommunity: (Community) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    messageRequestCount: Int,
    archiveCount: Int,
    onEditProfileClick: () -> Unit,
    onNewChatClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(VerbaDarkBackground)
    ) {
        // Spacious, clean WhatsApp-style conversation list
        val filteredConversations = remember(conversations, searchQuery) {
            if (searchQuery.isBlank()) conversations
            else conversations.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.lastMessage.contains(searchQuery, ignoreCase = true) ||
                it.participantUsername.contains(searchQuery, ignoreCase = true)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("sidebar_list")
        ) {
            // Direct Chat Items
            items(filteredConversations, key = { it.id }) { conv ->
                ConversationItemRow(
                    conversation = conv,
                    isSelected = conv.id == selectedChatId || "${conv.ownerUsername}_${conv.participantUsername}" == selectedChatId,
                    onClick = { onSelectChat(conv.id) }
                )
            }

            if (filteredConversations.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(VerbaPanel)
                                .border(1.dp, VerbaBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = VerbaTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Tidak ada chat yang cocok" else "Belum ada obrolan aktif",
                            color = VerbaTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Coba kata kunci lain" else "Mulai kirim pesan ke username lain",
                            color = VerbaTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubFilterPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    badgeCount: Int,
    testTag: String,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val bg = if (selected) activeColor.copy(alpha = 0.15f) else VerbaPanel
    val contentColor = if (selected) activeColor else VerbaTextSecondary
    val borderColor = if (selected) activeColor.copy(alpha = 0.4f) else VerbaBorder

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = if (selected) VerbaTextPrimary else VerbaTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (badgeCount > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(activeColor)
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CommunityChip(
    community: Community,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(VerbaPanel)
            .border(1.dp, VerbaBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = community.avatar,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = community.name,
                color = VerbaTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(VerbaOnline)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${community.activeCount} online",
                    color = VerbaTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ConversationItemRow(
    conversation: ChatConversation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
    val activeBorder = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, activeBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(10.dp)
            .testTag("conversation_row_${conversation.id}"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (conversation.participantUsername == "@verba_ai") MaterialTheme.colorScheme.primary
                        else VerbaPanel
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = conversation.avatar.ifBlank { "VC" },
                    color = if (conversation.participantUsername == "@verba_ai") Color.Black else VerbaTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            if (conversation.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(VerbaOnline)
                        .border(2.dp, VerbaSidebar, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = conversation.title,
                        color = VerbaTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (conversation.isGroup) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = "Grup",
                            tint = VerbaTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = conversation.lastMessageTime,
                    color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.primary else VerbaTextMuted,
                    fontSize = 11.sp,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (conversation.isTyping) "sedang mengetik..." else conversation.lastMessage.ifBlank { "Mulai obrolan" },
                    color = if (conversation.isTyping) MaterialTheme.colorScheme.primary else VerbaTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = if (conversation.isTyping) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (conversation.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = VerbaTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    if (conversation.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = conversation.unreadCount.toString(),
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
