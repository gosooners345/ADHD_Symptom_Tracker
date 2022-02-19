@file:Suppress("unused", "SpellCheckingInspection")

package com.activitylogger.release1.data

import java.util.*
import kotlin.collections.ArrayList
import com.activitylogger.release1.utils.StringUtils

class RecordsList : ArrayList<Records>()
{
    private var recordStats = ArrayList<Double>()
    private var recordDates = ArrayList<Date>()
    private var recordIDs = ArrayList<Int>()
    var successCt = 0
    var failCt = 0
    var emotionList = ArrayList<String>()
    var symptomList = ArrayList<String>()
    var dateRatingList = ArrayList<DatesandRatings>()
    private var recordStateList = ArrayList<Records.RecordState>()
    fun setRecordData()
    {
        if (emotionList.size > 0)
            emotionList.clear()
        if (symptomList.size > 0)
            symptomList.clear()
        for (record in this)
        {
            record.recordState = Records.RecordState.COLLAPSED
            recordStats.add(record.rating)
            recordDates.add(record.timeCreated)
            recordStateList.add(Records.RecordState.COLLAPSED)
            recordIDs.add(record.id)
            if (record.successState == true)
                successCt++
            else
                failCt++
            emotionList.addAll(
                StringUtils.sanitizeSearchQuery(record.emotions).trim(
                    '*',
                    ' ',
                    '"',
                    '\t',
                    '\n',
                    '\r',
                    '\b'
                ).trimStart().split(",", "and", "|", ":", ";", ".")
            )
            if (record.symptoms != "")
            {
                symptomList.addAll(record.symptoms.trim().split(","))
            }
            else
            {
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
