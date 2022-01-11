package com.activitylogger.release1.data

class Symptoms() :Comparable<Symptoms> {
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

}
class SymptomList():ArrayList<Symptoms>() {

    val originalSymptomList =ArrayList<String>()

    constructor(symptomList :ArrayList<String>,resourceList : ArrayList<String>):this()
    {
        originalSymptomList.addAll(resourceList)
        for(i in 0..symptomList.size-1)
        this.add(Symptoms(symptomList[i],0))
    }
fun addSymptoms(symptomList :ArrayList<String>){
    for(i in 0..symptomList.size-1)
    this.add(Symptoms(symptomList[i],0))
}

    override fun toArray(): Array<Any> {
        var symptomLists = ArrayList<Symptoms>()
        for (items in this)
        {
            symptomLists.add(Symptoms(items.symptom,items.count))
        }

        return symptomLists.toArray()
    }

    }