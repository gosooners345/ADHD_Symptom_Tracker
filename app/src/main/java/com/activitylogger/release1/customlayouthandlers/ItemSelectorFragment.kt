
package com.activitylogger.release1.customlayouthandlers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.MainActivity
import com.activitylogger.release1.R
import com.activitylogger.release1.supports.RecyclerViewSpaceExtender

@Suppress("LiftReturnOrAssignment", "ReplaceRangeToWithUntil")
class ItemSelectorFragment :AppCompatActivity(),OnItemSelected {
    private var resourceSymptoms = ArrayList<String>()
    private var symptomList: String? = ""
    private lateinit var saveButton: Button
    var symptoms = ArrayList<String>()
    private lateinit var itemClassAdapter: ItemClassAdapter
    lateinit var itemRCV: RecyclerView
// This code doesn't work because it resets the selected items list every time you go to open the screen
    //var itemClassList = ItemClassList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Making this an actual ArrayList prior to entering this activity may actually work.
        //Just gotta make sure to return the list in the end so that the values don't get erased
       // symptomList =
        //Likely to be deprecated if making an arraylist prior to entry will pass through both intents.
        symptoms= intent.getStringArrayListExtra("symptom")!!  //.addAll(symptomList!!.split(","))
        resourceSymptoms.addAll(resources.getStringArray(R.array.symptom_array))
        for (i in 0..resourceSymptoms.size - 1) {
            itemClassList.add(
                ItemClass(
                    resourceSymptoms[i],
                    symptoms.contains(resourceSymptoms[i])
                )
            )
        }
        setContentView(R.layout.itemclassview)
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener(saveButtonClickListener)

        val layoutPrefs = MainActivity.appPreferences



        itemRCV = findViewById(R.id.itemListDropDown)
        itemClassAdapter = ItemClassAdapter(itemClassList, this)
        val itemLayoutPrefs = layoutPrefs.getString("layoutOption", "linear")
        val gridSize = layoutPrefs.getInt("gridSize", 3)
        @Suppress("CanBeVal") var layoutMgr: RecyclerView.LayoutManager?
        val vertical = layoutPrefs.getString("linear_horizontal_symptoms", "vertical")
        if (itemLayoutPrefs == "linear")
            if (vertical == "horizontal")
                layoutMgr = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            else
                layoutMgr = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        else if (itemLayoutPrefs == "grid")
            layoutMgr = GridLayoutManager(this, gridSize)
        else {
            if (vertical == "horizontal")
                layoutMgr =
                    StaggeredGridLayoutManager(gridSize, StaggeredGridLayoutManager.HORIZONTAL)
            else
                layoutMgr =
                    StaggeredGridLayoutManager(gridSize, StaggeredGridLayoutManager.VERTICAL)
        }
        itemRCV.layoutManager = layoutMgr
        itemRCV.itemAnimator = DefaultItemAnimator()
        val divider = RecyclerViewSpaceExtender(8)
        itemRCV.addItemDecoration(divider)
        itemRCV.adapter = itemClassAdapter

    }

    private fun closeSymptomBox() {
        val returnIntent = Intent()
        //1/26/22 ArrayList was used to store the strings to ensure that the parent class to this one would have control over how to display the data
        returnIntent.putStringArrayListExtra("symptom", symptoms)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    private var saveButtonClickListener = View.OnClickListener {
        closeSymptomBox()
    }

    override fun onBackPressed() {
        closeSymptomBox()
        super.onBackPressed()
    }

    override fun onItemChecked(position: Int, checkedState: Boolean) {
        val item = itemClassList[position]
        item.selected = checkedState
        if (item.selected) {
            itemClassList.selectedItems.add(item.item)
            itemClassList.selectedCount++

        } else {
            itemClassList.selectedItems.remove(item.item)
            itemClassList.selectedCount--
        }
        symptoms = itemClassList.selectedItems
    }

    companion object {
        //Current Version Code
        var itemClassList =ItemClassList()

    }
}
