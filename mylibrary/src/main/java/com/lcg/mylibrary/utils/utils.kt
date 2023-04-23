package com.lcg.mylibrary.utils

import android.app.Activity
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*


/**
 * 杂项工具
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/1/16 10:07
 */
private val sdf by lazy { SimpleDateFormat() }
infix fun Date.format(format: String): String {
    return try {
        sdf.applyPattern(format)
        sdf.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
        "程序异常"
    }
}

fun Any.bean2map(): HashMap<String, String> {
    val map = hashMapOf<String, String>()
    val clazz = this::class.java
    val fields = clazz.declaredFields
    fields.forEach {
        try {
            it.isAccessible = true
            val obj = it.get(this)
            if (obj != null) {
                when (obj) {
                    is Array<*> -> {
                        map[it.name] = obj.joinToString(",") {
                            obj.toString()
                        }
                    }
                    is Iterable<*> -> {
                        map[it.name] = obj.joinToString(",") {
                            obj.toString()
                        }
                    }
                    else -> {
                        map[it.name] = obj.toString()
                    }
                }
            }
        } catch (_: Exception) {
        }
    }
    return map
}

/**just startActivityForResult code=clazz.unitNo.hashCode() and 0X0000ffff*/
fun <T : Activity> Activity.startActivityForResult(clazz: Class<T>) {
    startActivityForResult(Intent(this, clazz), clazz.getRequestCode())
}

/**just startActivityForResult code=clazz.unitNo.hashCode() and 0X0000ffff*/
fun <T : Activity> Class<T>.getRequestCode() = this.name.hashCode() and 0X0000ffff

/**线程池执行线程*/
fun thread(runnable: () -> Unit) {
    ThreadPoolUntil.getInstance().run(runnable)
}

/**UI线程*/
fun ui(runnable: () -> Unit) {
    UIUtils.runInMainThread(runnable)
}

