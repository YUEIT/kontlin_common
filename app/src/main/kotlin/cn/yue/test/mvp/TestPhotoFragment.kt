package cn.yue.test.mvp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.yue.base.mvp.components.BaseHintFragment
import cn.yue.base.mvp.photo.IPhotoView
import cn.yue.base.mvp.photo.PhotoHelper
import cn.yue.base.photo.data.MediaData
import cn.yue.base.router.FRouter
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
		binding.tvSelectAlbum.setOnClickListener {
			photoHelper?.openAlbum()
		}
		binding.tvSelectSystemAlbum.setOnClickListener {
			photoHelper?.openSystemAlbum()
		}
	}
	
	override fun selectImageResult(selectList: List<MediaData>?) {
		photoHelper?.autoCropPhoto()
	}
	
	override fun cropImageResult(image: Uri?) {
		Log.d("luo", "cropImageResult: $image")
		FRouter.instance.build("/common/viewPhoto")
			.withParcelableArrayList("uris", arrayListOf(image))
			.navigation(mActivity)
	}
	
	override fun uploadImageResult(serverList: List<String>?) {
	
	}
}