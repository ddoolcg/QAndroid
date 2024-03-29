package com.lcg.expressbus

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import com.lcg.comment.ListActivity
import com.lcg.expressbus.model.ListViewModelDemo
import com.lcg.mylibrary.BaseActivity
import com.lcg.mylibrary.fragment.newAlert
import com.lcg.mylibrary.utils.UIUtils

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val linearLayout = findViewById<LinearLayout>(R.id.ll)
        for (i in 0..100) {
            linearLayout.addView(Button(this).apply {
                text = "Button$i"
                setOnClickListener {
                    val bundle = Bundle().apply {
                        putString("id", "my id")
                    }
                    ListActivity.start(this@MainActivity, ListViewModelDemo::class.java, bundle)
                }
            })
        }
        UIUtils.post {
            (linearLayout.parent as? ScrollView)?.scrollTo(
                0,
                50 * UIUtils.dip2px(48f)
            )
        }
        //
        newAlert {
            title = "我是标题"
            message = "我是<font color=\"#FF8500\">内容</font>"
            setPositive("关闭") { dialog ->
                dialog.dismiss()
                UIUtils.showToastSafe("关闭对话框")
            }
        }.show(this)
    }
}
