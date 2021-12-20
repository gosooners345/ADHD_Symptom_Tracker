package com.activitylogger.release1.async

import android.os.AsyncTask
import com.activitylogger.release1.data.Records
import com.activitylogger.release1.databasehelpers.RecordsDao

class UpdateAsync(private val recordDao : RecordsDao) : AsyncTask<Records?,Void?,Void?>() {
    override fun doInBackground(vararg params: Records?): Void? {
        recordDao.updateRecord(*params)
        return null
    }
}