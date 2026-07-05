package com.example.blueheartv.ui.components

import com.example.blueheartv.model.ToolCall
import com.example.blueheartv.model.ToolCallStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class ToolDisplayMapperTest {

    @Test
    fun displayToolCall_launchHidesPackageName() {
        val display = displayToolCall(
            ToolCall(
                label = "launch",
                status = ToolCallStatus.RUNNING,
            ),
        )

        assertEquals("打开应用", display.title)
        assertEquals("正在打开目标应用", display.subtitle)
        assertFalse(display.containsDebugText())
    }

    @Test
    fun displayToolCall_tapHidesCoordinates() {
        val display = displayToolCall(
            ToolCall(
                label = "tap",
                status = ToolCallStatus.COMPLETED,
            ),
        )

        assertEquals("点击控件", display.title)
        assertEquals("已完成", display.statusText)
        assertFalse(display.containsDebugText())
    }

    @Test
    fun displayToolCall_unknownTechnicalNameUsesGenericAction() {
        val display = displayToolCall(
            ToolCall(
                label = "mobile_agent_execute_intent",
                status = ToolCallStatus.FAILED,
            ),
        )

        assertEquals("执行受控操作", display.title)
        assertEquals("需要重试", display.statusText)
        assertFalse(display.containsDebugText())
    }

    @Test
    fun displayToolCall_listAppsUsesFullChineseTitle() {
        val display = displayToolCall(
            ToolCall(
                label = "list_apps",
                toolName = "list_apps",
                status = ToolCallStatus.RUNNING,
            ),
        )

        assertEquals("读取手机应用列表", display.title)
        assertEquals("正在读取手机上的所有已安装应用", display.subtitle)
        assertFalse(display.title.contains("list_apps"))
    }

    @Test
    fun displayToolCall_medicalTravelDecisionUsesChineseTitle() {
        val display = displayToolCall(
            ToolCall(
                label = "medical_travel_decision",
                toolName = "medical_travel_decision",
                status = ToolCallStatus.RUNNING,
            ),
        )

        assertEquals("形成出行决策", display.title)
        assertEquals("正在结合偏好生成就医出行建议", display.subtitle)
        assertFalse(display.title.contains("medical_travel"))
    }

    @Test
    fun displayToolCall_meetingMinutesToolsUseChineseTitles() {
        val searchDisplay = displayToolCall(
            ToolCall(
                label = "search_files",
                toolName = "search_files",
                status = ToolCallStatus.RUNNING,
            ),
        )
        val sendDisplay = displayToolCall(
            ToolCall(
                label = "wecom_cli",
                toolName = "wecom_cli",
                status = ToolCallStatus.COMPLETED,
            ),
        )

        assertEquals("查找会议资料", searchDisplay.title)
        assertEquals("正在查找会议资料", searchDisplay.subtitle)
        assertEquals("发送到项目群", sendDisplay.title)
        assertEquals("已发送到项目群", sendDisplay.subtitle)
        assertFalse(searchDisplay.title.contains("search_files"))
        assertFalse(sendDisplay.title.contains("wecom_cli"))
    }

    @Test
    fun displayToolCall_requiredDemoToolsUseChineseMappings() {
        val expectedTitles = mapOf(
            "search_files" to "查找会议资料",
            "read_text_file" to "读取会议内容",
            "llm_summary" to "生成会议纪要",
            "wecom_cli" to "发送到项目群",
            "feishu_cli" to "发送到项目群",
            "get_location" to "获取当前位置",
            "weather_query" to "查询天气",
            "amap_mcp_tool" to "规划路线",
            "create_event" to "创建日历事件",
            "update_reminders" to "设置提醒",
        )

        expectedTitles.forEach { (toolName, title) ->
            val display = displayToolCall(
                ToolCall(
                    label = toolName,
                    toolName = toolName,
                    status = ToolCallStatus.RUNNING,
                ),
            )

            assertEquals(title, display.title)
            assertFalse(display.title.contains(toolName))
        }
    }

    @Test
    fun displayToolCall_unknownPlainEnglishToolNeverLeaksToolName() {
        val display = displayToolCall(
            ToolCall(
                label = "summarize_apps",
                status = ToolCallStatus.RUNNING,
            ),
        )

        assertEquals("执行受控操作", display.title)
        assertEquals("正在处理当前任务", display.subtitle)
        assertFalse(display.title.contains("summarize_apps"))
    }

    private fun ToolDisplayInfo.containsDebugText(): Boolean {
        val visible = listOfNotNull(title, subtitle, statusText).joinToString(" ")
        val debugMarkers = listOf(
            "{",
            "}",
            "\"x\"",
            "\"y\"",
            "package",
            "com.",
            "Intent",
            "Activity",
            "shell",
            "ADB",
            "currentPackage",
        )
        return debugMarkers.any { marker -> visible.contains(marker, ignoreCase = true) }
    }
}
