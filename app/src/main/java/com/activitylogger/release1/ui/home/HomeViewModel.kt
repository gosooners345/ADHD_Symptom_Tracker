package com.activitylogger.release1.ui.home

import androidx.lifecycle.ViewModel
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records
import kotlinx.coroutines.DelicateCoroutinesApi

class HomeViewModel() : ViewModel() {
var recordsRepo: RecordsRepository? =null
//Handles deleting records
@DelicateCoroutinesApi
fun deleteRecord(record: Records){
        recordsRepo!!.deleteRecord(record)

    }
    //There is likely going to be more code added to this portion of the application as I get further along in development.
    //My goal is to implement coroutines into this application so that the application can load faster. It will take me some time due to learning about
    // asynchronous  programming.
    //suspend fun deleteRecord(record: Records){}
    //suspend fun addRecord(record: Records){}
}

