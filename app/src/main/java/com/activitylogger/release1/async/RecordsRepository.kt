package com.activitylogger.release1.async

import android.content.Context
import androidx.lifecycle.LiveData
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDB
import kotlinx.coroutines.DelicateCoroutinesApi

class RecordsRepository(context: Context) {
    private var recordsDB : RecordsDB? = RecordsDB.getInstance(context)
    //Update Record
    @DelicateCoroutinesApi
    fun updateRecord(record : Records?){
        UpdateAsync(recordsDB!!.recordDao!!).execute(record)
    }

    @DelicateCoroutinesApi
    fun deleteRecord(record: Records?) {
        DeleteAsync(recordsDB!!.recordDao!!).execute(record)
    }

    @DelicateCoroutinesApi
    fun insertRecord(record: Records?) {
        InsertAsync(recordsDB!!.recordDao!!).execute(record)
    }

    fun getRecords(query: String): LiveData<List<Records>> {
        if (query == "")
            return recordsDB!!.recordDao!!.getRecords()
        else {
            val sequldquery = "%$query%"
            return getSearchedRecords(sequldquery)
        }
    }

    fun getSearchedRecords(query: String): LiveData<List<Records>> {


        return recordsDB!!.recordDao!!.getSearchedRecords(query)
    }

    // Close the DB so it can be rekeyed with new encryption keys
    fun closeDB() {

        recordsDB!!.close()
    }


}