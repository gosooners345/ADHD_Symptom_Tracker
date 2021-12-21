package com.activitylogger.release1.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.interfaces.OnRecordListener

class RecordsAdapter constructor(private val recordList : ArrayList<Records>,private val onRecordListener: OnRecordListener,private  val context: Context) :RecyclerView.Adapter<RecordsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater=LayoutInflater.from(context)
        val recordView=inflater.inflate(R.layout.records_item_layout,parent,false)
return ViewHolder(recordView,onRecordListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
val record = recordList[position]
        val recordTitle = holder.subjectView
        recordTitle.text=record.title
        val contentHolder = holder.contentView
        var timeStamp = holder.timeStamp
        var ratingHolder = holder.rating
        ratingHolder.text=record.rating.toString()
        contentHolder.text=record.content
        timeStamp.text = record.timeStamp




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

        override fun onClick(v: View?) {
onRecordListener.onRecordClick(bindingAdapterPosition)
        }
        init {
            itemView.setOnClickListener(this)
        }

    }
    init{notifyDataSetChanged()}
}