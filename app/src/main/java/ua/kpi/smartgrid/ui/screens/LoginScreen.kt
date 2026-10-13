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
import ua.kpi.smartgrid.data.DEMO_EMAIL
import ua.kpi.smartgrid.data.DEMO_PASSWORD
import ua.kpi.smartgrid.domain.Validator
import ua.kpi.smartgrid.ui.components.FormField
import ua.kpi.smartgrid.ui.components.LoadingButton
import ua.kpi.smartgrid.ui.components.MessageText
import ua.kpi.smartgrid.ui.components.ScreenTitle
import ua.kpi.smartgrid.viewmodel.AuthViewModel

/**
 * Екран входу. Дані перевіряються Validator, сам вхід виконує AuthViewModel.
 */
@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: (String) -> Unit, onRegisterClick: () -> Unit) {
    // rememberSaveable зберігає введені дані при повороті екрана
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var submitted by rememberSaveable { mutableStateOf(false) } // помилки показуємо після першої спроби

    // Стан із ViewModel (завантаження, помилка, повідомлення)
    val state by viewModel.uiState.collectAsState()

    val emailError = if (submitted) Validator.validateEmail(email) else null
    val passwordError = if (submitted) Validator.validateRequired(password, "Пароль") else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ScreenTitle("⚡ Smart Grid — вхід")

        FormField(email, { email = it }, "Електронна пошта", "Наприклад: operator@grid.ua",
            emailError, keyboardType = KeyboardType.Email)
        FormField(password, { password = it }, "Пароль", "", passwordError, isPassword = true)

        Spacer(modifier = Modifier.height(8.dp))

        LoadingButton("Увійти", state.isLoading) {
            submitted = true
            // Вхід лише якщо поля пройшли перевірку
            if (Validator.validateEmail(email) == null && password.isNotBlank()) {
                viewModel.login(email, password, onLoginSuccess)
            }
        }

        MessageText(state.error, state.message)

        TextButton(onClick = { viewModel.clearMessages(); onRegisterClick() }) {
            Text("Немає акаунта? Зареєструватися")
        }

        Text("Тестовий вхід: $DEMO_EMAIL / $DEMO_PASSWORD")
    }
}
