package com.activitylogger.release1.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat

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
    var timeCreated =timeCreatedValue
    @ColumnInfo(name="time_updated")
    var timeUpdated =timeCreated
    @ColumnInfo(name="timestamp")
    var timeStamp: String =DateFormat.getInstance().format(timeUpdated)

    constructor(parcel: Parcel) : this(parcel.readLong()) {
       id=parcel.readInt()
        title = parcel.readString().toString()
        content = parcel.readString().toString()
        rating = parcel.readDouble()
        timeCreated = parcel.readLong()
        timeUpdated = parcel.readLong()
        timeStamp = parcel.readString().toString()
    }

    fun update(titleContent : String, contentValue : String, ratingValue : Double) {
    this.timeUpdated=System.currentTimeMillis()
    timeStamp=DateFormat.getInstance().format(this.timeUpdated)
this.title=titleContent
    this.content=contentValue
    this.rating=ratingValue
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
    }

    override fun compareTo(other: Records): Int {
     return this.id.compareTo(other.id)
    }

}

