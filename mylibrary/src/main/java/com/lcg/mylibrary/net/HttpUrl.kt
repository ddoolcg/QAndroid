package com.lcg.mylibrary.net

import com.lcg.mylibrary.ProgressDialogInterface
import okhttp3.Call
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * 新联网框架
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 2.0
 * @since 2019/12/26 10:28
 */
open class HttpUrl(private val url: String) {
    protected var formMap: HashMap<String, String?>? = null
    protected var body: String? = null
    protected var progress: ProgressDialogInterface? = null
    protected var msg: String? = null
    protected var progressDialogClose = true
    protected var fail: ((code: Int, data: String?) -> Boolean)? = null

    /**响应处理器*/
    protected open fun <T> responseHandler(listener: OnSuccessListener<T>? = null): ResponseHandler {
        return object : BaseResponseHandler<T, String>() {
            override fun onStart(call: Call) {
                progress?.showProgressDialog(msg!!, call)
            }

            override fun onNetFinish() {
                if (progressDialogClose)
                    progress?.dismissProgressDialog(msg!!)
            }

            override fun onFail(code: Int, data: String?) {
                var breakable = false
                if (fail != null)
                    breakable = fail!!.invoke(code, data)
                if (!breakable && failDefault != null)
                    breakable = failDefault!!.invoke(code, data)
                if (!breakable)
                    super.onFail(code, data)
            }

            override fun onSuccess(data: T) {
                listener?.onSuccess(data)
            }

            override fun getType(position: Int): Type {
                return if (position == 0) {
                    if (listener == null) {
                        String::class.java
                    } else {
                        val clazz = listener.javaClass
                        val genericSuperclass = clazz.genericSuperclass
                        val type = genericSuperclass as? ParameterizedType
                        val arguments = type?.actualTypeArguments
                        arguments?.get(0) ?: String::class.java
                    }
                } else {
                    super.getType(position)
                }
            }
        }
    }

    /**html表单方式请求*/
    fun formBody(formMap: HashMap<String, String?>?): HttpUrl {
        this.body = null
        this.formMap = formMap
        return this
    }

    /**html表单方式请求*/
    fun formBody(vararg formMap: Pair<String, String?>): HttpUrl {
        this.body = null
        this.formMap = formMap.toMap(hashMapOf())
        return this
    }

    /**application/json 方式请求*/
    fun jsonBody(json: String?): HttpUrl {
        this.body = json
        this.formMap = null
        return this
    }

    /**接入进度对话框，用户关闭对话框会中断请求*/
    fun join(
        progressDialog: ProgressDialogInterface?,
        msg: String = "加载中...",
        autoClose: Boolean = true
    ): HttpUrl {
        this.progress = progressDialog
        this.msg = msg
        this.progressDialogClose = autoClose
        return this
    }

    /**捕获服务器的失败
     * @param observable observable返回值表示是否中断。<br/>
     * 生命周期为failDefault->observable->框架默认实现
     */
    fun catchFail(observable: ((code: Int, data: String?) -> Boolean)?): HttpUrl {
        fail = observable
        return this
    }

    inline fun <reified T> get(crossinline observable: (data: T) -> Unit): Call {
        return get(object : OnSuccessListener<T>() {
            override fun onSuccess(data: T) {
                observable(data)
            }
        })
    }

    inline fun <reified T> post(crossinline observable: (data: T) -> Unit): Call {
        return post(object : OnSuccessListener<T>() {
            override fun onSuccess(data: T) {
                observable(data)
            }
        })
    }

    inline fun <reified T> delete(crossinline observable: (data: T) -> Unit): Call {
        return delete(object : OnSuccessListener<T>() {
            override fun onSuccess(data: T) {
                observable(data)
            }
        })
    }

    inline fun <reified T> put(crossinline observable: (data: T) -> Unit): Call {
        return put(object : OnSuccessListener<T>() {
            override fun onSuccess(data: T) {
                observable(data)
            }
        })
    }

    /**建议使用lambda[get]替代*/
    @Deprecated("不建议使用")
    fun <T> get(listener: OnSuccessListener<T>? = null): Call {
        return HttpManager.getInstance().get(url, formMap, responseHandler(listener = listener))
    }

    /**建议使用lambda[post]替代*/
    @Deprecated("不建议使用")
    fun <T> post(listener: OnSuccessListener<T>? = null): Call {
        return when {
            formMap != null -> HttpManager.getInstance()
                .post(url, formMap, responseHandler(listener = listener))
            body != null -> HttpManager.getInstance()
                .post(url, body, responseHandler(listener = listener))
            else -> HttpManager.getInstance()
                .post(url, hashMapOf(), responseHandler(listener = listener))
        }
    }

    /**建议使用lambda[delete]替代*/
    @Deprecated("不建议使用")
    fun <T> delete(listener: OnSuccessListener<T>? = null): Call {
        return when {
            formMap != null -> HttpManager.getInstance()
                .delete(url, formMap, responseHandler(listener = listener))
            body != null -> HttpManager.getInstance()
                .delete(url, body, responseHandler(listener = listener))
            else -> HttpManager.getInstance().delete(url, responseHandler(listener = listener))
        }
    }

    /**建议使用lambda[put]替代*/
    @Deprecated("不建议使用")
    fun <T> put(listener: OnSuccessListener<T>? = null): Call {
        return when {
            formMap != null -> HttpManager.getInstance()
                .put(url, formMap, responseHandler(listener = listener))
            body != null -> HttpManager.getInstance()
                .put(url, body, responseHandler(listener = listener))
            else -> HttpManager.getInstance()
                .put(url, hashMapOf(), responseHandler(listener = listener))
        }
    }

    companion object {
        /**
         * 默认统一处理接口调用失败，函数返回值表示是否中断。<br/>
         * 生命周期为failDefault->catchFail->框架默认实现
         */
        @JvmStatic
        var failDefault: ((code: Int, data: String?) -> Boolean)? = null
    }
}

/**数据成功监听，支持泛型套泛型*/
abstract class OnSuccessListener<T> {
    abstract fun onSuccess(data: T)
}

/**服务器时间*/
val serverDate: Date
    get() = HttpManager.getInstance().serverDate