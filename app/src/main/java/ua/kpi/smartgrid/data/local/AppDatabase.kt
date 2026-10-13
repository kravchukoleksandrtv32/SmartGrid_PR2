package ua.kpi.smartgrid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * База даних Room: три таблиці й доступ до їхніх DAO.
 */
@Database(
    entities = [UserEntity::class, SubstationEntity::class, MeasurementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun substationDao(): SubstationDao
    abstract fun measurementDao(): MeasurementDao
}
