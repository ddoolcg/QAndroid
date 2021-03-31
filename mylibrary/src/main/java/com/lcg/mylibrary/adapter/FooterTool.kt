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
interface Footer {
    /**可见时自动加载*/
    var autoLoading: Boolean

    /**初始化*/
    fun init(group: ViewGroup): RecyclerView.ViewHolder

    /**加载数据*/
    fun load()
}

open class FooterTool(@LayoutRes private val layout: Int = R.layout.listview_footer, private val load: FooterTool.() -> Unit) : Footer {
    protected lateinit var rootView: View
    protected lateinit var pb: View
    protected lateinit var tv: TextView
    private var viewHolder: RecyclerView.ViewHolder? = null
    var status: Status = Status.ENABLE
        private set

    /**可见时自动加载*/
    override var autoLoading = true

    /**初始化*/
    override fun init(group: ViewGroup): RecyclerView.ViewHolder {
        if (viewHolder == null) {
            rootView = LayoutInflater.from(group.context).inflate(layout, group, false)
            pb = rootView.findViewById(R.id.v_list_load)
            tv = rootView.findViewById(R.id.tv_list) as TextView
            rootView.setOnClickListener {
                load()
            }
            viewHolder = object : RecyclerView.ViewHolder(rootView) {}
        }
        setStatus(Status.ENABLE)
        return viewHolder!!
    }

    /**加载数据*/
    override fun load() {
        if (status == Status.ENABLE) {
            setStatus(Status.LOADING)
            load(this)
        }
    }

    /**
     * 设置底部状态
     * @param status 状态
     */
    open fun setStatus(status: Status) {
        if (viewHolder == null) return
        this.status = status
        when (status) {
            Status.ENABLE -> {
                tv.paint.isUnderlineText = true // 下划线
                tv.paint.isAntiAlias = true // 抗锯齿
                tv.text = "点击加载更多"
                pb.visibility = View.GONE
                rootView.isClickable = true
                rootView.visibility = View.VISIBLE
            }
            Status.DISABLE -> {
                tv.paint.isUnderlineText = false
                tv.paint.isAntiAlias = true // 抗锯齿
                tv.text = "已经到底了~"
                pb.visibility = View.GONE
                rootView.isClickable = false
                rootView.visibility = View.VISIBLE
            }
            Status.LOADING -> {
                tv.paint.isUnderlineText = false
                tv.paint.isAntiAlias = true // 抗锯齿
                tv.text = "正在加载中···"
                pb.visibility = View.VISIBLE
                rootView.isClickable = false
                rootView.visibility = View.VISIBLE
            }
            else -> rootView.visibility = View.GONE
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