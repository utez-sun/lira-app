package com.lira.tracker.ui.components

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.ui.theme.DeltaNegative
import com.lira.tracker.ui.theme.DeltaPositive
import com.lira.tracker.ui.theme.DeltaZero
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryList(
    records: List<ListenerRecord>,
    modifier: Modifier = Modifier,
    onClearHistory: (() -> Unit)? = null
) {
    val themeSurface = MaterialTheme.colorScheme.surface
    val themeOnSurface = MaterialTheme.colorScheme.onSurface
    val themeOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val themeOutline = MaterialTheme.colorScheme.outline

    Card(
        modifier = modifier.fillMaxWidth(),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Журнал измерений",
                    style = MaterialTheme.typography.titleMedium,
                    color = themeOnSurface,
                    fontWeight = FontWeight.Bold
                )

                if (records.isNotEmpty() && onClearHistory != null) {
                    TextButton(onClick = onClearHistory) {
                        Text(
                            text = "Очистить",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (records.isEmpty()) {
                Text(
                    text = "Записей пока нет.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = themeOnSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                records.forEachIndexed { index, record ->
                    HistoryItemRow(record = record)
                    if (index < records.size - 1) {
                        HorizontalDivider(
                            color = themeOutline.copy(alpha = 0.35f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemRow(record: ListenerRecord) {
    val themeOnSurface = MaterialTheme.colorScheme.onSurface
    val themeOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

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
                color = themeOnSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Яндекс Музыка",
                style = MaterialTheme.typography.labelSmall,
                color = themeOnSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$numberFormatted сл.",
                style = MaterialTheme.typography.bodyMedium,
                color = themeOnSurface,
                fontWeight = FontWeight.Bold
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
