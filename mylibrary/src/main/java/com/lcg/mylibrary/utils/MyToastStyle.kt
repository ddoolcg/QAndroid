package com.lcg.mylibrary.utils

import android.view.Gravity
import com.hjq.toast.IToastStyle

/**
 * MyToastStyle
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/3/6 20:30
 */
object MyToastStyle : IToastStyle {
    override fun getZ(): Int = 0
    override fun getTextColor(): Int = -0x1
    override fun getBackgroundColor(): Int = -0x585859
    override fun getCornerRadius(): Int = 0
    override fun getMaxLines(): Int = 3
    override fun getPaddingLeft(): Int = 10
    override fun getPaddingRight(): Int = 10
    override fun getPaddingBottom(): Int = 5
    override fun getPaddingTop(): Int = 5
    override fun getGravity(): Int = Gravity.CENTER
    override fun getTextSize(): Float = 14f
    override fun getYOffset(): Int = 100
    override fun getXOffset(): Int = 0
}