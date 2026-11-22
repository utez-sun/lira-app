package com.lira.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.ui.theme.DarkBorder
import com.lira.tracker.ui.theme.DarkSurface
import com.lira.tracker.ui.theme.DarkSurfaceVariant
import com.lira.tracker.ui.theme.DeltaNegative
import com.lira.tracker.ui.theme.DeltaPositive
import com.lira.tracker.ui.theme.DeltaZero
import com.lira.tracker.ui.theme.LiraHeartCoral
import com.lira.tracker.ui.theme.TextMuted
import com.lira.tracker.ui.theme.TextPrimary
import com.lira.tracker.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MetricCard(
    record: ListenerRecord?,
    artistId: String,
    lastSyncTimestamp: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LiraHeartCoral.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = LiraHeartCoral,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Слушатели за месяц",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Артист ID: $artistId",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                if (record != null) {
                    DeltaBadge(delta = record.delta)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            val listenerFormatted = if (record != null) {
                NumberFormat.getNumberInstance(Locale.FRANCE).format(record.listeners)
            } else {
                "—"
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = listenerFormatted,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 38.sp),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "в месяц",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val lastUpdateStr = if (lastSyncTimestamp > 0) {
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                "Обновлено: ${dateFormat.format(Date(lastSyncTimestamp))} в ${timeFormat.format(Date(lastSyncTimestamp))}"
            } else if (record != null) {
                "Дата фиксации: ${record.dateString}"
            } else {
                "Данные ещё не запрашивались"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (record != null) DeltaPositive else TextMuted)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = lastUpdateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun DeltaBadge(delta: Long) {
    val (bg, fg, icon, text) = when {
        delta > 0 -> {
            val formatted = "+${NumberFormat.getNumberInstance(Locale.FRANCE).format(delta)}"
            Quad(DeltaPositive.copy(alpha = 0.15f), DeltaPositive, Icons.Default.ArrowUpward, formatted)
        }
        delta < 0 -> {
            val formatted = "-${NumberFormat.getNumberInstance(Locale.FRANCE).format(kotlin.math.abs(delta))}"
            Quad(DeltaNegative.copy(alpha = 0.15f), DeltaNegative, Icons.Default.ArrowDownward, formatted)
        }
        else -> {
            Quad(DarkSurfaceVariant, DeltaZero, Icons.Default.Equalizer, "0")
        }
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
