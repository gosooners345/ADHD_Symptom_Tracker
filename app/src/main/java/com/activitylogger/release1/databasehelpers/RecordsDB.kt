@file:Suppress("SpellCheckingInspection")

package com.activitylogger.release1.databasehelpers

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDB.Companion.encryptedDB
import net.sqlcipher.database.SQLiteDatabase

import net.sqlcipher.database.SupportFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

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
        const val newDB = "activitylogger_db.db"
        private const val encryptedDB = "encrypted-activitylogger_db.db"
        private var instance: RecordsDB? = null
        private var instance2: RecordsDB? = null
        private var dbKeys = MainActivity.appPreferences.getString("dbPassword","").toString()
         private var passwordKey = MainActivity.appPreferences.getString("password","").toString()
      private var passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
        private var newPassPhrase = SQLiteDatabase.getBytes(passwordKey.toCharArray())
        //var synced = passphrase.contentEquals(newPassPhrase)

        private var factory =SupportFactory(passphrase)
       private  var passFactory =SupportFactory(newPassPhrase)
        //passwordTranslation.toCharArray())
var synced = passphrase.contentEquals(newPassPhrase)



fun encryptDB(context: Context, originalDB: File, passcode : ByteArray ) {
    try {
        val attachKEY = String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
        SQLiteDatabase.loadLibs(context)
        if (originalDB.exists()) {
            var newFile = File.createTempFile("encrypted", "tmp")
            var newDBs = SQLiteDatabase.openDatabase(
                originalDB.absolutePath,
                "",
                null,
                SQLiteDatabase.OPEN_READWRITE
            )
            var version = newDBs.version
            newDBs.close()
            newDBs = SQLiteDatabase.openDatabase(
                newFile.absolutePath,
                passcode,
                null,
                SQLiteDatabase.OPEN_READWRITE,
                null,
                null
            )
            val st = newDBs.compileStatement(attachKEY)
            st.bindString(1, originalDB.absolutePath)
            st.execute()
            newDBs.rawExecSQL("SELECT sqlcipher_export('main','plaintext')")
            newDBs.rawExecSQL("DETACH DATABASE plaintext")
            newDBs.version = version
            //This would replace the existing file and accidentally delete it when it wasn't ready for removal during testing
            //     originalDB.copyTo(context.getDatabasePath(originalDB.name),true)
            //originalDB.delete()
            newFile.renameTo(context.getDatabasePath(newDB))

        } else {
            throw FileNotFoundException(originalDB.absolutePath + "not found")
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
}
        fun decryptDB(context: Context,originalDB: File,passcode: ByteArray) {
            try {
                SQLiteDatabase.loadLibs(context)
                val attachKEY = String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
                if (originalDB.exists()) {
                    val newFile = File.createTempFile("decrypted", "")
                    var db = SQLiteDatabase.openDatabase(
                        originalDB.absolutePath,
                        passcode, null, SQLiteDatabase.OPEN_READWRITE, null, null
                    )
                    var st = db.compileStatement(attachKEY)
                    st.bindString(1, newFile.absolutePath)
                    st.execute()
                    db.rawExecSQL("SELECT sqlcipher_export('plaintext)")
                    db.rawExecSQL("DETACH DATABASE plaintext")
                    val version = db.version
                    st.close()
                    db.close()
                    db = SQLiteDatabase.openDatabase(
                        newFile.absolutePath,
                        "",
                        null,
                        SQLiteDatabase.OPEN_READWRITE
                    )
                    db.version = version
                    db.close()
                    originalDB.delete()
                    newFile.renameTo(context.getDatabasePath(DATABASE_NAME))
                } else {
                    throw FileNotFoundException(originalDB.absolutePath + "not found")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

//This leaves too many files behind and its clunky.
        fun changeSyncDBs(context: Context,originalDB: File,newDBFile:File,oldPasscode: ByteArray,newPasscode: ByteArray)
        {
            decryptDB(context,newDBFile,oldPasscode)
            encryptDB(context,originalDB,newPasscode)
        }
        @JvmStatic
        fun getInstance(context: Context): RecordsDB? {
            if (instance == null) {
               try {
                   SQLiteDatabase.loadLibs(context)
                   var oldDBFile =(context.getDatabasePath(DATABASE_NAME))
val newDBFile = (context.getDatabasePath(newDB))
                   //Initial DB Encryption after 2 uses of the app so that we don't screw up the files because Android sucks sometimes
                   if(oldDBFile.exists()&&!newDBFile.exists()  && MainActivity.appPreferences.getInt("dbTimes",0)>1 )
                   {
                       encryptDB(context,oldDBFile, passphrase)
                   }
                   // Test Code for changing passwords and encrypting with a new key
                   if(newDBFile.exists()&&!synced) {

                       dbKeys =MainActivity.appPreferences.getString("dbPassword", "").toString()
                       passwordKey =
                           MainActivity.appPreferences.getString("password", "").toString()
                       passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
                       newPassPhrase = SQLiteDatabase.getBytes(passwordKey.toCharArray())
                       synced = passphrase.contentEquals(newPassPhrase)
                       if (!oldDBFile.exists())
                           oldDBFile = context.getDatabasePath(newDB)

                           changeSyncDBs(context, oldDBFile, newDBFile, passphrase, newPassPhrase)

                       factory = passFactory

                       MainActivity.appPreferences.edit().putString(
                           "dbPassword",
                           MainActivity.appPreferences.getString("password", "")
                       ).apply()
                   }


                     if(newDBFile.exists() && MainActivity.appPreferences.getInt("dbTimes",1)>1){
                       try {
                           instance = Room.databaseBuilder(
                               context.applicationContext,
                               RecordsDB::class.java,
                               newDB
                           )
                            //123   .createFromFile(context.getDatabasePath(newDB))
                               .openHelperFactory(factory)
                               .build()
                           Log.i("ENCRYPTEDDB","Encrypted Database is loading")
                           return instance
                       }
                       catch (ex:Exception)
                       {
                           ex.printStackTrace()
                           instance = Room.databaseBuilder(
                               context,
                               RecordsDB::class.java,
                               DATABASE_NAME
                           )
                               .build()
                           Log.i("UNENCRYPTEDDB","Unencrypted Database is loading")
                           return instance

                       }
                        }
                        else{
                            instance = Room.databaseBuilder(
                                context,
                                RecordsDB::class.java,
                                DATABASE_NAME
                            )
                                .build()
                            Log.i("UNENCRYPTEDDB","Unencrypted Database is loading")
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
