package com.activitylogger.release1.settings

import android.content.Context
import androidx.datastore.preferences.*
import androidx.preference.PreferenceDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


class AppSettingsStorage : PreferenceDataStore() {

    override fun putString(key: String?, value: String?) {
        super.putString(key, value)
    }

    override fun getString(key: String?, defValue: String?): String? {
        return super.getString(key, defValue)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return super.getBoolean(key, defValue)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        super.putBoolean(key, value)
    }


}