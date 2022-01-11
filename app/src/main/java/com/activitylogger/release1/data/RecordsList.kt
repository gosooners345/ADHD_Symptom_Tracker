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
    var symptomDataList = SymptomList()
var symptomCt = 0
    var symptomCtList = ArrayList<Int>()
    var symptomList = ArrayList<String>(    )
    var symptomLabels = ArrayList<String>()

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
            if (record.successState == true)
                successCt++
            else
                failCt++
            emotionList.addAll(
                sanitizeSearchQuery(record.emotions).trim(
                    '*',
                    ' ',
                    '"',
                    '\t',
                    '\n',
                    '\r',
                    '\b'
                ).trimStart().split(",", "and", "|", ":", ";", ".",)
            )
            if (record.symptoms != "") {
                symptomList.addAll(record.symptoms.trim().split(","))
            } else {
                symptomList.add("Impulsiveness")
            }

        }

    }


    fun getEmotionCount(){
        if(emotionDataList.count()>0)
            emotionDataList.clear()
       emotionDataList = importEmotions(emotionList)


    }
    fun importEmotions( emotionDataList:ArrayList<String>)  : EmotionList {
        emotionDataList.groupingBy { it }.eachCount()

        val frequencyMap: MutableMap<String, Int> = HashMap()
        for (count in emotionDataList) {
            var itemCt = frequencyMap[count]
            if (itemCt == null) itemCt = 0
            frequencyMap[count] = itemCt + 1
        }
        var emotionList = EmotionList()
        var superList = emotionDataList.groupingBy { it.trimStart().trimEnd() }.eachCount()
        var itememotions = superList.keys.toList()
        var itemCounts = superList.values.toList()
        for (i in 0..superList.size - 1) {
            emotionList.add(EmotionData(itememotions[i], itemCounts[i]!!))
        }
        Collections.sort(emotionList, EmotionData.compareCounts)
        return emotionList

    }
    fun getSymptomCount(){
        if (symptomDataList.count()>0)
            symptomDataList.clear()
        symptomDataList = importSymptoms(symptomList)
        for (i in 0..symptomDataList.size-1)
            symptomCt +=symptomDataList[i].count

    }
    fun importSymptoms(symptomData : ArrayList<String>) : SymptomList{
        symptomData.groupingBy { it }.eachCount()
        val frequencyMap :MutableMap<String,Int> = HashMap()
        for(count in symptomData){
            var itemCt = frequencyMap[count]
            if(itemCt == null) itemCt=0
            frequencyMap[count]=itemCt+1
        }
        val symptomsList = SymptomList()
        var superList = symptomData.groupingBy { it.trim() }.eachCount()
var symptomItems = superList.keys.toList()
var symptomCounts = superList.values.toList()
for (i in 0..superList.size-1)
{
    symptomsList.add(Symptoms(symptomItems[i],symptomCounts[i]))


}
    symptomCtList.addAll(symptomCounts)
        symptomLabels.addAll(symptomItems)
        return symptomsList
    }
}