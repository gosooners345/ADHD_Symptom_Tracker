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
}

