package com.activitylogger.release1.records

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.activitylogger.release1.R
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.ui.home.HomeFragment
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList as recordsList
import com.activitylogger.release1.ui.home.HomeFragment.Companion.homeViewModel as homeViewModel

import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class ComposeRecords : AppCompatActivity(){

    lateinit var title : String
    lateinit var  content : String
 @RequiresApi(Build.VERSION_CODES.N)
 var timeUpdated = Calendar.getInstance().timeInMillis
lateinit var timeCreated : Any
lateinit var record : Records
    var  mode =0
    var recordsRepo : RecordsRepository? = null
lateinit var saveButton : Button
    lateinit    var editButton : Button
    var recordTitleString=""
    var recordContentString=""
    var recordSymptomString = ""
    var recordEmotionString=""
    var recordSourcesString=""
    val emptyString = ""
    var ratingsInfo =0.0
var recordSymptoms = ""
    var isnewRecord = false
    lateinit var recordTitle : TextInputLayout
    lateinit var recordContent : TextInputLayout
    lateinit var recordEmotion : TextInputLayout
    lateinit var recordSources : TextInputLayout
    lateinit var recordSymptomCB : SmartMaterialSpinner<String>
    lateinit var ratingSeekbar : SeekBar
    lateinit var successChip : Chip
     var success =false
    lateinit var symptomArray : ArrayList<String>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.records_compose_layout)
        recordTitle = findViewById(R.id.topicContainer)
        recordContent = findViewById(R.id.contentContainer)
        recordEmotion = findViewById(R.id.emotionsContainer)
        recordSources = findViewById(R.id.sourcesContainer)
        ratingSeekbar = findViewById(R.id.ratingSeekbar)
        successChip = findViewById(R.id.successchip)
        ratingSeekbar.setOnSeekBarChangeListener(ratingSeekBarListener)
        successChip.setOnCheckedChangeListener(successChanged)
        symptomArray=ArrayList()
        symptomArray.addAll(resources.getStringArray(R.array.symptom_array))
        recordsRepo = RecordsRepository(this)
recordSymptomCB = findViewById(R.id.symptomsCB)

        recordSymptomCB.item = symptomArray
        recordSymptomCB.onItemSelectedListener=symptomCBListener
        if (!intentInfo) {
            recordTitle.editText!!.setText(record!!.title)
            recordContent.editText!!.setText(record!!.content)
            recordEmotion.editText!!.setText(record!!.emotions)
            successChip.isChecked = record!!.successState!!
            ratingSeekbar.progress = record!!.rating.toInt()
            if(record.sources!="" || record.sources ==null)
                recordSources.editText!!.setText(record.sources)
            else
                recordSources.editText!!.setText(emptyString)
            recordSymptomCB.setSelection(0)


Log.i(TAG,"Accessing Record for Editing")

        } else {
            record= Records(Date())
            recordTitle.editText!!.setText(emptyString)
            recordContent.editText!!.setText(emptyString)
            recordEmotion.editText!!.setText(emptyString)
            recordSources.editText!!.setText(emptyString)
            successChip.isChecked = false
            ratingSeekbar.progress = 0
            recordSymptomCB.setSelection(0)
///            recordSymptoms = recordSymptomCB.selectedItem
            recordSymptoms=emptyString
            Log.i(TAG,"Logging New Event")


        }
        recordTitle.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                recordTitleString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        recordContent.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                recordContentString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}

        })
        recordEmotion.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                recordEmotionString = s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        recordSources.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, after: Int) {
                 recordSourcesString= s.toString()
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        saveButton = findViewById(R.id.saveNote)
        saveButton.setOnClickListener(saveRecord)
        editButton = findViewById(R.id.editButton)
        editButton.setOnClickListener(editRecord)

    }
private val intentInfo : Boolean
@RequiresApi(Build.VERSION_CODES.O)
get(){
        if (intent.hasExtra("record_selected")) {

            record=getRecordData()
            Log.d(TAG, record.toString())
            mode= EDIT_ON
            isnewRecord=false
        return false
    }
        else{
            isnewRecord = true
            return true
        }

}

