package com.lcg.comment.net

import com.lcg.mylibrary.net.HttpUrl
import com.lcg.mylibrary.net.OnSuccessListener
import com.lcg.mylibrary.net.ResponseHandler
import com.lcg.mylibrary.utils.L
import com.lcg.mylibrary.utils.Token
import com.lcg.mylibrary.utils.getToken
import okhttp3.Call
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

var host = "https://ddoolcg.pythonanywhere.com"

/**
 * 自定义的联网框架
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2019/1/30 10:53
 */
class MyUrl(path: String) : HttpUrl(host + path) {
    override fun <T> responseHandler(observable: ((data: T) -> Unit)?, listener: OnSuccessListener<T>?): ResponseHandler {
        return object : Base200Handler<T>() {
            override fun onStart(call: Call) {
                L.i(Token.TOKEN + "-->" + getToken())
                progress?.showProgressDialog(msg!!, call)
            }

            override fun onNetFinish() {
                progress?.dismissProgressDialog(msg!!)
            }

            override fun onFail(code: String?, data: String?) {
                when {
                    failDefault != null -> failDefault!!.invoke(code?.toIntOrNull() ?: 200, data)
                    fail != null -> fail!!.invoke(code?.toIntOrNull() ?: 200, data)
                    else -> super.onFail(code, data)
                }
            }

            override fun onSuccess(data: T) {
                listener?.onSuccess(data)
                observable?.invoke(data)
            }

            override fun getType(): Type {
                if (observable != null) {
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
            }
        }
    }
}