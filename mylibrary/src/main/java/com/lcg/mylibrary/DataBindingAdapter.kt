package com.lcg.mylibrary

import android.databinding.BindingAdapter
import android.databinding.ViewDataBinding
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lcg.mylibrary.adapter.CommentAdapter
import com.lcg.mylibrary.utils.inflateBinding

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
    fun loadIcon(iv: ImageView, icon: String?) {
        Glide.with(iv.context).load(icon).error(android.R.drawable.ic_menu_gallery).into(iv)
    }

    /**为RecyclerView绑定适配器*/
    @BindingAdapter("adapter")
    @JvmStatic
    fun recyclerViewAdapter(rv: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        if (rv.layoutManager == null)
            rv.layoutManager = LinearLayoutManager(rv.context)
        rv.adapter = adapter
    }

    /**为RecyclerView绑定数据，如果BR设置不正确会导致items为空异常*/
    @BindingAdapter("items")
    @JvmStatic
    fun <T : CommentAdapter.Item> recyclerViewItems(rv: RecyclerView, items: ArrayList<T>) {
        if (rv.layoutManager == null)
            rv.layoutManager = LinearLayoutManager(rv.context)
        val adapter = CommentAdapter(items)
        rv.adapter = adapter
    }

    /**为LinearLayout以及子类绑定数据，如果BR设置不正确会导致items为空异常*/
    @BindingAdapter("items")
    @JvmStatic
    fun <T : CommentAdapter.Item> linearLayoutItems(ll: LinearLayout, items: ArrayList<T>) {
        ll.removeAllViews()
        val inflater = LayoutInflater.from(ll.context)
        try {
            items.forEach {
                val binding = inflater.inflateBinding<ViewDataBinding>(it.layoutId, ll, true)
                binding.setVariable(it.variableId, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}