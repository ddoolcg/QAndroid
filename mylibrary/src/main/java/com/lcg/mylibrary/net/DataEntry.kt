package com.lcg.mylibrary.net

import com.lcg.mylibrary.BaseActivity
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*

/**
 * 新联网框架
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2017/8/23 14:43
 */
open class DataEntry(private val url: String) {
    private var formMap: HashMap<String, String>? = null
    private var body: String? = null
    protected var baseActivity: BaseActivity? = null
    protected var msg: String? = null
    protected var finish2Close = true
    protected var fail: ((code: Int, data: String?) -> Unit)? = null
    protected open fun <T> baseDataHandler(observable: ((data: T) -> Unit)? = null, listener: OnSuccessListener<T>? = null): DataHandler {
        return object : BaseDataHandler<T, String>() {
            override fun onStart() {
                baseActivity?.showProgressDialog(msg!!, null)
            }

            override fun onNetFinish() {
                if (finish2Close)
                    baseActivity?.dismissProgressDialog(msg!!)
            }

            override fun onFail(code: Int, data: String?) {
                when {
                    failDefault != null -> failDefault!!.invoke(code, data)
                    fail != null -> fail!!.invoke(code, data)
                    else -> super.onFail(code, data)
                }
            }

            override fun onSuccess(code: Int, data: T) {
                listener?.onSuccess(data)
                observable?.invoke(data)
            }

            override fun getType(position: Int): Type {
                if (position == 0) {
                    if (observable != null) {
                        observable.javaClass.declaredMethods?.filter {
                            it.returnType.isAssignableFrom(Void.TYPE)
                                    || it.returnType == Unit::class.java
                        }?.forEach { return it.parameterTypes[0] }
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
    fun formBody(formMap: HashMap<String, String>): DataEntry {
        this.body = null
        this.formMap = formMap
        return this
    }

    /**application/json 方式请求*/
    fun jsonBody(json: String): DataEntry {
        this.body = json
        this.formMap = null
        return this
    }

    /**接入进度对话框*/
    fun joinProgressDialog(baseActivity: BaseActivity, msg: String = "加载中...", finish2Close: Boolean = true): DataEntry {
        this.baseActivity = baseActivity
        this.msg = msg
        this.finish2Close = finish2Close
        return this
    }

    /**捕获服务器的失败*/
    fun catchFail(observable: ((code: Int, data: String?) -> Unit)?): DataEntry {
        fail = observable
        return this
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> get(observable: ((data: T) -> Unit)? = null) {
        HttpManager.getInstance().get(url, formMap, baseDataHandler(observable = observable))
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> post(observable: ((data: T) -> Unit)? = null) {
        when {
            formMap != null -> HttpManager.getInstance().post(url, formMap, baseDataHandler(observable = observable))
            body != null -> HttpManager.getInstance().post(url, body, baseDataHandler(observable = observable))
            else -> HttpManager.getInstance().post(url, hashMapOf(), baseDataHandler(observable = observable))
        }
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> delete(observable: ((data: T) -> Unit)? = null) {
        when {
            formMap != null -> HttpManager.getInstance().delete(url, formMap, baseDataHandler(observable = observable))
            body != null -> HttpManager.getInstance().delete(url, body, baseDataHandler(observable = observable))
            else -> HttpManager.getInstance().delete(url, baseDataHandler(observable = observable))
        }
    }

    /** 不支持泛型套泛型的解析方式*/
    fun <T> put(observable: ((data: T) -> Unit)? = null) {
        when {
            formMap != null -> HttpManager.getInstance().put(url, formMap, baseDataHandler(observable = observable))
            body != null -> HttpManager.getInstance().put(url, body, baseDataHandler(observable = observable))
            else -> HttpManager.getInstance().put(url, hashMapOf(), baseDataHandler(observable = observable))
        }
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> get(listener: OnSuccessListener<T>? = null) {
        HttpManager.getInstance().get(url, formMap, baseDataHandler(listener = listener))
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> post(listener: OnSuccessListener<T>? = null) {
        when {
            formMap != null -> HttpManager.getInstance().post(url, formMap, baseDataHandler(listener = listener))
            body != null -> HttpManager.getInstance().post(url, body, baseDataHandler(listener = listener))
            else -> HttpManager.getInstance().post(url, hashMapOf(), baseDataHandler(listener = listener))
        }
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> delete(listener: OnSuccessListener<T>? = null) {
        when {
            formMap != null -> HttpManager.getInstance().delete(url, formMap, baseDataHandler(listener = listener))
            body != null -> HttpManager.getInstance().delete(url, body, baseDataHandler(listener = listener))
            else -> HttpManager.getInstance().delete(url, baseDataHandler(listener = listener))
        }
    }

    /** 支持泛型套泛型的解析方式*/
    fun <T> put(listener: OnSuccessListener<T>? = null) {
        when {
            formMap != null -> HttpManager.getInstance().put(url, formMap, baseDataHandler(listener = listener))
            body != null -> HttpManager.getInstance().put(url, body, baseDataHandler(listener = listener))
            else -> HttpManager.getInstance().put(url, hashMapOf(), baseDataHandler(listener = listener))
        }
    }

    companion object {
        /**默认统一处理接口调用失败*/
        @JvmStatic
        var failDefault: ((code: Int, data: String?) -> Unit)? = null
    }
}

/**数据成功监听，支持泛型套泛型*/
abstract class OnSuccessListener<T> {
    abstract fun onSuccess(data: T)
}