package com.activitylogger.release1.ui.home

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.SearchView
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
import java.util.*

class HomeFragment : Fragment() , OnRecordListener {

    private var _binding: FragmentHomeBinding? = null
    var reversed = false
    var tripped = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        homeViewModel.recordsRepo = RecordsRepository(requireContext())
        getRecords()


        val gridSize = MainActivity.appPreferences.getInt("gridSize_record", 3)
        val layoutString = MainActivity.appPreferences.getString("layoutOption_record", "linear")
        var layoutMgr: RecyclerView.LayoutManager?
        val vertical =
            MainActivity.appPreferences.getString("linear_horizontal_records", "vertical")
        if (layoutString == "linear")
            layoutMgr = if (vertical == "horizontal")
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            else
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        else if (layoutString == "staggered")
        {        if (vertical == "horizontal")
            layoutMgr = StaggeredGridLayoutManager(gridSize, StaggeredGridLayoutManager.HORIZONTAL)
        else
            layoutMgr = StaggeredGridLayoutManager(gridSize, StaggeredGridLayoutManager.VERTICAL)}
else
            layoutMgr = GridLayoutManager(context, gridSize)

        adapter = RecordsAdapter(recordsList,this)

        recordsRCV = root.findViewById(R.id.tracker_view)

        recordsRCV.layoutManager = layoutMgr
        recordsRCV.itemAnimator = DefaultItemAnimator()
        recordsRCV.adapter = adapter
        val divider = RecyclerViewSpaceExtender(8)
        recordsRCV.addItemDecoration(divider)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recordsRCV)
        setHasOptionsMenu(true)
        tripped = true
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_options, menu)

        val searchManager: SearchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_settings -> {
                val settingsIntent = Intent(requireContext(),AppSettingsActivity::class.java)
                requireContext().startActivity(settingsIntent)
                return true
            }
            R.id.expand_records->{
                adapter.expandAll()
                return true
            }
            R.id.collapse_records->{
                adapter.collapseAll()
                return true
            }
            R.id.created_date -> {

                Collections.sort(recordsList, Records.compareIds)
                if (!reversed) {
                    recordsList.reverse()
                    refreshAdapter()
                    reversed = true
                } else {
                    refreshAdapter()
                    reversed = false
                }
                true
            }
            R.id.recent_updated -> {
                Collections.sort(recordsList, Records.compareUpdatedTimes)
                if (!reversed) {
                    recordsList.reverse()
                    refreshAdapter()
                    reversed = true
                } else {
                    refreshAdapter()
                    reversed = false
                }
                true
            }
            R.id.success_fail -> {
                Collections.sort(recordsList, Records.compareSuccessStates)
                if (!reversed) {
                    recordsList.reverse()
                    refreshAdapter()
                    reversed = true
                } else {
                    refreshAdapter()
                    reversed = false
                }
                true
            }
            R.id.sort_A_to_Z -> {
                Collections.sort(recordsList, Records.compareAlphabetized)
                if (!reversed) {
                    recordsList.reverse()
                    refreshAdapter()
                    reversed = true
                } else {
                    refreshAdapter()
                    reversed = false
                }
                true
            }
            R.id.sort_by_rating -> {
                Collections.sort(recordsList, Records.compareRatings)
                if (!reversed) {
                    recordsList.reverse()
                    refreshAdapter()
                    reversed = true
                } else {
                    refreshAdapter()
                    reversed = false
                }
                true
            }

            else -> false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //This is a simpler accessor method for retrieving records for ComposeRecords Class
    private fun recordStore(record: Records): Intent {
        val recordIntent = Intent(context, ComposeRecords::class.java)
        recordIntent.putExtra(record_send, "SELECTED")
        recordIntent.putExtra("RECORDID", record.id)
        recordIntent.putExtra(RECORDTITLE, record.title)
        recordIntent.putExtra(RECORDDETAILS, record.content)
        recordIntent.putExtra(RECORDEMOTIONS, record.emotions)
        recordIntent.putExtra(RECORDSOURCES, record.sources)
        recordIntent.putExtra(RECORDRATINGS, record.rating)
        recordIntent.putExtra(RECORDSUCCESS, record.successState)
        if (record.symptoms != "")
            recordIntent.putExtra("RECORDSYMPTOMS", record.symptoms)
        else
            recordIntent.putExtra("RECORDSYMPTOMS", "")

        recordIntent.putExtra("TIMECREATED", record.timeCreated)
        return recordIntent
    }
// This handles record selection basically a read for the record.
    override fun onRecordClick(position: Int) {
        val recordSend = recordsList[position]
        val intent = recordStore(recordSend)
        intent.putExtra("record_selected_id", recordSend.id)
        Log.i("Tag", "${recordSend}")
        intent.putExtra("activityID", ACTIVITY_ID)
        startActivity(intent)
    }

    //Refreshes the adapter after a record is submitted to the DB
    fun refreshAdapter() {

        recordsList.setRecordData()
        symptomsList = SymptomList.importData(recordsList.symptomList)
        emotionList = EmotionList.importData(recordsList.emotionList)
        adapter.notifyDataSetChanged()
    }

    //Retrieves records from the DB
    private fun getRecords() {
        try {

            homeViewModel.recordsRepo!!.getRecords().observe(viewLifecycleOwner, { it ->
                if (recordsList.size > 0) recordsList.clear()
                if (it != null) {
                    recordsList.addAll(it)
                    Collections.sort(recordsList, Records.compareUpdatedTimes)
                    recordsList.reverse()
                    refreshAdapter()
                }
                refreshAdapter()

            })
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), "This failed", Toast.LENGTH_LONG).show()
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()

        }
    }

//This is the implementation for the delete method to take place.
    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(2, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                homeViewModel.deleteRecord(recordsList[viewHolder.bindingAdapterPosition])
                refreshAdapter()
            }
        }

    @SuppressLint("StaticFieldLeak")
    companion object {
        var recordsList = RecordsList()
        const val ACTIVITY_ID = 75
        lateinit var homeViewModel: HomeViewModel
        lateinit var adapter: RecordsAdapter
        lateinit var recordsRCV: RecyclerView
        fun newRecord(context: Context?, activityID: Int) {
            val intent = Intent(context, ComposeRecords::class.java)
            intent.putExtra("activityID", activityID)
            intent.putExtra("new_record", "NewRecord")
            context!!.startActivity(intent)
        }

        fun refreshData() {
            recordsList.setRecordData()
            adapter.notifyDataSetChanged()

        }

        const val record_send = "record_selected"
        const val RECORDTITLE = "RECORDTITLE"
        const val RECORDEMOTIONS = "RECORDEMOTIONS"
        const val RECORDDETAILS = "RECORDDETAILS"
        const val RECORDSOURCES = "RECORDSOURCES"
        const val RECORDRATINGS = "RECORDRATINGS"
        const val RECORDSUCCESS = "RECORDSUCCESS"
        var symptomsList = SymptomList()
        var emotionList = EmotionList()


    }
}