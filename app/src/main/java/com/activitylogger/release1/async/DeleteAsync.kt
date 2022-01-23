package com.activitylogger.release1.async

import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDao

class DeleteAsync(private val recordDao : RecordsDao) : CoroutinesAsyncTask<Records?,Void?,Void?>("delete") {
    override fun doInBackground(vararg params: Records?): Void? {
        recordDao.deleteRecord(*params)
        return null
    }
}