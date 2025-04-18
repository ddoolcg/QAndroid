package com.lcg.mylibrary

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.lcg.mylibrary.dialog.ProgressDialog
import okhttp3.Call

/**
 * 所有activity的基类
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2016/10/13 11:03
 */
open class BaseActivity : FragmentActivity(), ProgressDialogInterface {
    private var mProgressDialog: ProgressDialog? = null

    /**手机状态栏是否被设置过*/
    private var statusBySet = false
    var initStatusBarFontDark = statusFontDark
    override fun onCreate(savedInstanceState: Bundle?) {
        setTranslucentStatus(translucentStatusTheme)
        super.onCreate(savedInstanceState)
        activities.add(this)
        setStatusBarFontDark(initStatusBarFontDark)
    }

    override fun onDestroy() {
        dismissProgressDialog(null)
        activities.remove(this)
        super.onDestroy()
    }

    /**
     * 显示进度对话框
     *
     * @param msg  你想要显示的消息
     * @param call 网络请求的call
     */
    override fun showProgressDialog(
        msg: String,
        call: Call?,
        cancelable: Boolean,
        canceledOnTouchOutside: Boolean
    ) {
        if (mProgressDialog == null)
            mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.showProgressDialog(msg, call, cancelable, canceledOnTouchOutside)
    }

    /**
     * 关闭进度对话框
     *
     * @param msg 如果不为空，则只会关闭与之匹配的进度对话框。
     */
    override fun dismissProgressDialog(msg: String?) {
        if (mProgressDialog != null) {
            try {
                mProgressDialog!!.dismiss(msg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 去另外一个页面
     */
    fun startActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }

    fun back(v: View) {
        finish()
    }

    /**
     * 设置状态栏透明
     */
    protected fun setTranslucentStatus(translucent: Boolean) {
        if (!statusBySet) {
            statusBySet = true
            if (translucent) {
                // 5.0以上系统状态栏透明
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window = window
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.statusBarColor = Color.TRANSPARENT
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }
            }
        }
    }

    /**
     * 设置Android状态栏的字体颜色，状态栏为亮色的时候字体和图标是黑色，状态栏为暗色的时候字体和图标为白色
     *
     * @param dark 状态栏字体和图标是否为深色
     */
    protected fun setStatusBarFontDark(dark: Boolean) {
        // 小米MIUI
        try {
            val window = window
            val clazz = getWindow().javaClass
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            val darkModeFlag = field.getInt(layoutParams)
            val extraFlagField = clazz.getMethod(
                "setExtraFlags",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag)
            } else {       //清除黑色字体
                extraFlagField.invoke(window, 0, darkModeFlag)
            }
        } catch (_: Exception) {
        }

        // 魅族FlymeUI
        try {
            val window = window
            val lp = window.attributes
            val darkFlag =
                WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
            val meizuFlags = WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
            darkFlag.isAccessible = true
            meizuFlags.isAccessible = true
            val bit = darkFlag.getInt(null)
            var value = meizuFlags.getInt(lp)
            value = if (dark) {
                value or bit
            } else {
                value and bit.inv()
            }
            meizuFlags.setInt(lp, value)
            window.attributes = lp
        } catch (_: Exception) {
        }

        // android6.0+系统
        if (Build.VERSION.SDK_INT >= 23) {
            if (dark) {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or 0x00000010 //View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }

    companion object {
        @JvmStatic
        var activities = ArrayList<Activity>()

        /**全局透明状态控制*/
        @JvmStatic
        var translucentStatusTheme = false

        /**标题栏黑色字体*/
        @JvmStatic
        var statusFontDark = false
    }
}
