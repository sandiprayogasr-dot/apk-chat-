package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.Contact
import com.example.ui.screens.supportedCountries
import com.example.ui.theme.*

@Composable
fun NewChatDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    contacts: List<Contact>,
    onSelectContact: (username: String, name: String, avatar: String) -> Unit,
    onStartChatByUsername: (username: String) -> Unit,
    onCreateGroup: (name: String, avatar: String, members: List<Contact>) -> Unit = { _, _, _ -> },
    onAddContact: (name: String, phone: String, username: String) -> Unit = { _, _, _ -> },
    onCreateCommunity: (name: String, description: String, avatar: String) -> Unit = { _, _, _ -> },
    onCreateEvent: (title: String, dateTime: String, description: String, members: List<String>) -> Unit = { _, _, _, _ -> }
) {
    if (!isOpen) return

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Dialog state for the 4 action items
    var showNewGroupDialog by remember { mutableStateOf(false) }
    var showNewContactDialog by remember { mutableStateOf(false) }
    var showNewCommunityDialog by remember { mutableStateOf(false) }
    var showNewEventDialog by remember { mutableStateOf(false) }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VerbaDarkBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VerbaGlassSurface)
                        .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Tutup", tint = ElementEmerald)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Pilih kontak",
                                color = VerbaTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${contacts.size} kontak tersimpan",
                                color = VerbaTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(Icons.Default.Search, contentDescription = "Cari", tint = VerbaTextSecondary)
                        }
                    }
                }

                // Search Bar when active
                if (isSearchActive) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari kontak atau nomor HP...", color = VerbaTextMuted, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = ElementEmerald, modifier = Modifier.size(18.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, tint = VerbaTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = VerbaInputBg,
                            unfocusedContainerColor = VerbaInputBg,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder,
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .height(48.dp)
                    )
                }

                // WhatsApp-Style Action Menu + Contacts List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("new_chat_list")
                ) {
                    // Item A: Grup Baru
                    item {
                        ActionMenuItem(
                            icon = Icons.Default.Group,
                            title = "Grup baru",
                            onClick = { showNewGroupDialog = true }
                        )
                    }

                    // Item B: Kontak Baru
                    item {
                        ActionMenuItem(
                            icon = Icons.Default.PersonAdd,
                            title = "Kontak baru",
                            onClick = { showNewContactDialog = true }
                        )
                    }

                    // Item C: Komunitas Baru
                    item {
                        ActionMenuItem(
                            icon = Icons.Default.Groups,
                            title = "Komunitas baru",
                            onClick = { showNewCommunityDialog = true }
                        )
                    }

                    // Item D: Acara Baru
                    item {
                        ActionMenuItem(
                            icon = Icons.Default.Event,
                            title = "Acara baru",
                            onClick = { showNewEventDialog = true }
                        )
                    }

                    // Section Divider & Label
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "KONTAK DI ELEMENT MESSENGER",
                                color = VerbaTextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Contacts List Items
                    items(filteredContacts, key = { it.id }) { contact ->
                        ContactRow(
                            contact = contact,
                            onClick = {
                                onSelectContact(contact.username, contact.name, contact.avatar)
                                onDismiss()
                            }
                        )
                    }

                    if (filteredContacts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank()) "Kontak tidak ditemukan" else "Belum ada kontak",
                                    color = VerbaTextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // ==========================================
    // MODAL A: GRUP BARU
    // ==========================================
    if (showNewGroupDialog) {
        var groupName by remember { mutableStateOf("") }
        var selectedMembers by remember { mutableStateOf(setOf<String>()) }
        val groupAvatars = listOf("👥", "🚀", "💡", "⚡", "🎮", "💬")
        var selectedAvatar by remember { mutableStateOf("👥") }

        AlertDialog(
            onDismissRequest = { showNewGroupDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Buat Grup Baru", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Nama Grup") },
                        placeholder = { Text("contoh: Tim Proyek Mobile") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("new_group_name_input")
                    )

                    Text("Pilih Ikon Grup:", color = VerbaTextMuted, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        groupAvatars.forEach { icon ->
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedAvatar == icon) ElementEmerald.copy(alpha = 0.3f) else VerbaDarkBackground)
                                    .border(1.dp, if (selectedAvatar == icon) ElementEmerald else VerbaGlassBorder, CircleShape)
                                    .clickable { selectedAvatar = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 18.sp)
                            }
                        }
                    }

                    Text("Pilih Anggota (${selectedMembers.size}):", color = VerbaTextMuted, fontSize = 12.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        contacts.forEach { contact ->
                            val isChecked = selectedMembers.contains(contact.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedMembers = if (isChecked) selectedMembers - contact.id else selectedMembers + contact.id
                                    }
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedMembers = if (checked) selectedMembers + contact.id else selectedMembers - contact.id
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = ElementEmerald)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(contact.name, color = VerbaTextPrimary, fontSize = 13.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            val chosenContacts = contacts.filter { selectedMembers.contains(it.id) }
                            onCreateGroup(groupName.trim(), selectedAvatar, chosenContacts)
                            showNewGroupDialog = false
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Buat Grup", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewGroupDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }

    // ==========================================
    // MODAL B: KONTAK BARU
    // ==========================================
    if (showNewContactDialog) {
        var contactName by remember { mutableStateOf("") }
        var contactPhone by remember { mutableStateOf("") }
        var contactCountryCode by remember { mutableStateOf("+62") }
        var contactUsername by remember { mutableStateOf("") }
        var showCountryPicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showNewContactDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Tambah Kontak Baru", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = contactName,
                        onValueChange = { contactName = it },
                        label = { Text("Nama Kontak") },
                        placeholder = { Text("contoh: Budi Santoso") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("add_contact_name_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Surface(
                                onClick = { showCountryPicker = true },
                                color = VerbaInputBg,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, VerbaGlassBorder),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(contactCountryCode, color = VerbaTextPrimary, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ArrowDropDown, null, tint = VerbaTextSecondary)
                                }
                            }
                            DropdownMenu(
                                expanded = showCountryPicker,
                                onDismissRequest = { showCountryPicker = false },
                                modifier = Modifier.background(VerbaPanel)
                            ) {
                                supportedCountries.forEach { c ->
                                    DropdownMenuItem(
                                        text = { Text("${c.flag} ${c.name} (${c.code})", color = VerbaTextPrimary) },
                                        onClick = {
                                            contactCountryCode = c.code
                                            showCountryPicker = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it.filter { char -> char.isDigit() } },
                            label = { Text("Nomor HP") },
                            placeholder = { Text("812-3456-7890") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VerbaTextPrimary,
                                unfocusedTextColor = VerbaTextPrimary,
                                focusedBorderColor = ElementEmerald,
                                unfocusedBorderColor = VerbaGlassBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("add_contact_phone_input")
                        )
                    }

                    OutlinedTextField(
                        value = contactUsername,
                        onValueChange = { contactUsername = it.replace(" ", "") },
                        label = { Text("Username (Opsional)") },
                        placeholder = { Text("@budi") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (contactName.isNotBlank() && contactPhone.isNotBlank()) {
                            val fullPhone = "$contactCountryCode$contactPhone"
                            val cleanUser = if (contactUsername.isNotBlank()) contactUsername else "@${contactName.lowercase().replace(" ", "_")}"
                            onAddContact(contactName.trim(), fullPhone, cleanUser)
                            showNewContactDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Simpan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewContactDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }

    // ==========================================
    // MODAL C: KOMUNITAS BARU
    // ==========================================
    if (showNewCommunityDialog) {
        var commName by remember { mutableStateOf("") }
        var commDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewCommunityDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Buat Komunitas Baru", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Komunitas mengumpulkan anggota dalam grup-grup berbasis topik atau minat.",
                        color = VerbaTextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = commName,
                        onValueChange = { commName = it },
                        label = { Text("Nama Komunitas") },
                        placeholder = { Text("contoh: Komunitas Android Indonesia") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("new_community_name_input")
                    )
                    OutlinedTextField(
                        value = commDesc,
                        onValueChange = { commDesc = it },
                        label = { Text("Deskripsi Komunitas") },
                        placeholder = { Text("Jelaskan tujuan komunitas ini...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (commName.isNotBlank()) {
                            onCreateCommunity(commName.trim(), commDesc.trim(), "🌐")
                            showNewCommunityDialog = false
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Buat Komunitas", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewCommunityDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }

    // ==========================================
    // MODAL D: ACARA BARU
    // ==========================================
    if (showNewEventDialog) {
        var eventTitle by remember { mutableStateOf("") }
        var eventDateTime by remember { mutableStateOf("Besok, 10:00 WIB") }
        var eventDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewEventDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text("Buat Acara Baru", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Nama Acara") },
                        placeholder = { Text("contoh: Rapat Proyek Mingguan") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("new_event_title_input")
                    )
                    OutlinedTextField(
                        value = eventDateTime,
                        onValueChange = { eventDateTime = it },
                        label = { Text("Waktu & Tanggal") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = eventDesc,
                        onValueChange = { eventDesc = it },
                        label = { Text("Deskripsi Acara (Opsional)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (eventTitle.isNotBlank()) {
                            onCreateEvent(eventTitle.trim(), eventDateTime.trim(), eventDesc.trim(), listOf("Semua Anggota"))
                            showNewEventDialog = false
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Buat Acara", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewEventDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ActionMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // WhatsApp-style Emerald Circle Icon
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(ElementEmerald),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            color = VerbaTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ContactRow(
    contact: Contact,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ElementEmerald.copy(alpha = 0.2f))
                    .border(1.dp, ElementEmerald.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.avatar,
                    color = ElementEmerald,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            if (contact.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(ElementEmerald)
                        .border(2.dp, VerbaDarkBackground, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                color = VerbaTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            // Privacy-preserving masked phone number and bio
            val maskedPhone = formatMaskedPhone(contact.phone)
            val subtitle = if (contact.bio.isNotBlank()) "${maskedPhone.ifBlank { "" }} • ${contact.bio}" else maskedPhone
            Text(
                text = subtitle,
                color = VerbaTextMuted,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

fun formatMaskedPhone(phone: String): String {
    if (phone.isBlank()) return ""
    val digits = phone.filter { it.isDigit() }
    return if (digits.length >= 8) {
        val start = digits.take(4)
        val end = digits.takeLast(3)
        "+${digits.take(2)} $start-•••-$end"
    } else {
        phone
    }
}
