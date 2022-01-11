package com.activitylogger.release1.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.activitylogger.release1.R
import com.faskn.lib.Slice
import com.faskn.lib.legend.LegendAdapter
import com.faskn.lib.legend.LegendItemViewHolder


class PieLegendAdapter: LegendAdapter() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendItemViewHolder {
     return PieLegendViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pielegend_itemlayout,parent,false))
    }
    class PieLegendViewHolder(view: View) : LegendItemViewHolder(view){
        var imageCircle :ImageView = view.findViewById(R.id.imageViewCircleIndicator)
        var textView : TextView = view.findViewById(R.id.textViewSliceTitle)

        override fun bind(slice: Slice) {
            this.boundItem = slice
            imageCircle.imageTintList =
                ColorStateList.valueOf(slice.color)
textView.text=slice.name
        }
    }
}