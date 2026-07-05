package com.example.blueheartv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blueheartv.model.AgentProcessUiState
import com.example.blueheartv.model.AgentProgressItem
import com.example.blueheartv.model.AgentProgressStatus
import com.example.blueheartv.model.AppMatchUi
import com.example.blueheartv.ui.theme.BlueAccent
import com.example.blueheartv.ui.theme.DividerColor
import com.example.blueheartv.ui.theme.ErrorRed
import com.example.blueheartv.ui.theme.MutedText
import com.example.blueheartv.ui.theme.SuccessGreen
import com.example.blueheartv.ui.theme.TextBlack
import kotlinx.coroutines.delay

@Composable
fun AgentProcessCard(
    state: AgentProcessUiState,
    modifier: Modifier = Modifier,
    onToggleExpand: () -> Unit,
) {
    var expanded by remember(state.taskTitle) { mutableStateOf(!state.terminal || state.expanded) }
    var userToggled by remember(state.taskTitle) { mutableStateOf(false) }
    val latest = state.items.lastOrNull()
    val completed = state.items.count { it.status == AgentProgressStatus.Completed }
    val total = latest?.total ?: state.items.size.takeIf { it > 0 }

    LaunchedEffect(state.terminal, userToggled, state.items.size) {
        if (state.terminal && !userToggled) {
            delay(1_000)
            expanded = false
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .border(0.5.dp, DividerColor, RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        userToggled = true
                        expanded = !expanded
                        onToggleExpand()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                AgentStatusIcon(latest?.status ?: AgentProgressStatus.Running)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.taskTitle,
                        color = TextBlack,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = latest?.message ?: "正在处理任务",
                        color = MutedText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        maxLines = if (expanded) 2 else 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                total?.takeIf { it > 0 }?.let {
                    Text(
                        text = "$completed/$it",
                        color = BlueAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = MutedText,
                    modifier = Modifier.size(20.dp),
                )
            }

            latest?.let { item ->
                val progress = item.progressFraction()
                if (progress != null) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BlueAccent,
                        trackColor = DividerColor,
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                AgentProgressTimeline(items = state.items)
            }
        }
    }
}

@Composable
fun AgentProgressTimeline(
    items: List<AgentProgressItem>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        items.forEachIndexed { index, item ->
            AgentProgressRow(
                item = item,
                isLast = index == items.lastIndex,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun AgentProgressRow(
    item: AgentProgressItem,
    isLast: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AgentStatusIcon(item.status)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .size(width = 1.dp, height = 28.dp)
                        .background(DividerColor),
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 8.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.title,
                    color = TextBlack,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                item.progressLabel()?.let {
                    Text(text = it, color = MutedText, fontSize = 11.sp)
                }
            }
            Text(
                text = item.message,
                color = MutedText,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
fun AgentStatusIcon(status: AgentProgressStatus) {
    val color = when (status) {
        AgentProgressStatus.Pending -> MutedText
        AgentProgressStatus.Running -> BlueAccent
        AgentProgressStatus.WaitingConfirmation -> BlueAccent
        AgentProgressStatus.Completed -> SuccessGreen
        AgentProgressStatus.Failed -> ErrorRed
        AgentProgressStatus.Cancelled,
        AgentProgressStatus.TakenOver,
            -> MutedText
    }
    val icon = when (status) {
        AgentProgressStatus.Pending -> Icons.Outlined.RadioButtonUnchecked
        AgentProgressStatus.Running -> Icons.Outlined.HourglassEmpty
        AgentProgressStatus.WaitingConfirmation -> Icons.Outlined.TouchApp
        AgentProgressStatus.Completed -> Icons.Outlined.Check
        AgentProgressStatus.Failed -> Icons.Outlined.ErrorOutline
        AgentProgressStatus.Cancelled -> Icons.Outlined.Close
        AgentProgressStatus.TakenOver -> Icons.Outlined.PauseCircle
    }
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(color.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(15.dp),
        )
    }
}

@Composable
fun AgentResultCard(
    summary: String,
    matches: List<AppMatchUi>? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, DividerColor, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = summary.lineSequence().firstOrNull { it.isNotBlank() } ?: "结果已生成。",
            color = TextBlack,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
        )
        matches?.let { AppInventoryResultList(matches = it) }
    }
}

@Composable
fun AppInventoryResultList(matches: List<AppMatchUi>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${matches.size}",
                color = BlueAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = " 个匹配应用",
                color = MutedText,
                fontSize = 12.sp,
            )
        }
        if (matches.isEmpty()) {
            Text(
                text = "未检测到明确匹配应用",
                color = MutedText,
                fontSize = 13.sp,
            )
            return@Column
        }
        matches.forEach { match ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = match.name,
                    color = TextBlack,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = match.packageName,
                    color = MutedText,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

internal fun parseAppInventoryMatches(content: String): List<AppMatchUi> =
    APP_MATCH_PATTERN.findAll(content)
        .map { match ->
            AppMatchUi(
                name = match.groupValues[1].trim().trimEnd('：', ':', '-', ' '),
                packageName = match.groupValues[2].trim(),
            )
        }
        .filter { it.name.isNotBlank() && it.packageName.isNotBlank() }
        .distinctBy { it.packageName }
        .toList()

private fun AgentProgressItem.progressFraction(): Float? {
    val currentValue = current ?: return null
    val totalValue = total ?: return null
    if (totalValue <= 0) return null
    return (currentValue.toFloat() / totalValue.toFloat()).coerceIn(0f, 1f)
}

private fun AgentProgressItem.progressLabel(): String? =
    when {
        current != null && total != null && total > 0 -> "$current/$total"
        current != null -> "第 $current 步"
        else -> null
    }

private val APP_MATCH_PATTERN = Regex(
    "(?:^|\\n)\\s*(?:\\d+[.、]\\s*)?([^\\n（(]+?)\\s*[（(]\\s*([a-z][a-z0-9_]*(?:\\.[a-z][a-z0-9_]*){2,})\\s*[）)]",
    setOf(RegexOption.IGNORE_CASE),
)
