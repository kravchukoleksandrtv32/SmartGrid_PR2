package ua.kpi.smartgrid.data.remote

import kotlinx.coroutines.delay
import ua.kpi.smartgrid.data.local.SubstationEntity
import java.io.IOException
import kotlin.random.Random

/** Поточні показники мережі, які «приходять із сервера». */
data class GridReading(val load: Int, val frequency: Double)

/**
 * Віддалене джерело даних (імітація сервера диспетчерської).
 * delay() імітує мережеву затримку, іноді запит завершується помилкою.
 */
object RemoteDataSource {

    // Довідник підстанцій із сервера
    suspend fun fetchSubstations(): List<SubstationEntity> {
        delay(500)
        return listOf(
            SubstationEntity(1, "ПС «Північна»", 1000.0),
            SubstationEntity(2, "ПС «Центральна»", 2500.0),
            SubstationEntity(3, "ПС «Південна»", 630.0)
        )
    }

    // Поточні показники мережі; приблизно кожен 8-й запит — помилка зв'язку
    suspend fun fetchReading(): GridReading {
        delay(1000)
        if (Random.nextInt(8) == 0) throw IOException("Немає зв'язку з сервером")
        return GridReading(
            load = Random.nextInt(1000, 9000),
            frequency = 49.8 + Random.nextDouble() * 0.4
        )
    }
}
