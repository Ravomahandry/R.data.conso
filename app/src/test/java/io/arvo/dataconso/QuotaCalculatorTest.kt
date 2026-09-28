package io.arvo.dataconso

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuotaCalculatorTest {

    @Test
    fun testCalculateDailyQuota() {
        val daily = QuotaCalculator.calculateDailyQuota(30.0, 10.0, 10)
        assertEquals(2.0, daily, 0.001)
    }

    @Test
    fun testCalculateDailyQuotaZeroRemaining() {
        val daily = QuotaCalculator.calculateDailyQuota(30.0, 10.0, 0)
        assertEquals(0.0, daily, 0.001)
    }

    @Test
    fun testCalculateDaysRemaining() {
        val days = QuotaCalculator.calculateDaysRemaining(1)
        assertTrue(days >= 1)
    }

    @Test
    fun testCalculateIdealDaily() {
        val ideal = QuotaCalculator.calculateIdealDaily(30.0, 1)
        assertTrue(ideal >= 0.0)
    }
}
