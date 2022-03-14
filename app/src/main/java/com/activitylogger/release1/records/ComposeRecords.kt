package com.activitylogger.release1.records

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.activitylogger.release1.R
import com.activitylogger.release1.async.RecordsRepository
import com.activitylogger.release1.customlayouthandlers.ItemSelectorFragment
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.ui.home.HomeFragment
import com.activitylogger.release1.ui.home.HomeFragment.Companion.homeViewModel
import com.activitylogger.release1.ui.home.HomeFragment.Companion.recordsList
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
@Suppress("SpellCheckingInspection", "LiftReturnOrAssignment")
class ComposeRecords : AppCompatActivity(){

    lateinit var title : String
    lateinit var  content : String
    lateinit var record : Records
    private var  mode =0
    private var recordsRepo : RecordsRepository? = null
private lateinit var saveButton : Button
    private lateinit    var editButton : Button
    private lateinit var symptomSelectorCardView : MaterialCardView
    var recordTitleString=""
    var recordContentString=""
    var recordEmotionString=""
    var recordSourcesString=""
    private val emptyString = ""
    var ratingsInfo =0.0
private var recordSymptoms = ""
    private var isnewRecord = false
    private lateinit var titleHdr : TextView
    private lateinit var recordTitle : TextInputLayout
    private lateinit var recordContent : TextInputLayout
    private lateinit var recordEmotion : TextInputLayout
    private lateinit var recordSources : TextInputLayout
    private lateinit var enterArrow : ImageView
    
    private lateinit var symptomselectorCB : TextView
    private lateinit var ratingSeekbar: SeekBar
    private lateinit var ratingDisplay: TextView
    private lateinit var successChip: Chip
     var success =false
    
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        /*  if (MainActivity.buildType == "debug")
          {
              setContentView(R.layout.records_compose_layout_test)
              
          }*/
    
        setContentView(R.layout.records_compose_layout)
    
        titleHdr = findViewById(R.id.Login_TitleHdr)
        recordTitle = findViewById(R.id.topicContainer)
        recordContent = findViewById(R.id.contentContainer)
        recordEmotion = findViewById(R.id.emotionsContainer)
        recordSources = findViewById(R.id.sourcesContainer)
        ratingSeekbar = findViewById(R.id.ratingSeekbar)
        ratingDisplay = findViewById(R.id.ratingValue)
        successChip = findViewById(R.id.successchip)
        symptomSelectorCardView = findViewById(R.id.symptomSelectorCBLayout)
        enterArrow = findViewById(R.id.enterArrow)
        ratingSeekbar.setOnSeekBarChangeListener(ratingSeekBarListener)
        successChip.setOnCheckedChangeListener(successChanged)
        recordsRepo = RecordsRepository(this)
        symptomselectorCB = findViewById(R.id.symptomSelectorCB)
        symptomSelectorCardView.setOnClickListener(symptomSelectedListener)
        symptomselectorCB.setOnClickListener(symptomSelectedListener)
        enterArrow.setOnClickListener(symptomSelectedListener)
    
        if (!intentInfo)
        {
            titleHdr.text = "Edit Record"
            recordTitle.editText!!.setText(record.title)
            recordContent.editText!!.setText(record.content)
            recordEmotion.editText!!.setText(record.emotions)
            successChip.isChecked = record.successState!!
            ratingSeekbar.progress = record.rating.toInt()
            ratingDisplay.text = ratingSeekbar.progress.toString()
            if (record.sources != "")
                recordSources.editText!!.setText(record.sources)
            else
                recordSources.editText!!.setText(emptyString)
            if (record.symptoms != "")
            {
                symptomselectorCB.text = record.symptoms
                recordSymptoms = symptomselectorCB.text.toString()
            }
            else
            {
                symptomselectorCB.text = ""
                recordSymptoms = symptomselectorCB.text.toString()
            }
            
            
            Log.i(TAG, "Accessing Record for Editing")
            
        }
        else
        {
            titleHdr.text = "New Record"
            record = Records(Date())
            recordTitle.editText!!.setText(emptyString)
            recordContent.editText!!.setText(emptyString)
            recordEmotion.editText!!.setText(emptyString)
            recordSources.editText!!.setText(emptyString)
            successChip.isChecked = false
            ratingSeekbar.progress = 0
            ratingDisplay.text = ratingSeekbar.progress.toString()
            symptomselectorCB.text = ""
            
            recordSymptoms = symptomselectorCB.text.toString()
            
            Log.i(TAG, "Logging New Event")
            
            
        }
        recordTitle.editText!!.addTextChangedListener(object : TextWatcher
                                                      {
                                                          override fun beforeTextChanged(
                                                              s: CharSequence,
                                                              start: Int,
                                                              before: Int,
                                                              count: Int
                                                          )
                                                          {
                                                          }
            
                                                          override fun onTextChanged(
                                                              s: CharSequence,
                                                              start: Int,
                                                              before: Int,
                                                              after: Int
                                                          )
                                                          {
                                                              recordTitleString =
                                                                  s.toString()
                                                          }
            
                                                          override fun afterTextChanged(
                                                              editable: Editable
                                                          )
                                                          {
                                                          }
                                                      })
        recordContent.editText!!.addTextChangedListener(object : TextWatcher
                                                        {
                                                            override fun beforeTextChanged(
                                                                s: CharSequence,
                                                                start: Int,
                                                                count: Int,
                                                                after: Int
                                                            )
                                                            {
                                                            }
            
                                                            override fun onTextChanged(
                                                                s: CharSequence,
                                                                start: Int,
                                                                before: Int,
                                                                count: Int
                                                            )
                                                            {
                                                                recordContentString =
                                                                    s.toString()
                                                            }
            
                                                            override fun afterTextChanged(
                                                                editable: Editable
                                                            )
                                                            {
                                                            }
            
                                                        })
        recordEmotion.editText!!.addTextChangedListener(object : TextWatcher
                                                        {
                                                            override fun beforeTextChanged(
                                                                s: CharSequence,
                                                                start: Int,
                                                                before: Int,
                                                                count: Int
                                                            )
                                                            {
                                                            }
            
                                                            override fun onTextChanged(
                                                                s: CharSequence,
                                                                start: Int,
                                                                before: Int,
                                                                after: Int
                                                            )
                                                            {
                                                                recordEmotionString =
                                                                    s.toString()
                                                            }
            
                                                            override fun afterTextChanged(
                                                                editable: Editable
                                                            )
                                                            {
                                                            }
                                                        })
        recordSources.editText!!.addTextChangedListener(object : TextWatcher
                                                        {
                                                            override fun beforeTextChanged(
                                                                s: CharSequence,
                                                                start: Int,
                                                                before: Int,
                                                                count: Int
                                                            )
                                                            {
                                                            }
            
                                                            override fun onTextChanged(
                                                                s: CharSequence,
                                                                start: Int,
                                                                before: Int,
                                                                after: Int
                                                            )
                                                            {
                                                                recordSourcesString =
                                                                    s.toString()
                                                            }
            
                                                            override fun afterTextChanged(
                                                                editable: Editable
                                                            )
                                                            {
                                                            }
                                                        })
        
