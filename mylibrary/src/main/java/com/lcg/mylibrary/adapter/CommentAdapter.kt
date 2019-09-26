package com.lcg.mylibrary.adapter

import android.databinding.BaseObservable
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lcg.mylibrary.utils.inflateBinding

/**
 * 通用Adapter
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/01/16 13:57
 */
class CommentAdapter @JvmOverloads constructor(
    private val data: ArrayList<out BaseObservable>,
    private val mLayoutId: Int,
    private val mVariableId: Int,
    private var mFooterViewHolder: FooterViewHolder? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            mFooterViewHolder!!
        } else {
            val binding = LayoutInflater.from(parent.context)
                    .inflateBinding<ViewDataBinding>(mLayoutId, parent, false)
            ContentHolder(binding.root).apply {
                this.binding = binding
            }
        }
    }

    override fun getItemCount(): Int {
        var count = data.size
        if (mFooterViewHolder != null) {
            count += 1
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < data.size) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            (holder as ContentHolder).binding.setVariable(mVariableId, data[position])
        }
    }

    class ContentHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var binding: ViewDataBinding
    }
}
