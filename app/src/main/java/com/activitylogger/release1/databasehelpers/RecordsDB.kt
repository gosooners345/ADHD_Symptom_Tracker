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
        private const val newDB = "activitylogger_db.db"
        private const val encryptedDB = "encrypted-activitylogger_db.db"
        private var instance: RecordsDB? = null
        private var instance2: RecordsDB? = null
       private val dbKeys = MainActivity.appPreferences.getString("dbPassword","").toString()
        private val passwordKey = MainActivity.appPreferences.getString("password","").toString()
      private var passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
        private val newPassPhrase = SQLiteDatabase.getBytes(passwordKey.toCharArray())
        val synced = passphrase.contentEquals(newPassPhrase)

        private val factory =SupportFactory(passphrase)
        //passwordTranslation.toCharArray())

fun encryptDB(context: Context,originalDB: File,passcode : ByteArray ){
    try{
        val attachKEY = String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
        SQLiteDatabase.loadLibs(context)
        if(originalDB.exists())
        {
            var newFile = File.createTempFile("sqliteTest","tmp",context.cacheDir)
var newDBs = SQLiteDatabase.openDatabase(originalDB.absolutePath,"",null,SQLiteDatabase.OPEN_READWRITE)
var version = newDBs.version
            newDBs.close()
            newDBs = SQLiteDatabase.openDatabase(newFile.absolutePath,passcode,null,SQLiteDatabase.OPEN_READWRITE,null,null)
val st = newDBs.compileStatement(attachKEY)
            st.bindString(1,originalDB.absolutePath)
            st.execute()
newDBs.rawExecSQL("SELECT sqlcipher_export('main','plaintext')")
newDBs.rawExecSQL("DETACH DATABASE plaintext")
            newDBs.version=version
            originalDB.copyTo(context.getDatabasePath("test.db"),true)
            originalDB.delete()
            newFile.renameTo(context.getDatabasePath(newDB))

        }
        else
        {
            throw FileNotFoundException(originalDB.absolutePath + "not found")
        }
    }
    catch (ex:Exception)
    {
        ex.printStackTrace()
    }

}

        fun changeDBKeys(context: Context,originalDB: File,oldPasscode : ByteArray,newPasscode : ByteArray ){
            try{
                val attachKEY = String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
                SQLiteDatabase.loadLibs(context)
                if(originalDB.exists())
                {
                    var newFile = File.createTempFile(newDB,"tmp",context.cacheDir)
                    var newDB = SQLiteDatabase.openDatabase(originalDB.absolutePath,oldPasscode,null,SQLiteDatabase.OPEN_READWRITE,null,null)
                    var version = newDB.version
                    newDB.close()
                    newDB = SQLiteDatabase.openDatabase(newFile.absolutePath,newPasscode,null,SQLiteDatabase.OPEN_READWRITE,null,null)
                    val st = newDB.compileStatement(attachKEY)
                    st.bindString(1,originalDB.absolutePath)
                    st.execute()
                    newDB.rawExecSQL("SELECT sqlcipher_export('main','plaintext')")
                    newDB.rawExecSQL("DETACH DATABASE plaintext")
                    newDB.version=version
                    if(MainActivity.buildType=="debug")
                    originalDB.copyTo(context.getDatabasePath("oldpassword.db"),true)
                    originalDB.delete()
                    newFile.renameTo(originalDB)

                }
                else
                {
                    throw FileNotFoundException(originalDB.absolutePath + "not found")
                }
            }
            catch (ex:Exception)
            {
                ex.printStackTrace()
            }

        }
        fun decryptDB(context: Context,originalDB: File,passcode: ByteArray){
            try {
                SQLiteDatabase.loadLibs(context)
                val attachKEY = String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
                if(originalDB.exists()) {
                    var newFile = File.createTempFile("sqliteCipher", "tmp")
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
                db=SQLiteDatabase.openDatabase(newFile.absolutePath,"",null,SQLiteDatabase.OPEN_READWRITE)
                    db.version=version
                    db.close()
                    originalDB.copyTo(context.getDatabasePath("test"),true)
                    originalDB.delete()
                    newFile.renameTo(originalDB)
                }
                else{
                    throw FileNotFoundException(originalDB.absolutePath + "not found")
                }
            }
catch (ex:Exception)
{
    ex.printStackTrace()
}
        }




        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun getInstance(context: Context): RecordsDB? {
            if (instance == null) {
               try {
SQLiteDatabase.loadLibs(context)
                   var oldDBFile =(context.getDatabasePath(DATABASE_NAME))
val newDBFile = (context.getDatabasePath(newDB))
                   if(oldDBFile.exists())
                   {
                       encryptDB(context,oldDBFile, passphrase)
                   }
                   if(!synced)
                   {
                       changeDBKeys(context, newDBFile, passphrase, newPassPhrase)
                       MainActivity.appPreferences.edit().putString("dbPassword",MainActivity.appPreferences.getString("password","")).apply()
                       passphrase= newPassPhrase
                   }
                 //  decryptDB(context,newDBFile, passphrase)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if(newDBFile.exists()){
                       try {
                           instance = Room.databaseBuilder(
                               context.applicationContext,
                               RecordsDB::class.java,
                               newDB
                           )
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
                               "test"
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
                                newDB
                            )
                                .build()
                            Log.i("UNENCRYPTEDDB","Unencrypted Database is loading")
                            return instance
                        }
                    } else {

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
