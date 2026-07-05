package com.example.blueheartv.viewmodel

import com.example.blueheartv.chat.ChatStreamEvent
import com.example.blueheartv.model.AgentProcessUiState
import com.example.blueheartv.model.AgentProgressItem
import com.example.blueheartv.model.AgentProgressStatus
import com.example.blueheartv.model.TraceEvent
import com.example.blueheartv.model.TraceRunStatus
import com.example.blueheartv.model.TraceStepStatus
import java.util.Locale

object AgentProcessReducer {
    fun reduceTaskProgress(
        current: AgentProcessUiState?,
        event: ChatStreamEvent.TaskProgress,
    ): AgentProcessUiState {
        val status = event.status.toAgentProgressStatus()
        val title = event.safeTitle()
        val message = event.message?.takeIf { it.isSafeDisplayText() } ?: title
        val id = event.progressKey?.ifBlank { null }
            ?: "${event.phase}-${event.toolName.orEmpty()}-${event.currentStep ?: 0}"
        val item = AgentProgressItem(
            id = id,
            phase = event.phase,
            title = title,
            message = message,
            status = status,
            current = event.currentStep,
            total = event.totalSteps,
            toolName = event.toolName,
            timestamp = event.timestamp ?: System.currentTimeMillis(),
        )
        val existingItems = current?.items.orEmpty()
        val existingIndex = existingItems.indexOfFirst { it.id == id }
        val nextItems = if (existingIndex >= 0) {
            existingItems.toMutableList().also { items ->
                val previous = items[existingIndex]
                items[existingIndex] = item.copy(timestamp = previous.timestamp)
            }
        } else {
            existingItems + item
        }.takeLast(MAX_PROGRESS_ITEMS)

        val taskTitle = event.taskTitle
            ?.takeIf { it.isSafeDisplayText() }
            ?: current?.taskTitle
            ?: title
        return AgentProcessUiState(
            taskTitle = taskTitle,
            items = nextItems,
            expanded = current?.expanded ?: true,
            terminal = status.isTerminal(),
            finalSummary = current?.finalSummary,
        )
    }

    fun reduceTrace(
        current: AgentProcessUiState?,
        event: TraceEvent,
    ): AgentProcessUiState {
        return when (event) {
            is TraceEvent.RunStarted -> {
                val progress = ChatStreamEvent.TaskProgress(
                    label = "分析请求",
                    status = "running",
                    phase = "analysis",
                    taskTitle = current?.taskTitle ?: "处理任务",
                    stepTitle = "分析请求",
                    message = event.summary ?: "正在分析你的请求。",
                    progressKey = "trace-run-started-${event.runId}",
                    runId = event.runId,
                    threadId = event.threadId,
                )
                reduceTaskProgress(current, progress)
            }

            is TraceEvent.StepUpsert -> {
                if (!event.step.visibleToUser) {
                    current ?: emptyState()
                } else {
                    val progress = ChatStreamEvent.TaskProgress(
                        label = event.step.title,
                        status = event.step.status.toTaskProgressStatus(),
                        phase = event.step.kind,
                        taskTitle = current?.taskTitle,
                        stepTitle = event.step.title,
                        message = event.step.summary,
                        progressKey = "trace-step-${event.step.id}",
                        runId = event.runId,
                    )
                    reduceTaskProgress(current, progress)
                }
            }

            is TraceEvent.StepDetailAppend -> current ?: emptyState()
            is TraceEvent.RunTerminal -> (current ?: emptyState()).copy(
                terminal = true,
                items = current?.items.orEmpty().map { item ->
                    if (item.status == AgentProgressStatus.Running) {
                        item.copy(status = event.status.toAgentProgressStatus())
                    } else {
                        item
                    }
                },
            )
        }
    }

    fun withFinalSummary(
        current: AgentProcessUiState?,
        summary: String,
    ): AgentProcessUiState? {
        val safeSummary = summary.takeIf { it.isNotBlank() && it.isSafeDisplayText() } ?: return current
        return current?.copy(
            terminal = true,
            expanded = false,
            finalSummary = safeSummary,
        )
    }

    private fun ChatStreamEvent.TaskProgress.safeTitle(): String {
        stepTitle?.takeIf { it.isSafeDisplayText() }?.let { return it }
        taskTitle?.takeIf { it.isSafeDisplayText() }?.let { return it }
        appInventoryTitle(phase, toolName)?.let { return it }
        displayTitleForTool(toolName ?: label)?.let { return it }
        return "执行受控操作"
    }

