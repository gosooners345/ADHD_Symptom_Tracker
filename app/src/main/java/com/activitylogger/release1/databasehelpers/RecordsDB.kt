@file:Suppress("SpellCheckingInspection")

package com.activitylogger.release1.databasehelpers

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.activitylogger.release1.data.Records

@Database(
    entities = [Records::class],
    version = 3,
exportSchema = true,
    autoMigrations = [AutoMigration(from=1,to=2),AutoMigration(from = 2,to=3,spec=RecordsDB.RecordsAutoMigration::class),

    ]
)

abstract class RecordsDB : RoomDatabase() {
    abstract val recordDao: RecordsDao?
@DeleteTable.Entries(DeleteTable(tableName = "recordsfts"))
    class RecordsAutoMigration : AutoMigrationSpec


    companion object {
        private const val DATABASE_NAME = "activitylogger_db"
        private var instance: RecordsDB? = null

        @JvmStatic
        fun getInstance(context: Context): RecordsDB? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecordsDB::class.java,
                    DATABASE_NAME

                )
                    .fallbackToDestructiveMigration()
                    .build()
                return instance
            }

            return instance
        }
    }
}