package com.lcg.mylibrary.utils

import com.lcg.mylibrary.utils.Token.TOKEN
import com.lcg.mylibrary.utils.Token.token

/**
 * token认证
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/9/11 17:57
 */
object Token {
    internal var token: String? = null
    var TOKEN = "Authorization"
    var loginSubscriber: ((showToast: Boolean) -> Unit)? = null
    fun init(name: String = "Authorization", login: (showToast: Boolean) -> Unit) {
        TOKEN = name
        loginSubscriber = login
    }
}

/**
 * 认证的token
 */
fun getToken(): String {
    if (token == null) {
        token = PreferenceKTX.getString(TOKEN, "")
    }
    return token ?: ""
}

/**
 * 存储认证的token
 */
fun saveToken(token: String?) {
    Token.token = token ?: ""
    PreferenceKTX.setString(TOKEN, Token.token!!)
}