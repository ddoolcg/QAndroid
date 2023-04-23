package com.lcg.mylibrary

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
@BindingMethods(
    BindingMethod(
        type = TextView::class,
        attribute = "android:drawableTint",
        method = "setCompoundDrawableTintList"
    ),
    BindingMethod(
        type = View::class,
        attribute = "android:foregroundTint",
        method = "setForegroundTintList"
    ),
)
object DataBindingAdapter {

    @BindingAdapter("html")
    @JvmStatic
    fun parseHtml(tv: TextView, content: String?) {
        tv.text = if (content.isNullOrEmpty()) {
            ""
        } else {
            Html.fromHtml(content)
        }
    }

    @BindingAdapter("img")
    @JvmStatic
    fun loadIcon(iv: ImageView, icon: String?) {
        Glide.with(iv.context).load(icon).error(android.R.drawable.ic_menu_report_image).into(iv)
    }

    /**为RecyclerView绑定适配器*/
    @BindingAdapter("adapter")
    @JvmStatic
    fun recyclerViewAdapter(rv: RecyclerView, adapter: RecyclerView.Adapter<*>?) {
        if (rv.layoutManager == null)
            rv.layoutManager =
                LinearLayoutManager(rv.context)
        rv.adapter = adapter
    }

    /**为RecyclerView绑定数据，如果BR设置不正确会导致items为空异常*/
    @BindingAdapter("items")
    @JvmStatic
    fun <T : CommentAdapter.Item> recyclerViewItems(rv: RecyclerView, items: ArrayList<T>?) {
        if (rv.layoutManager == null)
            rv.layoutManager =
                LinearLayoutManager(rv.context)
        val adapter = rv.adapter
        val data = items ?: arrayListOf()
        if (adapter != null && adapter is CommentAdapter) {
            adapter.update(data)
        } else {
            rv.adapter = CommentAdapter(data)
        }
    }

    /**为LinearLayout以及子类绑定数据，如果BR设置不正确会导致items为空异常*/
    @BindingAdapter("items")
    @JvmStatic
    fun <T : CommentAdapter.Item> linearLayoutItems(ll: LinearLayout, items: ArrayList<T>?) {
        ll.removeAllViews()
        val inflater = LayoutInflater.from(ll.context)
        try {
            items?.forEach {
                val binding = inflater.inflateBinding<ViewDataBinding>(it.layoutId, ll, true)
                binding.setVariable(it.variableId, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}