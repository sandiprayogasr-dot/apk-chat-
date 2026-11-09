package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MessageType
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onSelectAttachment: (MessageType, String, String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = VerbaPanel,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = VerbaBorder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("attachment_bottom_sheet")
        ) {
            Text(
                text = "Bagikan Lampiran",
                color = VerbaTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentOptionItem(
                    icon = Icons.Default.Image,
                    label = "Foto",
                    bgColor = Color(0xFF2979FF),
                    testTag = "attach_photo",
                    onClick = {
                        onSelectAttachment(MessageType.IMAGE, "photo_${System.currentTimeMillis()}.jpg", "2.1 MB")
                        onDismiss()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.InsertDriveFile,
                    label = "Dokumen",
                    bgColor = Color(0xFF00C896),
                    testTag = "attach_doc",
                    onClick = {
                        onSelectAttachment(MessageType.FILE, "Spesifikasi_Proyek_v2.pdf", "1.4 MB")
                        onDismiss()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.Person,
                    label = "Kontak",
                    bgColor = Color(0xFFFF9100),
                    testTag = "attach_contact",
                    onClick = {
                        onSelectAttachment(MessageType.TEXT, "Kartu Kontak: Sarah Jenkins (+62 812-3344-5566)", "")
                        onDismiss()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.LocationOn,
                    label = "Lokasi",
                    bgColor = Color(0xFFE91E63),
                    testTag = "attach_location",
                    onClick = {
                        onSelectAttachment(MessageType.TEXT, "📍 Lokasi Terkini: Gedung Cyber 2, Jakarta Selatan", "")
                        onDismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AttachmentOptionItem(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(bgColor.copy(alpha = 0.2f))
                .border(1.dp, bgColor.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = bgColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = VerbaTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmojiPickerModal(
    onDismiss: () -> Unit,
    onEmojiSelected: (String) -> Unit
) {
    val emojis: List<String> = remember {
        listOf(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣",
            "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰",
            "😘", "😗", "😙", "😚", "😋", "😛", "😜", "🤪",
            "😎", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟",
            "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺",
            "😢", "😭", "😤", "😠", "😡", "🤯", "😳", "🥵",
            "👍", "👎", "👌", "✌️", "🤞", "🤟", "🤘", "🤙",
            "👏", "🙌", "👐", "🤲", "🤝", "🙏", "❤️", "🔥",
            "✨", "🎉", "🚀", "💡", "💯", "⭐", "✅", "☕"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VerbaPanel,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Pilih Emoji",
                color = VerbaTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Box(modifier = Modifier.height(240.dp)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = emojis) { emoji: String ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(VerbaCard)
                                .clickable {
                                    onEmojiSelected(emoji)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
