package cn.yue.base.middle.mvp.photo

import cn.yue.base.middle.mvp.IBaseView


/**
 * Description :
 * Created by yue on 2019/6/18
 */
open interface IPhotoView : IBaseView {
    fun selectImageResult(selectList: List<String>?)
    fun cropImageResult(image: String?)
    fun uploadImageResult(serverList: List<String>?)
}