package com.example.logan.scoutingappv2

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import java.lang.NullPointerException

//item decoration for recyclerview that creates a custom divider between each item (from drawable/line_divider)
class CustomDivider(/*private val verticalSpaceHeight: Int, */context: Context) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable = ContextCompat.getDrawable(context, R.drawable.line_divider) ?: throw NullPointerException()

    /*override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount - 1) {
            outRect.bottom = verticalSpaceHeight
        }
    }*/

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)

            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = child.bottom + params.bottomMargin // + verticalSpaceHeight / 2
            val bottom = top + mDivider.intrinsicHeight //- verticalSpaceHeight / 2

            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }
}