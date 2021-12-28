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

    fun deleteRecord(record: Records){
        recordsRepo!!.deleteRecord(record)

    }

    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }
}

