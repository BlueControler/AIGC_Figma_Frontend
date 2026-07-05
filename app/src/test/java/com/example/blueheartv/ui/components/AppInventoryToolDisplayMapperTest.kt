package com.example.blueheartv.ui.components

import com.example.blueheartv.model.ToolCall
import com.example.blueheartv.model.ToolCallStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppInventoryToolDisplayMapperTest {

    @Test
    fun displayToolCall_appInventoryToolsUseChineseCopy() {
        val intent = displayToolCall(ToolCall(label = "app_inventory_intent", status = ToolCallStatus.RUNNING))
        val filter = displayToolCall(ToolCall(label = "app_inventory_filter", status = ToolCallStatus.RUNNING))
        val finish = displayToolCall(ToolCall(label = "finish", status = ToolCallStatus.COMPLETED))

        assertEquals("分析检索目标", intent.title)
        assertEquals("过滤检索结果", filter.title)
        assertEquals("整理检索结果", finish.title)
        assertFalse(intent.containsDebugText())
        assertFalse(filter.containsDebugText())
        assertFalse(finish.containsDebugText())
    }

    private fun ToolDisplayInfo.containsDebugText(): Boolean {
        val visible = listOf(title, subtitle, statusText).joinToString(" ")
        val debugMarkers = listOf("app_inventory", "list_apps", "toolName", "raw", "com.")
        return debugMarkers.any { marker -> visible.contains(marker, ignoreCase = true) }
    }
}
