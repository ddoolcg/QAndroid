package com.xuebaedu.teacher

import android.databinding.BindingAdapter
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

/**
 * DataBindingAdapter
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2017/7/14 19:29
 */
object DataBindingAdapter {

    @BindingAdapter("html")
    @JvmStatic
    fun parseHtml(tv: TextView, content: String) {
        tv.text = Html.fromHtml(content)
    }

    @BindingAdapter("img")
    @JvmStatic
    fun loadIcon(iv: ImageView, icon: String) {
        Glide.with(iv.context).load(icon).error(android.R.drawable.ic_menu_gallery).into(iv)
    }
}