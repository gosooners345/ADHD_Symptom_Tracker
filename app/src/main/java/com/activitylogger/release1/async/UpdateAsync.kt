package com.activitylogger.release1.async

import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDao

class UpdateAsync(private val recordDao : RecordsDao) : CoroutinesAsyncTask<Records?,Void?,Void?>("update") {
    override fun doInBackground(vararg params: Records?): Void? {
        recordDao.updateRecord(*params)
        return null
    }
}