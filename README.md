

Варіант 8: система розподілу електроенергії (Smart Grid). Kotlin + Jetpack Compose.
Один застосунок, що розвивається від практичної до практичної:

## Як працює застосунок

1. **Вхід** (тестовий: `operator@grid.ua` / `grid2026`) або **реєстрація** нового оператора.
2. **Головне меню**: вітання з ім'ям, зведення з бази (кількість записів, середня потужність, перевантаження) і кнопки:
   - **Моніторинг мережі** — показники «з сервера» зі станами завантаження / успіху / помилки;
   - **Режими мережі** — денний / нічний / піковий / аварія (з ПР1);
   - **Розрахунок лінії** — P, ΔP і ККД за U, I, cosφ, R (з ПР1);
   - **Журнал вимірювань** — список з бази Room: пошук, сортування, видалення, редагування;
   - **Нове вимірювання** — форма з перевіркою діапазонів і розрахунком P та ΔP.

## Структура

```
app/src/main/java/ua/kpi/smartgrid/
├── MainActivity.kt              NavController + AppNavHost
├── SmartGridApplication.kt      створення бази та репозиторію
├── navigation/                  Screen (маршрути), AppNavHost (граф навігації)   — заняття 5
├── ui/screens/                  Login, Register, MainMenu, Monitoring, Modes,
│                                Calculator, Measurements, MeasurementForm
├── ui/components/               ScreenTitle, MenuButton, InfoRow, FormField, LoadingButton
├── domain/                      Validator (заняття 6), Calculator
├── viewmodel/                   AuthViewModel, MonitoringViewModel, MeasurementViewModel — заняття 7
└── data/
    ├── local/                   Entities, Daos, AppDatabase (Room)                — заняття 8
    ├── remote/RemoteDataSource  імітація сервера
    └── GridRepository           поєднує локальне і віддалене джерела
```

Запуск: Android Studio → File → Open → `SmartGrid` → Gradle Sync → Run.
