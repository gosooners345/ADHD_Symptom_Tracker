package com.activitylogger.release1.customlayouthandlers

interface onItemSelected {
    fun onItemChecked(position : Int,checkedState : Boolean)

}
interface onItemsSelected {
    fun onMultipleItemsChecked(positions : ArrayList<Int>,checkedState: Boolean)
}