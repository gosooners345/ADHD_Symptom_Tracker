@file:Suppress("SpellCheckingInspection")

package com.activitylogger.release1.databasehelpers

import android.content.Context
import android.os.Build
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.data.Records
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory
import java.io.File

@Database(
    entities = [Records::class],
    version = 4,
exportSchema = true,
    autoMigrations = [AutoMigration(from=1,to=2),AutoMigration(from = 2,to=3,spec=RecordsDB.RecordsAutoMigration::class),
AutoMigration(from=3,to=4)
    ]
)

abstract class RecordsDB : RoomDatabase() {
    abstract val recordDao: RecordsDao?
@DeleteTable.Entries(DeleteTable(tableName = "recordsfts"))
    class RecordsAutoMigration : AutoMigrationSpec


    companion object {
        private const val DATABASE_NAME = "activitylogger_db"
        private const val newDB = "activitylogger_db.db"
        private var instance: RecordsDB? = null
        private var instance2: RecordsDB? = null
       //private val dbKeys = MainActivity.dbCharKeys
      //private val passphrase = SQLiteDatabase.getBytes(dbKeys)
       // private val factory =SupportFactory(passphrase,null,false)
        //passwordTranslation.toCharArray())
        @JvmStatic
        fun getInstance(context: Context, dbName: String): RecordsDB? {
            if (instance2 == null) {
                instance2 = Room.databaseBuilder(
                    context.applicationContext,
                    RecordsDB::class.java,
                    dbName
                ).build()
                return instance2
            }
            return instance2
        }

        @JvmStatic
        fun getInstance(context: Context): RecordsDB? {
            if (instance == null) {
            /*   try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            RecordsDB::class.java,
                            newDB
                        )
                            .createFromFile((context.getDatabasePath(DATABASE_NAME)))
                            .openHelperFactory(factory)
                            .build()
                        return instance
                    } else {

                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            RecordsDB::class.java,
                            DATABASE_NAME
                        )
                            .build()
                        return instance
                    }
                }
                catch (ex: Exception) {
                    ex.printStackTrace()*/
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordsDB::class.java,
                        DATABASE_NAME
                    )
                        .build()
                    return instance
              //  }

            }
            return instance
        }
    }
}