package com.activitylogger.release1.customlayouthandlers

class ItemClassList : ArrayList<ItemClass>() {
    var selectedCount = 0
    var selectedItems = ArrayList<String>()

    override fun toString(): String {
        var itemListPrinted  = ""
        for(item in selectedItems)
        itemListPrinted+= String.format("$item,")
        return itemListPrinted
    }
}