package io.arvo.dataconso.ui.hotspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.model.HotspotStatistics
import io.arvo.dataconso.repository.HotspotRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HotspotHistoryState(
    val isLoading: Boolean = true,
    val isHotspotActive: Boolean = false,
    val sessions: List<HotspotSessionEntity> = emptyList(),
    val today: HotspotStatistics = HotspotStatistics(),
    val month: HotspotStatistics = HotspotStatistics(),
    val errorMessage: String? = null
)

@HiltViewModel
class HotspotHistoryViewModel @Inject constructor(
    private val hotspotRepository: HotspotRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HotspotHistoryState())
    val uiState: StateFlow<HotspotHistoryState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                hotspotRepository.getCurrentSession(),
                hotspotRepository.getAllSessions(),
                hotspotRepository.getTodayStatistics(),
                hotspotRepository.getMonthlyStatistics()
            ) { current, sessions, today, month ->
                HotspotHistoryState(
                    isLoading = false,
                    isHotspotActive = current != null,
                    sessions = sessions,
                    today = today,
                    month = month
                )
            }
                .catch { exception ->
                    _uiState.value = HotspotHistoryState(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
                .collect { _uiState.value = it }
        }
    }
}
