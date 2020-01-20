package com.lcg.mylibrary.utils

import android.content.Context
import android.content.SharedPreferences
import com.alibaba.fastjson.JSON

/**
 * SharedPreferences数据存储
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2018/3/22 10:46
 */
object PreferenceKTX {
    @JvmStatic
    fun getSharedPreferences(): SharedPreferences {
        val context = UIUtils.getContext()
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun getEdit(): SharedPreferences.Editor {
        return getSharedPreferences().edit()
    }

    @JvmStatic
    fun setString(key: String, value: String) {
        getEdit().putString(key, value).commit()
    }

    @JvmStatic
    fun setInt(key: String, value: Int) {
        getEdit().putInt(key, value).commit()
    }

    @JvmStatic
    fun setBoolean(key: String, value: Boolean) {
        getEdit().putBoolean(key, value).commit()
    }

    @JvmStatic
    fun setByte(key: String, value: ByteArray) {
        setString(key, value.toString())
    }

    @JvmStatic
    fun setShort(key: String, value: Short) {
        setString(key, value.toString())
    }

    @JvmStatic
    fun setLong(key: String, value: Long) {
        getEdit().putLong(key, value).commit()
    }

    @JvmStatic
    fun setFloat(key: String, value: Float) {
        getEdit().putFloat(key, value).commit()
    }

    @JvmStatic
    fun setDouble(key: String, value: Double) {
        setString(key, value.toString())
    }

    @JvmStatic
    fun setString(resID: Int, value: String) {
        setString(UIUtils.getString(resID), value)

    }

    @JvmStatic
    fun setInt(resID: Int, value: Int) {
        setInt(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun setBoolean(resID: Int, value: Boolean) {
        setBoolean(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun setByte(resID: Int, value: ByteArray) {
        setByte(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun setShort(resID: Int, value: Short) {
        setShort(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun setLong(resID: Int, value: Long) {
        setLong(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun setFloat(resID: Int, value: Float) {
        setFloat(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun setDouble(resID: Int, value: Double) {
        setDouble(UIUtils.getString(resID), value)
    }

    @JvmStatic
    fun getString(key: String, defaultValue: String = ""): String {
        return getSharedPreferences().getString(key, defaultValue)
    }

    @JvmStatic
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return getSharedPreferences().getInt(key, defaultValue)
    }

    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return getSharedPreferences().getBoolean(key, defaultValue)
    }

    @JvmStatic
    fun getByte(key: String, defaultValue: Byte = 0): Byte {
        try {
            return getSharedPreferences().getInt(key, defaultValue.toInt()).toByte()
        } catch (e: Exception) {
        }
        return 0
    }

    @JvmStatic
    fun getShort(key: String, defaultValue: Short = 0): Short {
        try {
            return getString(key, "").toShort()
        } catch (e: Exception) {
        }
        return defaultValue
    }

    @JvmStatic
    fun getLong(key: String, defaultValue: Long = 0): Long {
        return getSharedPreferences().getLong(key, defaultValue)
    }

    @JvmStatic
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return getSharedPreferences().getFloat(key, defaultValue)
    }

    @JvmStatic
    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        try {
            return getString(key, "").toDouble()
        } catch (e: Exception) {
        }
        return defaultValue
    }

    @JvmStatic
    fun getString(resID: Int, defaultValue: String = ""): String {
        return getString(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getInt(resID: Int, defaultValue: Int = 0): Int {
        return getInt(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getBoolean(resID: Int, defaultValue: Boolean = false): Boolean {
        return getBoolean(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getByte(resID: Int, defaultValue: Byte = 0): Byte {
        return getByte(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getShort(resID: Int, defaultValue: Short = 0): Short {
        return getShort(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getLong(resID: Int, defaultValue: Long = 0): Long {
        return getLong(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getFloat(resID: Int, defaultValue: Float = 0f): Float {
        return getFloat(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun getDouble(resID: Int, defaultValue: Double = 0.0): Double {
        return getDouble(UIUtils.getString(resID), defaultValue)
    }

    @JvmStatic
    fun remove(key: String) {
        getEdit().remove(key).commit()
    }

    @JvmStatic
    fun clear() {
        getEdit().clear().commit()
    }

    @JvmStatic
    fun contains(key: String): Boolean {
        return getSharedPreferences().contains(key)
    }

    /**直接保存bean对象*/
    @JvmStatic
    fun setConfig(entity: Any?) {
        entity?.run {
            setString(this::class.java.name.replace(".", "_"), JSON.toJSONString(entity))
        }
    }

    /**获取bean对象*/
    @JvmStatic
    fun <T> getConfig(clazz: Class<T>): T? {
        val string = getString(clazz.name.replace(".", "_"), "")
        return if (string == "") null else JSON.parseObject(string, clazz)
    }

    /**配置文件中是否包含该bean的存储*/
    @JvmStatic
    fun containsConfig(clazz: Class<*>): Boolean {
        return getSharedPreferences().contains(clazz.name.replace(".", "_"))
    }

    @JvmStatic
    fun removeConfig(clazz: Class<*>) {
        getEdit().remove(clazz.name.replace(".", "_")).commit()
    }
}

/**
 * Allows editing of this preference instance with a call to [apply][SharedPreferences.Editor.apply]
 * or [commit][SharedPreferences.Editor.commit] to persist the changes.
 * Default behaviour is [apply][SharedPreferences.Editor.apply].
 * ```
 * preferenceEdit {
 *     putString("key", value)
 * }
 * ```
 * To [commit][SharedPreferences.Editor.commit] changes:
 * ```
 * preferenceEdit(commit = true) {
 *     putString("key", value)
 * }
 * ```
 */
inline fun Any.preferenceEdit(commit: Boolean = false, action: SharedPreferences.Editor.() -> Unit) {
    val editor = PreferenceKTX.getEdit()
    action(editor)
    if (commit) {
        editor.commit()
    } else {
        editor.apply()
    }
}