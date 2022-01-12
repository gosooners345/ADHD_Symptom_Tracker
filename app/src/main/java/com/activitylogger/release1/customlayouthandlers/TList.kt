package com.activitylogger.release1.customlayouthandlers

import com.activitylogger.release1.interfaces.Indexer
import java.lang.Exception
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList

open class T() : Any(), Comparable<T>,Indexer {
    var id = 0
    var item = ""
    var counts = 0

    constructor(id: Int, item: String, counts: Int) : this() {
        this.id = id
        this.item = item
        this.counts = counts
    }

    override fun get(propertyIndex: Int) :Any {
        var item: Any
        when(propertyIndex){
            1 -> item =this.id
            2-> item =this.item
            3-> item =  this.counts
            else -> throw Exception("Invalid Index")
        }
        return  item
    }

    override fun get(propertyName: String): Any {
        var item = Any()
        when(propertyName.lowercase())
        {
            "","id"->item = id
            "item" ->item=this.item
            "counts"->item=this.counts
        }
        return  item
    }

    override fun compareTo(other: T): Int {
        return this.id.compareTo(other.id)
    }


    companion object {
        var compareItems = java.util.Comparator<T> { t1, t2 ->
            if (t1.item.lowercase().compareTo(t2.item.lowercase()) == 0)
                t1.compareTo(t2)
            t1.item.lowercase().compareTo(t2.item.lowercase())
        }
        var compareCounts = java.util.Comparator<T> { t1, t2 ->
            if (t1.counts.compareTo(t2.counts) == 0)
                t1.compareTo(t2)
            t1.counts.compareTo(t2.counts)
        }
        var compareIds= java.util.Comparator<T>{t1,t2->
            t1.compareTo(t2)
        }
    }
}



class TList : ArrayList<T>(){

    fun getItems() : ArrayList<String>{
        val itemList = ArrayList<String>()
        for (i in 0..this.size-1)
            itemList.add(this[i][1] as String)
        return itemList
    }
    fun getCounts():ArrayList<Int>{
        val countList = ArrayList<Int>()
        for (i in 0..this.size-1)
            countList.add(this[i][2] as Int)
        return countList
    }
fun getIDs(): ArrayList<Int>{
    val idList = ArrayList<Int>()
    forEach {idList.add( it.id) }
return idList
}

    companion object
    {
        fun importData(sourceData : ArrayList<String>) : ArrayList<T>{
            val classList = ArrayList<T>()
            for (i in 0..sourceData.size-1)
                classList.add(T(i,sourceData[i],0))
            return classList
        }
        fun importGroupedData(sourceData : ArrayList<String>) : ArrayList<T>{
            val classList = ArrayList<T>()
            val groupedList = sourceData.groupingBy { it.trimStart().trimEnd() }.eachCount()
            val itemKeys = groupedList.keys.toList()
            val itemValues = groupedList.values.toList()
            for (i in 0..groupedList.size-1)
                classList.add(T(i,itemKeys[i],itemValues[i]))
            return classList
            }

        fun importSortedData(sourceData: ArrayList<String>,sortID :String):ArrayList<T>{
            val classList = ArrayList<T>()
            val sortedList = sourceData.groupingBy { it.trimStart().trimEnd() }.eachCount()
            val itemKeys = sortedList.keys.toList()
            val itemValues = sortedList.values.toList()
            for (i in 0..sortedList.size-1)
                classList.add(T(i,itemKeys[i],itemValues[i]))
            when(sortID.lowercase()) {
                "","id"->Collections.sort(classList, T.compareIds)
                "counts"->Collections.sort(classList,T.compareCounts)
                "items"->Collections.sort(classList,T.compareItems)
            }
            return classList
        }
    }
}