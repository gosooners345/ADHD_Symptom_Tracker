package com.activitylogger.release1.customlayouthandlers

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.activitylogger.release1.R

@SuppressLint("NotifyDataSetChanged")
class ItemClassAdapter constructor(
    private val itemList: ItemClassList,
    private var OnItemSelected: OnItemSelected,
    private val symptomLists: Array<String>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        notifyDataSetChanged()

    }


    override fun getItemViewType(position: Int): Int {
        //  return super.getItemViewType(position)
        return when (position) {
            0, 8, 18, 26, 31 -> {
                0
            }
            else -> {
                1
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val context = parent!!.context
        val layoutInflater = LayoutInflater.from(context)
        val itemClassView = layoutInflater.inflate(R.layout.itemclasslayout, parent, false)
        val titleClassView = layoutInflater.inflate(R.layout.title_card_symptom_view, parent, false)
        return when (viewType) {
            0 -> {
                TitleViewHolder(titleClassView)
            }
            else -> {
                ViewHolder(itemClassView, OnItemSelected)
            }
        }
    }



    override fun getItemCount(): Int {
        return itemList.count()
    }



class ViewHolder(itemView: View, private var OnItemSelected: OnItemSelected): RecyclerView.ViewHolder(itemView),View.OnClickListener
{
    var itemCheckBox : CheckBox = itemView.findViewById(R.id.itemSelectedCB)

init {
    itemCheckBox.setOnClickListener(this)
}


    override fun onClick(v: View?) {

        OnItemSelected.onItemChecked(bindingAdapterPosition, this.itemCheckBox.isChecked)
        Log.i(
            "tag", itemCheckBox.text.toString() + "= ${itemCheckBox.isChecked}"
        )

    }

}

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleView = itemView.findViewById<TextView>(R.id.title_card_label)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val titleCard = holder as TitleViewHolder
                titleCard.titleView.text = symptomLists[position]

            }
            else -> {
                val itemHolder = holder as ViewHolder
                val item = itemList[position]
                val itemCB = itemHolder.itemCheckBox
                itemCB.text = item.item

                itemCB.isChecked = item.selected
            }
        }
    }
}