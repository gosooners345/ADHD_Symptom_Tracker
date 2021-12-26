package com.activitylogger.release1.databasehelpers

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsFTS

@Database(
    entities = [Records::class,RecordsFTS::class],
    version = 2,
exportSchema = true,
    autoMigrations = [AutoMigration(from=1,to=2)]
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

                ).addCallback(object : RoomDatabase.Callback(){
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        db.execSQL("Insert into recordsfts(recordsfts) Values('rebuild')")
                    }
                })
                    .fallbackToDestructiveMigration()
                    .build()
                return instance
            }

            return instance
        }
    }
}