package ua.kpi.smartgrid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ua.kpi.smartgrid.data.GridRepository

/** Стан екранів входу та реєстрації. */
data class AuthUiState(
    val isLoading: Boolean = false,  // показувати індикатор завантаження
    val error: String? = null,       // текст помилки від сервера / бази
    val message: String? = null      // повідомлення про успіх
)

/**
 * ViewModel для входу і реєстрації.
 * View (екран) лише викликає login()/register() і показує стан uiState.
 */
class AuthViewModel(private val repository: GridRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { repository.ensureDemoUser() }
    }

    // Вхід; при успіху викликає onSuccess з ім'ям користувача
    fun login(email: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            delay(700) // імітація запиту
            val user = repository.login(email, password)
            if (user != null) {
                _uiState.value = AuthUiState()
                onSuccess(user.name)
            } else {
                _uiState.value = AuthUiState(error = "Невірна пошта або пароль")
            }
        }
    }

    // Реєстрація; repository перевіряє унікальність email у базі
    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            delay(700)
            val result = repository.register(name, email, password)
            result.onSuccess {
                _uiState.value = AuthUiState(message = "Реєстрацію завершено, увійдіть")
                onSuccess()
            }.onFailure { e ->
                _uiState.value = AuthUiState(error = e.message)
            }
        }
    }

    fun clearMessages() {
        _uiState.value = AuthUiState()
    }
}
