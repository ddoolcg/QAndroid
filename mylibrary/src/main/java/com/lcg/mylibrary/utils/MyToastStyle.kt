package com.lcg.mylibrary.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import com.hjq.toast.style.BlackToastStyle

/**
 * MyToastStyle
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/3/6 20:30
 */
object MyToastStyle : BlackToastStyle() {
    override fun getTextColor(context: Context?): Int {
        return -0x1
    }

    override fun getTextSize(context: Context?): Float {
        return 14f
    }

    override fun getHorizontalPadding(context: Context?): Int {
        return 10
    }

    override fun getVerticalPadding(context: Context?): Int {
        return 5
    }

    override fun getBackgroundDrawable(context: Context?): Drawable {
        val drawable = GradientDrawable()
        drawable.setColor(-0x585859)
        drawable.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context!!.resources.displayMetrics)
        return drawable
    }
}