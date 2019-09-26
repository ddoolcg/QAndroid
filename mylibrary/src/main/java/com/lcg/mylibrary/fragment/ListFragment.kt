package com.lcg.mylibrary.fragment

import android.databinding.BaseObservable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lcg.mylibrary.BaseFragment
import com.lcg.mylibrary.R
import com.lcg.mylibrary.adapter.CommentAdapter
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*

/**
 * 列表Fragment
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/9/4 15:57
 */
class ListFragment : BaseFragment() {
    private val origin = arrayListOf<BaseObservable>()
    private val items = arrayListOf<BaseObservable>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        view.rv.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        view.rv.adapter = CommentAdapter(items, arguments!!.getInt("layoutId"), arguments!!.getInt("variableId"))
        return view
    }

    /**过滤*/
    fun filter(string: String) {
        if (!TextUtils.isEmpty(string.trim())) {
            val filter1 = origin.filter {
                val s1 = it.toString()
                if (s1.contains(string)) {
                    true
                } else {
                    var filter = false
                    val split = string.split(" ")
                    for (s in split) {
                        if (!TextUtils.isEmpty(s)) {
                            if (s1.contains(s)) {
                                filter = true
                                break
                            }
                        }
                    }
                    filter
                }
            }
            items.clear()
            items.addAll(filter1)
        } else {
            items.clear()
            items.addAll(origin)
        }
        rv?.adapter?.notifyDataSetChanged()
    }

    /**装载数据，会清除掉历史数据*/
    fun loadData(data: List<BaseObservable>) {
        origin.clear()
        items.clear()
        addData(data)
    }

    /**添加数据*/
    fun addData(data: List<BaseObservable>) {
        items.addAll(data)
        origin.addAll(data)
        rv?.adapter?.notifyDataSetChanged()
    }

    /**移除一条数据*/
    fun removeItem(item: BaseObservable) {
        val indexOf = items.indexOf(item)
        items.remove(item)
        origin.remove(item)
        rv?.adapter?.notifyItemRemoved(indexOf)
    }

    companion object {
        fun newInstance(layoutId: Int, variableId: Int): ListFragment {
            return ListFragment().apply {
                arguments = Bundle().apply {
                    putInt("layoutId", layoutId)
                    putInt("variableId", variableId)
                }
            }
        }
    }
}