package cn.yue.base.middle.mvp.photo

import android.net.Uri
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.middle.mvp.IBaseView


/**
 * Description :
 * Created by yue on 2019/6/18
 */
open interface IPhotoView : IBaseView {
    fun selectImageResult(selectList: List<MediaData>?)
    fun cropImageResult(image: Uri?)
    fun uploadImageResult(serverList: List<String>?)
}