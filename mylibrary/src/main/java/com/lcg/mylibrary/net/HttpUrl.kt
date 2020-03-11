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
    private var formMap: HashMap<String, String?>? = null
    private var body: String? = null
    protected var progress: ProgressDialogInterface? = null
    protected var msg: String? = null
    protected var fail: ((code: Int, data: String?) -> Boolean)? = null
    /**响应处理器*/
    protected open fun <T> responseHandler(observable: ((data: T) -> Unit)? = null, listener: OnSuccessListener<T>? = null): ResponseHandler {
        return object : BaseResponseHandler<T, String>() {
            override fun onStart(call: Call) {
                progress?.showProgressDialog(msg!!, call)
            }

            override fun onNetFinish() {
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
                observable?.invoke(data)
            }

            override fun getType(position: Int): Type {
                if (position == 0) {
                    if (observable != null) {
                        //lambda 接口方式实现
                        observable.javaClass.genericInterfaces.forEach {
                            if (it is ParameterizedType) {
                                val arguments = it.actualTypeArguments
                                if (arguments.size == 2) {
                                    return arguments[0]
                                }
                            }
                        }
                        //lambda直接使用Function1实现
                        observable.javaClass.declaredMethods.filter {
                            it.returnType.isAssignableFrom(Void.TYPE)
                                    || it.returnType == Unit::class.java
                        }.forEach { return it.parameterTypes[0] }
                        return String::class.java
                    } else {
                        val clazz = listener?.javaClass
                        val genericSuperclass = clazz?.genericSuperclass
                        val type = genericSuperclass as? ParameterizedType
                        val arguments = type?.actualTypeArguments
                        return arguments?.get(0) ?: String::class.java
                    }
                } else {
                    return super.getType(position)
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

    /**application/json 方式请求*/
    fun jsonBody(json: String?): HttpUrl {
        this.body = json
        this.formMap = null
        return this
    }

    /**接入进度对话框，用户关闭对话框会中断请求*/
    fun join(progressDialog: ProgressDialogInterface?, msg: String = "加载中..."): HttpUrl {
        this.progress = progressDialog
        this.msg = msg
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

    /** 不支持泛型套泛型的解析方式*/
    fun <T> get(observable: ((data: T) -> Unit)? = null) {
        HttpManager.getInstance().get(url, formMap, responseHandler(observable = observable))
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> post(observable: ((data: T) -> Unit)? = null) {
        when {
            formMap != null -> HttpManager.getInstance().post(url, formMap, responseHandler(observable = observable))
            body != null -> HttpManager.getInstance().post(url, body, responseHandler(observable = observable))
            else -> HttpManager.getInstance().post(url, hashMapOf(), responseHandler(observable = observable))
        }
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> delete(observable: ((data: T) -> Unit)? = null) {
        when {
            formMap != null -> HttpManager.getInstance().delete(url, formMap, responseHandler(observable = observable))
            body != null -> HttpManager.getInstance().delete(url, body, responseHandler(observable = observable))
            else -> HttpManager.getInstance().delete(url, responseHandler(observable = observable))
        }
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> put(observable: ((data: T) -> Unit)? = null) {
        when {
            formMap != null -> HttpManager.getInstance().put(url, formMap, responseHandler(observable = observable))
            body != null -> HttpManager.getInstance().put(url, body, responseHandler(observable = observable))
            else -> HttpManager.getInstance().put(url, hashMapOf(), responseHandler(observable = observable))
        }
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> get(listener: OnSuccessListener<T>? = null) {
        HttpManager.getInstance().get(url, formMap, responseHandler(listener = listener))
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> post(listener: OnSuccessListener<T>? = null) {
        when {
            formMap != null -> HttpManager.getInstance().post(url, formMap, responseHandler(listener = listener))
            body != null -> HttpManager.getInstance().post(url, body, responseHandler(listener = listener))
            else -> HttpManager.getInstance().post(url, hashMapOf(), responseHandler(listener = listener))
        }
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> delete(listener: OnSuccessListener<T>? = null) {
        when {
            formMap != null -> HttpManager.getInstance().delete(url, formMap, responseHandler(listener = listener))
            body != null -> HttpManager.getInstance().delete(url, body, responseHandler(listener = listener))
            else -> HttpManager.getInstance().delete(url, responseHandler(listener = listener))
        }
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> put(listener: OnSuccessListener<T>? = null) {
        when {
            formMap != null -> HttpManager.getInstance().put(url, formMap, responseHandler(listener = listener))
            body != null -> HttpManager.getInstance().put(url, body, responseHandler(listener = listener))
            else -> HttpManager.getInstance().put(url, hashMapOf(), responseHandler(listener = listener))
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
