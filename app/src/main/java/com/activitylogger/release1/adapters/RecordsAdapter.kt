package com.activitylogger.release1.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsList
import com.activitylogger.release1.interfaces.OnRecordListener
import org.w3c.dom.Text

import java.text.DateFormat
import kotlin.collections.ArrayList




class RecordsAdapter(
    private val recordList: RecordsList,
    private val onRecordListener: OnRecordListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    //Changes Made: Added the ability to collapse and expand the view without needing a special adapter class.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return when(viewType){
            Records.RecordState.COLLAPSED.ordinal ->{
                (CollapsedRecordsViewHolder(LayoutInflater.from(context).inflate(R.layout.record_item_layout_collapsed,parent,false),onRecordListener))}
        Records.RecordState.EXPANDED.ordinal->{
            ExpandedViewHolder(LayoutInflater.from(context).inflate(R.layout.records_item_layout,parent,false),onRecordListener)}
        else ->
            CollapsedRecordsViewHolder(LayoutInflater.from(context).inflate(R.layout.record_item_layout_collapsed,parent,false),onRecordListener)
        }

    }

    @SuppressLint("SetTextI18n")
     override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val record = recordList[position]
        when (record.recordState) {
            Records.RecordState.COLLAPSED -> {
                val vh = (holder as CollapsedRecordsViewHolder)
                val title = vh.titleTV
                val ratings = vh.ratingTV
                val collapse_expandButton = vh.expandButton
                val symptoms = vh.symptomsTV
                val timeCreated = vh.timeCreateTV
                val timeUpdated = vh.timeUpdatedTV
                title.text = record.title
                ratings.text = "Rating: ${record.rating}"
                symptoms.text =
                    record.content//"ADHD Symptoms/Benefits: ${if (record.symptoms != "") record.symptoms else "Record Symptoms Here"}"
                timeCreated.text =
                    "Time Created: " + DateFormat.getInstance().format(record.timeCreated)
                timeUpdated.text =
                    "Last Updated: " + DateFormat.getInstance().format(record.timeUpdated)
                collapse_expandButton.setOnClickListener { expandItem(position) }

            }
            Records.RecordState.EXPANDED -> {
                val vh = (holder as ExpandedViewHolder)
                val recordTitle = vh.subjectView
                val contentHolder = vh.contentView
                val timeStamp = vh.timeStamp
                val ratingHolder = vh.rating
                val successHolder = vh.successTV
                val emotionHolder = vh.emotionTV
                val timeCreatedHolder = vh.timeCreated
                val sourcesHolder = vh.sourcesTV
                val symptomHolder = vh.symptomsTV
                val collapseButton = vh.collapseButton

                //Assign values to variable references
                recordTitle.text = record.title
                ratingHolder.text = "Rating: " + record.rating.toString()
                contentHolder.text = record.content
                emotionHolder.text = "I felt: " + record.emotions
                sourcesHolder.text = "Sources behind this/My plans are : " + record.sources
                successHolder.text =
                    "This was a : ${if (record.successState!!) "Success" else "Fail"}"
                timeCreatedHolder.text =
                    "Time Created: " + DateFormat.getInstance().format(record.timeCreated)
                timeStamp.text =
                    "Last Updated: " + DateFormat.getInstance().format(record.timeUpdated)
                symptomHolder.text =
                    "ADHD Symptoms/Benefits: ${if (record.symptoms != "") record.symptoms else "Record Symptoms Here"}"
                collapseButton.setOnClickListener {
                    collapseItem(position)
                }

            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return recordList[position].recordState.ordinal
    }

    override fun getItemCount(): Int {
        return  recordList.size
    }
    fun collapseAll() {
        for (i in 0..recordList.size-1)
            if (recordList[i].recordState == Records.RecordState.EXPANDED)
                collapseItem(i)

    }
    fun expandAll(){
        for(i in 0..recordList.size-1)
    if(recordList[i].recordState==Records.RecordState.COLLAPSED)
        expandItem(i)
    }

    private fun collapseItem(position: Int){
        val item = recordList[position]
        val nextPosition = position+1
        when (item.recordState){
            Records.RecordState.COLLAPSED->
            {
                outerloop@ while (true){
                    if(nextPosition==recordList.size || recordList[nextPosition].recordState==Records.RecordState.COLLAPSED)
                    {break@outerloop}
                }
                expandItem(position)
            }
            Records.RecordState.EXPANDED->
            {item.recordState=Records.RecordState.COLLAPSED

                recordList[position]=item
                notifyDataSetChanged()
            }
        }

    }

    private fun expandItem(position : Int){
        val item =recordList[position]
        val nextPos = position+1
        when (item.recordState){
            Records.RecordState.COLLAPSED->{

                item.recordState=Records.RecordState.EXPANDED
                recordList[position] = item
notifyDataSetChanged()
            }
            Records.RecordState.EXPANDED->{
                collapseItem(position)
            }
        }

    }
    class CollapsedRecordsViewHolder(itemView: View,var onRecordListener: OnRecordListener):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var expandButton : ImageButton = itemView.findViewById(R.id.expand_collapse_button)
        var titleTV :TextView = itemView.findViewById(R.id.content_Title)
        var ratingTV : TextView = itemView.findViewById(R.id.ratingDisplay)
        var symptomsTV : TextView = itemView.findViewById(R.id.symptoms_text)
        var timeCreateTV : TextView = itemView.findViewById(R.id.time_created)
        var timeUpdatedTV : TextView = itemView.findViewById(R.id.timeStamp)
        override fun onClick(v: View?) {
            onRecordListener.onRecordClick(bindingAdapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }
    class ExpandedViewHolder(itemView: View, var onRecordListener: OnRecordListener):
            RecyclerView.ViewHolder(itemView),View.OnClickListener
    {
        var collapseButton : ImageButton = itemView.findViewById(R.id.expand_collapse_button)
        var subjectView: TextView = itemView.findViewById(R.id.content_Title)
var contentView : TextView=itemView.findViewById(R.id.content_text)
var rating : TextView = itemView.findViewById(R.id.ratingDisplay)
var timeStamp : TextView=itemView.findViewById(R.id.timeStamp)
var timeCreated : TextView = itemView.findViewById(R.id.time_created)
        var emotionTV : TextView = itemView.findViewById(R.id.emotions_text)
        var successTV : TextView = itemView.findViewById(R.id.success_state_label)
        var sourcesTV : TextView=itemView.findViewById(R.id.sources_text)
       var symptomsTV : TextView = itemView.findViewById(R.id.symptoms_text)
        override fun onClick(v: View?) {
onRecordListener.onRecordClick(bindingAdapterPosition)
        }
        init {
            itemView.setOnClickListener(this)
        }

    }
    init{notifyDataSetChanged()}


}