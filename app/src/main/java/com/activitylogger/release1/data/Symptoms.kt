@file:Suppress("LiftReturnOrAssignment", "unused", "ReplaceRangeToWithUntil",
    "ReplaceManualRangeWithIndicesCalls"
)

package com.activitylogger.release1.data

import com.activitylogger.release1.interfaces.Indexer
import java.lang.Exception

class Symptoms() : Indexer,Comparable<Symptoms> {
    var symptom: String = ""
    var count = 0

    constructor(symptomValue: String, countValue: Int) : this() {
        symptom = symptomValue
        count = countValue
    }
    companion object
    {
        var compareCounts = java.util.Comparator<Symptoms>{
            thing1,thing2->
            thing1.compareTo(thing2)
        }
    }

        override fun compareTo(other: Symptoms): Int {
return this.count.compareTo(other.count)
    }

    override fun get(propertyIndex: Int): Any {

       when(propertyIndex)
        {
            1 ->return  symptom
            2 ->return  count
            else -> throw Exception("Invalid Property Index")
        }
    }

    override fun get(propertyName: String): Any {
       when (propertyName.lowercase())
       {
           "symptom","symptoms" -> return symptom
           "count","counts","quantity"->return count
           else -> throw Exception("Invalid property name")
       }
    }

}
class SymptomList:ArrayList<Symptoms>() {

fun getSymptoms():ArrayList<String>{
    val symptomList = ArrayList<String>()
    for(i in 0..this.size-1)
        symptomList.add(this[i].symptom)
    return symptomList
}

companion object{
    fun importData(symptomData : ArrayList<String>):SymptomList{
        val symptomList = SymptomList()
        val sortedList = symptomData.groupingBy { it.trimStart().trimEnd().trim() }.eachCount()
        val itemSymptoms = sortedList.keys.toList()
        val itemCounts = sortedList.values.toList()
        for(i in 0..sortedList.size-1)
            if(itemSymptoms[i]!="")
            symptomList.add(Symptoms(itemSymptoms[i],itemCounts[i]))
        return symptomList
    }
}



    }