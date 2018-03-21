package com.xuebaedu.teacher.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.lcg.comment.R

/**
 * 底部加载更多的ViewHolder
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2017/10/31 20:07
 */
class FooterViewHolder(itemView: View, listener: FooterViewHolder.LoadListener) : RecyclerView.ViewHolder(itemView) {
    private var pb: View? = null
    private var tv: TextView? = null

    init {
        assignViews()
        setFooterStatus(FooterStatus.ENABLE)
        itemView.setOnClickListener {
            setFooterStatus(FooterStatus.LOADING)
            listener.loadMore()
        }
    }

    private fun assignViews() {
        pb = itemView.findViewById(R.id.v_list_load)
        tv = itemView.findViewById(R.id.tv_list) as TextView
    }

    /**
     * 设置底部状态

     * @param status 状态
     */
    internal fun setFooterStatus(status: FooterStatus) {
        when (status) {
            FooterStatus.ENABLE -> {
                tv?.apply {
                    paint.isUnderlineText = true // 下划线
                    paint.isAntiAlias = true// 抗锯齿
                    text = "点击加载更多"
                    pb?.visibility = View.GONE
                    itemView.isEnabled = true
                    itemView.visibility = View.VISIBLE
                }
            }
            FooterStatus.DISABLE -> {
                tv?.apply {
                    paint.isUnderlineText = false
                    paint.isAntiAlias = true// 抗锯齿
                    text = "已经到底了~"
                    pb?.visibility = View.GONE
                    itemView.isEnabled = false
                    itemView.visibility = View.VISIBLE
                }
            }
            FooterStatus.LOADING -> {
                tv?.apply {
                    paint.isUnderlineText = false
                    paint.isAntiAlias = true// 抗锯齿
                    text = "正在加载中···"
                    pb?.visibility = View.VISIBLE
                    itemView.isEnabled = false
                    itemView.visibility = View.VISIBLE
                }
            }
            else -> itemView.visibility = View.GONE
        }
    }

    /**
     * FooterView状态枚举
     */
    internal enum class FooterStatus {
        /**隐藏底部*/
        NO,
        /**点击加载更多*/
        ENABLE,
        /**已经到底了~*/
        DISABLE,
        /**正在加载中···*/
        LOADING
    }

    /**点击加载监听*/
    interface LoadListener {
        fun loadMore()
    }
}