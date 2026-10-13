package ua.kpi.smartgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.kpi.smartgrid.data.GridRepository
import ua.kpi.smartgrid.domain.Calculator


sealed class MonitoringUiState {
    object Loading : MonitoringUiState()
    data class Success(
        val load: Int,          // навантаження, кВт
        val frequency: Double,  // частота, Гц
        val percent: Double,    // завантаження, %
        val status: String     // статус мережі
    ) : MonitoringUiState()
    data class Error(val message: String) : MonitoringUiState()
}

class MonitoringViewModel(private val repository: GridRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MonitoringUiState>(MonitoringUiState.Loading)
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        refresh() // перше завантаження при відкритті екрана
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = MonitoringUiState.Loading
            try {
                val reading = repository.fetchReading()
                val percent = Calculator.calculateLoadPercent(reading.load.toDouble(), 10000.0)
                _uiState.value = MonitoringUiState.Success(
                    load = reading.load,
                    frequency = reading.frequency,
                    percent = percent,
                    status = Calculator.getLoadStatus(percent)
                )
            } catch (e: Exception) {
                _uiState.value = MonitoringUiState.Error(e.message ?: "Невідома помилка")
            }
        }
    }
}
