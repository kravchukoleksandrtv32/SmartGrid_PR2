package ua.kpi.smartgrid.navigation

import android.net.Uri

/**
 * Маршрути застосунку (sealed class — усі екрани в одному місці).
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    // Головне меню отримує ім'я користувача як аргумент
    object Menu : Screen("menu/{userName}") {
        fun create(userName: String) = "menu/${Uri.encode(userName)}"
    }

    object Monitoring : Screen("monitoring")
    object Calculator : Screen("calculator")
    object Modes : Screen("modes")
    object Measurements : Screen("measurements")

    // Форма вимірювання: id = 0 — новий запис, інакше — редагування
    object MeasurementForm : Screen("measurement_form/{id}") {
        fun create(id: Int) = "measurement_form/$id"
    }
}
