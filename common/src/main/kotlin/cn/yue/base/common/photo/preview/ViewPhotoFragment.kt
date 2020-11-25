package cn.yue.base.common.photo.preview

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.widget.TopBar
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.*

/**
 * Description :
 * Created by yue on 2019/3/11
 */
@Route(path = "/common/viewPhoto")
class ViewPhotoFragment : BaseFragment() {
    private var photoList: MutableList<String> = ArrayList()
    private var photoUriList: MutableList<Uri> = ArrayList()
    private var currentIndex = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_view_photo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply { 
            getStringArrayList("urls")?.let {
                photoList.addAll(it)
            }
            getParcelableArrayList<Uri>("uris")?.let {
                photoUriList.addAll(it)
            }
            currentIndex = getInt("position")
        }
    }
    
    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        if (photoList.isNotEmpty()) {
            topBar.setCenterTextStr((currentIndex + 1).toString() + "/" + photoList.size)
        } else if (photoUriList.isNotEmpty()) {
            topBar.setCenterTextStr((currentIndex + 1).toString() + "/" + photoUriList.size)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val viewPager = findViewById<PhotoViewPager>(R.id.viewPager)
        viewPager.adapter = photoAdapter
        viewPager.currentItem = currentIndex
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                if (photoList.isNotEmpty()) {
                    topBar.setCenterTextStr((position + 1).toString() + "/" + photoList.size)
                } else if (photoUriList.isNotEmpty()) {
                    topBar.setCenterTextStr((position + 1).toString() + "/" + photoUriList.size)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private val photoAdapter: PagerAdapter = object : PagerAdapter() {
        private val mViewCache = HashMap<Int, PhotoView>()
        override fun getCount(): Int {
            return if (photoList.size == 0) photoUriList.size else photoList.size
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            super.setPrimaryItem(container, position, `object`)
            val photoView = mViewCache[position]
            if (photoList.isNotEmpty()) {
                (container as PhotoViewPager).setCurrentPhotoView(photoView!!, position, photoList[position], null)
            } else if (photoUriList.isNotEmpty()) {
                (container as PhotoViewPager).setCurrentPhotoView(photoView!!, position, null, photoUriList[position])
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var photoView = mViewCache[position]
            if (photoView == null) {
                photoView = PhotoView(mActivity)
                photoView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                if (photoList.isNotEmpty()) {
                    photoView.loadImage(photoList[position])
                } else if (photoUriList.isNotEmpty()) {
                    photoView.loadImage(photoUriList[position])
                }
                mViewCache[position] = photoView
            }
            container.addView(photoView)
            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
            val photoView = mViewCache[position]
            if (photoView != null) {
                mViewCache.remove(position)
            }
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }
}