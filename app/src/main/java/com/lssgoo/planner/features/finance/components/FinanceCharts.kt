package com.lssgoo.planner.features.finance.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lssgoo.planner.features.finance.models.TransactionCategory
import com.lssgoo.planner.ui.theme.FinanceColors
import java.util.Locale

@Composable
fun SpendingPieChart(
    data: Map<TransactionCategory, Double>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()
    if (total == 0.0) return

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(140.dp)) {
                var startAngle = -90f
                data.forEach { (cat, value) ->
                    val sweepAngle = (value / total).toFloat() * 360f * animatedProgress.value
                    drawArc(
                        color = getCategoryColor(cat),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 40f, cap = StrokeCap.Round)
                    )
                    startAngle += (value / total).toFloat() * 360f
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${total.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            data.entries.sortedByDescending { it.value }.take(5).forEach { (cat, value) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(getCategoryColor(cat)))
                    Text(cat.name.lowercase().capitalize(), fontSize = 12.sp, modifier = Modifier.weight(1f))
                    Text("${(value / total * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExpenseLineChart(
    data: Map<Long, Double>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    
    val sortedData = data.entries.sortedBy { it.key }
    val maxVal = sortedData.maxOf { it.value }.coerceAtLeast(1.0)
    
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.animateTo(1f, animationSpec = tween(1200))
    }

    Box(modifier = modifier.fillMaxWidth().height(180.dp).padding(top = 16.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val spacing = width / (sortedData.size - 1).coerceAtLeast(1)
            
            // Draw points and lines
            var lastPoint: Offset? = null
            sortedData.forEachIndexed { index, entry ->
                val x = index * spacing
                val y = height - (entry.value / maxVal).toFloat() * height * animatedProgress.value
                val currentPoint = Offset(x, y)
                
                if (lastPoint != null) {
                    drawLine(
                        color = FinanceColors.expense,
                        start = lastPoint!!,
                        end = currentPoint,
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }
                
                drawCircle(
                    color = FinanceColors.expense,
                    center = currentPoint,
                    radius = 8f
                )
                
                lastPoint = currentPoint
            }
        }
    }
}

private fun getCategoryColor(category: TransactionCategory): Color {
    return when (category) {
        TransactionCategory.FOOD -> Color(0xFFFF9800)
        TransactionCategory.TRANSPORT -> Color(0xFF2196F3)
        TransactionCategory.SHOPPING -> Color(0xFFE91E63)
        TransactionCategory.ENTERTAINMENT -> Color(0xFF9C27B0)
        TransactionCategory.HEALTH -> Color(0xFF4CAF50)
        TransactionCategory.EDUCATION -> Color(0xFF00BCD4)
        TransactionCategory.SALARY -> Color(0xFF4CAF50)
        TransactionCategory.INVESTMENT -> Color(0xFFFFC107)
        TransactionCategory.BILL -> Color(0xFF607D8B)
        TransactionCategory.RENT -> Color(0xFF795548)
        TransactionCategory.GIFT -> Color(0xFFFF5722)
        TransactionCategory.OTHER -> Color(0xFF9E9E9E)
        TransactionCategory.DEBT_REPAYMENT -> Color(0xFF3F51B5)
    }
}

private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
