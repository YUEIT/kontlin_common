package cn.yue.base.photo.preview

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.photo.data.MediaType
import cn.yue.base.utils.app.FragmentUtils
import cn.yue.base.video.ViewVideoFragment

class ViewMediaActivity : BaseFragmentActivity() {

    override fun getFragment(): Fragment {
        val photoUriList = intent.getParcelableArrayListExtra<Uri>("uris")
        val currentIndex = intent.getIntExtra("position", 0)
        val mediaType = MediaType.valueOf(intent.getIntExtra("mediaType", MediaType.PHOTO.value))
        val bundle = Bundle()
        bundle.putParcelableArrayList("uris", photoUriList)
        bundle.putInt("position", currentIndex)
        return if (mediaType == MediaType.VIDEO) {
            FragmentUtils.instantiate(this, ViewVideoFragment::class.java, bundle)
        } else {
            FragmentUtils.instantiate(this, ViewPhotoFragment::class.java, bundle)
        }
    }
}