package com.activitylogger.release1.data

import java.util.*
import kotlin.collections.ArrayList

class EmotionData()  : Comparable<EmotionData>{

    var emotionCount : Int? =0
    var emotion : String = ""
constructor(emotionString:String,count :Int?):this(){
    this.emotion=emotionString
    emotionCount=count!!
}
fun getEmotions() :String
{    return emotion
}
override fun compareTo(emotion:EmotionData):Int{
    return this.emotionCount!!.compareTo(emotion.emotionCount!!)
}

    fun equals(other: EmotionData): Boolean {
        return this.emotion==other.emotion
    }



companion object
{
    var compareCounts = java.util.Comparator<EmotionData>{
        emotion1 , emotion2 ->
        emotion1.compareTo(emotion2)
    }
}
}
class EmotionList : ArrayList<EmotionData>()
{
    fun getEmotions() : ArrayList<String> {
        val emotionList = ArrayList<String>()
        for (i in 0..this.size - 1)
            emotionList.add(this[i].emotion)
        return emotionList
    }


   companion object {
       fun importData(emotionData: ArrayList<String>): EmotionList {
           val emotionList = EmotionList()
           val superList = emotionData.groupingBy { it.trimStart().trimEnd().lowercase() }.eachCount()
           val itemEmotions = superList.keys.toList()
           val itemCounts = superList.values.toList()
           for (i in 0..superList.size - 1) {
               emotionList.add(EmotionData(itemEmotions[i], itemCounts[i]))
           }
           Collections.sort(emotionList, EmotionData.compareCounts)
           return emotionList

       }
   }


    fun toStringCount(): String
    {
        return this.count().toString()
    }

}