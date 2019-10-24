package com.lcg.mylibrary

import android.app.Application
import android.support.annotation.LayoutRes
import com.lcg.mylibrary.dialog.ProgressDialog
import com.lcg.mylibrary.net.DataEntry
import com.lcg.mylibrary.utils.L
import com.lcg.mylibrary.utils.Token
import com.lcg.mylibrary.utils.UIUtils

/**
 * 核心
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/10/24 17:40
 */
object QAndroid {
    /**初始化http认证*/
    fun initToken(headerKey: String = "token", login: (showToast: Boolean) -> Unit): QAndroid {
        Token.init(headerKey, login)
        return this
    }

    /**初始化UIUtils工具类*/
    fun initUIUtils(app: Application, completed: (isMain: Boolean) -> Unit): QAndroid {
        val isMain = UIUtils.init(app)
        completed.invoke(isMain)
        return this
    }

    /**日志输出控制，通常使用BuildConfig.DEBUG*/
    fun setDebug(debug: Boolean): QAndroid {
        L.DEBUG = debug
        return this
    }

    /**沉浸式*/
    fun setTranslucentStatusTheme(translucent: Boolean): QAndroid {
        BaseActivity.translucentStatusTheme = translucent
        return this
    }

    /**
     * 设置进度对话框的layout布局
     *
     * @param layout ID for an XML layout resource to load (e.g.,
     * `R.layout.dialog_loading`)
     */
    fun setProgressLayout(@LayoutRes layout: Int): QAndroid {
        ProgressDialog.setLayout(layout)
        return this
    }

    /**设置奔溃信息收集服务器地址*/
    fun setCrashURL(url: String): QAndroid {
        CrashHandler.URL_LOGS = url
        return this
    }

    /**设置联网请求统一的失败处理器*/
    fun setNetFailDefault(handler: ((code: Int, data: String?) -> Boolean)): QAndroid {
        DataEntry.failDefault = handler
        return this
    }
}