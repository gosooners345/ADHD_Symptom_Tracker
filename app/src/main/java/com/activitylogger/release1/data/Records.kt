package com.activitylogger.release1.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.*
import org.jetbrains.annotations.NotNull
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.Date.parse
import java.util.logging.Level.parse

@Entity(tableName = "records")
class Records: Cloneable,Comparable<Records>
{
    @JvmField
    @PrimaryKey(autoGenerate = true)
    var id : Int =0
   @ColumnInfo(name="title")
    var title: String=""
    @ColumnInfo(name="content")
    var content : String=""
    @ColumnInfo(name="rating")
    var rating : Double=0.0
    @TypeConverters(DateConverter::class)
    @ColumnInfo(name="time_created")
    var timeCreated =Date()
    @ColumnInfo(name="time_updated")
    var timeUpdated :Long = 0
@ColumnInfo(name="success")
var successState :Boolean?=null
    @ColumnInfo(name="emotions")
    var emotions : String = ""
    @ColumnInfo(name="sources",defaultValue = "")
    var sources : String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(
        title: String?,
        id: Int?,
        dateValue: Date,
        emotions: String?,
        details: String?,
        ratings: Double,
        timeValue: Long,
        success: Boolean,
        sources: String?,

    ){
        this.id=id!!
        this.title=title!!
content=details!!
this.emotions=emotions!!
this.rating=ratings
        this.timeUpdated=timeValue
        this.successState=success!!
        this.sources=sources!!
        this.timeCreated=dateValue

    }
constructor(timeCreatedValue:Date){
    this.title=""
    this.content=""
    this.sources=""
    this.emotions=""
    this.rating=0.0
    this.timeUpdated=System.currentTimeMillis()
       this.timeCreated = timeCreatedValue
    this.successState=false


}
constructor()


    companion object  {


    var compareCreatedTimes = java.util.Comparator<Records> { record1, record2 ->
        record1.compareTo(record2)
    }
        var compareUpdatedTimes = java.util.Comparator<Records> { record1, record2 ->

            if (record1.timeUpdated.compareTo(record2.timeUpdated) ==0)
                record1.compareTo(record2)
            else
                record1.timeUpdated.compareTo(record2.timeUpdated)
        }
        var compareRatings = java.util.Comparator<Records>{
            record1, record2 -> if( record1.rating.toInt().compareTo(record2.rating.toInt())==0)
                record1.compareTo(record2)
            else
            record1.rating.toInt().compareTo(record2.rating.toInt())
        }
        var compareSuccessStates = java.util.Comparator<Records>{
            record1,record2->
           if(record1.successState!!.equals(record2.successState!!))
               record1.compareTo(record2)
            else
                (record1.successState!!.compareTo(record2.successState!!))

        }
        var compareAlphabetized = java.util.Comparator<Records>{
            record1, record2 ->
            if(record1.title.lowercase(Locale.getDefault()).compareTo(record2.title.lowercase(
                    Locale.getDefault()))==0)
                record1.compareTo(record2)
            else
                record1.title.lowercase(Locale.getDefault()).compareTo(record2.title.lowercase(
                    Locale.getDefault()))
        }

     var compareIds = java.util.Comparator<Records>{
         record1, record2 ->
         record1.compareTo(record2)
     }



    }

    override fun compareTo(other: Records): Int {
     return this.id.compareTo(other.id)
    }

    override fun toString(): String {
        return String.format("Event title: $title,\r\n" +
                "Event: $content\r\n" +
                "Rating: ${rating.toString()}\r\n" +
                "Time Occurred: ${DateFormat.getInstance().format(timeCreated)}\r\n" +
                "Emotions: $emotions \r\n " +
                "Sources: $sources \r\n"+
                "Success or Fail: ${ if(successState!!)"success" else "fail"}"                 )
    }

}

@Fts4(contentEntity = Records::class)
@Entity(tableName="recordsfts")
data class RecordsFTS(
    @ColumnInfo(name="title")
    var title: String,
    @ColumnInfo(name="content")
    var content: String,
    @ColumnInfo(name="emotions")
    var emotions : String,
    @ColumnInfo(name="sources")
    var sources: String)
