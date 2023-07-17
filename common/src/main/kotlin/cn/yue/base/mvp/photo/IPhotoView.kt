package cn.yue.base.mvp.photo

import android.net.Uri
import cn.yue.base.mvp.IBaseView
import cn.yue.base.photo.data.MediaData


/**
 * Description :
 * Created by yue on 2019/6/18
 */
open interface IPhotoView : IBaseView {
    fun selectImageResult(selectList: List<MediaData>?)
    fun cropImageResult(image: Uri?)
    fun uploadImageResult(serverList: List<String>?)
}