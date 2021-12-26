package com.activitylogger.release1.databasehelpers

import androidx.lifecycle.LiveData
import androidx.room.*
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.data.RecordsFTS

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

    //This is for the Full Text Search DB implementation
    @Query(""" SELECT * from records join recordsfts on records.title=recordsfts.title where recordsfts match :query""")
    suspend fun search(query : String) : List<Records>

}