        saveButton = findViewById(R.id.saveNote)
        saveButton.setOnClickListener(saveRecord)
        editButton = findViewById(R.id.editButton)
        editButton.setOnClickListener(editRecord)
        /*if (MainActivity.buildType == "debug")
        {
            var types = ArrayList<String>()
            types.addAll(resources.getStringArray(R.array.entry_categories))
            var typeAdapter = ArrayAdapter(
                this, R.layout
                    .support_simple_spinner_dropdown_item, types
            )
            
            var recordSpinner = SmartMaterialSpinner<Any>(this)
            recordSpinner.item = types as List<Any>?
            
            recordSpinner.adapter = typeAdapter
            
            
        }*/
    }

    private val intentInfo : Boolean
        get(){
            if (intent.hasExtra("RECORDSENT")) {

                record= intent.getParcelableExtra("RECORDSENT")!!
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
    
    
    private var symptomSelectedListener = View.OnClickListener{
val sendIntent = Intent(this,ItemSelectorFragment::class.java)

    val listofSymptoms = ArrayList<String>()
    listofSymptoms.addAll(recordSymptoms.split(','))
    sendIntent.putStringArrayListExtra("symptom",listofSymptoms)
startActivityForResult(sendIntent, REQ_CODE_SYMPTOM)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        recordSymptoms=""
        symptomselectorCB.text=""

        if(requestCode == REQ_CODE_SYMPTOM) {
            if (resultCode == RESULT_OK) {
                val symptomList = data!!.getStringArrayListExtra("symptom")!!
                for (item in symptomList)
                    recordSymptoms += String.format("$item,")
                recordSymptoms = recordSymptoms.trimEnd(',',' ')
            }
            symptomselectorCB.text =recordSymptoms.trimEnd(',',' ')

        }
    }

    private var ratingSeekBarListener :SeekBar.OnSeekBarChangeListener = object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            ratingsInfo = progress.times(1.0)
            ratingDisplay.text = progress.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            ratingsInfo = seekBar!!.progress.times(1.0)
            ratingDisplay.text = seekBar!!.progress.toString()
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            ratingsInfo = seekBar!!.progress.times(1.0)
            ratingDisplay.text = seekBar!!.progress.toString()
        }

    }


    @DelicateCoroutinesApi
    private var saveRecord =View.OnClickListener{

recordContentString=recordContent.editText!!.text.toString()
        recordTitleString=recordTitle.editText!!.text.toString()
        recordEmotionString=recordEmotion.editText!!.text.toString()
        recordSourcesString=recordSources.editText!!.text.toString()
        ratingsInfo = ratingSeekbar.progress.times(1.0)
        record.timeUpdated = System.currentTimeMillis()
        record.title= recordTitleString
        record.content=recordContentString
        record.emotions=recordEmotionString
        record.sources = recordSourcesString
        record.rating=ratingsInfo
        record.symptoms = symptomselectorCB.text.toString()
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
        symptomselectorCB.isEnabled = true
        symptomSelectorCardView.isEnabled=true
        ratingSeekbar.isEnabled=true
        enterArrow.isEnabled=true
        successChip.isEnabled=true
        recordSources.isEnabled=true
        val anchorView = findViewById<View>(R.id.masterLayout)
        Snackbar.make(anchorView,"Record Editing Enabled",Snackbar.LENGTH_SHORT).show()
    }

    private fun disableEdit() {
        mode = EDIT_OFF
        recordContent.isEnabled=false
        recordTitle.isEnabled=false
        enterArrow.isEnabled=false
        symptomSelectorCardView.isEnabled=false

        recordEmotion.isEnabled=false
        recordSources.isEnabled=false
        symptomselectorCB.isEnabled=false
        ratingSeekbar.isEnabled=false
        successChip.isEnabled=false
        val anchorView = findViewById<View>(R.id.masterLayout)
        Snackbar.make(anchorView,"Record Editing Disabled",Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
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
        const val REQ_CODE_SYMPTOM = 45


    }
}
