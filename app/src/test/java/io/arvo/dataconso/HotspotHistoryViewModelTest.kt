package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.repository.HotspotClock
import io.arvo.dataconso.repository.HotspotBatteryProvider
import io.arvo.dataconso.repository.HotspotQuotaProvider
import io.arvo.dataconso.repository.HotspotRepository
import io.arvo.dataconso.repository.HotspotTrafficCounters
import io.arvo.dataconso.ui.hotspot.HotspotHistoryViewModel
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.lifecycle.ViewModelStore

@OptIn(ExperimentalCoroutinesApi::class)
class HotspotHistoryViewModelTest {

    @Test
    fun stateExposesActiveStatusDailyMonthlyTotalsAndHistory() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModelStore = ViewModelStore()
        try {
            val dao = FakeHotspotSessionDao()
            val now = Calendar.getInstance().apply {
                set(2025, Calendar.JUNE, 10, 10, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            dao.insert(
                HotspotSessionEntity(
                    startTimestamp = now - 10_000,
                    endTimestamp = now - 1_000,
                    durationMillis = 9_000,
                    totalBytes = 50,
                    sessionId = "closed"
                )
            )
            dao.insert(
                HotspotSessionEntity(
                    startTimestamp = now,
                    sessionId = "active"
                )
            )

            val repository = HotspotRepository(
                dao,
                object : HotspotTrafficCounters {
                    override fun rxBytes() = 0L
                    override fun txBytes() = 0L
                },
                object : HotspotClock {
                    override fun nowMillis() = now
                }
            )
            val viewModel = HotspotHistoryViewModel(
                repository,
                object : HotspotQuotaProvider {
                    override fun monthlyQuotaBytes() = flowOf(5_000L)
                },
                object : HotspotBatteryProvider {
                    override fun batteryPercent() = 80
                }
            )
            viewModelStore.put("hotspot-history", viewModel)
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertTrue(state.isHotspotActive)
            assertEquals(2, state.sessions.size)
            assertEquals(2, state.today.sessionCount)
            assertEquals(50L, state.today.totalBytes)
            assertEquals(2, state.month.sessionCount)
        } finally {
            viewModelStore.clear()
            Dispatchers.resetMain()
        }
    }
}
