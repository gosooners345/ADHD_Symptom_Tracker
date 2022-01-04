package com.activitylogger.release1.data

import java.util.*
import kotlin.collections.ArrayList

class RecordsList : ArrayList<Records>() {

    var recordStats = ArrayList<Double>()
    var recordDates = ArrayList<Date>()
var recordIDs = ArrayList<Int>()
    var successCt = 0
    var failCt = 0
    var emotionList = ArrayList<String>()
var emotionDataList = EmotionList()

    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }

    fun setRecordData()
    {
        if(emotionList.size>0)
            emotionList.clear()
        for(record in this) {
            recordStats.add(record.rating)
            recordDates.add(record.timeCreated)
        recordIDs.add(record.id)
            if(record.successState == true )
                successCt++
            else
                failCt++
            emotionList.addAll(sanitizeSearchQuery(record.emotions).trim('*',' ','"','\t','\n','\r','\b').trimStart().split(",","and","|",":",";",".",))


        }

    }


    fun getEmotionCount(){
        if(emotionDataList.count()>0)
            emotionDataList.clear()
       emotionDataList = importEmotions(emotionList)

    }
    fun importEmotions( emotionDataList:ArrayList<String>)  : EmotionList
    {
val names = emotionDataList.groupingBy { it }.eachCount()

               val frequencyMap : MutableMap<String,Int> = HashMap()
               for (count in emotionDataList)
               {
                   var itemCt = frequencyMap[count]
                   if(itemCt==null) itemCt = 0
                   frequencyMap[count] = itemCt + 1


               }
        var emotionList = EmotionList()
       /* for(item in emotionDataList)
        {
            val emotionData =EmotionData()
            emotionData.emotion = item
emotionData.emotionCount=1
if(!!emotionList.contains(emotionData))
    emotionList.add(emotionData)
            else
{val index = emotionList.indexOf(emotionData)
if(index>-1)
    emotionList[index].emotionCount= emotionList[index].emotionCount!!+1
}
        }*/


        var superList = emotionDataList.groupingBy { it.trimStart().trimEnd() }.eachCount()
       var itememotions = superList.keys.toList()
var itemCounts = superList.values.toList()

//val freqList = frequencyMap.toList()
     //   val freqList = names.toList()
       /* for(i in 0..freqList.size-1)
        {
emotionList.add(EmotionData(freqList[i].first,freqList[i].second))
                            }*/
        for(i in 0..superList.size-1)
        {
            emotionList.add(EmotionData(itememotions[i],itemCounts[i]!!))
        }
Collections.sort(emotionList,EmotionData.compareCounts)
        return  emotionList

    }
}