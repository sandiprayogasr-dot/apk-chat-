package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiMessage
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AiAssistantScreen(
    messages: List<AiMessage>,
    isThinking: Boolean,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var copiedNotice by remember { mutableStateOf(false) }

    val quickPrompts = listOf(
        "Rangkum obrolan penting hari ini",
        "Buatkan draf balasan profesional",
        "Terjemahkan pesan ke Bahasa Inggris",
        "Perbaiki tata bahasa tulisan ini"
    )

    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaPanel)
                .border(1.dp, VerbaBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(VerbaPrimary.copy(alpha = 0.15f))
                        .border(1.dp, VerbaPrimary.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "VerbaAI",
                        tint = VerbaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "VerbaAI Assistant",
                            color = VerbaTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(VerbaPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Smart AI",
                                color = VerbaPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Text(
                        text = if (isThinking) "Sedang menganalisis & mengetik..." else "Online • Siap membantu",
                        color = if (isThinking) VerbaPrimary else VerbaOnline,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = onClearChat,
                modifier = Modifier.testTag("btn_clear_ai_chat")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Bersihkan Percakapan",
                    tint = VerbaTextSecondary
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VerbaPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = VerbaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(
                        modifier = Modifier
                            .widthIn(max = 320.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                )
                            )
                            .background(if (isUser) VerbaOutgoingBubble else VerbaIncomingBubble)
                            .border(
                                1.dp,
                                if (isUser) VerbaPrimary.copy(alpha = 0.4f) else VerbaBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = msg.content,
                            color = if (isUser) VerbaOutgoingText else VerbaIncomingText,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = msg.timestamp,
                                color = if (isUser) Color.White.copy(alpha = 0.7f) else VerbaTextSecondary,
                                fontSize = 10.sp
                            )

                            if (!isUser) {
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(msg.content))
                                        copiedNotice = true
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Salin Teks",
                                        tint = VerbaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(VerbaPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = VerbaPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(VerbaIncomingBubble)
                                .border(1.dp, VerbaBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = VerbaPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("VerbaAI sedang berpikir...", color = VerbaTextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    onClick = { onSendMessage(prompt) },
                    color = VerbaCard,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VerbaBorder)
                ) {
                    Text(
                        text = prompt,
                        color = VerbaTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaPanel)
                .border(1.dp, VerbaBorder, RoundedCornerShape(0.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_ai_prompt"),
                placeholder = {
                    Text("Tanyakan sesuatu ke VerbaAI...", color = VerbaTextMuted, fontSize = 14.sp)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = VerbaInputBg,
                    unfocusedContainerColor = VerbaInputBg,
                    focusedBorderColor = VerbaPrimary,
                    unfocusedBorderColor = VerbaBorder,
                    focusedTextColor = VerbaTextPrimary,
                    unfocusedTextColor = VerbaTextPrimary
                ),
                shape = RoundedCornerShape(20.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            FloatingActionButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        onSendMessage(textInput.trim())
                        textInput = ""
                    }
                },
                containerColor = VerbaPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("btn_send_ai_prompt")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Kirim ke AI",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
