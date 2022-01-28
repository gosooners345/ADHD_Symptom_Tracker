@file:Suppress("unused")

package com.activitylogger.release1.customlayouthandlers

class ItemClass() : Comparable<ItemClass>{
    var item : String =""
    var selected = false


    constructor(itemName:String, selectedState : Boolean) : this() {
        item = itemName
        selected=selectedState
    }


    override fun compareTo(other: ItemClass): Int {
return if(this.selected.compareTo(other.selected)==0) this.item.compareTo(other.item) else this.selected.compareTo(other.selected)
    }
    companion object{
        var compareItemNames = java.util.Comparator<ItemClass>{
           item1,item2-> item1.item.compareTo(item2.item)
        }

    }
}