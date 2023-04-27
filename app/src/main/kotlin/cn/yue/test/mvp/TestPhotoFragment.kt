package cn.yue.test.mvp

import android.net.Uri
import android.os.Bundle
import android.view.View
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.middle.mvp.components.BaseHintFragment
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestPhotoBinding
import com.alibaba.android.arouter.facade.annotation.Route

/**
 * Description :
 * Created by yue on 2023/4/27
 */
@Route(path = "/app/testPhoto")
class TestPhotoFragment: BaseHintFragment() {
	
	override fun getContentLayoutId(): Int {
		return R.layout.fragment_test_photo
	}
	
	private lateinit var binding: FragmentTestPhotoBinding
	
	override fun bindLayout(inflated: View) {
		binding = FragmentTestPhotoBinding.bind(inflated)
	}
	
	override fun initView(savedInstanceState: Bundle?) {
		super.initView(savedInstanceState)
		binding.tvSelectPhoto.setOnClickListener {
			getPhotoHelper().openAlbum()
		}
	}
	
	override fun selectImageResult(selectList: List<MediaData>?) {
		getPhotoHelper().autoCropPhoto()
	}
	
	override fun cropImageResult(image: Uri?) {
		super.cropImageResult(image)
	}
}