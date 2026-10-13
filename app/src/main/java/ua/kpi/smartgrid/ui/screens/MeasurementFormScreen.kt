package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.domain.Calculator
import ua.kpi.smartgrid.domain.Validator
import ua.kpi.smartgrid.ui.components.FormField
import ua.kpi.smartgrid.ui.components.InfoRow
import ua.kpi.smartgrid.ui.components.ScreenTitle
import ua.kpi.smartgrid.viewmodel.MeasurementViewModel

/**
 * Форма вимірювання: додавання (id = 0) або редагування запису.
 * Числові поля перевіряються на діапазон, результат розрахунку видно одразу.
 */
@Composable
fun MeasurementFormScreen(id: Int, viewModel: MeasurementViewModel, onDone: () -> Unit) {
    val substations by viewModel.substations.collectAsState()

    var substationId by rememberSaveable { mutableIntStateOf(0) }
    var voltage by rememberSaveable { mutableStateOf("0.4") }
    var current by rememberSaveable { mutableStateOf("") }
    var cosPhi by rememberSaveable { mutableStateOf("0.9") }
    var resistance by rememberSaveable { mutableStateOf("0.1") }

    // При редагуванні один раз завантажуємо запис з бази
    LaunchedEffect(id) {
        if (id != 0) {
            viewModel.getMeasurement(id)?.let { m ->
                substationId = m.substationId
                voltage = m.voltage.toString()
                current = m.current.toString()
                cosPhi = m.cosPhi.toString()
                resistance = m.resistance.toString()
            }
        }
    }

    // Перевірка діапазонів
    val voltageError = Validator.validateNumber(voltage, 0.1, 110.0)
    val currentError = Validator.validateNumber(current, 0.0, 5000.0)
    val cosPhiError = Validator.validateNumber(cosPhi, 0.0, 1.0)
    val resistanceError = Validator.validateNumber(resistance, 0.0, 10.0)
    val substationError = if (substationId == 0) "Оберіть підстанцію" else null
    val isValid = listOf(voltageError, currentError, cosPhiError, resistanceError, substationError).all { it == null }

    // Попередній розрахунок (якщо значення правильні)
    val u = voltage.replace(',', '.').toDoubleOrNull() ?: 0.0
    val i = current.replace(',', '.').toDoubleOrNull() ?: 0.0
    val c = cosPhi.replace(',', '.').toDoubleOrNull() ?: 0.0
    val r = resistance.replace(',', '.').toDoubleOrNull() ?: 0.0
    val power = Calculator.calculatePower(u, i, c)
    val losses = Calculator.calculateLosses(i, r)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        ScreenTitle(if (id == 0) "Нове вимірювання" else "Редагування")

        // Вибір підстанції
        Text("Підстанція")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            substations.forEach { s ->
                FilterChip(
                    selected = substationId == s.id,
                    onClick = { substationId = s.id },
                    label = { Text(s.name.removePrefix("ПС ")) }
                )
            }
        }
        if (substationError != null) Text(substationError, color = MaterialTheme.colorScheme.error)

        FormField(voltage, { voltage = it }, "Напруга U, кВ", "0,1 – 110 кВ", voltageError,
            keyboardType = KeyboardType.Decimal)
        FormField(current, { current = it }, "Струм I, А", "0 – 5000 А",
            if (current.isEmpty()) null else currentError, keyboardType = KeyboardType.Decimal)
        FormField(cosPhi, { cosPhi = it }, "cosφ", "0 – 1", cosPhiError, keyboardType = KeyboardType.Decimal)
        FormField(resistance, { resistance = it }, "Опір фази R, Ом", "0 – 10 Ом", resistanceError,
            keyboardType = KeyboardType.Decimal)

        // Результат розрахунку
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Column(modifier = Modifier.padding(12.dp)) {
                InfoRow("Потужність P", "%.2f кВт".format(power))
                InfoRow("Втрати ΔP", "%.2f кВт".format(losses))
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    viewModel.save(id, substationId, u, i, c, r)
                    onDone()
                },
                enabled = isValid
            ) { Text("Зберегти") }
            OutlinedButton(onClick = onDone) { Text("Скасувати") }
        }
    }
}
