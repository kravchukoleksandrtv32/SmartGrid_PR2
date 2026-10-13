package ua.kpi.smartgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ua.kpi.smartgrid.data.GridRepository
import ua.kpi.smartgrid.data.MeasurementSummary
import ua.kpi.smartgrid.data.local.MeasurementEntity
import ua.kpi.smartgrid.data.local.MeasurementWithSubstation
import ua.kpi.smartgrid.data.local.SubstationEntity

/**
 * ViewModel журналу вимірювань: пошук, сортування, зведення,
 * додавання, редагування і видалення записів у Room.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MeasurementViewModel(private val repository: GridRepository) : ViewModel() {

    // Параметри пошуку і сортування (змінює UI)
    val query = MutableStateFlow("")
    val sortByPower = MutableStateFlow(false)

    // Помилка завантаження довідника підстанцій
    val error = MutableStateFlow<String?>(null)

    // Список вимірювань: при зміні query або sortByPower виконується новий запит до Room
    val measurements: StateFlow<List<MeasurementWithSubstation>> =
        combine(query, sortByPower) { q, s -> q to s }
            .flatMapLatest { (q, s) -> repository.measurements(q, s) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Зведені показники рахуються з поточного списку
    val summary: StateFlow<MeasurementSummary> =
        measurements.map { repository.summarize(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MeasurementSummary())

    val substations: StateFlow<List<SubstationEntity>> =
        repository.substations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.refreshSubstations().onFailure { error.value = it.message }
        }
    }

    suspend fun getMeasurement(id: Int): MeasurementEntity? = repository.getMeasurement(id)

    fun save(id: Int, substationId: Int, voltage: Double, current: Double, cosPhi: Double, resistance: Double) {
        viewModelScope.launch {
            repository.saveMeasurement(id, substationId, voltage, current, cosPhi, resistance)
        }
    }

    fun delete(measurement: MeasurementEntity) {
        viewModelScope.launch { repository.deleteMeasurement(measurement) }
    }
}
