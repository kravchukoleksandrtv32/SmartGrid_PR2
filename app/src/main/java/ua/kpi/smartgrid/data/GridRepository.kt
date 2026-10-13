package ua.kpi.smartgrid.data

import kotlinx.coroutines.flow.Flow
import ua.kpi.smartgrid.data.local.AppDatabase
import ua.kpi.smartgrid.data.local.MeasurementEntity
import ua.kpi.smartgrid.data.local.MeasurementWithSubstation
import ua.kpi.smartgrid.data.local.SubstationEntity
import ua.kpi.smartgrid.data.local.UserEntity
import ua.kpi.smartgrid.data.remote.GridReading
import ua.kpi.smartgrid.data.remote.RemoteDataSource
import ua.kpi.smartgrid.domain.Calculator

/** Зведені показники журналу вимірювань (агрегація даних). */
data class MeasurementSummary(
    val count: Int = 0,
    val averagePower: Double = 0.0,
    val maxPower: Double = 0.0,
    val overloadCount: Int = 0
)

/**
 * Repository — єдина точка доступу до даних для ViewModel.
 * Поєднує два джерела: локальну базу Room і віддалений сервер (RemoteDataSource).
 */
const val DEMO_EMAIL = "operator@grid.ua"
const val DEMO_PASSWORD = "grid2026"

class GridRepository(
    private val database: AppDatabase,
    private val remote: RemoteDataSource
) {
    private val userDao = database.userDao()
    private val substationDao = database.substationDao()
    private val measurementDao = database.measurementDao()

    // ---------- Користувачі ----------

    // Тестовий оператор, щоб одразу можна було увійти
    suspend fun ensureDemoUser() {
        if (userDao.findByEmail(DEMO_EMAIL) == null) {
            userDao.insert(UserEntity(name = "Черговий оператор", email = DEMO_EMAIL, password = DEMO_PASSWORD))
        }
    }

    // Реєстрація з перевіркою унікальності email
    suspend fun register(name: String, email: String, password: String): Result<Unit> {
        val key = email.trim().lowercase()
        if (userDao.findByEmail(key) != null) {
            return Result.failure(Exception("Користувач з такою поштою вже існує"))
        }
        userDao.insert(UserEntity(name = name.trim(), email = key, password = password))
        return Result.success(Unit)
    }

    // Вхід: повертає користувача або null
    suspend fun login(email: String, password: String): UserEntity? {
        val user = userDao.findByEmail(email.trim().lowercase())
        return if (user != null && user.password == password) user else null
    }

    // ---------- Підстанції (кешування) ----------

    val substations: Flow<List<SubstationEntity>> = substationDao.getAll()

    // Якщо в базі ще немає підстанцій — завантажуємо з сервера і зберігаємо (кеш)
    suspend fun refreshSubstations(): Result<Unit> {
        return try {
            if (substationDao.count() == 0) {
                substationDao.insertAll(remote.fetchSubstations())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------- Показники мережі (віддалене джерело) ----------

    suspend fun fetchReading(): GridReading = remote.fetchReading()

    // ---------- Вимірювання ----------

    fun measurements(query: String, sortByPower: Boolean): Flow<List<MeasurementWithSubstation>> =
        measurementDao.search(query, sortByPower)

    suspend fun getMeasurement(id: Int): MeasurementEntity? = measurementDao.getById(id)

    // Збереження: потужність і втрати обчислюються тут (бізнес-логіка)
    suspend fun saveMeasurement(
        id: Int, substationId: Int, voltage: Double, current: Double, cosPhi: Double, resistance: Double
    ) {
        val entity = MeasurementEntity(
            id = id,
            substationId = substationId,
            voltage = voltage,
            current = current,
            cosPhi = cosPhi,
            resistance = resistance,
            power = Calculator.calculatePower(voltage, current, cosPhi),
            losses = Calculator.calculateLosses(current, resistance),
            createdAt = System.currentTimeMillis()
        )
        if (id == 0) measurementDao.insert(entity) else measurementDao.update(entity)
    }

    suspend fun deleteMeasurement(measurement: MeasurementEntity) = measurementDao.delete(measurement)

    // Агрегація: кількість, середня й максимальна потужність, кількість перевантажень
    fun summarize(list: List<MeasurementWithSubstation>): MeasurementSummary {
        if (list.isEmpty()) return MeasurementSummary()
        val powers = list.map { it.measurement.power }
        val overloads = list.count {
            Calculator.calculateLoadPercent(it.measurement.power, it.capacityKw) > 100
        }
        return MeasurementSummary(list.size, powers.average(), powers.max(), overloads)
    }
}
