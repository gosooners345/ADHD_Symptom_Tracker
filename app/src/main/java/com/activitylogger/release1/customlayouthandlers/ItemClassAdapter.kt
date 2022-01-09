package com.activitylogger.release1.customlayouthandlers

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.activitylogger.release1.R
import com.activitylogger.release1.customlayouthandlers.ItemSelectorFragment.Companion.itemClassList

class ItemClassAdapter constructor(val onItemSelected: onItemSelected):RecyclerView.Adapter<ItemClassAdapter.ViewHolder>() {
    init {
        notifyDataSetChanged()

    }

val itemList = ItemSelectorFragment.itemClassList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val itemClassView = layoutInflater.inflate(R.layout.itemclasslayout, parent, false)
        return ViewHolder(itemClassView,onItemSelected)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        val itemCB = holder.itemCheckBox
        itemCB.text = item.item

        itemCB.isChecked = item.selected

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }



class ViewHolder(itemView: View,var onItemSelected: onItemSelected): RecyclerView.ViewHolder(itemView),View.OnClickListener
{
    var itemCheckBox : CheckBox = itemView.findViewById(R.id.itemSelectedCB)

init {


    itemCheckBox.setOnClickListener(this)
}


    override fun onClick(v: View?) {

       onItemSelected.onItemChecked(bindingAdapterPosition,this.itemCheckBox.isChecked)
        Log.i("tag",itemCheckBox.text.toString()+"= ${itemCheckBox.isChecked}")
    }

}
}