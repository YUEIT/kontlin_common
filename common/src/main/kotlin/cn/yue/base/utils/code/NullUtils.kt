package cn.yue.base.utils.code

/**
 * Description :
 * Created by yue on 2020/8/6
 */

object NullUtils {

    fun <T> hasValue(collection: Collection<T>?): Boolean {
        return collection != null && collection.isNotEmpty()
    }

    fun <T> forEachNullable(iterable: Iterable<T>?, action: (T) -> Unit) {
        if (iterable == null) return
        for (element in iterable) action(element)
    }

    fun <T> forEachIndexedNullable(iterable: Iterable<T>?, action: (index: Int, T) -> Unit) {
        if (iterable == null) return
        var index = 0
        for (item in iterable) action(index++, item)
    }

    fun hasValue(charSequence: CharSequence?): Boolean {
        return charSequence != null && charSequence.isNotEmpty()
    }

    fun <T : CharSequence> hasValue(charSequence: T?, action: (data: T) -> Unit): Boolean {
        val value = (charSequence != null && charSequence.isNotEmpty())
        if (value) {
            action.invoke(charSequence!!)
        }
        return value
    }

    fun realValue(value: String?): String {
        if (value == null || value.isEmpty()) {
            return ""
        }
        return value
    }

    fun realValue(value: Int?): Int {
        if (value == null) {
            return 0
        }
        return value
    }

    fun realValue(value: Float?): Float {
        if (value == null) {
            return 0f
        }
        return value
    }

    fun realValue(value: Double?): Double {
        if (value == null) {
            return 0.0
        }
        return value
    }

    fun <T> realSize(collection: Collection<T>?): Int {
        if (collection == null || collection.isEmpty()) {
            return 0
        }
        return collection.size
    }
}

fun <T> Collection<T>?.hasValue(): Boolean {
    return this != null && this.isNotEmpty()
}

inline fun <T> Iterable<T>?.forEachNullable(action: (T) -> Unit): Unit {
    if (this == null) return
    for (element in this) action(element)
}

inline fun <T> Iterable<T>?.forEachIndexedNullable(action: (index: Int, T) -> Unit): Unit {
    if (this == null) return
    var index = 0
    for (item in this) action(index++, item)
}

fun CharSequence?.hasValue(): Boolean {
    return this != null && this.isNotEmpty()
}

inline fun <T : CharSequence> T?.hasValue(action: (data: T) -> Unit): Boolean {
    val value = (this != null && this.isNotEmpty())
    if (value) {
        action.invoke(this!!)
    }
    return value
}

fun String?.realValue(): String {
    if (this == null || this.isEmpty()) {
        return ""
    }
    return this
}

fun Int?.realValue(): Int {
    if (this == null) {
        return 0
    }
    return this
}

fun Float?.realValue(): Float {
    if (this == null) {
        return 0f
    }
    return this
}

fun Double?.realValue(): Double {
    if (this == null) {
        return 0.0
    }
    return this
}

fun <T> Collection<T>?.realSize(): Int {
    if (this == null || this.isEmpty()) {
        return 0
    }
    return this.size
}


