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


    fun setRecordData()
    {
        for(record in this) {
            recordStats.add(record.rating)
            recordDates.add(record.timeCreated)
        recordIDs.add(record.id)
            if(record.successState == true )
                successCt++
            else
                failCt++
        }
    }


}