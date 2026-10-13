package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.domain.Calculator

/**
 * Розрахунок лінії електропередачі (практичне заняття 2).
 * Бізнес-логіка винесена в Calculator (domain/Calculator.kt). Екран з практичної роботи №1.
 */
@Composable
fun CalculatorScreen(onBack: () -> Unit) {
    // Стан полів введення
    var voltage by remember { mutableStateOf("0.4") }
    var current by remember { mutableStateOf("") }
    var cosPhi by remember { mutableStateOf("0.9") }
    var resistance by remember { mutableStateOf("") }
    // Стан результатів
    var power by remember { mutableStateOf(0.0) }
    var losses by remember { mutableStateOf(0.0) }

    val calculator = Calculator
    val efficiency = if (power > 0) (power - losses) / power * 100 else 0.0 // ККД, %

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Розрахунок лінії", style = MaterialTheme.typography.titleLarge)

        TextField(value = voltage, onValueChange = { voltage = it }, label = { Text("Напруга (кВ)") })
        TextField(value = current, onValueChange = { current = it }, label = { Text("Струм (А)") })
        TextField(value = cosPhi, onValueChange = { cosPhi = it }, label = { Text("cosφ") })
        TextField(value = resistance, onValueChange = { resistance = it }, label = { Text("Опір лінії (Ом)") })

        Button(onClick = {
            // Некоректне введення перетворюємо на 0.0
            val i = current.toDoubleOrNull() ?: 0.0
            power = calculator.calculatePower(voltage.toDoubleOrNull() ?: 0.0, i, cosPhi.toDoubleOrNull() ?: 0.0)
            losses = calculator.calculateLosses(i, resistance.toDoubleOrNull() ?: 0.0)
        }) {
            Text("Розрахувати")
        }

        Text("Потужність: ${"%.2f".format(power)} кВт")
        Text("Втрати в лінії: ${"%.2f".format(losses)} кВт")
        Text("Потужність у споживачів: ${"%.2f".format(power - losses)} кВт")
        Text("ККД передачі: ${"%.1f".format(efficiency)} %", color = MaterialTheme.colorScheme.primary)

        // Візуалізація ККД
        LinearProgressIndicator(
            progress = { (efficiency / 100).toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        TextButton(onClick = onBack) { Text("Назад до меню") }
    }
}
