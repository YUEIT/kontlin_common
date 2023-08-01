package cn.yue.base.utils.code

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * Description :
 * Created by yue on 2023/7/31
 */

fun <T : Parcelable> Bundle.getParcelableExt(key: String, clazz: KClass<T>): T? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getParcelable(key, clazz.java)
	} else {
		this.getParcelable<T>(key)
	}
}

fun <T: Parcelable> Bundle.getParcelableArrayExt(key: String, clazz: KClass<T>): Array<T>? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getParcelableArray(key, clazz.java)
	} else {
		this.getParcelableArray(key) as Array<T>
	}
}

fun <T : Parcelable> Bundle.getParcelableArrayListExt(key: String, clazz: KClass<T>): ArrayList<T>? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			this.getParcelableArrayList(key, clazz.java)
		} else {
			this.getParcelableArrayList<T>(key)
		}
}

fun <T : Parcelable> Bundle.getSparseParcelableArrayExt(key: String, clazz: KClass<T>): SparseArray<T>? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getSparseParcelableArray(key, clazz.java)
	} else {
		this.getSparseParcelableArray<T>(key)
	}
}

fun <T : Serializable> Bundle.getSerializableExt(key: String, clazz: KClass<T>): Serializable? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getSerializable(key, clazz.java)
	} else {
		this.getSerializable(key)
	}
}

fun <T : Parcelable> Intent.getParcelableExt(key: String, clazz: KClass<T>): T? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getParcelableExtra(key, clazz.java)
	} else {
		this.getParcelableExtra<T>(key)
	}
}

fun <T: Parcelable> Intent.getParcelableArrayExt(key: String, clazz: KClass<T>): Array<T>? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getParcelableArrayExtra(key, clazz.java)
	} else {
		this.getParcelableArrayExtra(key) as Array<T>
	}
}

fun <T : Parcelable> Intent.getParcelableArrayListExt(key: String, clazz: KClass<T>): ArrayList<T>? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getParcelableArrayListExtra(key, clazz.java)
	} else {
		this.getParcelableArrayListExtra<T>(key)
	}
}

fun <T : Serializable> Intent.getSerializableExt(key: String, clazz: KClass<T>): Serializable? {
	return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		this.getSerializableExtra(key, clazz.java)
	} else {
		this.getSerializableExtra(key)
	}
}