    private fun appInventoryTitle(phase: String, toolName: String?): String? =
        if (phase != "app_inventory_query" || toolName.isNullOrBlank()) {
            null
        } else {
            appInventoryTitles[toolName.normalizeToolKey()]
        }

    private fun displayTitleForTool(toolName: String): String? =
        toolTitles[toolName.normalizeToolKey()]

    private fun String.normalizeToolKey(): String =
        trim()
            .replace(Regex("([a-z])([A-Z])"), "$1_$2")
            .lowercase(Locale.US)
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')

    private fun String.toAgentProgressStatus(): AgentProgressStatus =
        when (lowercase(Locale.US)) {
            "pending", "queued" -> AgentProgressStatus.Pending
            "waiting_confirmation", "waiting_for_user", "needs_confirmation" -> AgentProgressStatus.WaitingConfirmation
            "completed", "complete", "done", "succeeded", "success", "end" -> AgentProgressStatus.Completed
            "failed", "error" -> AgentProgressStatus.Failed
            "cancelled", "canceled" -> AgentProgressStatus.Cancelled
            "taken_over", "take_over" -> AgentProgressStatus.TakenOver
            else -> AgentProgressStatus.Running
        }

    private fun TraceStepStatus.toTaskProgressStatus(): String =
        when (this) {
            TraceStepStatus.QUEUED -> "pending"
            TraceStepStatus.RUNNING -> "running"
            TraceStepStatus.SUCCEEDED -> "completed"
            TraceStepStatus.FAILED -> "failed"
            TraceStepStatus.CANCELLED -> "cancelled"
            TraceStepStatus.WAITING_FOR_USER -> "waiting_confirmation"
        }

    private fun TraceRunStatus.toAgentProgressStatus(): AgentProgressStatus =
        when (this) {
            TraceRunStatus.SUCCEEDED -> AgentProgressStatus.Completed
            TraceRunStatus.FAILED -> AgentProgressStatus.Failed
            TraceRunStatus.CANCELLED -> AgentProgressStatus.Cancelled
            TraceRunStatus.WAITING_FOR_USER -> AgentProgressStatus.WaitingConfirmation
            TraceRunStatus.INTERRUPTED -> AgentProgressStatus.Failed
            TraceRunStatus.RUNNING -> AgentProgressStatus.Running
        }

    private fun AgentProgressStatus.isTerminal(): Boolean =
        this in setOf(
            AgentProgressStatus.Completed,
            AgentProgressStatus.Failed,
            AgentProgressStatus.Cancelled,
            AgentProgressStatus.TakenOver,
        )

    private fun String.isSafeDisplayText(): Boolean {
        val lower = lowercase(Locale.US)
        if (lower.contains("traceback") || lower.contains("ui tree") || lower.contains("base64")) return false
        if (trim().startsWith("{") || trim().startsWith("[")) return false
        return !contains("<think", ignoreCase = true)
    }

    private fun emptyState(): AgentProcessUiState =
        AgentProcessUiState(
            taskTitle = "处理任务",
            items = emptyList(),
            expanded = true,
            terminal = false,
        )

    private val appInventoryTitles = mapOf(
        "app_inventory_intent" to "分析检索目标",
        "list_apps" to "读取手机应用列表",
        "app_inventory_filter" to "过滤检索结果",
        "finish" to "整理检索结果",
    )

    private val toolTitles = appInventoryTitles + mapOf(
        "observe" to "读取屏幕信息",
        "launch" to "打开应用",
        "search_files" to "查找会议记录",
        "read_text_file" to "读取会议内容",
        "llm_summary" to "生成会议纪要",
        "wecom_cli" to "发送到项目群",
        "feishu_cli" to "发送到项目群",
        "medical_travel_intent" to "识别就医需求",
        "read_user_memory" to "读取过往记忆",
        "weather_query" to "查询天气",
        "amap_mcp_tool" to "规划出行路线",
        "medical_travel_decision" to "形成出行决策",
        "needs_confirmation" to "等待确认",
        "create_event_update_reminders" to "创建日历提醒",
        "finish" to "完成",
        "tap" to "点击控件",
        "type" to "输入内容",
        "swipe" to "滑动页面",
        "scroll" to "滚动页面",
        "create_event" to "创建日程",
        "list_events" to "读取日程",
        "update_reminders" to "设置提醒",
        "get_location" to "获取当前位置",
        "interact" to "等待用户确认",
        "take_over" to "等待用户接管",
    )

    private const val MAX_PROGRESS_ITEMS = 50
}
