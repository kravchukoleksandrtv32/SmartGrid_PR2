package ua.kpi.smartgrid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ua.kpi.smartgrid.R
import ua.kpi.smartgrid.domain.Calculator

/**
 * Режими роботи мережі (екран з практичної роботи №1).
 */
@Composable
fun ModesScreen(onBack: () -> Unit) {
    var mode by remember { mutableStateOf("Денний") }
    var gridLoad by remember { mutableIntStateOf(5200) } // навантаження, кВт
    val onLoadChange: (Int) -> Unit = { gridLoad = it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Режими роботи мережі", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопки режимів у два рядки
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeButton("Денний") { mode = "Денний"; onLoadChange(5200) }
            ModeButton("Нічний") { mode = "Нічний"; onLoadChange(2100) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModeButton("Піковий") { mode = "Піковий"; onLoadChange(8700) }
            ModeButton("Аварія") { mode = "Аварія"; onLoadChange(10500) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Зображення: в аварійному режимі — знак попередження
        Image(
            painter = painterResource(
                id = if (mode == "Аварія") R.drawable.ic_grid_warning else R.drawable.ic_power_grid
            ),
            contentDescription = "Стан мережі",
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        InfoCard(mode = mode, load = gridLoad)

        TextButton(onClick = onBack) { Text("Назад до меню") }
    }
}

/** Кнопка режиму: текст і дія передаються параметрами. */
@Composable
fun ModeButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text)
    }
}

/** Картка з інформацією про поточний стан мережі. */
@Composable
fun InfoCard(mode: String, load: Int) {
    val percent = Calculator.calculateLoadPercent(load.toDouble(), 10000.0)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Режим: $mode")
            Text("Навантаження: $load кВт", fontWeight = FontWeight.Bold)
            Text("Завантаження підстанції: ${"%.0f".format(percent)} %")
            Text("Статус: ${Calculator.getLoadStatus(percent)}")
        }
    }
}
