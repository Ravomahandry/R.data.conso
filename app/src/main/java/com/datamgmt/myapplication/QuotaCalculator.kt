package com.datamgmt.myapplication

object QuotaCalculator {

    fun calculateDailyQuota(
        monthlyQuotaGb: Double,
        usedGb: Double,
        remainingDays: Int
    ): Double {

        val remainingData =
            monthlyQuotaGb - usedGb

        if (remainingDays <= 0) {
            return 0.0
        }

        return remainingData / remainingDays
    }
}