package com.activitylogger.release1.databasehelpers

import androidx.lifecycle.LiveData
import androidx.room.*
import com.activitylogger.release1.data.Records

@Dao
interface RecordsDao {

    @Insert
    fun insertRecord(vararg records: Records?): LongArray?

    @Query("Select * from records")
    fun getRecords(): LiveData<List<Records>>

    @Delete
    fun deleteRecord(vararg records : Records?) : Int

    @Update
    fun updateRecord(vararg record: Records?): Int

    @Query("Select * from records where records.id =:searchid")
    fun getRecord(searchid : Int):LiveData<Records?>

    @Query("Select * from records where records.rating=:ratings")
    fun getRecord(ratings:Double):LiveData<Records?>

}