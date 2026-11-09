package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.UserProfile
import com.example.ui.theme.*

@Composable
fun EditProfileDialog(
    isOpen: Boolean,
    userProfile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (name: String, username: String, bio: String, avatar: String, phone: String?) -> Unit,
    onRequestPhoneOtp: (phone: String, onOtpSent: (String) -> Unit) -> Unit
) {
    if (!isOpen) return

    var nameInput by remember { mutableStateOf(userProfile.name) }
    var usernameInput by remember { mutableStateOf(userProfile.username.removePrefix("@")) }
    var bioInput by remember { mutableStateOf(userProfile.bio) }
    var avatarInput by remember { mutableStateOf(userProfile.avatar.ifBlank { "EM" }) }
    var phoneInput by remember { mutableStateOf(userProfile.phone) }

    // Phone change modal state
    var showChangePhoneDialog by remember { mutableStateOf(false) }
    var newPhoneInput by remember { mutableStateOf("") }
    var isVerifyingNewPhone by remember { mutableStateOf(false) }
    var phoneOtpSent by remember { mutableStateOf<String?>(null) }
    var phoneOtpInput by remember { mutableStateOf("") }
    var phoneOtpError by remember { mutableStateOf<String?>(null) }

    val presetAvatars = listOf(
        "EM" to "Emerald",
        "SP" to "Cyan",
        "SJ" to "Purple",
        "⚡" to "Energy",
        "🚀" to "Rocket",
        "⭐" to "Star",
        "🌿" to "Nature",
        "👤" to "User"
    )

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
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Batal", tint = ElementEmerald)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profil",
                            color = VerbaTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                onSaveProfile(
                                    nameInput.trim(),
                                    usernameInput.trim(),
                                    bioInput.trim(),
                                    avatarInput,
                                    phoneInput
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("save_profile_button")
                    ) {
                        Text("Simpan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // Avatar Photo Preview & Picker
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(ElementEmerald.copy(alpha = 0.2f))
                                .border(2.dp, ElementEmerald, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = avatarInput,
                                color = ElementEmerald,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(ElementEmerald)
                                .border(2.dp, VerbaDarkBackground, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Ubah Foto", tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }

                    Text(
                        text = "Pilih Avatar / Foto Profil:",
                        color = VerbaTextMuted,
                        fontSize = 12.sp
                    )

                    // Avatar presets selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        presetAvatars.forEach { (avatar, _) ->
                            val isSelected = avatarInput == avatar
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) ElementEmerald.copy(alpha = 0.3f) else VerbaPanel)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ElementEmerald else VerbaGlassBorder,
                                        shape = CircleShape
                                    )
                                    .clickable { avatarInput = avatar },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatar, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (isSelected) ElementEmerald else VerbaTextPrimary)
                            }
                        }
                    }

                    HorizontalDivider(color = VerbaGlassBorder)

                    // Display Name
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nama Tampilan") },
                        placeholder = { Text("Nama Anda") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = ElementEmerald) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = VerbaInputBg,
                            unfocusedContainerColor = VerbaInputBg,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder,
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input")
                    )

                    // Username
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it.replace(" ", "").lowercase() },
                        label = { Text("Username") },
                        leadingIcon = { Text(" @ ", color = ElementEmerald, fontWeight = FontWeight.Bold) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = VerbaInputBg,
                            unfocusedContainerColor = VerbaInputBg,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder,
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_username_input")
                    )

                    // Bio
                    OutlinedTextField(
                        value = bioInput,
                        onValueChange = { bioInput = it },
                        label = { Text("Info (Bio)") },
                        leadingIcon = { Icon(Icons.Default.Info, null, tint = ElementEmerald) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = VerbaInputBg,
                            unfocusedContainerColor = VerbaInputBg,
                            focusedBorderColor = ElementEmerald,
                            unfocusedBorderColor = VerbaGlassBorder,
                            focusedTextColor = VerbaTextPrimary,
                            unfocusedTextColor = VerbaTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("edit_bio_input")
                    )

                    // Phone Number Card with Verification
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(VerbaGlassCard)
                            .border(1.dp, VerbaGlassBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
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
                                        .background(ElementEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Phone, null, tint = ElementEmerald, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Nomor Telepon Terverifikasi", color = VerbaTextMuted, fontSize = 11.sp)
                                    Text(
                                        text = phoneInput.ifBlank { "Belum ada nomor" },
                                        color = VerbaTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            TextButton(onClick = { showChangePhoneDialog = true }) {
                                Text("Ubah", color = ElementEmerald, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Ubah Nomor HP dengan Verifikasi OTP
    if (showChangePhoneDialog) {
        AlertDialog(
            onDismissRequest = {
                showChangePhoneDialog = false
                isVerifyingNewPhone = false
                phoneOtpSent = null
                phoneOtpError = null
            },
            containerColor = VerbaPanel,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = if (!isVerifyingNewPhone) "Ubah Nomor Telepon" else "Verifikasi Nomor Baru",
                    color = VerbaTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (!isVerifyingNewPhone) {
                        Text(
                            text = "Masukkan nomor HP baru Anda. Kami akan mengirimkan kode OTP untuk memverifikasi nomor ini sebelum disimpan.",
                            color = VerbaTextSecondary,
                            fontSize = 12.sp
                        )
                        OutlinedTextField(
                            value = newPhoneInput,
                            onValueChange = { newPhoneInput = it.filter { c -> c.isDigit() || c == '+' } },
                            label = { Text("Nomor HP Baru") },
                            placeholder = { Text("+62 812-3456-7890") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VerbaTextPrimary,
                                unfocusedTextColor = VerbaTextPrimary,
                                focusedBorderColor = ElementEmerald,
                                unfocusedBorderColor = VerbaGlassBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    } else {
                        Text(
                            text = "Masukkan 6 digit kode OTP yang telah dikirim ke: $newPhoneInput",
                            color = VerbaTextSecondary,
                            fontSize = 12.sp
                        )
                        phoneOtpSent?.let { code ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElementEmerald.copy(alpha = 0.15f))
                                    .padding(8.dp)
                            ) {
                                Text("SMS Gateway: Kode OTP Anda adalah $code", color = ElementEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OutlinedTextField(
                            value = phoneOtpInput,
                            onValueChange = { if (it.length <= 6) phoneOtpInput = it.filter { c -> c.isDigit() } },
                            label = { Text("Kode OTP 6-Digit") },
                            placeholder = { Text("123456") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = VerbaTextPrimary,
                                unfocusedTextColor = VerbaTextPrimary,
                                focusedBorderColor = ElementEmerald,
                                unfocusedBorderColor = VerbaGlassBorder
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        if (phoneOtpError != null) {
                            Text(phoneOtpError ?: "", color = VerbaDanger, fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                if (!isVerifyingNewPhone) {
                    Button(
                        onClick = {
                            if (newPhoneInput.length >= 8) {
                                onRequestPhoneOtp(newPhoneInput) { generatedOtp ->
                                    phoneOtpSent = generatedOtp
                                    isVerifyingNewPhone = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald)
                    ) {
                        Text("Kirim OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (phoneOtpInput == phoneOtpSent) {
                                phoneInput = newPhoneInput
                                showChangePhoneDialog = false
                                isVerifyingNewPhone = false
                                phoneOtpSent = null
                            } else {
                                phoneOtpError = "Kode OTP tidak cocok."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald)
                    ) {
                        Text("Verifikasi & Simpan", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangePhoneDialog = false
                        isVerifyingNewPhone = false
                    }
                ) {
                    Text("Batal", color = VerbaTextSecondary)
                }
            }
        )
    }
}
