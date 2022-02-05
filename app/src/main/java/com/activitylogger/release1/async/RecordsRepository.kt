package com.activitylogger.release1.async

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDB
import kotlinx.coroutines.DelicateCoroutinesApi

class RecordsRepository(context: Context) {
    @RequiresApi(Build.VERSION_CODES.O)
    private val recordsDB : RecordsDB? = RecordsDB.getInstance(context)
    //Update Record
    @RequiresApi(Build.VERSION_CODES.O)
    @DelicateCoroutinesApi
    fun updateRecord(record : Records?){
        UpdateAsync(recordsDB!!.recordDao!!).execute(record)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @DelicateCoroutinesApi
    fun deleteRecord(record : Records?){
        DeleteAsync(recordsDB!!.recordDao!!).execute(record)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @DelicateCoroutinesApi
    fun insertRecord(record: Records?){
        InsertAsync(recordsDB!!.recordDao!!).execute(record)
    }
/*@RequiresApi(Build.VERSION_CODES.O)
suspend fun getRecords(): List<Records>{
    return recordsDB!!.recordDao!!.getRecords()
}*/
fun getRecords() : LiveData<List<Records>>
{
    return recordsDB!!.recordDao!!.getRecords()
}

}