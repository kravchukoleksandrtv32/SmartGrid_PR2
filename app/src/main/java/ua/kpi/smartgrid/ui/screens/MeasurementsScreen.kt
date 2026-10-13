package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.data.local.MeasurementWithSubstation
import ua.kpi.smartgrid.domain.Calculator
import ua.kpi.smartgrid.ui.components.ScreenTitle
import ua.kpi.smartgrid.viewmodel.MeasurementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Журнал вимірювань (заняття 8): список з бази Room з пошуком, сортуванням,
 * видаленням і переходом до редагування.
 */
@Composable
fun MeasurementsScreen(
    viewModel: MeasurementViewModel,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val list by viewModel.measurements.collectAsState()
    val query by viewModel.query.collectAsState()
    val sortByPower by viewModel.sortByPower.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ScreenTitle("Журнал вимірювань")

        // Пошук за назвою підстанції
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.query.value = it },
            label = { Text("Пошук за підстанцією") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Сортування
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            FilterChip(selected = !sortByPower, onClick = { viewModel.sortByPower.value = false },
                label = { Text("За датою") })
            FilterChip(selected = sortByPower, onClick = { viewModel.sortByPower.value = true },
                label = { Text("За потужністю") })
        }

        if (error != null) Text("Довідник підстанцій: $error", color = MaterialTheme.colorScheme.error)

        if (list.isEmpty()) {
            Text("Записів немає. Додайте перше вимірювання.", modifier = Modifier.padding(vertical = 16.dp))
        }

        // Список записів; займає весь вільний простір
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list, key = { it.measurement.id }) { item ->
                MeasurementCard(item, onClick = { onEdit(item.measurement.id) },
                    onDelete = { viewModel.delete(item.measurement) })
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAdd) { Text("Додати") }
            OutlinedButton(onClick = onBack) { Text("Назад") }
        }
    }
}

/** Картка одного вимірювання. Натискання — редагування, кошик — видалення. */
@Composable
fun MeasurementCard(item: MeasurementWithSubstation, onClick: () -> Unit, onDelete: () -> Unit) {
    val m = item.measurement
    val percent = Calculator.calculateLoadPercent(m.power, item.capacityKw)
    val date = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault()).format(Date(m.createdAt))

    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.substationName, fontWeight = FontWeight.Bold)
                Text("P = ${"%.1f".format(m.power)} кВт, ΔP = ${"%.2f".format(m.losses)} кВт")
                Text("${"%.0f".format(percent)} % — ${Calculator.getLoadStatus(percent)}")
                Text(date, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Видалити")
            }
        }
    }
}
