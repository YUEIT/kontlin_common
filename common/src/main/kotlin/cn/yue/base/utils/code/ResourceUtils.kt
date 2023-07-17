package cn.yue.base.utils.code

import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import cn.yue.base.utils.Utils

object ResourceUtils {

    fun getString(@StringRes resId: Int): String {
        return Utils.getContext().getString(resId)
    }

    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(Utils.getContext(), resId)
    }

    fun getColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(Utils.getContext(), resId)
    }
}

fun Int.getString(): String {
    return Utils.getContext().getString(this)
}

fun Int.getDrawable(): Drawable? {
    return ContextCompat.getDrawable(Utils.getContext(), this)
}

fun Int.getColor(): Int {
    return ContextCompat.getColor(Utils.getContext(), this)
}