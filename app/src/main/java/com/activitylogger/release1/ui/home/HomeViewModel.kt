package com.activitylogger.release1.ui.home

import android.content.Context
import android.text.Editable
import android.widget.Toast
import androidx.lifecycle.*
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.calculateScore
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel() : ViewModel() {
var recordsRepo: RecordsRepository? =null
//Handles deleting records
    fun deleteRecord(record: Records){
        recordsRepo!!.deleteRecord(record)

    }
    fun getRecord(recordID : Int):Records {
    return    recordsRepo!!.getRecordData(recordID)!!
    }
    //There is likely going to be more code added to this portion of the application as I get further along in development.
    //My goal is to implement coroutines into this application so that the application can load faster. It will take me some time due to learning about
    // asynchronous  programming.
    //suspend fun deleteRecord(record: Records){}
    //suspend fun addRecord(record: Records){}
}

