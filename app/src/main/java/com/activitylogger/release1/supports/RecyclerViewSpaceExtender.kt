package com.activitylogger.release1.supports

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewSpaceExtender : RecyclerView.ItemDecoration {

    var space = 0
    val VERTICAL = 1

    constructor(spacing: Int?) {
        this.space = spacing!!
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        var position = parent.getChildViewHolder(view).bindingAdapterPosition
        var itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingforDirection(outRect, layoutManager!!, position, itemCount)


        //super.getItemOffsets(outRect, view, parent, state)
    }

    fun setSpacingforDirection(
        outRect: Rect,
        layoutMgr: RecyclerView.LayoutManager,
        position: Int,
        itemCt: Int
    ) {
        outRect.top = space
        outRect.bottom = space
/*        outRect.left = space - 2
        outRect.right = space - 2*/

    }

}