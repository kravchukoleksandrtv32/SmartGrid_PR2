package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.ui.components.InfoRow
import ua.kpi.smartgrid.ui.components.ScreenTitle
import ua.kpi.smartgrid.viewmodel.MonitoringUiState
import ua.kpi.smartgrid.viewmodel.MonitoringViewModel

/**
 * Моніторинг мережі (заняття 7, MVVM).
 * Екран не містить логіки: показує один із трьох станів ViewModel.
 */
@Composable
fun MonitoringScreen(viewModel: MonitoringViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ScreenTitle("Моніторинг мережі")

        when (val s = state) {
            // Завантаження
            is MonitoringUiState.Loading -> {
                CircularProgressIndicator()
                Text("Отримання даних з сервера…")
            }
            // Успіх
            is MonitoringUiState.Success -> {
                InfoRow("Навантаження", "${s.load} кВт")
                InfoRow("Частота", "%.2f Гц".format(s.frequency))
                InfoRow("Завантаження ПС", "%.0f %%".format(s.percent))
                InfoRow("Статус", s.status)
            }
            // Помилка
            is MonitoringUiState.Error -> {
                Text("Помилка: ${s.message}", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { viewModel.refresh() }) { Text("Оновити дані") }
        TextButton(onClick = onBack) { Text("Назад до меню") }
    }
}
