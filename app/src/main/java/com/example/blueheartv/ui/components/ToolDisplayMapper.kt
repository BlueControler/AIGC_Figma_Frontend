package com.example.blueheartv.ui.components

import com.example.blueheartv.model.ToolCall
import com.example.blueheartv.model.ToolCallStatus
import java.util.Locale

enum class ToolDisplayType {
    OBSERVE,
    LAUNCH,
    TAP,
    TYPE,
    SWIPE,
    LONG_PRESS,
    DOUBLE_TAP,
    KEY_EVENT,
    INTERACT,
    SYSTEM,
    GENERIC,
}

data class ToolDisplayInfo(
    val title: String,
    val subtitle: String,
    val statusText: String,
    val type: ToolDisplayType = ToolDisplayType.GENERIC,
)

private data class ToolDisplaySpec(
    val title: String,
    val runningSubtitle: String,
    val completedSubtitle: String,
    val type: ToolDisplayType,
)

private val toolDisplaySpecs = mapOf(
    "observe" to ToolDisplaySpec("读取屏幕信息", "正在读取当前界面", "已读取当前界面", ToolDisplayType.OBSERVE),
    "launch" to ToolDisplaySpec("打开应用", "正在打开目标应用", "已打开目标应用", ToolDisplayType.LAUNCH),
    "open_app" to ToolDisplaySpec("打开应用", "正在打开目标应用", "已打开目标应用", ToolDisplayType.LAUNCH),
    "start_app" to ToolDisplaySpec("打开应用", "正在打开目标应用", "已打开目标应用", ToolDisplayType.LAUNCH),
    "tap" to ToolDisplaySpec("点击控件", "正在点击目标位置", "已完成点击", ToolDisplayType.TAP),
    "tap_element" to ToolDisplaySpec("点击控件", "正在点击目标位置", "已完成点击", ToolDisplayType.TAP),
    "click" to ToolDisplaySpec("点击控件", "正在点击目标位置", "已完成点击", ToolDisplayType.TAP),
    "type" to ToolDisplaySpec("输入内容", "正在输入内容", "已完成输入", ToolDisplayType.TYPE),
    "input_text" to ToolDisplaySpec("输入内容", "正在输入内容", "已完成输入", ToolDisplayType.TYPE),
    "type_text" to ToolDisplaySpec("输入内容", "正在输入内容", "已完成输入", ToolDisplayType.TYPE),
    "swipe" to ToolDisplaySpec("滑动页面", "正在滑动页面", "已完成滑动", ToolDisplayType.SWIPE),
    "scroll" to ToolDisplaySpec("滚动页面", "正在滚动页面", "已完成滚动", ToolDisplayType.SWIPE),
    "longpress" to ToolDisplaySpec("长按屏幕", "正在长按目标位置", "已完成长按", ToolDisplayType.LONG_PRESS),
    "long_press" to ToolDisplaySpec("长按屏幕", "正在长按目标位置", "已完成长按", ToolDisplayType.LONG_PRESS),
    "doubletap" to ToolDisplaySpec("双击屏幕", "正在双击目标位置", "已完成双击", ToolDisplayType.DOUBLE_TAP),
    "double_tap" to ToolDisplaySpec("双击屏幕", "正在双击目标位置", "已完成双击", ToolDisplayType.DOUBLE_TAP),
    "keyevent" to ToolDisplaySpec("发送按键", "正在执行系统按键", "已完成按键操作", ToolDisplayType.KEY_EVENT),
    "key_event" to ToolDisplaySpec("发送按键", "正在执行系统按键", "已完成按键操作", ToolDisplayType.KEY_EVENT),
    "back" to ToolDisplaySpec("返回上一页", "正在返回上一页", "已返回上一页", ToolDisplayType.KEY_EVENT),
    "home" to ToolDisplaySpec("回到桌面", "正在回到桌面", "已回到桌面", ToolDisplayType.KEY_EVENT),
    "interact" to ToolDisplaySpec("等待用户确认", "需要你在手机上确认", "已收到确认", ToolDisplayType.INTERACT),
    "take_over" to ToolDisplaySpec("等待用户接管", "需要你手动完成当前步骤", "已继续执行", ToolDisplayType.INTERACT),
    "app_inventory_intent" to ToolDisplaySpec("分析检索目标", "正在分析需要检索的应用目标", "已完成检索目标分析", ToolDisplayType.SYSTEM),
    "list_apps" to ToolDisplaySpec("读取手机应用列表", "正在读取手机上的所有已安装应用", "已完成应用列表读取", ToolDisplayType.SYSTEM),
    "app_inventory_filter" to ToolDisplaySpec("过滤检索结果", "正在根据目标过滤检索结果", "已完成检索结果过滤", ToolDisplayType.SYSTEM),
    "search_files" to ToolDisplaySpec("查找会议资料", "正在查找会议资料", "已找到会议资料", ToolDisplayType.SYSTEM),
    "read_text_file" to ToolDisplaySpec("读取会议内容", "正在读取会议内容", "已读取会议内容", ToolDisplayType.SYSTEM),
    "llm_summary" to ToolDisplaySpec("生成会议纪要", "正在生成会议纪要", "已生成会议纪要", ToolDisplayType.SYSTEM),
    "wecom_cli" to ToolDisplaySpec("发送到项目群", "正在发送到项目群", "已发送到项目群", ToolDisplayType.SYSTEM),
    "feishu_cli" to ToolDisplaySpec("发送到项目群", "正在发送到项目群", "已发送到项目群", ToolDisplayType.SYSTEM),
    "medical_travel_intent" to ToolDisplaySpec("识别就医需求", "正在识别就医时间和医院", "已识别就医需求", ToolDisplayType.SYSTEM),
    "read_user_memory" to ToolDisplaySpec("读取过往记忆", "正在读取过往偏好", "已读取过往偏好", ToolDisplayType.SYSTEM),
    "weather_query" to ToolDisplaySpec("查询天气", "正在查询天气", "已完成天气查询", ToolDisplayType.SYSTEM),
    "amap_mcp_tool" to ToolDisplaySpec("规划路线", "正在规划路线", "已完成路线规划", ToolDisplayType.SYSTEM),
    "medical_travel_decision" to ToolDisplaySpec("形成出行决策", "正在结合偏好生成就医出行建议", "已形成出行决策", ToolDisplayType.SYSTEM),
    "needs_confirmation" to ToolDisplaySpec("等待确认", "正在等待确认", "已收到确认", ToolDisplayType.INTERACT),
    "create_event_update_reminders" to ToolDisplaySpec("创建日历提醒", "正在创建日历提醒", "已创建日历提醒", ToolDisplayType.SYSTEM),
    "finish" to ToolDisplaySpec("整理检索结果", "正在整理检索结果", "结果已生成", ToolDisplayType.SYSTEM),
    "create_event" to ToolDisplaySpec("创建日历事件", "正在创建日历事件", "已创建日历事件", ToolDisplayType.SYSTEM),
    "list_events" to ToolDisplaySpec("读取日程", "正在读取日程", "已读取日程", ToolDisplayType.SYSTEM),
    "update_event" to ToolDisplaySpec("更新日程", "正在更新日程", "已更新日程", ToolDisplayType.SYSTEM),
    "list_reminders" to ToolDisplaySpec("读取提醒", "正在读取提醒事项", "已读取提醒事项", ToolDisplayType.SYSTEM),
    "update_reminders" to ToolDisplaySpec("设置提醒", "正在设置提醒", "已设置提醒", ToolDisplayType.SYSTEM),
    "get_location" to ToolDisplaySpec("获取当前位置", "正在获取当前位置", "已获取当前位置", ToolDisplayType.SYSTEM),
)

