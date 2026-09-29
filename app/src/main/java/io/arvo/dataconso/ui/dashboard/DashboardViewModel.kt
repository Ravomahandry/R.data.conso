package io.arvo.dataconso.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.arvo.dataconso.RealTimeData
import io.arvo.dataconso.ai.ConsumptionPredictor
import io.arvo.dataconso.domain.usecase.GetDashboardDataUseCase
import io.arvo.dataconso.repository.HotspotRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val consumptionPredictor: ConsumptionPredictor,
    private val hotspotRepository: HotspotRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    private var dataJob: Job? = null

    init {
        startObservingData()
        observeRealTimeSpeeds()
        observeHotspot()
    }

    private fun observeHotspot() {
        viewModelScope.launch {
            combine(
                hotspotRepository.getCurrentSession(),
                hotspotRepository.getTodayStatistics(),
                hotspotRepository.getMonthlyStatistics()
            ) { current, today, month ->
                DashboardHotspotData(
                    isActive = current != null,
                    todayBytes = today.totalBytes,
                    todaySessions = today.sessionCount,
                    monthBytes = month.totalBytes
                )
            }.collect { hotspot ->
                _uiState.update { it.copy(hotspot = hotspot) }
            }
        }
    }

    private fun startObservingData() {
        dataJob?.cancel()
        dataJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Re-trigger flow when source or granularity changes
            combine(
                _uiState.map { it.currentSource }.distinctUntilChanged(),
                _uiState.map { it.currentGranularity }.distinctUntilChanged()
            ) { source, gran ->
                source to gran
            }.flatMapLatest { (source, gran) ->
                getDashboardDataUseCase(source, gran)
            }.collect { data ->
                val predictions = if (data.history.size >= 10) {
                    val historyMo = data.history.map { it.bytes / (1024.0 * 1024.0) }
                    consumptionPredictor.predict7Days(historyMo)
                } else emptyList()

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        usageData = data.usage.copy(
                            downloadSpeedBps = state.usageData.downloadSpeedBps,
                            uploadSpeedBps = state.usageData.uploadSpeedBps
                        ),
                        history = data.history,
                        predictions = predictions,
                        topApps = data.topApps
                    )
                }
            }
        }
    }

    private fun observeRealTimeSpeeds() {
        viewModelScope.launch {
            combine(RealTimeData.dlSpeed, RealTimeData.ulSpeed) { dl, ul ->
                dl to ul
            }.collect { (dl, ul) ->
                _uiState.update { state ->
                    state.copy(usageData = state.usageData.copy(
                        downloadSpeedBps = dl,
                        uploadSpeedBps = ul
                    ))
                }
            }
        }
    }

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.Refresh -> startObservingData()
            is DashboardIntent.SelectSource -> {
                _uiState.update { it.copy(currentSource = intent.source) }
                // Sommité : On force le rafraîchissement immédiat pour synchroniser les deux onglets
                startObservingData()
            }
            is DashboardIntent.SelectGranularity -> {
                _uiState.update { it.copy(currentGranularity = intent.granularity) }
                startObservingData()
            }
        }
    }
}
