@file:Suppress("SpellCheckingInspection")

package com.activitylogger.release1.databasehelpers

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.data.Records
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.nio.file.Files.exists
import java.nio.file.Files.notExists

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
       private val dbKeys = MainActivity.appPreferences.getString("dbPassword","").toString()
      private val passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
        private val factory =SupportFactory(passphrase)
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

        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun getInstance(context: Context): RecordsDB? {
            if (instance == null) {
               try {

                   var oldDBFile =(context.getDatabasePath(DATABASE_NAME))
                                    File(oldDBFile.path).copyTo((context.getDatabasePath(newDB)))
var file1 = (context.getDatabasePath(DATABASE_NAME+"-shm"))
                   File(file1.path).copyTo((context.getDatabasePath(newDB+"-shm")))
var file2 =(context.getDatabasePath(DATABASE_NAME+"-wal"))
File(file2.path).copyTo((context.getDatabasePath(newDB+"-wal")))

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            RecordsDB::class.java,
                            newDB
                        )
                            .createFromFile((context.getDatabasePath(newDB)))
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
                    ex.printStackTrace()
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
            return instance
    }
}}
