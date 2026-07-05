package com.example.blueheartv.model

data class AgentProgressItem(
    val id: String,
    val phase: String,
    val title: String,
    val message: String,
    val status: AgentProgressStatus,
    val current: Int? = null,
    val total: Int? = null,
    val toolName: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class AgentProgressStatus {
    Pending,
    Running,
    WaitingConfirmation,
    Completed,
    Failed,
    Cancelled,
    TakenOver,
}

data class AgentProcessUiState(
    val taskTitle: String,
    val items: List<AgentProgressItem>,
    val expanded: Boolean,
    val terminal: Boolean,
    val finalSummary: String? = null,
)

data class AppMatchUi(
    val name: String,
    val packageName: String,
)
