package com.activitylogger.release1.searchhandlers

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.adapters.RecordsAdapter
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.interfaces.OnRecordListener
import com.activitylogger.release1.records.ComposeRecords
import com.activitylogger.release1.supports.RecyclerViewSpaceExtender
import com.activitylogger.release1.ui.home.HomeFragment
import com.activitylogger.release1.ui.home.HomeFragment.Companion.homeViewModel
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(),OnRecordListener{

    lateinit var cancelButton: Button
lateinit var homeTV : TextView
    var resultList =HomeFragment.recordsList
    lateinit var recordsRCV: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_results)
        homeTV = findViewById(R.id.home_label)
        homeTV.visibility= View.GONE
        recordsRCV = findViewById(R.id.tracker_view)
        recordsRCV.layoutManager = LinearLayoutManager(this)
        recordsRCV.itemAnimator = DefaultItemAnimator()
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                searchDB(query)
            }
        }
        Collections.sort(resultList, Records.compareAlphabetized)
        recordsRCV.adapter = RecordsAdapter(resultList as RecordsList, this)
        val divider = RecyclerViewSpaceExtender(8)
        recordsRCV.addItemDecoration(divider)
        cancelButton = findViewById(R.id.clearButton)
        cancelButton.setOnClickListener(cancelButtonClickListener)

    }

    fun searchDB(query: String) {
var i =resultList.size-1
        while (i >-1) {

            val searchArray = ArrayList<String>()
            var counter = 0
            searchArray.add(resultList[i].title)
            searchArray.add(resultList[i].content)
            searchArray.add(resultList[i].emotions)
            searchArray.add(resultList[i].sources)
            searchArray.add(resultList[i].symptoms)
            for (i in 0..searchArray.size - 1) {
                if (searchArray[i].contains(query)) {
                    counter++
                }
            }
            Log.i("Size","${resultList.size} entries")
            if (counter == 0)
                resultList.removeAt(i)

            i--

        }
Collections.sort(resultList,Records.compareAlphabetized)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        cancelButton()
    }

    var cancelButtonClickListener = View.OnClickListener {
        cancelButton()
    }

    private fun cancelButton() {
        finish()
    }

    override fun onRecordClick(position: Int) {
        val record = resultList[position]

        val recordSend =record
        val intent = recordStore(recordSend)
        intent.putExtra("record_selected_id", recordSend.id)
        Log.i("Tag", "$recordSend")
        intent.putExtra("activityID", HomeFragment.ACTIVITY_ID)
        startActivity(intent)
    }

    private fun recordStore(record: Records): Intent {
        val recordIntent = Intent(this, ComposeRecords::class.java)
        recordIntent.putExtra(HomeFragment.record_send, "SELECTED")
        recordIntent.putExtra("RECORDID", record.id)
        recordIntent.putExtra(HomeFragment.RECORDTITLE, record.title)
        recordIntent.putExtra(HomeFragment.RECORDDETAILS, record.content)
        recordIntent.putExtra(HomeFragment.RECORDEMOTIONS, record.emotions)
        recordIntent.putExtra(HomeFragment.RECORDSOURCES, record.sources)
        recordIntent.putExtra(HomeFragment.RECORDRATINGS, record.rating)
        recordIntent.putExtra(HomeFragment.RECORDSUCCESS,record.successState)
        recordIntent.putExtra("RECORDSYMPTOMS",record.symptoms)
        recordIntent.putExtra("TIMECREATED", record.timeCreated)

        return recordIntent
    }

    companion object {
        const val record_send = "record_selected"
        const val RECORDTITLE = "RECORDTITLE"
        const val RECORDEMOTIONS = "RECORDEMOTIONS"
        const val RECORDDETAILS = "RECORDDETAILS"
        const val RECORDSOURCES = "RECORDSOURCES"
        const val RECORDRATINGS = "RECORDRATINGS"
        const val RECORDSUCCESS = "RECORDSUCCESS"
    }

}