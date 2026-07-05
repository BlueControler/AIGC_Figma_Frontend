package com.example.blueheartv.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class AppInventoryResultParserTest {

    @Test
    fun parseAppInventoryMatches_extractsChineseNamesAndPackageNames() {
        val matches = parseAppInventoryMatches(
            """
            共找到 2 个浏览器类应用：
            1. Chrome（com.android.chrome）
            2. 系统浏览器（com.vivo.browser）
            """.trimIndent(),
        )

        assertEquals(2, matches.size)
        assertEquals("Chrome", matches[0].name)
        assertEquals("com.android.chrome", matches[0].packageName)
        assertEquals("系统浏览器", matches[1].name)
        assertEquals("com.vivo.browser", matches[1].packageName)
    }
}
