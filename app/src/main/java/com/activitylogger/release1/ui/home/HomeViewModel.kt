package com.activitylogger.release1.ui.home

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
var recordsRepo: RecordsRepository? =null

    fun deleteRecord(record: Records){
       HomeFragment.recordsList.remove(record)
        recordsRepo!!.deleteRecord(record)

    }
}