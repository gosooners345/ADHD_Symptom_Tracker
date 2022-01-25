package com.activitylogger.release1.customlayouthandlers

class ItemClassList : ArrayList<ItemClass>() {
    var selectedCount = 0
    var selectedItems = ArrayList<String>()

fun getSelectedItems(){
    this.selectedItems.clear()

    for(item in this)
        if(item.selected)
            this.selectedItems.add(item.item)
}

    override fun toString(): String {
        var itemListPrinted  = ""
        for(item in selectedItems)

        itemListPrinted+= String.format("$item,")
        return itemListPrinted
    }
}