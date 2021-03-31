package com.lcg.mylibrary.adapter

import android.os.SystemClock
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lcg.mylibrary.R
import com.lcg.mylibrary.utils.UIUtils

/**
 * 底部加载更多栏
 *
 * @author lei.chuguang Email:475825657@qq.com
 */
interface Footer {
    /**可见时自动加载*/
    var autoLoading: Boolean

    /**初始化*/
    fun create(group: ViewGroup): RecyclerView.ViewHolder

    /**加载数据*/
    fun load()
}

open class FooterTool(@LayoutRes private val layout: Int = R.layout.listview_footer, private val load: FooterTool.() -> Unit) : Footer {
    protected var rootView: View? = null
    protected lateinit var pb: View
    protected lateinit var tv: TextView
    var status: Status = Status.ENABLE
        private set

    /**可见时自动加载*/
    override var autoLoading = true

    /**初始化*/
    override fun create(group: ViewGroup): RecyclerView.ViewHolder {
        rootView = LayoutInflater.from(group.context).inflate(layout, group, false).also {
            pb = it.findViewById(R.id.v_list_load)
            tv = it.findViewById(R.id.tv_list) as TextView
            it.setOnClickListener {
                load()
            }
        }
        setStatus(status)
        return object : RecyclerView.ViewHolder(rootView!!) {}
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
        this.status = status
        UIUtils.removeCallbacks(runnable)
        val time = 200 + lastRunnableTime - SystemClock.elapsedRealtime()
        if (time > 0) {
            UIUtils.postDelayed(time, runnable)
        } else {
            UIUtils.post(runnable)
        }
    }

    private var lastRunnableTime = 0L
    private val runnable by lazy {
        Runnable {
            lastRunnableTime = SystemClock.elapsedRealtime()
            if (rootView != null)
                when (status) {
                    Status.ENABLE -> {
                        tv.paint.isUnderlineText = true // 下划线
                        tv.paint.isAntiAlias = true // 抗锯齿
                        tv.text = "点击加载更多"
                        pb.visibility = View.GONE
                        rootView!!.isClickable = true
                    }
                    Status.DISABLE -> {
                        tv.paint.isUnderlineText = false
                        tv.paint.isAntiAlias = true // 抗锯齿
                        tv.text = "已经到底了~"
                        pb.visibility = View.GONE
                        rootView!!.isClickable = false
                    }
                    Status.LOADING -> {
                        tv.paint.isUnderlineText = false
                        tv.paint.isAntiAlias = true // 抗锯齿
                        tv.text = "正在加载中···"
                        pb.visibility = View.VISIBLE
                        rootView!!.isClickable = false
                    }
                    else -> {
                        tv.text = ""
                        pb.visibility = View.GONE
                        rootView!!.isClickable = false
                    }
                }
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