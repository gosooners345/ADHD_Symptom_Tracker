package com.activitylogger.release1.interfaces

interface Indexer  {
    operator fun get(propertyIndex : Int) : Any
    operator fun get(propertyName: String):Any
}