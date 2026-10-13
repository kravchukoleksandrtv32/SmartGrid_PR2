package ua.kpi.smartgrid.data.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Таблиця користувачів (операторів мережі).
 * Індекс з unique = true гарантує, що email не повторюється.
 */
@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String
)

/**
 * Таблиця підстанцій.
 * Одна підстанція має багато вимірювань (зв'язок один-до-багатьох).
 */
@Entity(tableName = "substations")
data class SubstationEntity(
    @PrimaryKey val id: Int,
    val name: String,
    @ColumnInfo(name = "capacity_kw") val capacityKw: Double // номінальна потужність, кВт
)

/**
 * Таблиця вимірювань лінії. Поле substationId — зовнішній ключ на підстанцію.
 * Потужність і втрати — обчислювані поля, рахуються при збереженні.
 */
@Entity(
    tableName = "measurements",
    foreignKeys = [ForeignKey(
        entity = SubstationEntity::class,
        parentColumns = ["id"],
        childColumns = ["substationId"],
        onDelete = ForeignKey.CASCADE // видалення підстанції видаляє її вимірювання
    )],
    indices = [Index("substationId")]
)
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val substationId: Int,
    val voltage: Double,     // напруга, кВ
    val current: Double,     // струм, А
    val cosPhi: Double,      // коефіцієнт потужності
    val resistance: Double,  // опір фази лінії, Ом
    val power: Double,       // активна потужність, кВт (обчислюється)
    val losses: Double,      // втрати, кВт (обчислюються)
    val createdAt: Long      // час запису, мс
)

/**
 * Результат запиту з JOIN: вимірювання + назва і потужність його підстанції.
 */
data class MeasurementWithSubstation(
    @Embedded val measurement: MeasurementEntity,
    val substationName: String,
    val capacityKw: Double
)
