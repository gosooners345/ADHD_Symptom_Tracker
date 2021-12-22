package com.activitylogger.release1.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.adapters.RecordsAdapter
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databinding.FragmentHomeBinding
import com.activitylogger.release1.interfaces.OnRecordListener
import com.activitylogger.release1.records.ComposeRecords
import com.activitylogger.release1.supports.RecyclerViewSpaceExtender

class HomeFragment : Fragment() , OnRecordListener {


    private var _binding: FragmentHomeBinding? = null
//lateinit var adapter : RecordsAdapter
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
        homeViewModel.recordsRepo= RecordsRepository(requireContext())
        getRecords()
        adapter = RecordsAdapter(recordsList, this, requireContext())
        binding.trackerView.layoutManager = LinearLayoutManager(requireContext())
        binding.trackerView.itemAnimator = DefaultItemAnimator()
        binding.trackerView.adapter = adapter
        val divider = RecyclerViewSpaceExtender(8)
        binding.trackerView.addItemDecoration(divider)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.trackerView)
        setHasOptionsMenu(true)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_options,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecordClick(position: Int) {
        val intent = Intent(context, ComposeRecords::class.java)
        intent.putExtra("record_selected", recordsList[position])
        intent.putExtra("activityID", ACTIVITY_ID)
        startActivity(intent)
    }

    fun refreshAdapter(){
        adapter.notifyDataSetChanged()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.created_date -> {
                recordsList.sortedWith(Records.compareCreatedTimes)
                refreshAdapter()
                true
            }
            R.id.created_date_rev -> {
                recordsList.sortedWith(Records.compareUpdatedTimes)
                recordsList.reverse()
                refreshAdapter()
                true
            }
            R.id.recent_updated -> {
                recordsList.sortedWith(Records.compareUpdatedTimes)
                refreshAdapter()
                true
            }
            R.id.success_fail -> {
                recordsList.sortedWith(Records.compareSuccessStates)
                refreshAdapter()
                true
            }
            else -> false
        }

    }
    private fun getRecords() {
        try {
            homeViewModel.recordsRepo!!.getRecords().observeForever {
                if (recordsList.size > 0) recordsList.clear()
                if (it != null) {
                    recordsList.addAll(it)
                    recordsList.sortedWith(Records.compareUpdatedTimes)
                }
                refreshAdapter()
            }
        }
        catch (ex: Exception) {
            Toast.makeText(requireContext(), "This failed", Toast.LENGTH_LONG).show()
            ex.printStackTrace()
            Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
            homeViewModel.recordsRepo!!.getRecords().observe(viewLifecycleOwner, { it ->
                if (recordsList.size > 0) recordsList.clear()
                if (it != null) {
                    recordsList.addAll(it)
                    recordsList.sortedWith(Records.compareUpdatedTimes)
                }
                refreshAdapter()
            })
        }
    }

    private var itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
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
    companion object{
        var recordsList = ArrayList<Records>()
        const val ACTIVITY_ID = 75
         lateinit var homeViewModel: HomeViewModel
        lateinit var adapter : RecordsAdapter
        fun newRecord(context : Context?,activityID:Int)
        {
            val intent = Intent(context, ComposeRecords::class.java)
        intent.putExtra("activityID",activityID)
            intent.putExtra("new_record","NewRecord")
            context!!.startActivity(intent)
        }
        fun refreshData(){
            adapter.notifyDataSetChanged()
        }
    }
}