package io.arvo.dataconso.domain.model

data class UsageData(
    val downloadBytes: Long = 0L,
    val uploadBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val remainingGb: Double = 0.0,
    val dailyQuotaGb: Double = 0.0,
    val todayUsedGb: Double = 0.0,
    val progress: Float = 0f,
    val isLimitExceeded: Boolean = false,
    val downloadSpeedBps: Long = 0L,
    val uploadSpeedBps: Long = 0L
)
