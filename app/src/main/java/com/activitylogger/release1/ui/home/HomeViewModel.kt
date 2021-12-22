package com.activitylogger.release1.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records

class HomeViewModel : ViewModel() {
var recordsRepo: RecordsRepository? =null

    fun deleteRecord(record: Records){
        val recordPosition = HomeFragment.recordsList.indexOf(record)
       HomeFragment.recordsList.remove(record)
        recordsRepo!!.deleteRecord(record)

    }

}