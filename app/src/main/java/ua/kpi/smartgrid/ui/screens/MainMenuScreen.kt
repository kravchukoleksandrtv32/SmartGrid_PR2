package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.ui.components.InfoRow
import ua.kpi.smartgrid.ui.components.MenuButton
import ua.kpi.smartgrid.ui.components.ScreenTitle
import ua.kpi.smartgrid.viewmodel.MeasurementViewModel

/**
 * Головне меню (заняття 5). Ім'я користувача приходить як аргумент навігації.
 * Картка зі зведенням оновлюється автоматично при поверненні з інших екранів.
 */
@Composable
fun MainMenuScreen(
    userName: String,
    viewModel: MeasurementViewModel,
    onMonitoring: () -> Unit,
    onCalculator: () -> Unit,
    onModes: () -> Unit,
    onMeasurements: () -> Unit,
    onNewMeasurement: () -> Unit,
    onLogout: () -> Unit
) {
    val summary by viewModel.summary.collectAsState()

    // BoxWithConstraints — щоб знати ширину екрана і зробити адаптивне меню
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth > 600.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScreenTitle("Вітаємо, $userName!")

            // Зведення з бази даних
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    InfoRow("Записів у журналі", summary.count.toString())
                    InfoRow("Середня потужність", "%.1f кВт".format(summary.averagePower))
                    InfoRow("Перевантажень", summary.overloadCount.toString())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isWide) {
                // Широкий екран: кнопки у два стовпці
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuButton("Моніторинг мережі", Modifier.weight(1f), onMonitoring)
                    MenuButton("Режими мережі", Modifier.weight(1f), onModes)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuButton("Розрахунок лінії", Modifier.weight(1f), onCalculator)
                    MenuButton("Журнал вимірювань", Modifier.weight(1f), onMeasurements)
                }
                MenuButton("Нове вимірювання", onClick = onNewMeasurement)
            } else {
                // Телефон: кнопки одна під одною
                MenuButton("Моніторинг мережі", onClick = onMonitoring)
                MenuButton("Режими мережі", onClick = onModes)
                MenuButton("Розрахунок лінії", onClick = onCalculator)
                MenuButton("Журнал вимірювань", onClick = onMeasurements)
                MenuButton("Нове вимірювання", onClick = onNewMeasurement)
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = onLogout) { Text("Вийти") }
        }
    }
}
