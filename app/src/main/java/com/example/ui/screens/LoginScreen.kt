package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.PhoneAuthStep
import com.example.ui.VerbaViewModel
import com.example.ui.theme.*

data class CountryOption(val code: String, val name: String, val flag: String)

val supportedCountries = listOf(
    CountryOption("+62", "Indonesia", "🇮🇩"),
    CountryOption("+60", "Malaysia", "🇲🇾"),
    CountryOption("+65", "Singapura", "🇸🇬"),
    CountryOption("+1", "Amerika Serikat", "🇺🇸"),
    CountryOption("+966", "Arab Saudi", "🇸🇦"),
    CountryOption("+44", "Inggris", "🇬🇧"),
    CountryOption("+81", "Jepang", "🇯🇵"),
    CountryOption("+61", "Australia", "🇦🇺")
)

@Composable
fun LoginScreen(
    viewModel: VerbaViewModel,
    modifier: Modifier = Modifier
) {
    val authStep by viewModel.phoneAuthStep.collectAsStateWithLifecycle()
    val phoneNumber by viewModel.phoneNumberInput.collectAsStateWithLifecycle()
    val countryCode by viewModel.countryCode.collectAsStateWithLifecycle()
    val otpInput by viewModel.otpInput.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val dispatchedOtp by viewModel.dispatchedOtpNotice.collectAsStateWithLifecycle()
    val countdown by viewModel.otpTimerSeconds.collectAsStateWithLifecycle()

    var showCountryMenu by remember { mutableStateOf(false) }

    // Profile Setup states
    var newDisplayName by remember { mutableStateOf("") }
    var newUsername by remember { mutableStateOf("") }
    var newBio by remember { mutableStateOf("Ada, menggunakan Element Messenger ✨") }
    var selectedAvatar by remember { mutableStateOf("EM") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkMeshBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Element Emerald Emblem
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LiquidGlassEmeraldGradient)
                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Element Messenger",
                color = VerbaTextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "Komunikasi Modern • Enkripsi End-to-End",
                color = ElementEmerald,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Main Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(VerbaGlassCard)
                    .border(1.dp, VerbaGlassBorder, RoundedCornerShape(22.dp))
                    .padding(22.dp)
            ) {
                AnimatedContent(
                    targetState = authStep,
                    label = "auth_step_transition"
                ) { step ->
                    when (step) {
                        PhoneAuthStep.INPUT_PHONE -> {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "MASUK DENGAN NOMOR HP",
                                    color = ElementEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Text(
                                    text = "Masukkan nomor telepon Anda untuk menerima kode verifikasi OTP 6-digit.",
                                    color = VerbaTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                // Country Code Selector & Phone Input
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Country Selector
                                    Box {
                                        Surface(
                                            onClick = { showCountryMenu = true },
                                            color = VerbaInputBg,
                                            shape = RoundedCornerShape(12.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, VerbaGlassBorder),
                                            modifier = Modifier
                                                .height(56.dp)
                                                .testTag("country_code_selector")
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                val curr = supportedCountries.find { it.code == countryCode }
                                                Text(curr?.flag ?: "🇮🇩", fontSize = 18.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(countryCode, color = VerbaTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                Icon(Icons.Default.ArrowDropDown, null, tint = VerbaTextSecondary)
                                            }
                                        }

                                        DropdownMenu(
                                            expanded = showCountryMenu,
                                            onDismissRequest = { showCountryMenu = false },
                                            modifier = Modifier.background(VerbaPanel)
                                        ) {
                                            supportedCountries.forEach { item ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text("${item.flag} ${item.name} (${item.code})", color = VerbaTextPrimary)
                                                    },
                                                    onClick = {
                                                        viewModel.setCountryCode(item.code)
                                                        showCountryMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Phone Number Input
                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = {
                                            viewModel.setPhoneNumber(it.filter { char -> char.isDigit() })
                                        },
                                        placeholder = { Text("812-3456-7890", color = VerbaTextMuted) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .testTag("phone_number_input")
                                    )
                                }

                                if (authError != null) {
                                    Text(
                                        text = authError ?: "",
                                        color = VerbaDanger,
                                        fontSize = 12.sp
                                    )
                                }

                                // Continue Button
                                Button(
                                    onClick = { viewModel.requestPhoneOtp() },
                                    enabled = !isLoading && phoneNumber.length >= 6,
                                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("phone_continue_button")
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Text("Lanjutkan", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Quick test numbers
                                Text(
                                    text = "Uji coba nomor cepat:",
                                    color = VerbaTextMuted,
                                    fontSize = 11.sp
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(
                                        ("81234567890" to "Sandi Prayoga (Terdaftar)"),
                                        ("81398765432" to "Sarah Jenkins (Terdaftar)"),
                                        ("85711223344" to "Pengguna Baru (Registrasi)")
                                    ).forEach { (num, label) ->
                                        Surface(
                                            onClick = {
                                                viewModel.setCountryCode("+62")
                                                viewModel.setPhoneNumber(num)
                                                viewModel.requestPhoneOtp("+62$num")
                                            },
                                            color = VerbaPanel,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, VerbaGlassBorder),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(label, color = VerbaTextPrimary, fontSize = 12.sp)
                                                Text("+62 $num", color = ElementEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        PhoneAuthStep.OTP_VERIFY -> {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { viewModel.backToPhoneInput() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Ubah Nomor", tint = ElementEmerald)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VERIFIKASI KODE OTP",
                                        color = ElementEmerald,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Text(
                                    text = "Masukkan 6 digit kode yang dikirimkan ke:\n$countryCode $phoneNumber",
                                    color = VerbaTextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )

                                // Real SMS Gateway simulation banner
                                dispatchedOtp?.let { code ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ElementEmerald.copy(alpha = 0.12f))
                                            .border(1.dp, ElementEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Sms, contentDescription = null, tint = ElementEmerald, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text("SMS Verifikasi Element Messenger:", color = ElementEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text("Kode OTP Anda: $code", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // 6 Digit OTP input
                                OutlinedTextField(
                                    value = otpInput,
                                    onValueChange = {
                                        if (it.length <= 6) {
                                            viewModel.setOtp(it.filter { char -> char.isDigit() })
                                        }
                                    },
                                    placeholder = { Text("0 0 0 0 0 0", color = VerbaTextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
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
                                    textStyle = LocalTextStyle.current.copy(
                                        textAlign = TextAlign.Center,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 8.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .testTag("otp_input")
                                )

                                if (authError != null) {
                                    Text(
                                        text = authError ?: "",
                                        color = VerbaDanger,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // Verify Button
                                Button(
                                    onClick = { viewModel.verifyPhoneOtp() },
                                    enabled = !isLoading && otpInput.length == 6,
                                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("verify_otp_button")
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Text("Verifikasi", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }

                                // Resend Countdown
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (countdown > 0) {
                                        Text(
                                            text = "Kirim ulang kode dalam $countdown detik",
                                            color = VerbaTextMuted,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        TextButton(onClick = { viewModel.resendPhoneOtp() }) {
                                            Text("Kirim Ulang OTP", color = ElementEmerald, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }

                        PhoneAuthStep.PROFILE_SETUP -> {
                            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "PROFIL PENGGUNA BARU",
                                    color = ElementEmerald,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Text(
                                    text = "Nomor Anda berhasil diverifikasi. Lengkapi nama Anda untuk memulai chat.",
                                    color = VerbaTextSecondary,
                                    fontSize = 13.sp
                                )

                                // Avatar Preview & Picker
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(76.dp)
                                            .clip(CircleShape)
                                            .background(ElementEmerald.copy(alpha = 0.2f))
                                            .border(2.dp, ElementEmerald, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (newDisplayName.isNotBlank()) newDisplayName.take(2).uppercase() else selectedAvatar,
                                            color = ElementEmerald,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // Display Name
                                OutlinedTextField(
                                    value = newDisplayName,
                                    onValueChange = { newDisplayName = it },
                                    label = { Text("Nama Lengkap") },
                                    placeholder = { Text("contoh: Sandi Prayoga") },
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
                                    modifier = Modifier.fillMaxWidth().testTag("new_display_name_input")
                                )

                                // Username
                                OutlinedTextField(
                                    value = newUsername,
                                    onValueChange = { newUsername = it.replace(" ", "").lowercase() },
                                    label = { Text("Username") },
                                    placeholder = { Text("contoh: sandi") },
                                    leadingIcon = { Text("@", color = ElementEmerald, fontWeight = FontWeight.Bold) },
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
                                    modifier = Modifier.fillMaxWidth().testTag("new_username_input")
                                )

                                // Bio
                                OutlinedTextField(
                                    value = newBio,
                                    onValueChange = { newBio = it },
                                    label = { Text("Info (Bio)") },
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
                                    modifier = Modifier.fillMaxWidth()
                                )

                                if (authError != null) {
                                    Text(
                                        text = authError ?: "",
                                        color = VerbaDanger,
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.completePhoneRegistration(
                                            displayName = newDisplayName.trim(),
                                            username = newUsername.trim(),
                                            bio = newBio.trim(),
                                            avatar = if (newDisplayName.isNotBlank()) newDisplayName.take(2).uppercase() else selectedAvatar
                                        )
                                    },
                                    enabled = !isLoading && newDisplayName.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElementEmerald),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("finish_registration_button")
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Text("Selesai & Mulai Chat", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
