package com.example.investtracker.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {
    @Query("SELECT * FROM deals ORDER BY date DESC")
    fun getAll(): Flow<List<Deal>>

    @Insert suspend fun insert(deal: Deal)
    @Delete suspend fun delete(deal: Deal)
    @Update suspend fun update(deal: Deal)
}

@Database(entities = [Deal::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dealDao(): DealDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: android.content.Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "invest.db"
                ).build().also { INSTANCE = it }
            }
    }
}
