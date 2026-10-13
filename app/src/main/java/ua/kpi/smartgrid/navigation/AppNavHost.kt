package ua.kpi.smartgrid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ua.kpi.smartgrid.SmartGridApplication
import ua.kpi.smartgrid.ui.screens.CalculatorScreen
import ua.kpi.smartgrid.ui.screens.LoginScreen
import ua.kpi.smartgrid.ui.screens.MainMenuScreen
import ua.kpi.smartgrid.ui.screens.MeasurementFormScreen
import ua.kpi.smartgrid.ui.screens.MeasurementsScreen
import ua.kpi.smartgrid.ui.screens.ModesScreen
import ua.kpi.smartgrid.ui.screens.MonitoringScreen
import ua.kpi.smartgrid.ui.screens.RegisterScreen
import ua.kpi.smartgrid.viewmodel.AuthViewModel
import ua.kpi.smartgrid.viewmodel.MeasurementViewModel
import ua.kpi.smartgrid.viewmodel.MonitoringViewModel

/**
 * Граф навігації: усі екрани та переходи між ними.
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    // Репозиторій беремо з класу застосунку
    val repository = (LocalContext.current.applicationContext as SmartGridApplication).repository

    // ViewModel, спільні для кількох екранів
    val authViewModel: AuthViewModel = viewModel { AuthViewModel(repository) }
    val measurementViewModel: MeasurementViewModel = viewModel { MeasurementViewModel(repository) }

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { userName ->
                    // Переходимо в меню і прибираємо екран входу зі стеку
                    navController.navigate(Screen.Menu.create(userName)) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegistered = { navController.popBackStack() }, // назад до входу
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Menu.route,
            arguments = listOf(navArgument("userName") { type = NavType.StringType })
        ) { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Оператор"
            MainMenuScreen(
                userName = userName,
                viewModel = measurementViewModel,
                onMonitoring = { navController.navigate(Screen.Monitoring.route) },
                onCalculator = { navController.navigate(Screen.Calculator.route) },
                onModes = { navController.navigate(Screen.Modes.route) },
                onMeasurements = { navController.navigate(Screen.Measurements.route) },
                onNewMeasurement = { navController.navigate(Screen.MeasurementForm.create(0)) },
                onLogout = {
                    // Вихід: очищаємо весь стек і відкриваємо вхід
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Monitoring.route) {
            val monitoringViewModel: MonitoringViewModel = viewModel { MonitoringViewModel(repository) }
            MonitoringScreen(viewModel = monitoringViewModel, onBack = { navController.popBackStack() })
        }

        // Екрани з практичної роботи №1
        composable(Screen.Calculator.route) {
            CalculatorScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Modes.route) {
            ModesScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Measurements.route) {
            MeasurementsScreen(
                viewModel = measurementViewModel,
                onAdd = { navController.navigate(Screen.MeasurementForm.create(0)) },
                onEdit = { id -> navController.navigate(Screen.MeasurementForm.create(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MeasurementForm.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            MeasurementFormScreen(
                id = id,
                viewModel = measurementViewModel,
                onDone = { navController.popBackStack() }
            )
        }
    }
}
