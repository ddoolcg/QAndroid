package com.lcg.mylibrary.adapter

import android.databinding.BaseObservable
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.utils.inflateBinding

/**
 * 通用Adapter
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/01/16 13:57
 */
class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var data: ArrayList<out BaseObservable>
    private var mLayoutId: Int? = null
    private var mVariableId: Int? = null
    private var footer: FooterToolInterface? = null

    @JvmOverloads
    constructor(data: ArrayList<out Item>, footer: FooterToolInterface? = null) : super() {
        this.data = data
        this.footer = footer
    }

    @JvmOverloads
    constructor(data: ArrayList<out BaseObservable>,
                layoutId: Int,
                variableId: Int,
                footer: FooterToolInterface? = null) : super() {
        this.data = data
        this.mLayoutId = layoutId
        this.mVariableId = variableId
        this.footer = footer
    }

    /**更新列表*/
    fun update(data: ArrayList<out BaseObservable>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            footer!!.create(parent)
        } else {
            val layoutId = if (viewType == 0) mLayoutId!! else viewType
            val binding = LayoutInflater.from(parent.context)
                    .inflateBinding<ViewDataBinding>(layoutId, parent, false)
            ContentHolder(binding.root).apply {
                this.binding = binding
            }
        }
    }

    override fun getItemCount(): Int {
        var count = data.size
        if (footer != null) {
            count += 1
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < data.size) {
            val item = data[position]
            if (item is Item) {
                item.layoutId
            } else {
                0
            }
        } else {
            1 //LayoutRes生成的第一个字节为7F
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == 1) {
            footer!!.bind(itemCount)
        } else {
            val item = data[position]
            if (type == 0) {
                (holder as ContentHolder).binding.setVariable(mVariableId!!, item)
            } else {
                (holder as ContentHolder).binding.setVariable((item as Item).variableId, item)
            }
        }
    }

    class ContentHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ViewDataBinding
    }

    /**多变的item，适用list有多种layout的情况*/
    abstract class Item(activity: BaseActivity? = null) : BaseObservableMe(activity) {
        /**LayoutRes*/
        abstract val layoutId: Int

        /**BR id*/
        abstract val variableId: Int
    }
}
