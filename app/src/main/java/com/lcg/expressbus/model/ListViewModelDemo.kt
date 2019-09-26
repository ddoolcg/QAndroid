package com.lcg.expressbus.model

import android.arch.lifecycle.LifecycleOwner
import com.lcg.comment.ListActivity
import com.lcg.expressbus.BR
import com.lcg.expressbus.R
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe
import com.lcg.mylibrary.adapter.CommentAdapter
import com.lcg.mylibrary.utils.L
import com.lcg.mylibrary.utils.UIUtils

/**
 * ListViewModel的demo
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/9/26 11:32
 */
class ListViewModelDemo(activity: BaseActivity) : ListActivity.ListViewModel(activity) {
    val items = arrayListOf<ItemDemo>()
    override var title = BaseObservableMe(activity).apply {
        titleText = "list test"
    }
    override var adapter = CommentAdapter(items, R.layout.item_demo, BR.item)

    override fun onCreate(owner: LifecycleOwner) {
        val id = bundle?.getString("id")
        L.i("id=$id")
        activity.showProgressDialog("加载中", null)
        UIUtils.postDelayed(1000) {
            activity.dismissProgressDialog(null)
            (0..10).forEach {
                items.add(ItemDemo(activity, "内容$it"))
            }
            adapter.notifyDataSetChanged()
        }
    }

    /**item*/
    class ItemDemo(activity: BaseActivity, val content: String) : BaseObservableMe(activity)
}