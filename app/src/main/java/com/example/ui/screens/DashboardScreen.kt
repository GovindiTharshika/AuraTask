package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Task
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    onNavigateToTasks: () -> Unit,
    onNavigateToAiCoach: () -> Unit
) {
    val tasks by viewModel.tasksState.collectAsState()
    val streak by viewModel.streakCount.collectAsState()
    val history by viewModel.activityHistory.collectAsState()

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val pendingTasks = totalTasks - completedTasks
    val progressRatio = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0.0f

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header greeting Card ---
        HeaderGreeting(streak = streak)

        // --- Custom Image Banner Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag("dashboard_promo_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_dashboard_header),
                    contentDescription = "Modern workspace banner decoration",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Beautiful dark gradient overlay covering the bottom and left areas
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.85f),
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        )
                )

                // Text overlay details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AURA WORKSPACE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryCyan,
                            letterSpacing = 1.sp
                        )
                    }

                    Column {
                        Text(
                            text = "Supercharge Your Workday",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Leverage Aura scheduled focus intervals to align energy.",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        // --- Core completion Stats Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Task completed card with mini ring
            Card(
                modifier = Modifier
                    .weight(1.2f)
                    .height(140.dp)
                    .testTag("summary_progress_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Productivity",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${(progressRatio * 100).toInt()}% Done",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryCyan
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "$completedTasks/$totalTasks items finished",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Interactive progress Arc canvas
                    Box(
                        modifier = Modifier.size(70.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val animatedPercent = animateFloatAsState(
                            targetValue = progressRatio,
                            animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
                            label = "completion_ring"
                        )

                        Canvas(modifier = Modifier.size(64.dp)) {
                            // Background track
                            drawCircle(
                                color = if (animatedPercent.value > 0) Color.DarkGray.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.1f),
                                style = Stroke(width = 6.dp.toPx())
                            )
                            // Progress arc
                            drawArc(
                                brush = Brush.linearGradient(listOf(PrimaryCyan, SecondaryPurple)),
                                startAngle = -90f,
                                sweepAngle = animatedPercent.value * 360f,
                                useCenter = false,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success tick",
                            tint = PrimaryCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // High Priority count card
            Card(
                modifier = Modifier
                    .weight(0.8f)
                    .height(140.dp)
                    .testTag("priority_stats_card"),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.PriorityHigh,
                        contentDescription = "Priority flag",
                        tint = PriorityColorHigh,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        val highPriorityCount = tasks.count { it.priority.equals("high", ignoreCase = true) && !it.isCompleted }
                        Text(
                            text = "$highPriorityCount Critical",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pending actions",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // --- Custom Canvas Productivity Chart Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .testTag("productivity_analytics_chart"),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Workload Analytics",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Distribution of tasks by Category",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Mini Legend
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LegendItem("Work", TertiaryIndigo)
                        LegendItem("Study", SecondaryPurple)
                        LegendItem("Other", PrimaryCyan)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Beautiful custom Bar Chart drawing via Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val workCount = tasks.count { it.category.equals("work", ignoreCase = true) }.toFloat()
                    val studyCount = tasks.count { it.category.equals("study", ignoreCase = true) }.toFloat()
                    val otherCount = tasks.count { !it.category.equals("work", ignoreCase = true) && !it.category.equals("study", ignoreCase = true) }.toFloat()

                    val counts = listOf(workCount, studyCount, otherCount)
                    val maxVal = maxOf(counts.maxOrNull() ?: 1.0f, 4.0f) // Keep a default floor height

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barCount = 3
                        val barWidth = size.width / 6f
                        val spacing = (size.width - (barWidth * barCount)) / (barCount + 1)

                        val barColors = listOf(TertiaryIndigo, SecondaryPurple, PrimaryCyan)
                        val labels = listOf("Work", "Study", "Personal")

                        // Draw background reference grid lines
                        val gridCount = 3
                        for (i in 0..gridCount) {
                            val y = i * (size.height / gridCount)
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.1f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        // Draw Bars
                        for (index in 0 until barCount) {
                            val countVal = counts[index]
                            val barHeightRatio = countVal / maxVal
                            val barHeightInPx = barHeightRatio * size.height * 0.85f

                            val xOffset = spacing + (index * (barWidth + spaceOfBars(barCount, barWidth, size.width)))
                            val yOffset = size.height - barHeightInPx

                            // Render rounded pill bar
                            drawRoundRect(
                                color = barColors[index],
                                topLeft = Offset(xOffset, yOffset),
                                size = Size(barWidth, barHeightInPx),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        // --- AI Daily Tip display ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_insight_tip_box"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PrimaryCyan.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Idea light",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "Aura Coach Focus Tip",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "To minimize distraction, group smaller Work items inside a designated 'Administrative block' instead of switching context continuously.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // --- User Action Hotlinks ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onNavigateToTasks,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.List, "Tasks menu", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Manage Tasks", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }

            Button(
                onClick = onNavigateToAiCoach,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(Icons.Default.OfflineBolt, "Aura bot", tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Ask AI Coach", color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // --- Recent Activity logs ---
        Text(
            text = "Aura Activity Log",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (history.isEmpty()) {
                    Text(
                        text = "No activities registered yet. Create tasks or consult Aura Coach!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    history.take(4).forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SecondaryPurple, CircleShape)
                            )
                            Text(
                                text = item,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
        Text(text = text, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun HeaderGreeting(streak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "My Workspace",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Welcome to Aura premium center",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Streak tracker badge with subtle glow container
        Row(
            modifier = Modifier
                .background(SecondaryPurple.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Whatshot,
                contentDescription = "Streak flame",
                tint = SecondaryPurple,
                modifier = Modifier.size(18.dp)
              )
            Text(
                text = "$streak Day Streak",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryPurple
            )
        }
    }
}

private fun spaceOfBars(count: Int, width: Float, totalWidth: Float): Float {
    if (count <= 1) return 0f
    return (totalWidth - (width * count)) / (count + 1)
}
