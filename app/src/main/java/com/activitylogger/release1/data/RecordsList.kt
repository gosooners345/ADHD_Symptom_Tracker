@file:Suppress("SpellCheckingInspection")

package com.activitylogger.release1.data

import com.activitylogger.release1.utils.StringUtils
import java.util.*

class RecordsList : ArrayList<Records>()
{
    private var recordStats = ArrayList<Double>()
    private var recordDates = ArrayList<Date>()
    private var recordIDs = ArrayList<Int>()
    var successCt = 0
    var failCt = 0
    
    //  var avgRatingStat =0.0
    var emotionList = ArrayList<String>()
    var symptomList = ArrayList<String>()
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
//            avgRatingStat +=record.rating
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

