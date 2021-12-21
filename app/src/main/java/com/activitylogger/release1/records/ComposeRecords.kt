package com.activitylogger.release1.records

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.activitylogger.release1.R
import kotlin.properties.Delegates

class ComposeRecords : AppCompatActivity(){

    var rating : Double =0.0
    lateinit var title : String
    lateinit var  content : String
var time_created by Delegates.notNull<Long>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.records_compose_layout)

    }

    companion object{}
}