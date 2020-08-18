package cn.yue.base.common.image

import android.graphics.Bitmap

/**
 * Description :
 * Created by yue on 2019/6/19
 */
interface LoadBitmapCallBack {
    fun onBitmapLoaded(bitmap: Bitmap)
    fun onBitmapNoFound();
}