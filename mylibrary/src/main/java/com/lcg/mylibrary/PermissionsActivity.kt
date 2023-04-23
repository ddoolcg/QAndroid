package com.lcg.mylibrary

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * activity申请权限基类，目前想到的最省事的写法
 *
 * @author lei.chuguang Email:475825657@qq.com
 */
abstract class PermissionsActivity : BaseActivity() {
    /**权限结果集*/
    private val permissionsResultMap: HashMap<Int, (Boolean) -> Unit> = hashMapOf()

    /**请求权限*/
    fun requestPermissions(vararg permissions: String, result: (Boolean) -> Unit) {
        val list = arrayListOf<String>()
        permissions.forEach {
            val permission = ContextCompat.checkSelfPermission(this, it)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                list.add(it)
            }
        }
        if (list.isEmpty()) {
            result(true)
        } else {
            val code = list.hashCode() and 0xFFFF
            permissionsResultMap[code] = result
            ActivityCompat.requestPermissions(this, list.toTypedArray(), code)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsResultMap.remove(requestCode)?.invoke(grantResults.all { it == PackageManager.PERMISSION_GRANTED })
    }
}

