package cn.yue.base.utils.code

import android.os.Parcelable
import cn.yue.base.utils.Utils
import com.tencent.mmkv.MMKV

object MMKVUtils {

    fun init() {
        MMKV.initialize(Utils.getContext())
    }

    fun put(key: String, value: String): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Boolean): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Int): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Long): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Float): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Double): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: ByteArray): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Parcelable): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun put(key: String, value: Set<String>): Boolean {
        return MMKV.defaultMMKV().encode(key, value)
    }

    fun getString(key: String): String? {
        return MMKV.defaultMMKV().decodeString(key)
    }

    fun getBoolean(key: String): Boolean {
        return MMKV.defaultMMKV().decodeBool(key)
    }

    fun getInt(key: String): Int {
        return MMKV.defaultMMKV().decodeInt(key)
    }

    fun getLong(key: String): Long {
        return MMKV.defaultMMKV().decodeLong(key)
    }

    fun getFloat(key: String): Float {
        return MMKV.defaultMMKV().decodeFloat(key)
    }

    fun getDouble(key: String): Double {
        return MMKV.defaultMMKV().decodeDouble(key)
    }

    fun getByteArray(key: String): ByteArray? {
        return MMKV.defaultMMKV().decodeBytes(key)
    }

    fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>): Parcelable? {
        return MMKV.defaultMMKV().decodeParcelable(key, clazz)
    }

    fun getStringSet(key: String): Set<String>? {
        return MMKV.defaultMMKV().decodeStringSet(key)
    }

    fun remove(key: String) {
        MMKV.defaultMMKV().removeValueForKey(key)
    }

}