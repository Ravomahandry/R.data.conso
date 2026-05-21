package com.datamgmt.myapplication

import org.junit.Assert.*
import org.junit.Test

class QuotaCalculatorTest {

    @Test
    fun calculateDailyQuota_defaultQuota() {
        // 4.5 Go / 30 days = 0.15 Go/day
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 0.0,
            remainingDays = 30
        )
        assertEquals(0.15, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_withRollover() {
        // 4.5 Go quota, 1.0 Go used in first 10 days, 20 days remaining
        // Remaining = 3.5 Go / 20 days = 0.175 Go/day (rollover effect)
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 1.0,
            remainingDays = 20
        )
        assertEquals(0.175, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_unusedDaysGiveMore() {
        // If user used only 0.5 Go in 20 days, 10 days remaining
        // Remaining = 4.0 Go / 10 days = 0.4 Go/day (unused data rolls over)
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 0.5,
            remainingDays = 10
        )
        assertEquals(0.4, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_allDataUsed() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 4.5,
            remainingDays = 10
        )
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_overQuota() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 5.0,
            remainingDays = 10
        )
        assertEquals(-0.05, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_zeroDaysRemaining() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 2.0,
            remainingDays = 0
        )
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_negativeDays() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 2.0,
            remainingDays = -1
        )
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_lastDay() {
        // Last day, 1.0 Go used, 3.5 Go remaining for today
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 1.0,
            remainingDays = 1
        )
        assertEquals(3.5, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_noUsage() {
        // Full month remaining, no usage
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 4.5,
            usedGb = 0.0,
            remainingDays = 15
        )
        assertEquals(0.3, daily, 0.001)
    }
}
