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


    constructor(symptomList :ArrayList<String>):this()
    {
        for(i in 0..symptomList.size-1)
        this.add(Symptoms(symptomList[i],0))
    }
fun addSymptoms(symptomList :ArrayList<String>){
    for(i in 0..symptomList.size-1)
    this.add(Symptoms(symptomList[i],0))
}

    fun getSymptoms() : ArrayList<String>
    {
        var symptomList = ArrayList<String>()
for(i in 0..this.size-1)
    symptomList.add(this[i].symptom)
        return symptomList
    }
    }