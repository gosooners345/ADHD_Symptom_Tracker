package com.activitylogger.release1.anims

import android.view.View

class Animations {

    fun toggleExpansion(view: View, isExpanded : Boolean): Boolean{
        if(isExpanded){
view.animate().setDuration(200).rotation(180f)
            return true
        }
        else{
            view.animate().setDuration(200).rotation(0f)
            return false
        }
    }
    fun expand(view:View){

    }
    fun collapse(view:View){

    }
}