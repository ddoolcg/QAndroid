package com.lcg.comment.model

import android.view.View
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.BaseObservableMe

/**
 * 可点击的  List Title Item
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2017/12/28 20:27
 */
open class ItemListTitle(activity: BaseActivity?) : BaseObservableMe(activity) {
    var primary: String = ""
    var secondary: String = ""
    var isCheck = true
    open fun click(v: View) {}
}