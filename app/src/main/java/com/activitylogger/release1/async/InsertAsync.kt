package com.activitylogger.release1.async

import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDao

class InsertAsync(private val recordDao : RecordsDao) : CoroutinesAsyncTask<Records?, Void?, Void?>("insert") {

    override fun doInBackground(vararg params: Records?): Void? {
        recordDao.insertRecord(*params)
        return null
    }
}