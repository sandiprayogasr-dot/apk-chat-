package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.CallType
import com.example.ui.ActiveCallState
import com.example.ui.theme.*

@Composable
fun ActiveCallDialog(
    callState: ActiveCallState,
    onMuteToggle: () -> Unit,
    onSpeakerToggle: () -> Unit,
    onEndCall: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "callRings")
    val pulseSize by infiniteTransition.animateFloat(
        initialValue = 110f,
        targetValue = 140f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseSize"
    )

    val minutes = callState.durationSeconds / 60
    val seconds = callState.durationSeconds % 60
    val durationText = String.format("%02d:%02d", minutes, seconds)

    Dialog(
        onDismissRequest = {}, // Cannot be dismissed by clicking outside
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1113).copy(alpha = 0.96f))
                .padding(24.dp)
                .testTag("active_call_overlay"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight().padding(vertical = 32.dp)
            ) {
                // Header: Call status & Contact Info
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (callState.type == CallType.VOICE) "Panggilan Suara Verba" else "Panggilan Video HD Verba",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = callState.contactName,
                        color = VerbaTextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = durationText,
                        color = VerbaTextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Center: Animated Calling Avatar
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Pulsing Ring
                    Box(
                        modifier = Modifier
                            .size(pulseSize.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    )

                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(VerbaPanel)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = callState.contactAvatar,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Bottom Call Action Controls
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mute button
                        IconButton(
                            onClick = onMuteToggle,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (callState.isMuted) Color.White else VerbaPanel)
                                .border(1.dp, VerbaBorder, CircleShape)
                                .testTag("call_mute_toggle")
                        ) {
                            Icon(
                                imageVector = if (callState.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = if (callState.isMuted) Color.Black else VerbaTextPrimary
                            )
                        }

                        // End Call Button
                        IconButton(
                            onClick = onEndCall,
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(VerbaDanger)
                                .testTag("call_end_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "Akhiri Panggilan",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        // Speaker button
                        IconButton(
                            onClick = onSpeakerToggle,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (callState.isSpeakerOn) MaterialTheme.colorScheme.primary else VerbaPanel)
                                .border(1.dp, VerbaBorder, CircleShape)
                                .testTag("call_speaker_toggle")
                        ) {
                            Icon(
                                imageVector = if (callState.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Speaker",
                                tint = if (callState.isSpeakerOn) Color.Black else VerbaTextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enkripsi end-to-end aktif",
                        color = VerbaTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
