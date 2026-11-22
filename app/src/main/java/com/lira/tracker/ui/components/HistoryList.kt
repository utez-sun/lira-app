package com.lira.tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.ui.theme.DarkBorder
import com.lira.tracker.ui.theme.DarkSurface
import com.lira.tracker.ui.theme.DeltaNegative
import com.lira.tracker.ui.theme.DeltaPositive
import com.lira.tracker.ui.theme.DeltaZero
import com.lira.tracker.ui.theme.TextMuted
import com.lira.tracker.ui.theme.TextPrimary
import com.lira.tracker.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryList(
    records: List<ListenerRecord>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(DarkBorder, DarkBorder)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Журнал измерений",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (records.isEmpty()) {
                Text(
                    text = "Записей пока нет.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                records.forEachIndexed { index, record ->
                    HistoryItemRow(record = record)
                    if (index < records.size - 1) {
                        HorizontalDivider(
                            color = DarkBorder.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(record: ListenerRecord) {
    val numberFormatted = NumberFormat.getNumberInstance(Locale.FRANCE).format(record.listeners)
    val deltaFormatted = when {
        record.delta > 0 -> "+${NumberFormat.getNumberInstance(Locale.FRANCE).format(record.delta)}"
        record.delta < 0 -> "-${NumberFormat.getNumberInstance(Locale.FRANCE).format(kotlin.math.abs(record.delta))}"
        else -> "0"
    }
    val deltaColor = when {
        record.delta > 0 -> DeltaPositive
        record.delta < 0 -> DeltaNegative
        else -> DeltaZero
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = record.dateString,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Яндекс Музыка",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$numberFormatted сл.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "($deltaFormatted)",
                style = MaterialTheme.typography.labelSmall,
                color = deltaColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
