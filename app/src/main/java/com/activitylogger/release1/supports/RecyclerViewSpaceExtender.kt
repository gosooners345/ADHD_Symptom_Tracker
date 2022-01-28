package com.activitylogger.release1.supports

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("SpellCheckingInspection")
class RecyclerViewSpaceExtender(spacing: Int?) : RecyclerView.ItemDecoration() {

    private var space = 0

    init {
        this.space = spacing!!
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.getChildViewHolder(view).bindingAdapterPosition
        state.itemCount
        parent.layoutManager
        setSpacingforDirection(outRect)


        //super.getItemOffsets(outRect, view, parent, state)
    }

    private fun setSpacingforDirection(
        outRect: Rect
    ) {
        outRect.top = space
        outRect.bottom = space
/*        outRect.left = space - 2
        outRect.right = space - 2*/

    }

}