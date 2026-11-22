package com.lira.tracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.ui.theme.DeltaNegative
import com.lira.tracker.ui.theme.DeltaPositive
import com.lira.tracker.ui.theme.DeltaZero
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ListenersChart(
    records: List<ListenerRecord>,
    modifier: Modifier = Modifier
) {
    var selectedRecord by remember(records) {
        mutableStateOf<ListenerRecord?>(records.lastOrNull())
    }

    val themePrimary = MaterialTheme.colorScheme.primary
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
                Column {
                    Text(
                        text = "Динамика слушателей",
                        style = MaterialTheme.typography.titleMedium,
                        color = themeOnSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedRecord != null) {
                            "Точка: ${selectedRecord?.dateString}"
                        } else {
                            "График ежедневного прироста"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = themeOnSurfaceVariant
                    )
                }

                if (selectedRecord != null) {
                    val record = selectedRecord!!
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

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = numberFormatted,
                            style = MaterialTheme.typography.titleMedium,
                            color = themePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "($deltaFormatted)",
                            style = MaterialTheme.typography.labelSmall,
                            color = deltaColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (records.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Данных пока нет.\nНажмите «Получить данные сейчас», чтобы зафиксировать первую точку.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = themeOnSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else if (records.size == 1) {
                val record = records.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Зафиксирована первая точка (${record.dateString})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = themeOnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${NumberFormat.getNumberInstance(Locale.FRANCE).format(record.listeners)} слушателей",
                            style = MaterialTheme.typography.headlineSmall,
                            color = themePrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Линия динамики появится после следующих измерений",
                            style = MaterialTheme.typography.labelSmall,
                            color = themeOnSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                val minVal = records.minOf { it.listeners }
                val maxVal = records.maxOf { it.listeners }
                val range = if (maxVal == minVal) 10L else (maxVal - minVal)
                val padding = (range * 0.15f).toLong().coerceAtLeast(1L)
                val chartMin = (minVal - padding).coerceAtLeast(0L)
                val chartMax = maxVal + padding
                val totalSpan = (chartMax - chartMin).toFloat().coerceAtLeast(1f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(records) {
                                detectTapGestures { tapOffset ->
                                    val count = records.size
                                    if (count > 1) {
                                        val stepX = size.width / (count - 1)
                                        val index = (tapOffset.x / stepX)
                                            .toInt()
                                            .coerceIn(0, count - 1)
                                        selectedRecord = records[index]
                                    }
                                }
                            }
                    ) {
                        val width = size.width
                        val height = size.height - 30.dp.toPx()
                        val count = records.size
                        val stepX = width / (count - 1)

                        // Горизонтальные линии сетки
                        val gridLines = 3
                        for (i in 0..gridLines) {
                            val y = height * (i.toFloat() / gridLines)
                            drawLine(
                                color = themeOnSurface.copy(alpha = 0.07f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Вычисление координат точек
                        val points = records.mapIndexed { index, rec ->
                            val x = index * stepX
                            val normalizedY = (rec.listeners - chartMin) / totalSpan
                            val y = height - (normalizedY * height)
                            Offset(x, y)
                        }

                        // Кривая Безье
                        val strokePath = Path().apply {
                            if (points.isNotEmpty()) {
                                moveTo(points[0].x, points[0].y)
                                for (i in 1 until points.size) {
                                    val prev = points[i - 1]
                                    val curr = points[i]
                                    val cx1 = prev.x + (curr.x - prev.x) / 2f
                                    val cy1 = prev.y
                                    val cx2 = prev.x + (curr.x - prev.x) / 2f
                                    val cy2 = curr.y
                                    cubicTo(cx1, cy1, cx2, cy2, curr.x, curr.y)
                                }
                            }
                        }

                        // Градиентная заливка
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(points.last().x, height)
                            lineTo(points.first().x, height)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    themePrimary.copy(alpha = 0.35f),
                                    themePrimary.copy(alpha = 0.04f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = height
                            )
                        )

                        // Линия графика
                        drawPath(
                            path = strokePath,
                            color = themePrimary,
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        // Точки
                        points.forEachIndexed { idx, pt ->
                            val isSelected = records[idx] == selectedRecord
                            if (isSelected) {
                                drawLine(
                                    color = themeOnSurface.copy(alpha = 0.35f),
                                    start = Offset(pt.x, 0f),
                                    end = Offset(pt.x, height),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                                drawCircle(
                                    color = themePrimary.copy(alpha = 0.3f),
                                    radius = 10.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = themePrimary,
                                    radius = 5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = themeSurface,
                                    radius = 2.5.dp.toPx(),
                                    center = pt
                                )
                            } else {
                                drawCircle(
                                    color = themeSurface,
                                    radius = 4.5.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = themePrimary,
                                    radius = 2.5.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    // Подписи крайних дат
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = records.first().dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = themeOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                        if (records.size > 2) {
                            Text(
                                text = records[records.size / 2].dateString,
                                style = MaterialTheme.typography.labelSmall,
                                color = themeOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = records.last().dateString,
                            style = MaterialTheme.typography.labelSmall,
                            color = themeOnSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
