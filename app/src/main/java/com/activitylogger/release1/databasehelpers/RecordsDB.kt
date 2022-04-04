@file:Suppress("SpellCheckingInspection")

/// Author Brandon Guerin
/*Copyright (c) 2008-2020 Zetetic LLC
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
* Neither the name of the ZETETIC LLC nor the
names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY ZETETIC LLC ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL ZETETIC LLC BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/









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
    version = 5,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2), AutoMigration(
            from = 2,
            to = 3,
            spec = RecordsDB.RecordsAutoMigration::class
        ),
        AutoMigration(from = 3, to = 4), AutoMigration(from = 4, to = 5),
    ]
)

abstract class RecordsDB : RoomDatabase()
{
    abstract val recordDao: RecordsDao?
    
    @DeleteTable.Entries(DeleteTable(tableName = "recordsfts"))
    class RecordsAutoMigration : AutoMigrationSpec
    
    
    companion object {
        private const val DATABASE_NAME = "activitylogger_db"
        private const val newDB = "activitylogger_db.db"
        private var instance: RecordsDB? = null
        private lateinit var dbKeys: String
        private lateinit var passwordKey: String
        private lateinit var passphrase: ByteArray
        private lateinit var newPassPhrase: ByteArray
        private lateinit var factory: SupportFactory
        var dbVersion = 0
        var newDBVersion = 5

        private var encryptedState: Boolean = false

        // Encrypts existing old DB Versions and so forth
        private fun encryptDB(context: Context, originalDB: File, passcode: ByteArray) {
            try {
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

        // Encrypts the database
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

        //Decrypts the database.
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
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        // Check the DB Version and upgrade if out of date, this forces decryption so it can be upgraded without causing problems for end user
        private fun checkDBVerison(context: Context, originalDB: File, passcode: ByteArray) {
            try {
                val db = SQLiteDatabase.openDatabase(
                    originalDB.absolutePath,
                    passcode, null, SQLiteDatabase.OPEN_READWRITE, null, null
                )
                dbVersion = db.version
                Log.i("Database Version", "DB Version is $dbVersion")
                db.close()
                if (dbVersion != newDBVersion) {
                    decryptDBv2(context, originalDB, passphrase)
                    instance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            RecordsDB::class.java,
                            newDB
                        )
                            .build()
                    encryptDBv2(context, originalDB, passphrase)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()

            }
        }


        // Change the encryption keys on the databases
        private fun changeSyncDBs(
            context: Context,
            dbFile: File,
            oldPasscode: ByteArray,
            newPasscode: ByteArray,
            passwordKey: String
        ) {
            decryptDBv2(context, dbFile, oldPasscode)
            Log.i("KEYS", "Changing DB Keys for encryption")
            if (passwordKey != "")
                encryptDBv2(context, dbFile, newPasscode)

        }

        private fun getEncyptedState(condition : Boolean) : Boolean{
            return  condition
        }

        //Initialize the Passcodes and the associated ByteArrays
        private fun initializePasscodes() {
            dbKeys = MainActivity.appPreferences
                .getString("dbPassword", "").toString()
            passwordKey = MainActivity.appPreferences
                .getString("password", "").toString()
            encryptedState = getEncyptedState(dbKeys.isNotBlank())
            passphrase = SQLiteDatabase.getBytes(dbKeys.toCharArray())
            if (passwordKey != "")
                newPassPhrase = SQLiteDatabase.getBytes(passwordKey.toCharArray())
            else
                newPassPhrase = passphrase
                
        }

        private fun renewDBPassCode() {
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
                try {
                    //This checks encrypted state

                    SQLiteDatabase.loadLibs(context)
                    //Initialize the passcode variables
                    initializePasscodes()
                    val synced = passphrase.contentEquals(newPassPhrase)
                    val oldDBFile = (context.getDatabasePath(DATABASE_NAME))
                    val newDBFile = (context.getDatabasePath(newDB))
                    //Removes legacy DB files with old naming scheme and replaces with new encrypted db
                    if (oldDBFile.exists() && !newDBFile.exists())
                        encryptDB(context, oldDBFile, passphrase)
                    //This decrypts the database and opens it for this round, when the user goes to
                    //Change passwords, the encryption will re-encrypt the database
                    checkDBVerison(context, newDBFile, passphrase)
// Check for password changes
                    if (newDBFile.exists() && !synced) {
                        if (MainActivity.appPreferences.getString("password", "") != "") {
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
