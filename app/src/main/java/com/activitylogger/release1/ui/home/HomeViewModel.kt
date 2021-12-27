package com.activitylogger.release1.ui.home

import android.content.Context
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.calculateScore
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.lang.Exception

class HomeViewModel : ViewModel() {
var recordsRepo: RecordsRepository? =null
private val _searchResults=MutableLiveData<List<Records>>()
    val searchResults : LiveData<List<Records>>
    get()=_searchResults




 /*   fun getAllRecords(){
        viewModelScope.launch { recordsRepo!!.recordsDB!!.recordDao!!.allRecords().let {
            _searchResults.postValue(it)
        } }
    }*/

    fun deleteRecord(record: Records){
      // HomeFragment.recordsList.remove(record)
        recordsRepo!!.deleteRecord(record)

    }
   /* fun search(query : Editable?){
        viewModelScope.launch {
            if (query.isNullOrBlank()) {
                recordsRepo!!.recordsDB!!.recordDao!!.allRecords().let {
                    _searchResults.postValue(it)
                }
            } else {
                val sanitizedQuery = sanitizeSearchQuery(query)
                recordsRepo!!.recordsDB!!.recordDao!!.search(sanitizedQuery).let {
                    _searchResults.postValue(it)
                }
            }
        }
    }
    fun searchWithScore(query: Editable?){
        viewModelScope.launch {
            if (query.isNullOrBlank()) {
                recordsRepo!!.recordsDB!!.recordDao!!.allRecords().let {
                    _searchResults.postValue(it)
                }
            }
            else
            { val sanitizedQuery = sanitizeSearchQuery(query)
                recordsRepo!!.recordsDB!!.recordDao!!.searchWithMatchInfo(sanitizedQuery).let {
                    results ->
results.sortedByDescending{ result ->
    calculateScore(result.matchInfo)}
        .map{result -> result.record}
        .let{_searchResults.postValue(it)}

}


                }

            }
        }
    }*/

    private fun sanitizeSearchQuery(query: Editable?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }
}

