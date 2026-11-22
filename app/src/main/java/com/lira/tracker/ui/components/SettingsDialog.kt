package com.lira.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lira.tracker.ui.theme.DarkBorder
import com.lira.tracker.ui.theme.DarkSurface
import com.lira.tracker.ui.theme.DarkSurfaceVariant
import com.lira.tracker.ui.theme.LiraHeartCoral
import com.lira.tracker.ui.theme.TextMuted
import com.lira.tracker.ui.theme.TextPrimary
import com.lira.tracker.ui.theme.TextSecondary

@Composable
fun SettingsDialog(
    initialArtistId: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onSave: (artistId: String, hour: Int, minute: Int) -> Unit
) {
    var artistId by remember { mutableStateOf(initialArtistId) }
    var hourText by remember { mutableStateOf(initialHour.toString().padStart(2, '0')) }
    var minuteText by remember { mutableStateOf(initialMinute.toString().padStart(2, '0')) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = DarkSurface,
        title = {
            Text(
                text = "Настройки приложения",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "ID артиста на Яндекс Музыке",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = artistId,
                    onValueChange = {
                        artistId = it.filter { char -> char.isDigit() }
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("Например: 24578741", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = LiraHeartCoral,
                        unfocusedBorderColor = DarkBorder,
                        focusedContainerColor = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Время ежедневного обновления",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = {
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                hourText = it
                                errorMessage = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("15", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LiraHeartCoral,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = {
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                minuteText = it
                                errorMessage = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("00", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = LiraHeartCoral,
                            unfocusedBorderColor = DarkBorder,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Каждый день в указанное время фоновая служба проверит Яндекс Музыку и сохранит данные.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = LiraHeartCoral
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = hourText.toIntOrNull()
                    val m = minuteText.toIntOrNull()
                    if (artistId.isBlank()) {
                        errorMessage = "Укажите ID артиста"
                        return@Button
                    }
                    if (h == null || h !in 0..23) {
                        errorMessage = "Часы должны быть от 0 до 23"
                        return@Button
                    }
                    if (m == null || m !in 0..59) {
                        errorMessage = "Минуты должны быть от 0 до 59"
                        return@Button
                    }
                    onSave(artistId, h, m)
                },
                colors = ButtonDefaults.buttonColors(containerColor = LiraHeartCoral),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сохранить", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
            ) {
                Text("Отмена")
            }
        }
    )
}
