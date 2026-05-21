package com.datamgmt.myapplication

import org.junit.Assert.*
import org.junit.Test

class QuotaCalculatorTest {

    @Test
    fun calculateDailyQuota_normalCase() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 10.0,
            remainingDays = 20
        )
        assertEquals(1.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_allDataUsed() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 30.0,
            remainingDays = 10
        )
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_overQuota() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 35.0,
            remainingDays = 10
        )
        assertEquals(-0.5, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_zeroDaysRemaining() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 10.0,
            remainingDays = 0
        )
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_negativeDays() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 10.0,
            remainingDays = -1
        )
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_oneDay() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 25.0,
            remainingDays = 1
        )
        assertEquals(5.0, daily, 0.001)
    }

    @Test
    fun calculateDailyQuota_noUsage() {
        val daily = QuotaCalculator.calculateDailyQuota(
            monthlyQuotaGb = 30.0,
            usedGb = 0.0,
            remainingDays = 30
        )
        assertEquals(1.0, daily, 0.001)
    }
}
