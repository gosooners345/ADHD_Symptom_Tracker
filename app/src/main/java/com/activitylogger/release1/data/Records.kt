package com.activitylogger.release1.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import java.text.DateFormat
import java.util.*

@Entity(tableName = "records",indices = [Index(value=["id"],unique=true)])
class Records() : Cloneable,Comparable<Records>,Parcelable
{
    @JvmField
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id")
    var id : Int =0
   @ColumnInfo(name="title")
    var title: String=""
    @ColumnInfo(name="content")
    var content : String=""
    @ColumnInfo(name="rating")
    var rating : Double=0.0
    @TypeConverters(DateConverter::class)
    @ColumnInfo(name = "time_created")
    var timeCreated = Date()

    @ColumnInfo(name = "time_updated")
    var timeUpdated: Long = 0

    @ColumnInfo(name = "success")
    var successState: Boolean? = null

    @ColumnInfo(name = "emotions")
    var emotions: String = ""

    @ColumnInfo(name = "sources", defaultValue = "")
    var sources: String = ""

    @ColumnInfo(name = "tags", defaultValue = "")
    var tags: String = ""

    @ColumnInfo(name = "symptoms", defaultValue = "")
    var symptoms = ""

    @Ignore
    lateinit var recordState: RecordState

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        title = parcel.readString()!!
        content = parcel.readString()!!
        rating = parcel.readDouble()
        timeUpdated = parcel.readLong()
        successState = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        timeCreated =parcel.readSerializable() as Date
        emotions = parcel.readString()!!
        sources = parcel.readString()!!
        symptoms = parcel.readString()!!
        tags = parcel.readString()!!
    }

    enum class RecordState {
        COLLAPSED,EXPANDED
    }


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
        symptoms: String, tags: String
    ) : this()
    {
        this.id = id!!
        this.title = title!!
        content = details!!
        this.emotions = emotions!!
        this.symptoms = symptoms
        this.rating = ratings
        this.timeUpdated = timeValue
        this.successState = success
        this.sources = sources!!
        this.timeCreated = dateValue
        recordState = RecordState.COLLAPSED
        this.tags = tags
    }
    
    constructor(timeCreatedValue: Date) : this()
    {
        this.title = ""
        this.content = ""
        this.sources = ""
        this.emotions = ""
        this.symptoms = ""
        this.rating = 0.0
        this.timeUpdated = System.currentTimeMillis()
        this.timeCreated = timeCreatedValue
        this.successState = false
        this.tags = ""
        this.recordState = RecordState.COLLAPSED
    }
    
    override fun compareTo(other: Records): Int
    {
        return this.id.compareTo(other.id)
    }
    
    override fun toString(): String
    {
        return String.format(
            "Entry title: $title \r\n" +
                    "Event: $content\r\n" +
                    "Rating: $rating\r\n" +
                    "Time Occurred: ${
                        DateFormat.getInstance().format(timeCreated)
                    }\r\n" +
                    "Emotions: $emotions \r\n " +
                    "Sources: $sources \r\n" +
                    "ADHD Symptoms or Benefits: $symptoms \r\n" +
                    "Tags: $tags\r\n" +
                    "Success or Fail: ${if (successState!!) "success" else "fail"}"
        )
    }
    
    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeDouble(rating)
        parcel.writeLong(timeUpdated)
        parcel.writeValue(successState)
        parcel.writeSerializable(timeCreated)
        parcel.writeString(emotions)
        parcel.writeString(sources)
        parcel.writeString(symptoms)
        parcel.writeString(tags)
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
            record1.timeCreated.compareTo(record2.timeCreated)
        }
        var compareUpdatedTimes = java.util.Comparator<Records> { record1, record2 ->

            if (record1.timeUpdated.compareTo(record2.timeUpdated) == 0)
                record1.compareTo(record2)
            else
                record1.timeUpdated.compareTo(record2.timeUpdated)
        }
        var compareRatings = java.util.Comparator<Records> { record1, record2 ->
            if (record1.rating.toInt().compareTo(record2.rating.toInt()) == 0)
                record1.compareTo(record2)
            else
                record1.rating.toInt().compareTo(record2.rating.toInt())
        }
        var compareSuccessStates = java.util.Comparator<Records> { record1, record2 ->
            if (record1.successState!! == record2.successState!!)
                record1.compareTo(record2)
            else
                (record1.successState!!.compareTo(record2.successState!!))

        }
        var compareAlphabetized = java.util.Comparator<Records> { record1, record2 ->
            if (record1.title.lowercase(Locale.getDefault()).compareTo(
                    record2.title.lowercase(
                        Locale.getDefault()
                    )
                ) == 0
            )
                record1.compareTo(record2)
            else
                record1.title.lowercase(Locale.getDefault()).compareTo(
                    record2.title.lowercase(
                        Locale.getDefault()
                    )
                )
        }

        var compareIds = java.util.Comparator<Records> { record1, record2 ->
            record1.compareTo(record2)
        }
    }

}





