@file:Suppress("SpellCheckingInspection")

package com.activitylogger.release1.databasehelpers

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.data.Records
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File
import java.io.FileNotFoundException

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
        private var instance: RecordsDB? = null
        var testKeys = "1234"
        private var dbKeys = MainActivity.appPreferences.getString("dbPassword", "").toString()
        private var passwordKey = MainActivity.appPreferences.getString("password", "").toString()
        private var passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
        private var newPassPhrase = SQLiteDatabase.getBytes(passwordKey.toCharArray())
        var synced = passphrase.contentEquals(newPassPhrase)



        fun encryptDB(context: Context, originalDB: File, passcode: ByteArray) {
            try {
                val attachKEY = String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
                SQLiteDatabase.loadLibs(context)
                if (originalDB.exists()) {
                    val newFile = File.createTempFile("encrypted", "tmp")
                    var newDBs = SQLiteDatabase.openDatabase(
                        originalDB.absolutePath,
                        "",
                        null,
                        SQLiteDatabase.OPEN_READWRITE
                    )
                    val version = newDBs.version
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
                    originalDB.delete()
                    newFile.renameTo(context.getDatabasePath(newDB))

                } else {
                    throw FileNotFoundException(originalDB.absolutePath + "not found")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun encryptDBv2(context: Context, originalDB: File, passcode: ByteArray) {
            try {
                val attachKEY = String.format("ATTACH DATABASE ? AS records KEY ''")
                SQLiteDatabase.loadLibs(context)
                if (originalDB.exists()) {
                    Log.i("ENCRYPTION", "Encrypting database")
                    val newFile = File.createTempFile("encrypted", "tmp")
                    var newDBs = SQLiteDatabase.openDatabase(
                        originalDB.absolutePath,
                        "",
                        null,
                        SQLiteDatabase.OPEN_READWRITE
                    )
                    val version = newDBs.version
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
                    newDBs.rawExecSQL("SELECT sqlcipher_export('main','records')")
                    newDBs.rawExecSQL("DETACH DATABASE records")
                    newDBs.version = version
                    originalDB.delete()
                    newFile.renameTo(originalDB)

                } else {
                    throw FileNotFoundException(originalDB.absolutePath + "not found")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun decryptDBv2(context: Context, originalDB: File, passcode: ByteArray) {
            try {
                SQLiteDatabase.loadLibs(context)
                val attachKEY = String.format("ATTACH DATABASE ? AS records  KEY ''")

                if (originalDB.exists()) {
                    Log.i("DECRYPTION", "Decrypting database")
                    val newFile = File.createTempFile("decrypted", "")
                    var db = SQLiteDatabase.openDatabase(
                        originalDB.absolutePath,
                        passcode, null, SQLiteDatabase.OPEN_READWRITE, null, null
                    )
                    val st = db.compileStatement(attachKEY)
                    st.bindString(1, newFile.absolutePath)
                    st.execute()
                    db.rawExecSQL("SELECT sqlcipher_export('records')")
                    db.rawExecSQL("DETACH DATABASE records")
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
                    newFile.renameTo(originalDB)
                } else {
                    throw FileNotFoundException(originalDB.absolutePath + "not found")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        // Change the encryption keys on the databases
        fun changeSyncDBs(
            context: Context,
            dbFile: File,
            oldPasscode: ByteArray,
            newPasscode: ByteArray
        ) {
            decryptDBv2(context, dbFile, oldPasscode)
            Log.i("KEYS", "Changing DB Keys for encryption")
            encryptDBv2(context, dbFile, newPasscode)
        }

        @JvmStatic
        fun getInstance(context: Context): RecordsDB? {
            if (instance == null) {
                try {
                    SQLiteDatabase.loadLibs(context)
                    var oldDBFile = (context.getDatabasePath(DATABASE_NAME))
                    val newDBFile = (context.getDatabasePath(newDB))

                    if (oldDBFile.exists() && !newDBFile.exists()) {
                        encryptDB(context, oldDBFile, passphrase)
                    }
                    // Test Code for changing passwords and encrypting with a new key
                    //    passphrase=SQLiteDatabase.getBytes(testKeys.toCharArray())

                    if (newDBFile.exists() && !synced) {
                        changeSyncDBs(context, newDBFile, passphrase, newPassPhrase)
                        MainActivity.appPreferences.edit().putString(
                            "dbPassword",
                            MainActivity.appPreferences.getString("password", "")
                        ).apply()
                        dbKeys = MainActivity.appPreferences.getString("dbPassword", "").toString()

                        passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
                        synced = passphrase.contentEquals(newPassPhrase)
                    }

                    val factory = SupportFactory(passphrase,null,false)


                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecordsDB::class.java,
                        newDB
                    )
                        .openHelperFactory(factory)
                        .build()
                    Log.i("ENCRYPTEDDB", "Encrypted Database is loading")
                    return instance

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            return instance
        }
    }
}
