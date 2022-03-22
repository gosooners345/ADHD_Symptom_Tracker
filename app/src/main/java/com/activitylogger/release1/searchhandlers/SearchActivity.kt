@file:Suppress("SpellCheckingInspection")

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
import com.activitylogger.release1.BuildConfig
import com.activitylogger.release1.R
import com.activitylogger.release1.adapters.RecordsAdapter
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.interfaces.OnRecordListener
import com.activitylogger.release1.records.ComposeRecords
import com.activitylogger.release1.supports.RecyclerViewSpaceExtender
import com.activitylogger.release1.ui.home.HomeFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
@Suppress("ReplaceRangeToWithUntil")
class SearchActivity : AppCompatActivity(), OnRecordListener
{
  
  private lateinit var cancelButton: Button
  private lateinit var homeTV: TextView
  private var resultList = HomeFragment.recordsList
  private lateinit var recordsRCV: RecyclerView
  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.search_results)
    homeTV = findViewById(R.id.home_label)
    homeTV.visibility = View.GONE
    recordsRCV = findViewById(R.id.tracker_view)
    recordsRCV.layoutManager = LinearLayoutManager(this)
    recordsRCV.itemAnimator = DefaultItemAnimator()
    if (Intent.ACTION_SEARCH == intent.action)
    {
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
  
  private fun searchDB(query: String)
  {
    var i = resultList.size - 1
    while (i > -1)
    {
      
      val searchArray = ArrayList<String>()
      var counter = 0
      searchArray.add(resultList[i].title)
      searchArray.add(resultList[i].content)
      searchArray.add(resultList[i].emotions)
      searchArray.add(resultList[i].sources)
      searchArray.add(resultList[i].symptoms)
      searchArray.add(resultList[i].tags)
      for (x in 0..searchArray.size - 1)
      {
        if (searchArray[x].contains(query))
        {
          counter++
        }
      }
      if (BuildConfig.DEBUG)
        Log.i("Size", "${resultList.size} entries")
      if (counter == 0)
        resultList.removeAt(i)
      i--
    }
    Collections.sort(resultList, Records.compareUpdatedTimes)
  }
  
  override fun onBackPressed()
  {
    super.onBackPressed()
    cancelButton()
  }
  
  private var cancelButtonClickListener = View.OnClickListener {
    cancelButton()
  }
  
  private fun cancelButton()
  {
    finish()
  }
  
  override fun onRecordClick(position: Int)
  {
    val recordSend = resultList[position]
    val intent = Intent(applicationContext, ComposeRecords::class.java)
    intent.putExtra("RECORDSENT", recordSend)
    intent.putExtra("activityID", HomeFragment.ACTIVITY_ID)
    startActivity(intent)
  }
  
}