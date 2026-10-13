package ua.kpi.smartgrid

import android.app.Application
import androidx.room.Room
import ua.kpi.smartgrid.data.GridRepository
import ua.kpi.smartgrid.data.local.AppDatabase
import ua.kpi.smartgrid.data.remote.RemoteDataSource

/**
 * Клас застосунку: створює базу даних і репозиторій один раз на весь застосунок.
 */
class SmartGridApplication : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "smartgrid.db").build()
    }

    val repository: GridRepository by lazy {
        GridRepository(database, RemoteDataSource)
    }
}