@RequiresApi(Build.VERSION_CODES.O)
fun getRecordData():Records
{
    val recordID = intent.getIntExtra("RECORDID",0)
    val recordtitle= intent.getStringExtra(HomeFragment.RECORDTITLE)
    val recordContent = intent.getStringExtra(HomeFragment.RECORDDETAILS)
    val recordEmotions = intent.getStringExtra(HomeFragment.RECORDEMOTIONS)
    var recordRating = intent.getDoubleExtra(HomeFragment.RECORDRATINGS,0.0)
    val recordTimeCreated=intent.getSerializableExtra("TIMECREATED")
    val recordSources = intent.getStringExtra(HomeFragment.RECORDSOURCES)
    val recordSymptoms = intent.getStringExtra("RECORDSYMPTOMS")!!
    val recordSuccess = intent.getBooleanExtra(HomeFragment.RECORDSUCCESS,false)
    return Records(recordtitle,recordID,
        recordTimeCreated as Date,recordEmotions,recordContent,recordRating,System.currentTimeMillis(),recordSuccess,recordSources,recordSymptoms)

}

    override fun onBackPressed() {
        val messageString = if (!isnewRecord) "Save your edits?" else "Save new record?"

        MaterialAlertDialogBuilder(this)
            .setTitle("Save Record?")
            .setMessage(String.format(messageString))
            .setNegativeButton("No") { _, _ ->
                finish()
            }
            .setPositiveButton("Yes") { _, _ -> saveButton.performClick() }
            .setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    var symptomCBListener : AdapterView.OnItemSelectedListener = object :
    AdapterView.OnItemSelectedListener{
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

recordSymptoms =String.format(parent!!.selectedItem.toString())
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {

        }
    }


    private var ratingSeekBarListener :SeekBar.OnSeekBarChangeListener = object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            ratingsInfo = progress.times(1.0)

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            ratingsInfo = seekBar!!.progress.times(1.0)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            ratingsInfo = seekBar!!.progress.times(1.0)
        }

    }

    private var saveRecord =View.OnClickListener{

recordContentString=recordContent.editText!!.text.toString()
        recordTitleString=recordTitle.editText!!.text.toString()
        recordEmotionString=recordEmotion.editText!!.text.toString()
        recordSourcesString=recordSources.editText!!.text.toString()
        recordSymptomString  = recordSymptoms
        ratingsInfo = ratingSeekbar.progress.times(1.0)
        record.timeUpdated = System.currentTimeMillis()
        record.title= recordTitleString
        record.content=recordContentString
        record.emotions=recordEmotionString
        record.sources = recordSourcesString
        record.rating=ratingsInfo
        record.symptoms = recordSymptomString
        record.successState = success
        run {
            if (isnewRecord) {
                recordsRepo!!.insertRecord(record)
                HomeFragment.refreshData()
                recordsList.add(record)
            } else {
                homeViewModel.recordsRepo!!.updateRecord(record)
                HomeFragment.refreshData()

            }
            Log.i(TAG,"Saving Record to storage")
            Log.i(TAG,record.toString())
        }
        finish()
    }
private var editRecord = View.OnClickListener {
    when (mode){
        EDIT_OFF -> enableEdit()
        EDIT_ON -> disableEdit()
    }
}

    private fun enableEdit(){
        mode = EDIT_ON
        recordContent.isEnabled=true
        recordTitle.isEnabled=true
        recordEmotion.isEnabled=true
        ratingSeekbar.isEnabled=true
        successChip.isEnabled=true
        val anchorView = findViewById<View>(R.id.masterLayout)
        Snackbar.make(anchorView,"Record Editing Enabled",Snackbar.LENGTH_SHORT).show()
    }

    private fun disableEdit() {
        mode = EDIT_OFF
        recordContent.isEnabled=false
        recordTitle.isEnabled=false
        recordEmotion.isEnabled=false
        ratingSeekbar.isEnabled=false
        successChip.isEnabled=false
        val anchorView = findViewById<View>(R.id.masterLayout)
        Snackbar.make(anchorView,"Record Editing Disabled",Snackbar.LENGTH_SHORT).show()
    }

    private var successChanged =CompoundButton.OnCheckedChangeListener { compoundButton, _ ->
        if (compoundButton.isChecked) {
            success = true
            compoundButton.text = "Success"
        } else {
            success = false
            compoundButton.text = "Fail"
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mode = savedInstanceState.getInt("mode")
        if (mode == EDIT_ON) {
            val anchorView = findViewById<View>(R.id.masterLayout)
            Snackbar.make(anchorView, "Record Editing Enabled", Snackbar.LENGTH_SHORT).show()

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("mode", mode)
    }


    companion object {
        const val EDIT_ON = 1
        const val EDIT_OFF = 0
        const val TAG = "ComposeRecords"

    }
}