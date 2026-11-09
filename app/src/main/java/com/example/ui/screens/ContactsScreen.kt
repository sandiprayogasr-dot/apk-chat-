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
import com.example.model.CallType
import com.example.model.Contact
import com.example.ui.components.formatMaskedPhone
import com.example.ui.theme.*

@Composable
fun ContactsScreen(
    contacts: List<Contact>,
    onOpenChatWithContact: (Contact) -> Unit,
    onStartCall: (String, String, CallType) -> Unit,
    onAddContact: (String, String, String) -> Unit,
    onToggleBlock: (Contact) -> Unit,
    onDeleteContact: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var inspectedContact by remember { mutableStateOf<Contact?>(null) }

    val filteredContacts = remember(contacts, searchQuery) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.username.contains(searchQuery, ignoreCase = true) ||
            it.phone.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VerbaDarkBackground)
            .testTag("contacts_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VerbaGlassSurface)
                .border(1.dp, VerbaGlassBorder, RoundedCornerShape(0.dp))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Kontak",
                    color = ElementEmerald,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${contacts.size} kontak tersimpan",
                    color = VerbaTextMuted,
                    fontSize = 12.sp
                )
            }
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_contact_button")
            ) {
                Icon(Icons.Default.PersonAdd, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari berdasarkan nama atau nomor HP...", color = VerbaTextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = ElementEmerald, modifier = Modifier.size(18.dp)) },
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
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .height(48.dp)
        )

        // Contacts List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredContacts, key = { it.id }) { contact ->
                ContactItemRow(
                    contact = contact,
                    onContactClick = { inspectedContact = contact },
                    onChatClick = { onOpenChatWithContact(contact) },
                    onVoiceCallClick = { onStartCall(contact.name, contact.avatar, CallType.VOICE) },
                    onVideoCallClick = { onStartCall(contact.name, contact.avatar, CallType.VIDEO) },
                    onBlockClick = { onToggleBlock(contact) },
                    onDeleteClick = { onDeleteContact(contact) }
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
                            text = if (searchQuery.isNotBlank()) "Kontak tidak ditemukan" else "Belum ada kontak tersimpan",
                            color = VerbaTextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    // Add Contact Dialog
    if (showAddDialog) {
        var nameInput by remember { mutableStateOf("") }
        var phoneInput by remember { mutableStateOf("") }
        var usernameInput by remember { mutableStateOf("") }
        var countryCode by remember { mutableStateOf("+62") }
        var showCountryPicker by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = { Text("Tambah Kontak Baru", color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
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
                                    Text(countryCode, color = VerbaTextPrimary, fontWeight = FontWeight.Bold)
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
                                            countryCode = c.code
                                            showCountryPicker = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it.filter { char -> char.isDigit() } },
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
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it.replace(" ", "") },
                        label = { Text("Username (Opsional)") },
                        placeholder = { Text("@username") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank() && phoneInput.isNotBlank()) {
                            val fullPhone = "$countryCode$phoneInput"
                            val cleanUser = if (usernameInput.isNotBlank()) usernameInput else "@${nameInput.lowercase().replace(" ", "_")}"
                            onAddContact(nameInput.trim(), fullPhone, cleanUser)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Simpan Kontak", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }

    // Contact Profile Details Modal
    inspectedContact?.let { contact ->
        AlertDialog(
            onDismissRequest = { inspectedContact = null },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ElementEmerald.copy(alpha = 0.2f))
                            .border(2.dp, ElementEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(contact.avatar, color = ElementEmerald, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(contact.name, color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text(contact.username, color = ElementEmerald, fontSize = 12.sp)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HorizontalDivider(color = VerbaGlassBorder)

                    // Phone & Privacy
                    Column {
                        Text("Nomor HP (Privasi Terjaga):", color = VerbaTextMuted, fontSize = 11.sp)
                        Text(formatMaskedPhone(contact.phone), color = VerbaTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }

                    // Bio
                    if (contact.bio.isNotBlank()) {
                        Column {
                            Text("Bio / Info:", color = VerbaTextMuted, fontSize = 11.sp)
                            Text(contact.bio, color = VerbaTextSecondary, fontSize = 13.sp)
                        }
                    }

                    // Status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (contact.isOnline) ElementEmerald else VerbaTextMuted)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (contact.isOnline) "Sedang Online" else "Terakhir aktif baru saja",
                            color = if (contact.isOnline) ElementEmerald else VerbaTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        inspectedContact = null
                        onOpenChatWithContact(contact)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kirim Pesan", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { inspectedContact = null }) {
                    Text("Tutup", color = VerbaTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ContactItemRow(
    contact: Contact,
    onContactClick: () -> Unit,
    onChatClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onVideoCallClick: () -> Unit,
    onBlockClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VerbaGlassCard)
            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onContactClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ElementEmerald.copy(alpha = 0.15f))
                        .border(1.dp, ElementEmerald.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.avatar,
                        color = ElementEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = contact.name,
                    color = if (contact.isBlocked) VerbaDanger else VerbaTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatMaskedPhone(contact.phone),
                    color = VerbaTextMuted,
                    fontSize = 12.sp
                )
                if (contact.bio.isNotBlank()) {
                    Text(
                        text = contact.bio,
                        color = VerbaTextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }

        // Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onChatClick, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Chat, contentDescription = "Buka Chat", tint = ElementEmerald, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onVoiceCallClick, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Call, contentDescription = "Panggil Suara", tint = VerbaTextSecondary, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onVideoCallClick, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Videocam, contentDescription = "Panggil Video", tint = VerbaTextSecondary, modifier = Modifier.size(18.dp))
            }

            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu Kontak", tint = VerbaTextMuted, modifier = Modifier.size(18.dp))
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(VerbaPanel)
                ) {
                    DropdownMenuItem(
                        text = { Text("Lihat Profil", color = VerbaTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = ElementEmerald) },
                        onClick = {
                            showMenu = false
                            onContactClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (contact.isBlocked) "Buka Blokir" else "Blokir Kontak", color = VerbaTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Block, null, tint = VerbaDanger) },
                        onClick = {
                            showMenu = false
                            onBlockClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus Kontak", color = VerbaDanger) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = VerbaDanger) },
                        onClick = {
                            showMenu = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}
