package cn.yue.base.common.utils.code

/**
 * Description :
 * Created by yue on 2020/8/6
 */

inline fun <T> Collection<T>?.hasValue(): Boolean {
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

inline fun CharSequence?.hasValue(): Boolean {
    return this != null && this.isNotEmpty()
}

inline fun <T : CharSequence> T?.hasValue(action: (data: T) -> Unit): Boolean {
    val value = (this != null && this.isNotEmpty())
    if (value) {
        action.invoke(this!!)
    }
    return value
}

inline fun String?.realValue(): String {
    if (this == null || this.isEmpty()) {
        return ""
    }
    return this
}

inline fun Int?.realValue(): Int {
    if (this == null) {
        return 0
    }
    return this
}

inline fun Float?.realValue(): Float {
    if (this == null) {
        return 0f
    }
    return this
}

inline fun Double?.realValue(): Double {
    if (this == null) {
        return 0.0
    }
    return this
}

inline fun <T> Collection<T>?.realSize(): Int {
    if (this == null || this.isEmpty()) {
        return 0
    }
    return this.size
}


