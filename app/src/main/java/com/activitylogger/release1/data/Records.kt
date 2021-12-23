package com.activitylogger.release1.data

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.text.DateFormat
import java.util.*

@Entity(tableName = "records")
class Records(): Parcelable,Cloneable,Comparable<Records>
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
    var timeCreated :Long =0
    @ColumnInfo(name="time_updated")
    var timeUpdated :Long = 0
@ColumnInfo(name="success")
var successState :Boolean?=null
    @ColumnInfo(name="emotions")
    var emotions : String = ""
    @ColumnInfo(name="sources",defaultValue = "")
    var sources : String = ""


    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this() {
       id=parcel.readInt()
        title = parcel.readString()!!
        content = parcel.readString()!!
        emotions=parcel.readString().toString()
        sources=parcel.readString().toString()
        successState=parcel.readBoolean()
        rating = parcel.readDouble()
        timeCreated = parcel.readLong()
        timeUpdated = parcel.readLong()


    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
       parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(emotions)
        parcel.writeString(sources)
        parcel.writeDouble(rating)
        parcel.writeLong(timeCreated!!)
        parcel.writeLong(timeUpdated!!)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(successState!!)
        }

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Records> {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): Records {
            return Records(parcel)
        }

        override fun newArray(size: Int): Array<Records?> {
            return arrayOfNulls(size)
        }
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

