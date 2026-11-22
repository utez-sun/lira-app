package com.lira.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.data.preferences.AppPreferences
import com.lira.tracker.ui.theme.LiraCoralPrimary
import com.lira.tracker.ui.theme.WavePrimary
import com.lira.tracker.ui.theme.WaveSecondary

@Composable
fun SettingsScreen(
    currentArtistId: String,
    currentHour: Int,
    currentMinute: Int,
    isDarkTheme: Boolean,
    currentStyle: String,
    records: List<ListenerRecord>,
    onSaveSettings: (artistId: String, hour: Int, minute: Int) -> Unit,
    onToggleTheme: (isDark: Boolean) -> Unit,
    onSelectStyle: (style: String) -> Unit,
    onClearHistory: () -> Unit
) {
    var artistIdInput by remember(currentArtistId) { mutableStateOf(currentArtistId) }
    var hourInput by remember(currentHour) { mutableStateOf(currentHour.toString().padStart(2, '0')) }
    var minuteInput by remember(currentMinute) { mutableStateOf(currentMinute.toString().padStart(2, '0')) }
    var inputError by remember { mutableStateOf<String?>(null) }

    val themeSurface = MaterialTheme.colorScheme.surface
    val themeOnSurface = MaterialTheme.colorScheme.onSurface
    val themeOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val themeOutline = MaterialTheme.colorScheme.outline
    val themePrimary = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ------------------ БЛОК 1: ПАРАМЕТРЫ ОБНОВЛЕНИЯ ------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = themeSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(themeOutline.copy(alpha = 0.6f), themeOutline.copy(alpha = 0.6f)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(themePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = themePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Параметры трекера",
                        style = MaterialTheme.typography.titleMedium,
                        color = themeOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "ID артиста на Яндекс Музыке",
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = artistIdInput,
                    onValueChange = {
                        artistIdInput = it.filter { c -> c.isDigit() }
                        inputError = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("24578741", color = themeOnSurfaceVariant.copy(alpha = 0.6f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = themeOnSurface,
                        unfocusedTextColor = themeOnSurface,
                        focusedBorderColor = themePrimary,
                        unfocusedBorderColor = themeOutline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Время ежедневного обновления",
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hourInput,
                        onValueChange = {
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                hourInput = it
                                inputError = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("15") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeOnSurface,
                            unfocusedTextColor = themeOnSurface,
                            focusedBorderColor = themePrimary,
                            unfocusedBorderColor = themeOutline
                        )
                    )

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.titleLarge,
                        color = themeOnSurface,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp)
                    )

                    OutlinedTextField(
                        value = minuteInput,
                        onValueChange = {
                            if (it.length <= 2 && it.all { c -> c.isDigit() }) {
                                minuteInput = it
                                inputError = null
                            }
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = themeOnSurface,
                            unfocusedTextColor = themeOnSurface,
                            focusedBorderColor = themePrimary,
                            unfocusedBorderColor = themeOutline
                        )
                    )
                }

                if (inputError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = inputError!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val h = hourInput.toIntOrNull()
                        val m = minuteInput.toIntOrNull()
                        if (artistIdInput.isBlank()) {
                            inputError = "Укажите ID артиста"
                            return@Button
                        }
                        if (h == null || h !in 0..23) {
                            inputError = "Часы должны быть от 0 до 23"
                            return@Button
                        }
                        if (m == null || m !in 0..59) {
                            inputError = "Минуты должны быть от 0 до 59"
                            return@Button
                        }
                        onSaveSettings(artistIdInput, h, m)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themePrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Сохранить параметры", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ------------------ БЛОК 2: ВНЕШНИЙ ВИД И ТЕМЫ ------------------
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = themeSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(themeOutline.copy(alpha = 0.6f), themeOutline.copy(alpha = 0.6f)))
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(themePrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = themePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Внешний вид",
                        style = MaterialTheme.typography.titleMedium,
                        color = themeOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Переключатель темы (Темная / Светлая)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = themePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Тёмная тема",
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeOnSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isDarkTheme) "Включена тёмная тема" else "Включена светлая тема",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeOnSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onToggleTheme,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = themePrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Стиль оформления",
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeOnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Вариант оформления 1: LIRA Coral
                val isCoralSelected = currentStyle == AppPreferences.STYLE_CORAL
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isCoralSelected) 2.dp else 1.dp,
                            color = if (isCoralSelected) LiraCoralPrimary else themeOutline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(if (isCoralSelected) LiraCoralPrimary.copy(alpha = 0.08f) else Color.Transparent)
                        .clickable { onSelectStyle(AppPreferences.STYLE_CORAL) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(LiraCoralPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("LU", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LIRA Coral",
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Фирменный стиль с коралловым сердцем",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeOnSurfaceVariant
                            )
                        }
                    }

                    if (isCoralSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = LiraCoralPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Вариант оформления 2: Neon Wave
                val isWaveSelected = currentStyle == AppPreferences.STYLE_NEON_WAVE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isWaveSelected) 2.dp else 1.dp,
                            color = if (isWaveSelected) WavePrimary else themeOutline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(if (isWaveSelected) WavePrimary.copy(alpha = 0.08f) else Color.Transparent)
                        .clickable { onSelectStyle(AppPreferences.STYLE_NEON_WAVE) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(WavePrimary, WaveSecondary))
                                )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Neon Wave (Волна)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Электрик-фиолетовый и бирюзовый градиент",
                                style = MaterialTheme.typography.labelSmall,
                                color = themeOnSurfaceVariant
                            )
                        }
                    }

                    if (isWaveSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = WavePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ------------------ БЛОК 3: ЖУРНАЛ ИЗМЕРЕНИЙ ------------------
        HistoryList(
            records = records,
            onClearHistory = onClearHistory
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
