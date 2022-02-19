package com.activitylogger.release1.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R
import com.activitylogger.release1.adapters.RecordsAdapter
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.EmotionList
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.data.SymptomList
import com.activitylogger.release1.databinding.FragmentHomeBinding
import com.activitylogger.release1.interfaces.OnRecordListener
import com.activitylogger.release1.records.ComposeRecords
import com.activitylogger.release1.searchhandlers.SearchActivity
import com.activitylogger.release1.settings.AppSettingsActivity
import com.activitylogger.release1.supports.RecyclerViewSpaceExtender
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
@Suppress("LiftReturnOrAssignment", "CascadeIf", "SpellCheckingInspection",
          "MoveLambdaOutsideParentheses"
)
class HomeFragment : Fragment(), OnRecordListener
{
  
  private var _binding: FragmentHomeBinding? = null
  private var reversed = false
  private var tripped = false
  private lateinit var password: String
  private var paused = false
  lateinit var title : TextView
  
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!
  
  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
                           ): View
  {
    homeViewModel =
      ViewModelProvider(this)[HomeViewModel::class.java]
    
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root
    val greetingName = MainActivity.appPreferences.getString("greeting","")
    title = root.findViewById(R.id.home_label)
    title.text = "Hello $greetingName. What would you like to record?"
    //this method handles loading the database into the application by proxy of calling the Repo
    homeViewModel.recordsRepo = RecordsRepository(requireContext())
    recordsRCV = root.findViewById(R.id.tracker_view)
    getRecords()
    adapter = RecordsAdapter(recordsList, this)
    recordsRCV.adapter = adapter
    setupRecordCards()
    paused = false
    password = MainActivity.appPreferences.getString("password", "")!!
    return root
  }
  
  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater)
  {
    inflater.inflate(R.menu.sort_options, menu)
    
    val searchManager: SearchManager =
      requireActivity().getSystemService(
        Context.SEARCH_SERVICE
                                        ) as SearchManager
    ((menu.findItem(R.id.menu_search_widget).actionView) as SearchView).apply {
      setSearchableInfo(
        searchManager.getSearchableInfo(
          ComponentName(
            context,
            SearchActivity::class.java
          )
        )
      )
    }
    
    
    super.onCreateOptionsMenu(menu, inflater)
  }
  
  override fun onOptionsItemSelected(item: MenuItem): Boolean
  {
    return when (item.itemId)
    {
      R.id.navigation_settings ->
      {
        val settingsIntent =
          Intent(requireContext(), AppSettingsActivity::class.java)
        startActivityForResult(settingsIntent, SETTINGS_CODE)
        return true
      }
      R.id.expand_records      ->
      {
        adapter.expandAll()
        return true
      }
      R.id.collapse_records    ->
      {
        adapter.collapseAll()
        return true
      }
      R.id.created_date        ->
      {
        
        Collections.sort(recordsList, Records.compareIds)
        if (!reversed)
        {
          recordsList.reverse()
          refreshAdapter()
          reversed = true
        }
        else
        {
          refreshAdapter()
          reversed = false
        }
        true
      }
      R.id.recent_updated      ->
      {
        Collections.sort(recordsList, Records.compareUpdatedTimes)
        if (!reversed)
        {
          recordsList.reverse()
          refreshAdapter()
          reversed = true
        }
        else
        {
          refreshAdapter()
          reversed = false
        }
        true
      }
      R.id.success_fail        ->
      {
        Collections.sort(recordsList, Records.compareSuccessStates)
        if (!reversed)
        {
          recordsList.reverse()
          refreshAdapter()
          reversed = true
        }
        else
        {
          refreshAdapter()
          reversed = false
        }
        true
      }
      R.id.sort_A_to_Z         ->
      {
        Collections.sort(recordsList, Records.compareAlphabetized)
        if (!reversed)
        {
          recordsList.reverse()
          refreshAdapter()
          reversed = true
        }
        else
        {
          refreshAdapter()
          reversed = false
        }
        true
      }
      R.id.sort_by_rating      ->
      {
        Collections.sort(recordsList, Records.compareRatings)
        if (!reversed)
        {
          recordsList.reverse()
          refreshAdapter()
          reversed = true
        }
        else
        {
          refreshAdapter()
          reversed = false
        }
        true
      }
      
      else                     -> false
    }
    
  }
  
  
  private fun setupRecordCards()
  {
    val layoutString =
      MainActivity.appPreferences.getString("layoutOption_record", "linear")
    
    val vertical =
      if (layoutString == "linear") MainActivity.appPreferences.getString(
        "linear_horizontal_records", "vertical"
                                                                         )
      else
        "horizontal"
    recordsRCV.itemAnimator = DefaultItemAnimator()
    val divider = RecyclerViewSpaceExtender(8)
    recordsRCV.addItemDecoration(divider)
    
    var gridHorizontal = false
    var staggeredHorizontal = false
    var lineHorizontal = false
    
    when (layoutString)
    {
      "linear" ->
      {
        when (vertical)
        {
          "horizontal" ->
          {
            lineHorizontal = true
            gridHorizontal = false
            staggeredHorizontal = false
          }
          "vertical"   ->
          {
            gridHorizontal = false
            staggeredHorizontal = false
            lineHorizontal = false
            
          }
        }
      }
      "grid" -> when (vertical)
      {
        "horizontal" ->
        {
          gridHorizontal = true
          staggeredHorizontal = false
          lineHorizontal = false
          
        }
      }
      "staggered" ->
        when (vertical)
        {
          "horizontal" ->
          {
            staggeredHorizontal = true
            
            gridHorizontal = false
            lineHorizontal = false
          }
        }
    }
    val layoutMgr = if (lineHorizontal) LinearLayoutManager(
      requireContext(),
      LinearLayoutManager.HORIZONTAL,
      false
                                                           )
    else if (gridHorizontal)
      GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
    else if (staggeredHorizontal)
      StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL)
    else
      LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    recordsRCV.layoutManager = layoutMgr
    if (lineHorizontal || staggeredHorizontal || gridHorizontal)
      ItemTouchHelper(deleteUpTouchHandler).attachToRecyclerView(recordsRCV)
    else
      ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recordsRCV)
    setHasOptionsMenu(true)
    tripped = true
    
  }
  
  /*Returns from settings two things, if the password has changed, the application will close the DB so the DB can be rekeyed,
   otherwise the app is refreshed on return*/
  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  )
  {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == SETTINGS_CODE)
      if (resultCode == RESULT_OK)
      {
        
        if (MainActivity.appPreferences.getString("password", "") != password)
        {
          if (MainActivity.buildType == "debug")
            Toast.makeText(
              requireContext(),
              "Closing the database so it can be reencrypted with your new password",
              Toast.LENGTH_LONG
                          ).show()
          else
            Toast.makeText(
              requireContext(),
              "When you log in next time, use your new password.",
              Toast.LENGTH_LONG
                          ).show()
          homeViewModel.recordsRepo!!.closeDB()
          homeViewModel.recordsRepo = RecordsRepository(requireContext())
          getRecords()
          adapter = RecordsAdapter(recordsList, this)
          recordsRCV.adapter = adapter
          setupRecordCards()
        }
        else
          setupRecordCards()
      }
    
  }
  
  override fun onDestroyView()
  {
    super.onDestroyView()
    
    _binding = null
  }
  
  
  // Opens an exisiting record for editing
  override fun onRecordClick(position: Int)
  {
    val recordSend = recordsList[position]
    val intent = Intent(context, ComposeRecords::class.java)
    intent.putExtra("RECORDSENT", recordSend)
    intent.putExtra("activityID", ACTIVITY_ID)
    startActivity(intent)
  }
  
  //Refreshes the adapter after a record is submitted to the DB
  @SuppressLint("NotifyDataSetChanged")
  fun refreshAdapter()
  {
    recordsList.setRecordData()
    symptomsList = SymptomList.importData(recordsList.symptomList)
    emotionList = EmotionList.importData(recordsList.emotionList)
    adapter.notifyDataSetChanged()
  }
  
  // Prevents accidental deletions
  private fun validateDeleteRecordChoice(position: Int)
  {
    val titleString = "Delete Record?"
    val messageString = "Are you sure you want to delete this record?"
    MaterialAlertDialogBuilder(requireContext())
      .setTitle(titleString)
      .setMessage(String.format(messageString))
      .setNegativeButton("No") { dialog, _ ->
        dialog.cancel()
      }
      .setPositiveButton("Yes") { dialog, _ ->
        Log.i("Records", "Deleted Record Info: ${recordsList[position]}")
        homeViewModel.deleteRecord(recordsList[position])
        adapter.notifyItemRemoved(position)
        refreshAdapter()
        
      }
      .show()
    refreshAdapter()
    
  }
  
  
  //Retrieves records from the DB
  private fun getRecords()
  {
    try
    {
      net.sqlcipher.database.SQLiteDatabase.loadLibs(requireContext())
      
      homeViewModel.recordsRepo!!.getRecords().observe(viewLifecycleOwner, {
        if (recordsList.size > 0) recordsList.clear()
        if (it != null)
        {
          recordsList.addAll(it)
          Collections.sort(recordsList, Records.compareUpdatedTimes)
          recordsList.reverse()
          refreshAdapter()
        }
        refreshAdapter()
      })
    }
    catch (ex: Exception)
    {
      Toast.makeText(requireContext(), "This failed", Toast.LENGTH_LONG).show()
      ex.printStackTrace()
      Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
      
    }
  }
  
  //This is the implementation for the delete method to take place.
  @DelicateCoroutinesApi
  private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
    object : ItemTouchHelper.SimpleCallback(
      ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
      ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT)
                                           )
    {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
                         ): Boolean
      {
        recyclerView.adapter!!.notifyItemMoved(
          viewHolder.bindingAdapterPosition, target.bindingAdapterPosition
                                              )
        
        return true
      }
      
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
      {
        validateDeleteRecordChoice(viewHolder.bindingAdapterPosition)
      }
    }
  private var deleteUpTouchHandler: ItemTouchHelper.SimpleCallback =
    object : ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.UP)
    {
      override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
                         ): Boolean
      {
        return false
      }
      
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
      {
        validateDeleteRecordChoice(viewHolder.bindingAdapterPosition)
      }
    }
  
  @SuppressLint("StaticFieldLeak")
  companion object
  {
    
    var recordsList = RecordsList()
    const val ACTIVITY_ID = 75
    const val SETTINGS_CODE = 55
    lateinit var homeViewModel: HomeViewModel
    lateinit var adapter: RecordsAdapter
    lateinit var recordsRCV: RecyclerView
    fun newRecord(context: Context?, activityID: Int)
    {
      val intent = Intent(context, ComposeRecords::class.java)
      intent.putExtra("activityID", activityID)
      intent.putExtra("new_record", "NewRecord")
      context!!.startActivity(intent)
    }
    
    @SuppressLint("NotifyDataSetChanged")
    fun refreshData()
    {
      recordsList.setRecordData()
      adapter.notifyDataSetChanged()
      
    }
    
    var symptomsList = SymptomList()
    var emotionList = EmotionList()
    
    
  }
}