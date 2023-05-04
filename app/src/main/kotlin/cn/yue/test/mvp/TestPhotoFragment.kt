package cn.yue.test.mvp

import android.net.Uri
import android.os.Bundle
import android.view.View
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.middle.mvp.components.BaseHintFragment
import cn.yue.base.middle.mvp.photo.IPhotoView
import cn.yue.base.middle.mvp.photo.PhotoHelper
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestPhotoBinding
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Description :
 * Created by yue on 2023/4/27
 */
@Route(path = "/app/testPhoto")
class TestPhotoFragment: BaseHintFragment(), IPhotoView {
	
	override fun getContentLayoutId(): Int {
		return R.layout.fragment_test_photo
	}
	
	private lateinit var binding: FragmentTestPhotoBinding
	
	override fun bindLayout(inflated: View) {
		binding = FragmentTestPhotoBinding.bind(inflated)
	}
	
	private var photoHelper: PhotoHelper? = null
	
	override fun initView(savedInstanceState: Bundle?) {
		super.initView(savedInstanceState)
		photoHelper = PhotoHelper(mActivity, this)
		binding.tvSelectPhoto.setOnClickListener {
			photoHelper?.openSystemAlbum()
		}
	}
	
	override fun selectImageResult(selectList: List<MediaData>?) {
		photoHelper?.autoCropPhoto()
	}
	
	override fun cropImageResult(image: Uri?) {
	
	}
	
	override fun uploadImageResult(serverList: List<String>?) {
	
	}
}