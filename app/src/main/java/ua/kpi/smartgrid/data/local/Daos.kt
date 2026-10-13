package ua.kpi.smartgrid.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Доступ до таблиці користувачів. */
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?
}

/** Доступ до таблиці підстанцій. */
@Dao
interface SubstationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(substations: List<SubstationEntity>)

    @Query("SELECT * FROM substations ORDER BY name")
    fun getAll(): Flow<List<SubstationEntity>> // Flow — UI оновлюється сам при зміні таблиці

    @Query("SELECT COUNT(*) FROM substations")
    suspend fun count(): Int
}

/** Доступ до таблиці вимірювань: додавання, оновлення, видалення, пошук, сортування. */
@Dao
interface MeasurementDao {
    @Insert
    suspend fun insert(measurement: MeasurementEntity)

    @Update
    suspend fun update(measurement: MeasurementEntity)

    @Delete
    suspend fun delete(measurement: MeasurementEntity)

    @Query("SELECT * FROM measurements WHERE id = :id")
    suspend fun getById(id: Int): MeasurementEntity?

    /**
     * Пошук за назвою підстанції (LIKE) і сортування:
     * sortByPower = true — за потужністю, інакше — за датою (нові зверху).
     */
    @Transaction
    @Query(
        """
        SELECT measurements.*, substations.name AS substationName, substations.capacity_kw AS capacityKw
        FROM measurements
        INNER JOIN substations ON measurements.substationId = substations.id
        WHERE substations.name LIKE '%' || :query || '%'
        ORDER BY CASE WHEN :sortByPower THEN measurements.power ELSE measurements.createdAt END DESC
        """
    )
    fun search(query: String, sortByPower: Boolean): Flow<List<MeasurementWithSubstation>>
}
