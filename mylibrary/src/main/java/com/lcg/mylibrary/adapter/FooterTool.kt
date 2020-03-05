package com.lcg.mylibrary.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lcg.mylibrary.R

/**
 * 底部加载更多栏
 *
 * @author lei.chuguang Email:475825657@qq.com
 */
open class FooterTool(@LayoutRes private val layout: Int = R.layout.listview_footer, private val load: FooterTool.() -> Unit) {
    private var rootView: View? = null
    private var pb: View? = null
    private var tv: TextView? = null
    /**初始化*/
    internal open fun init(group: ViewGroup): RecyclerView.ViewHolder {
        rootView = LayoutInflater.from(group.context).inflate(layout, group, false)
        pb = rootView!!.findViewById(R.id.v_list_load)
        tv = rootView!!.findViewById(R.id.tv_list) as TextView
        setStatus(Status.ENABLE)
        rootView!!.setOnClickListener {
            setStatus(Status.LOADING)
            load(this)
        }
        return object : RecyclerView.ViewHolder(rootView!!) {}
    }

    /**
     * 设置底部状态

     * @param status 状态
     */
    open fun setStatus(status: Status) {
        when (status) {
            Status.ENABLE -> {
                tv?.apply {
                    paint.isUnderlineText = true // 下划线
                    paint.isAntiAlias = true // 抗锯齿
                    text = "点击加载更多"
                    pb?.visibility = View.GONE
                    rootView.isEnabled = true
                    rootView.visibility = View.VISIBLE
                }
            }
            Status.DISABLE -> {
                tv?.apply {
                    paint.isUnderlineText = false
                    paint.isAntiAlias = true // 抗锯齿
                    text = "已经到底了~"
                    pb?.visibility = View.GONE
                    rootView.isEnabled = false
                    rootView.visibility = View.VISIBLE
                }
            }
            Status.LOADING -> {
                tv?.apply {
                    paint.isUnderlineText = false
                    paint.isAntiAlias = true // 抗锯齿
                    text = "正在加载中···"
                    pb?.visibility = View.VISIBLE
                    rootView.isEnabled = false
                    rootView.visibility = View.VISIBLE
                }
            }
            else -> rootView?.visibility = View.GONE
        }
    }

    /**
     * FooterView状态枚举
     */
    enum class Status {
        /**隐藏底部*/
        NO,
        /**点击加载更多*/
        ENABLE,
        /**已经到底了~*/
        DISABLE,
        /**正在加载中···*/
        LOADING
    }
}