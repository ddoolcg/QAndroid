package com.lcg.mylibrary

import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.LayoutRes
import com.lcg.mylibrary.dialog.ProgressDialog
import com.lcg.mylibrary.model.AlertDialogObservable
import com.lcg.mylibrary.net.HttpManager
import com.lcg.mylibrary.net.HttpUrl
import com.lcg.mylibrary.utils.L
import com.lcg.mylibrary.utils.Token
import com.lcg.mylibrary.utils.UIUtils
import java.io.File
import java.util.Date

/**
 * 核心
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/10/24 17:40
 */
object QAndroid {
    /**初始化http认证*/
    @JvmOverloads
    fun initToken(
        headerKey: String = "Authorization",
        login: (showToast: Boolean) -> Unit
    ): QAndroid {
        Token.init(headerKey, login)
        return this
    }

    /**初始化UIUtils工具类*/
    fun initUIUtils(app: Application): QAndroid {
        UIUtils.init(app)
        return this
    }

    /**日志输出控制，通常使用BuildConfig.DEBUG*/
    fun setDebug(debug: Boolean): QAndroid {
        L.DEBUG = debug
        HttpManager.logcat = debug
        return this
    }

    /**沉浸式*/
    @JvmOverloads
    fun setTranslucentStatusTheme(translucent: Boolean, fontDark: Boolean = false): QAndroid {
        BaseActivity.translucentStatusTheme = translucent
        BaseActivity.statusFontDark = fontDark
        return this
    }

    /**
     * 设置进度对话框的layout布局
     *
     * @param layout ID for an XML layout resource to load (e.g.,
     * `R.layout.dialog_loading`)
     */
    fun setProgressLayout(
        @LayoutRes layout: Int,
        cancelable: Boolean = true,
        canceledOnTouchOutside: Boolean = true,
    ): QAndroid {
        ProgressDialogInterface.layout = layout
        ProgressDialogInterface.cancelable = cancelable
        ProgressDialogInterface.canceledOnTouchOutside = canceledOnTouchOutside
        return this
    }

    /**设置newAlert的AlertDialog样式*/
    @JvmOverloads
    fun setAlert(
        background: Int = -0x1, textColor: Int = -0x1000000,
        @LayoutRes layoutId: Int = R.layout.dialog_alert, variableId: Int = BR.dialog
    ): QAndroid {
        AlertDialogObservable.background = background
        AlertDialogObservable.textColor = textColor
        AlertDialogObservable.layoutId = layoutId
        AlertDialogObservable.variableId = variableId
        return this
    }

    /**设置奔溃信息收集服务器地址
     * @param url post请求， 请求表单：
     *
     * title：file.getName()
     *
     * app_name：app包名
     *
     *version_code：app的versionCode
     *
     * version_name：app的versionName
     *
     * content： 异常内容
     * @param intercept 异常拦截
     */
    @Synchronized
    fun setCrashURL(
        app: Application,
        url: String,
        intercept: (String, StringBuffer) -> Boolean = { fileName, sb ->
            if (File(app.filesDir.path, fileName).exists()) {
                true
            } else {
                val code = try {
                    val pi = app.packageManager.getPackageInfo(
                        app.packageName,
                        PackageManager.GET_ACTIVITIES
                    )
                    pi?.versionCode ?: -1
                } catch (e: Exception) {
                    -1
                }
                sb.append(
                    """------------------------------------------------------------------------------------------
                异常时间：${Date().toLocaleString()} 异常版本：$code
                DEVICE：os=${Build.VERSION.SDK_INT} d=${Build.MANUFACTURER} ${Build.MODEL} ${Build.DEVICE} cpu=${Build.CPU_ABI}${Build.CPU_ABI2}
                FINGERPRINT：${Build.FINGERPRINT}
            """.trimIndent()
                )
                false
            }
        }
    ): QAndroid {
        val crashHandler = CrashHandler.getInstance(app, url, intercept)
        Thread.setDefaultUncaughtExceptionHandler(crashHandler)
        crashHandler.sendPreviousReportsToServer()
        return this
    }

    /**设置联网请求统一的失败处理器*/
    fun setNetFailDefault(handler: ((code: Int, data: String?) -> Boolean)): QAndroid {
        HttpUrl.failDefault = handler
        return this
    }
}