fun displayToolCall(toolCall: ToolCall): ToolDisplayInfo {
    val spec = toolCall.displayKeys()
        .firstNotNullOfOrNull { key -> toolDisplaySpecs[key] }
        ?: genericSpecFor(toolCall.label)

    return ToolDisplayInfo(
        title = spec.title,
        subtitle = when (toolCall.status) {
            ToolCallStatus.RUNNING -> spec.runningSubtitle
            ToolCallStatus.COMPLETED -> spec.completedSubtitle
            ToolCallStatus.FAILED -> "操作未完成，可稍后重试"
        },
        statusText = when (toolCall.status) {
            ToolCallStatus.RUNNING -> "执行中"
            ToolCallStatus.COMPLETED -> "已完成"
            ToolCallStatus.FAILED -> "需要重试"
        },
        type = spec.type,
    )
}

private fun ToolCall.displayKeys(): List<String> =
    listOfNotNull(toolName, progressKey, label)
        .flatMap { value ->
            val normalized = value.normalizeToolKey()
            listOf(normalized, normalized.replace("_", ""))
        }
        .distinct()

private fun String.normalizeToolKey(): String =
    trim()
        .replace(Regex("([a-z])([A-Z])"), "$1_$2")
        .lowercase(Locale.US)
        .replace(Regex("[^a-z0-9]+"), "_")
        .trim('_')

private fun genericSpecFor(label: String): ToolDisplaySpec {
    return ToolDisplaySpec(
        title = "执行受控操作",
        runningSubtitle = "正在处理当前任务",
        completedSubtitle = "已完成当前任务",
        type = ToolDisplayType.GENERIC,
    )
}
