package cn.yue.base.middle.mvp.photo

import cn.yue.base.middle.mvp.IWaitView
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.android.FragmentEvent

/**
 * Description :
 * Created by yue on 2019/6/18
 */
interface IPhotoView : IWaitView, LifecycleProvider<FragmentEvent> {

    fun selectImageResult(selectList: MutableList<String>)

    fun cropImageResult(image: String)

    fun uploadImageResult(serverList: MutableList<String>)
}