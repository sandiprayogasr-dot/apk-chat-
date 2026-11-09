package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AudioVoicePlayer(
    durationSeconds: Int,
    isOutgoing: Boolean,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (currentSeconds < durationSeconds && isPlaying) {
                delay(1000)
                currentSeconds += 1
            }
            if (currentSeconds >= durationSeconds) {
                isPlaying = false
                currentSeconds = 0
            }
        }
    }

    val waveBars = remember {
        listOf(0.3f, 0.6f, 0.9f, 0.4f, 0.8f, 1.0f, 0.7f, 0.5f, 0.85f, 0.6f, 0.4f, 0.95f, 0.7f, 0.35f, 0.8f, 0.6f, 0.5f, 0.75f)
    }

    val activeColor = MaterialTheme.colorScheme.primary
    val barColor = if (isOutgoing) activeColor.copy(alpha = 0.5f) else VerbaTextSecondary.copy(alpha = 0.4f)
    val playedColor = if (isOutgoing) activeColor else VerbaTextPrimary

    Row(
        modifier = modifier
            .width(220.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play / Pause Circle
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isOutgoing) activeColor else VerbaPanel)
                .border(1.dp, if (isOutgoing) activeColor else VerbaBorder, CircleShape)
                .clickable { isPlaying = !isPlaying }
                .testTag("audio_player_play_pause"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Jeda" else "Putar",
                tint = if (isOutgoing) Color.Black else VerbaTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Waveform & Timer
        Column(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                val totalBars = waveBars.size
                val barWidth = 4.dp.toPx()
                val spacing = (size.width - (totalBars * barWidth)) / (totalBars - 1)
                val progressRatio = if (durationSeconds > 0) currentSeconds.toFloat() / durationSeconds else 0f
                val activeBarIndex = (progressRatio * totalBars).toInt()

                waveBars.forEachIndexed { index, heightRatio ->
                    val barHeight = size.height * heightRatio
                    val startX = index * (barWidth + spacing)
                    val startY = (size.height - barHeight) / 2
                    val color = if (index <= activeBarIndex) playedColor else barColor

                    drawRoundRect(
                        color = color,
                        topLeft = Offset(startX, startY),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val displaySec = if (isPlaying) currentSeconds else durationSeconds
                val minutes = displaySec / 60
                val seconds = displaySec % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    color = if (isOutgoing) activeColor.copy(alpha = 0.9f) else VerbaTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Voice note",
                    color = VerbaTextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun VoiceRecorderBar(
    recordingSeconds: Int,
    onCancel: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val minutes = recordingSeconds / 60
    val seconds = recordingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VerbaCard)
            .border(1.dp, VerbaBorder, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("voice_recorder_bar"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Blinking red recording circle
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(VerbaDanger.copy(alpha = alphaAnim))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = VerbaDanger,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Merekam suara: $timeFormatted",
                color = VerbaTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cancel button
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("cancel_voice_record_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Batal Rekam",
                    tint = VerbaTextSecondary
                )
            }

            // Send voice button
            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("send_voice_record_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Kirim Voice Note",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
