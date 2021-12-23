package com.activitylogger.release1.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.interfaces.OnRecordListener
import org.w3c.dom.Text
import java.text.DateFormat

class RecordsAdapter constructor(private val recordList : ArrayList<Records>,private val onRecordListener: OnRecordListener,private  val context: Context) :RecyclerView.Adapter<RecordsAdapter.ViewHolder>(){

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
        //Assign values to variable references
        recordTitle.text=record.title
        ratingHolder.text=record.rating.toString()
        contentHolder.text=record.content
    emotionHolder.text = record.emotions
        sourcesHolder.text = record.sources
        successHolder.text="Success/Fail State: ${if(record.successState!!)"Success" else "Fail"}"
        timeCreatedHolder.text="Time Created: "+ DateFormat.getInstance().format(record.timeCreated)
        timeStamp.text ="Last Updated: "+ DateFormat.getInstance().format(record.timeUpdated)


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
        override fun onClick(v: View?) {
onRecordListener.onRecordClick(bindingAdapterPosition)
        }
        init {
            itemView.setOnClickListener(this)
        }

    }
    init{notifyDataSetChanged()}
}