package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.domain.Validator
import ua.kpi.smartgrid.ui.components.FormField
import ua.kpi.smartgrid.ui.components.LoadingButton
import ua.kpi.smartgrid.ui.components.MessageText
import ua.kpi.smartgrid.ui.components.ScreenTitle
import ua.kpi.smartgrid.viewmodel.AuthViewModel

/**
 * Форма реєстрації оператора (заняття 6).
 * Під кожним полем — підказка; при помилці вона змінюється на червоний текст.
 * Кнопка активна лише коли всі поля правильні. Унікальність email перевіряє база даних.
 */
@Composable
fun RegisterScreen(viewModel: AuthViewModel, onRegistered: () -> Unit, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf("") }

    val state by viewModel.uiState.collectAsState()

    // Помилки рахуються одразу під час введення (поле ще порожнє — помилки не показуємо)
    val nameError = if (name.isEmpty()) null else Validator.validateRequired(name, "Ім'я")
    val emailError = if (email.isEmpty()) null else Validator.validateEmail(email)
    val passwordError = if (password.isEmpty()) null else Validator.validatePassword(password)
    val confirmError = if (confirm.isEmpty()) null else Validator.validateConfirm(password, confirm)

    // Форма правильна, якщо всі поля заповнені і без помилок
    val isFormValid = listOf(name, email, password, confirm).all { it.isNotBlank() } &&
            listOf(nameError, emailError, passwordError, confirmError).all { it == null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ScreenTitle("Реєстрація оператора")

        FormField(name, { name = it }, "Ім'я", "Як до вас звертатися", nameError)
        FormField(email, { email = it }, "Електронна пошта", "Буде логіном для входу", emailError,
            keyboardType = KeyboardType.Email)
        FormField(password, { password = it }, "Пароль", "Щонайменше 8 символів, літери й цифри",
            passwordError, isPassword = true)
        FormField(confirm, { confirm = it }, "Підтвердження пароля", "Повторіть пароль",
            confirmError, isPassword = true)

        Spacer(modifier = Modifier.height(8.dp))

        LoadingButton("Зареєструватися", state.isLoading, enabled = isFormValid) {
            viewModel.register(name, email, password, onRegistered)
        }

        MessageText(state.error)

        // Row: дві текстові кнопки поруч
        Row {
            TextButton(onClick = { viewModel.clearMessages(); onBack() }) { Text("Назад до входу") }
        }
    }
}
