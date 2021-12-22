package com.activitylogger.release1.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat
import java.util.*

@Entity(tableName = "records")
data class Records(var timeCreatedValue:Long): Parcelable,Cloneable,Comparable<Records>
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
    @ColumnInfo(name="time_created")
    var timeCreated = if(timeCreatedValue==null) 0 else timeCreatedValue
    @ColumnInfo(name="time_updated")
    var timeUpdated =System.currentTimeMillis()
    @ColumnInfo(name="timestamp")
    var timeStamp: String =DateFormat.getInstance().format(timeUpdated)
@ColumnInfo(name="success")
var successState :Boolean? = null
    @ColumnInfo(name="emotions")
    var emotions : String = ""


    constructor(parcel: Parcel) : this(parcel.readLong()) {
       id=parcel.readInt()
        title = parcel.readString()!!
        content = parcel.readString()!!
        rating = parcel.readDouble()
        timeCreated = parcel.readLong()
        timeUpdated = parcel.readLong()
        timeStamp = parcel.readString().toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            successState = parcel.readBoolean()
        }
        else
            successState=null
emotions=parcel.readString()!!

    }

    fun update(titleContent : String, contentValue : String, ratingValue : Double,emotionState:String,success:Boolean) {
    this.timeUpdated=System.currentTimeMillis()
    timeStamp=DateFormat.getInstance().format(this.timeUpdated)
this.title=titleContent
    this.content=contentValue
    this.rating=ratingValue
        this.emotions=emotionState
        this.successState=success
}


    override fun writeToParcel(parcel: Parcel, flags: Int) {
       parcel.writeInt(id)
        parcel.writeLong(timeCreatedValue)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeDouble(rating)
        parcel.writeLong(timeCreated)
        parcel.writeLong(timeUpdated)
        parcel.writeString(timeStamp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(successState!!)
        }
        parcel.writeString(emotions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Records> {
        override fun createFromParcel(parcel: Parcel): Records {
            return Records(parcel)
        }

        override fun newArray(size: Int): Array<Records?> {
            return arrayOfNulls(size)
        }
    var compareCreatedTimes = java.util.Comparator<Records> { record1, record2 ->
        if(record1.timeCreated!! == record2.timeCreated!!)record1.compareTo(record2)
        else if(record1.timeCreated!! <= record2.timeCreated!!)-1
        else 1
    }
        var compareUpdatedTimes = java.util.Comparator<Records> { record1, record2 ->
            if(record1.timeUpdated!! == record2.timeUpdated!!)record1.compareTo(record2)
            else if(record1.timeUpdated!! <= record2.timeUpdated!!)-1
            else 1
        }
        var compareSuccessStates = java.util.Comparator<Records>{
            record1,record2->
            if(record1.successState!! == record2.successState!!)
                record1.compareTo(record2)
            else
                if ((record1.successState!! == true) && (record2.successState!! == false))
                    1
        else
            -1
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
                "Rating: $rating\r\n" +
                "Time Occurred: ${DateFormat.getInstance().format(timeCreated)}\r\n" +
                "Emotions: $emotions" +
                "Success or Fail: ${ if(successState!!)"success" else "fail"}"                 )
    }

}

