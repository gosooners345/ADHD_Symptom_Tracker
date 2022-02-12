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

abstract class RecordsDB : RoomDatabase()
{
    abstract val recordDao: RecordsDao?
    
    @DeleteTable.Entries(DeleteTable(tableName = "recordsfts"))
    class RecordsAutoMigration : AutoMigrationSpec
    
    
    companion object
    {
        private const val DATABASE_NAME = "activitylogger_db"
        private const val newDB = "activitylogger_db.db"
        private var instance: RecordsDB? = null
        private lateinit var dbKeys: String
        private lateinit var passwordKey: String
        private lateinit var passphrase :ByteArray
        private lateinit var newPassPhrase :ByteArray
        private  lateinit var factory: SupportFactory
        
        //       lateinit var synced =false
        private var encryptedState: Boolean = false
        
        
        private fun encryptDB(context: Context, originalDB: File, passcode: ByteArray)
        {
            try
            {
                val attachKEY =
                    String.format("ATTACH DATABASE ? AS plaintext  KEY ''")
                SQLiteDatabase.loadLibs(context)
                if (originalDB.exists())
                {
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
                    newDBs.rawExecSQL(
                      "SELECT sqlcipher_export('main','plaintext')"
                    )
                    newDBs.rawExecSQL("DETACH DATABASE plaintext")
                    newDBs.version = version
                    originalDB.delete()
                    newFile.renameTo(context.getDatabasePath(newDB))
                    encryptedState = true
                }
                else
                {
                    throw FileNotFoundException(
                      originalDB.absolutePath + "not found"
                    )
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
        
        private fun encryptDBv2(context: Context, originalDB: File, passcode: ByteArray)
        {
            try
            {
                val attachKEY =
                    String.format("ATTACH DATABASE ? AS records KEY ''")
                SQLiteDatabase.loadLibs(context)
                if (originalDB.exists())
                {
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
                    newDBs.rawExecSQL(
                      "SELECT sqlcipher_export('main','records')"
                    )
                    newDBs.rawExecSQL("DETACH DATABASE records")
                    newDBs.version = version
                    originalDB.delete()
                    newFile.renameTo(originalDB)
                    encryptedState = true
                }
                else
                {
                    throw FileNotFoundException(
                      originalDB.absolutePath + "not found"
                    )
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
        
        private fun decryptDBv2(context: Context, originalDB: File, passcode: ByteArray)
        {
            try
            {
                SQLiteDatabase.loadLibs(context)
                val attachKEY =
                    String.format("ATTACH DATABASE ? AS records  KEY ''")
                
                if (originalDB.exists())
                {
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
                    encryptedState = false
                }
                else
                {
                    throw FileNotFoundException(
                      originalDB.absolutePath + "not found"
                    )
                }
            }
            catch (ex: Exception)
            {
                ex.printStackTrace()
            }
        }
        
        // Change the encryption keys on the databases
        private fun changeSyncDBs(
            context: Context,
            dbFile: File,
            oldPasscode: ByteArray,
            newPasscode: ByteArray,
            passwordKey : String
        )
        {
            decryptDBv2(context, dbFile, oldPasscode)
            Log.i("KEYS", "Changing DB Keys for encryption")
            if(passwordKey!="")
            encryptDBv2(context, dbFile, newPasscode)
           
        }

        private fun getEncyptedState(condition : Boolean) : Boolean{
            return  condition
        }
        
        //Initialize the Passcodes and the associated ByteArrays
        private fun initializePasscodes()
        {
            dbKeys = MainActivity.appPreferences
                .getString("dbPassword", "").toString()
            passwordKey = MainActivity.appPreferences
                .getString("password", "").toString()
            encryptedState = getEncyptedState(dbKeys.isNotBlank())
           passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
            if(passwordKey!="")
            newPassPhrase = SQLiteDatabase.getBytes(passwordKey.toCharArray())
            else
                newPassPhrase= passphrase
                
        }
        
        private fun renewDBPassCode()
        {
            
            MainActivity.appPreferences.edit().putString(
              "dbPassword",
              MainActivity.appPreferences.getString("password", "")
            ).apply()
            dbKeys = MainActivity.appPreferences.getString(
                  "dbPassword", ""
                ).toString()
            
            passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
            
        }
        
        @JvmStatic
        fun getInstance(context: Context): RecordsDB?
        {
            if (instance == null)
            {
                try
                {
                    SQLiteDatabase.loadLibs(context)
                    //Initialize the passcode variables
                    initializePasscodes()
                    val synced = passphrase.contentEquals(newPassPhrase)
                    val oldDBFile = (context.getDatabasePath(DATABASE_NAME))
                    val newDBFile = (context.getDatabasePath(newDB))
                    //Encrypt and remove the Old DB if there's a password
                    if (oldDBFile.exists() && !newDBFile.exists())
                        encryptDB(context, oldDBFile, passphrase)
//Check for password changes since last load
                    if (newDBFile.exists() && !synced)
                    {
                        if( MainActivity.appPreferences.getString("password", "")!="")
                        {
                            changeSyncDBs(
                              context, newDBFile, passphrase, newPassPhrase,
                              passwordKey
                            )
                            renewDBPassCode()
                        }
                    }
                    factory = SupportFactory(passphrase, null, false)
                    instance =
                        Room.databaseBuilder(
                          context.applicationContext,
                          RecordsDB::class.java,
                          newDB
                        )
                            .openHelperFactory(factory)
                            .build()
                    return instance
                    
                }
                catch (ex: Exception)
                {
                    ex.printStackTrace()
                }
            }
            return instance
        }
    }
}
