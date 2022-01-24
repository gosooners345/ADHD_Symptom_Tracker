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
    var symptomList = ArrayList<String>()
var dateRatingList = ArrayList<DatesandRatings>()
    var recordStateList = ArrayList<Records.RecordState>()




    private fun sanitizeSearchQuery(query: String?): String {
        if (query == null) {
            return "";
        }
        val queryWithEscapedQuotes = query.replace(Regex.fromLiteral("\""), "\"\"")
        return "*\"$queryWithEscapedQuotes\"*"
    }

    fun sortDatesAndRatings(){
        var date = Date()
        var ratingsList=ArrayList<Double>()
        date = this.recordDates[0]
        for(i in 0..this.size-1)
        {
            if(date.month ==recordDates[i].month && date.day==recordDates[i].day)
                    ratingsList.add(recordStats[i])
            else
                {
                    val dateItem = DatesandRatings(date,ratingsList)
                    date=this.recordDates[i]
                    ratingsList.clear()
                    dateRatingList.add(dateItem)
                }

        }
    }

    fun setRecordData(){
        if(emotionList.size>0)
            emotionList.clear()
        if(symptomList.size>0)
            symptomList.clear()
        for(record in this) {
            record.recordState=Records.RecordState.COLLAPSED
            recordStats.add(record.rating)
            recordDates.add(record.timeCreated)
            recordStateList.add(Records.RecordState.COLLAPSED)
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
                symptomList.add("")
            }

        }

    }

}

class DatesandRatings(var date : Date,var rating:ArrayList<Double>)
{
    companion object{
        var compareDates = java.util.Comparator<DatesandRatings>{
            r1,r2 -> r1.date.compareTo(r2.date)
        }
    }
}
