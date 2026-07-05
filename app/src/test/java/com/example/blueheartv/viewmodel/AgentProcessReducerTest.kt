package com.example.blueheartv.viewmodel

import com.example.blueheartv.chat.ChatStreamEvent
import com.example.blueheartv.model.AgentProcessUiState
import com.example.blueheartv.model.AgentProgressStatus
import com.example.blueheartv.model.TraceEvent
import com.example.blueheartv.model.TraceRunStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AgentProcessReducerTest {

    @Test
    fun taskProgress_appInventoryUsesChineseToolTitlesAndUpdatesSameProgressKey() {
        val running = AgentProcessReducer.reduceTaskProgress(
            current = null,
            event = ChatStreamEvent.TaskProgress(
                label = "list_apps",
                status = "running",
                phase = "app_inventory_query",
                toolName = "list_apps",
                progressKey = "apps-read",
                currentStep = 2,
                totalSteps = 4,
                message = "正在读取手机上的所有已安装应用。",
            ),
        )
        val completed = AgentProcessReducer.reduceTaskProgress(
            current = running,
            event = ChatStreamEvent.TaskProgress(
                label = "list_apps",
                status = "completed",
                phase = "app_inventory_query",
                toolName = "list_apps",
                progressKey = "apps-read",
                currentStep = 2,
                totalSteps = 4,
                message = "已完成应用列表读取，共读取 126 个应用。",
            ),
        )

        assertEquals(1, completed.items.size)
        val item = completed.items.single()
        assertEquals("读取手机应用列表", item.title)
        assertEquals("已完成应用列表读取，共读取 126 个应用。", item.message)
        assertEquals(AgentProgressStatus.Completed, item.status)
        assertFalse(item.title.contains("list_apps"))
        assertFalse(item.message.contains("list_apps"))
    }

    @Test
    fun taskProgress_unknownToolUsesControlledOperationWhenBackendDoesNotProvideText() {
        val state = AgentProcessReducer.reduceTaskProgress(
            current = null,
            event = ChatStreamEvent.TaskProgress(
                label = "raw_internal_tool",
                status = "running",
                phase = "phone_tool",
                toolName = "raw_internal_tool",
            ),
        )

        val item = state.items.single()
        assertEquals("执行受控操作", item.title)
        assertEquals("执行受控操作", item.message)
        assertFalse(item.title.contains("raw_internal_tool"))
        assertFalse(item.message.contains("raw_internal_tool"))
    }

    @Test
    fun taskProgressKeepsOnlyLatestFiftyItems() {
        val state: AgentProcessUiState = (1..55).fold(null as AgentProcessUiState?) { current, index ->
            AgentProcessReducer.reduceTaskProgress(
                current = current,
                event = ChatStreamEvent.TaskProgress(
                    label = "步骤 $index",
                    status = "running",
                    phase = "phase",
                    stepTitle = "步骤 $index",
                    progressKey = "step-$index",
                ),
            )
        }!!

        assertEquals(50, state.items.size)
        assertEquals("step-6", state.items.first().id)
        assertEquals("step-55", state.items.last().id)
    }

    @Test
    fun traceRunStartedAndTerminalMapToProcessStateWithoutDetails() {
        val started = AgentProcessReducer.reduceTrace(
            current = null,
            event = TraceEvent.RunStarted(
                runId = "run-1",
                eventId = "evt-1",
                seq = 1,
                summary = "正在理解你的请求。",
            ),
        )
        val terminal = AgentProcessReducer.reduceTrace(
            current = started,
            event = TraceEvent.RunTerminal(
                runId = "run-1",
                eventId = "evt-2",
                seq = 2,
                status = TraceRunStatus.SUCCEEDED,
            ),
        )

        assertEquals("分析请求", started.items.single().title)
        assertEquals("正在理解你的请求。", started.items.single().message)
        assertTrue(terminal.terminal)
    }
}
