package com.activitylogger.release1.async

import android.content.Context
import androidx.lifecycle.LiveData
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDB

class RecordsRepository(context: Context) {
    private val recordsDB : RecordsDB? = RecordsDB.getInstance(context!!)

    //Update Record
    fun updateRecord(record : Records?){
        UpdateAsync(recordsDB!!.recordDao!!).execute(record)
    }
    fun deleteRecord(record : Records?){
        DeleteAsync(recordsDB!!.recordDao!!).execute(record)
    }
    fun insertRecord(record: Records?){
        InsertAsync(recordsDB!!.recordDao!!).execute(record)
    }
fun getRecords(): LiveData<List<Records>>{
    return recordsDB!!.recordDao!!.getRecords()
}
    fun getRecord(searchID : Int):LiveData<Records?>{
        return  recordsDB!!.recordDao!!.getRecord(searchID)

    }

}