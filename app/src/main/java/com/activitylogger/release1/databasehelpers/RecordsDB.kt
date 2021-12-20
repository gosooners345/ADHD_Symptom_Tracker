package com.activitylogger.release1.databasehelpers

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.activitylogger.release1.data.Records

@Database(
    entities = [Records::class],
    version = 1,
exportSchema = true,
    autoMigrations = []

)

abstract class RecordsDB : RoomDatabase() {
    abstract val recordDao: RecordsDao?

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

                ).build()
                return instance
            }

            return instance
        }
    }
}