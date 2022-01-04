package com.activitylogger.release1.data

class EmotionData  : Comparable<EmotionData>{

    var emotionCount : Int? =0
    var emotion : String = ""
constructor(emotionString:String,count :Int?){

    this.emotion=emotionString
    emotionCount=count!!

}
fun getEmotions() :String
{
    return emotion
}
override fun compareTo(emotion:EmotionData):Int{
    return this.emotionCount!!.compareTo(emotion.emotionCount!!)
}

    fun equals(other: EmotionData): Boolean {
        return this.emotion==other.emotion
    }


    constructor()
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
//var emotions =ArrayList<String>()
    fun getEmotions() : ArrayList<String>{
    val emotionList  = ArrayList<String>()
    for ( i in 0..this.size-1)
    emotionList.add(this[i].emotion)
return  emotionList
    }




    fun toStringCount(): String
    {
        return this.count().toString()
    }

}