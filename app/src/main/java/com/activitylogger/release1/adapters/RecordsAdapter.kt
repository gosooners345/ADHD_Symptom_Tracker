package com.activitylogger.release1.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.interfaces.OnRecordListener
import java.text.DateFormat
import kotlin.collections.ArrayList

class RecordDiffer : DiffUtil.ItemCallback<Records>(){
    override fun areContentsTheSame(oldItem: Records, newItem: Records): Boolean {
return oldItem.equals(newItem)

    }

    override fun areItemsTheSame(oldItem: Records, newItem: Records): Boolean {
return oldItem.timeCreated == newItem.timeCreated
    }
}



class RecordsAdapter(
    private val recordList: ArrayList<Records>,
    private val onRecordListener: OnRecordListener
) :RecyclerView.Adapter<RecordsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater=LayoutInflater.from(context)
        val recordView=inflater.inflate(R.layout.records_item_layout,parent,false)
return ViewHolder(recordView,onRecordListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
val record = recordList[position]
        val recordTitle = holder.subjectView
        val contentHolder = holder.contentView
        var timeStamp = holder.timeStamp
        var ratingHolder = holder.rating
        var successHolder = holder.successTV
        var emotionHolder = holder.emotionTV
        var timeCreatedHolder = holder.timeCreated
        var sourcesHolder = holder.sourcesTV
        var symptomHolder = holder.symptomsTV
        //Assign values to variable references
        recordTitle.text=record.title
        ratingHolder.text=record.rating.toString()
        contentHolder.text=record.content
    emotionHolder.text = "I felt: "+ record.emotions
        sourcesHolder.text = "Sources behind this/My plans are : "+record.sources
        successHolder.text="This was a : ${if(record.successState!!)"Success" else "Fail"}"
        timeCreatedHolder.text="Time Created: "+ DateFormat.getInstance().format(record.timeCreated)
        timeStamp.text ="Last Updated: "+ DateFormat.getInstance().format(record.timeUpdated)
        symptomHolder.text = "ADHD Symptoms/Benefits: ${if(record.symptoms!="")record.symptoms else "Record Symptoms Here"}"


    }

    override fun getItemCount(): Int {
        return  recordList.size
    }

    class ViewHolder(itemView: View, var onRecordListener: OnRecordListener):
            RecyclerView.ViewHolder(itemView),View.OnClickListener
    {
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