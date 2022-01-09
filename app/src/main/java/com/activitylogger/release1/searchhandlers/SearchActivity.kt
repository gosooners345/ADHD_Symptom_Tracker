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
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(),OnRecordListener {

    lateinit var cancelButton: Button
lateinit var homeTV : TextView
    var resultList = RecordsList()
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
        recordsRCV.adapter = RecordsAdapter(resultList, this)
        val divider = RecyclerViewSpaceExtender(8)
        recordsRCV.addItemDecoration(divider)
        cancelButton = findViewById(R.id.clearButton)
        cancelButton.setOnClickListener(cancelButtonClickListener)

    }

    fun searchDB(query: String) {

        for (record in recordsList) {

            val searchArray = ArrayList<String>()
            var counter = 0
            searchArray.add(record.title)
            searchArray.add(record.content)
            searchArray.add(record.emotions)
            searchArray.add(record.sources)
            searchArray.add(record.symptoms)
            for (i in 0..searchArray.size - 1) {
                if (searchArray[i].contains(query)) {
                    counter++
                }
            }
            if (counter > 0)
                resultList.add(record)

        }
        resultList.setRecordData()

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
        val recordSend = resultList[position]
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