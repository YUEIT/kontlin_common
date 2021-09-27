package cn.yue.base.common.photo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.photo.data.MediaType
import cn.yue.base.common.photo.data.MediaData
import cn.yue.base.common.widget.viewpager.PagerSlidingTabStrip
import cn.yue.base.common.widget.viewpager.SampleTabStrip
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.*

/**
 * Description :
 * Created by yue on 2019/3/11
 */
@Route(path = "/common/selectPhoto")
class SelectPhotoActivity : BaseFragmentActivity() {
    private var maxNum = 1
    private var photoList: MutableList<MediaData> = ArrayList()
    private var mediaType: MediaType = MediaType.ALL
    private var isPreview: Boolean = false

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: PagerSlidingTabStrip
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            maxNum = intent.getIntExtra("maxNum", 1)
            val defaultList: List<Uri>? = intent.getParcelableArrayListExtra("uris")
            if (defaultList != null) {
                for (uri in defaultList) {
                    val mediaVO = MediaData()
                    mediaVO.uri = uri
                    photoList.add(mediaVO)
                }
            }
            val defaultMediaList: List<MediaData>? = intent.getParcelableArrayListExtra("medias")
            if (defaultMediaList != null) {
                photoList.addAll(defaultMediaList)
            }
            mediaType = MediaType.valueOf(intent.getIntExtra("mediaType", MediaType.ALL.value))
            isPreview = intent.getBooleanExtra("isPreview", false)
        }
        initTopBar()
        initView()
//        changeFragment(SelectPhotoFragment::class.java.name, "最近照片")
    }

    override fun getContentViewLayoutId(): Int {
        return R.layout.activity_select_photo
    }

    private fun initTopBar() {
        getTopBar().setLeftImage(R.drawable.app_icon_back)
            .setLeftClickListener{ finish() }
            .setRightTextStr(if (photoList.isEmpty()) "取消" else "确定(" + photoList.size + "/" + maxNum + ")")
            .setRightClickListener{
                if (photoList.isEmpty()) {
                    finish()
                } else {
                    finishAllWithResult(photoList as ArrayList<MediaData>)
                }
            }
    }

    private fun initView() {
        viewPager = findViewById<ViewPager>(R.id.viewPager)
        val adapter = object : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int {
                return 2
            }

            override fun getItem(position: Int): Fragment {
                return if (position == 0) {
                    getFragment(SelectPhotoFolderFragment::class.java.name)
                } else {
                    getFragment(SelectPhotoFragment::class.java.name)
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return if (position == 0) {
                    "相册选择"
                } else {
                    "最近照片"
                }
            }
        }
        viewPager.adapter = adapter
        tabs = findViewById<PagerSlidingTabStrip>(R.id.tabs)
        tabs.setViewPagerAutoRefresh(viewPager)
        viewPager.currentItem = 1
    }

    fun changeToSelectPhotoFragment(folderId: String?, name: String?) {
        val fragment = getFragment(fragmentNames[1])
        if (fragment is SelectPhotoFragment) {
            fragment.refresh(folderId)
        }
        val textView = tabs.getTab(1)
        if (textView is TextView) {
            textView.text = name
        }
        viewPager.currentItem = 1
    }

    private val fragmentNames = arrayOf(SelectPhotoFolderFragment::class.java.name, SelectPhotoFragment::class.java.name)
    private val fragments: MutableMap<String?, BaseFragment?> = HashMap()
    private fun getFragment(fragmentName: String): BaseFragment {
        var fragment = fragments[fragmentName]
        if (fragment == null) {
            val bundle = Bundle()
            fragment = Fragment.instantiate(this, fragmentName, bundle) as BaseFragment
            fragments[fragmentName] = fragment
        }
        return fragment
    }

    override fun getFragment(): Fragment? {
        return null
    }

    fun getPhotoList(): MutableList<MediaData> {
        return photoList
    }

    fun setPhotoList(photoList: MutableList<MediaData>) {
        this.photoList = photoList
    }

    fun getMaxNum(): Int {
        return maxNum
    }

    fun getMediaType(): MediaType {
        return mediaType
    }

    fun getIsPreview(): Boolean {
        return isPreview
    }

    private fun finishAllWithResult(selectList: ArrayList<MediaData>) {
        val intent = Intent()
        intent.putParcelableArrayListExtra("medias", selectList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /*************************不使用viewpager时，可通过下面方法************************/

    private fun changeTopBar(title: String?) {
        if (getCurrentFragment() is SelectPhotoFolderFragment) {
            getTopBar().setCenterTextStr(title)
                .setCenterImage(0)
                .setCenterClickListener {  }
                .setLeftImage(R.drawable.app_icon_back)
                .setLeftTextStr("")
                .setLeftClickListener{ finish() }
                .setRightTextStr("取消")
                .setRightClickListener{ finish() }
        } else if (getCurrentFragment() is SelectPhotoFragment) {
            getTopBar().setCenterTextStr(title)
                .setCenterImage(R.drawable.app_icon_search)
                .setCenterClickListener { changeFragment(SelectPhotoFolderFragment::class.java.name, "相册选择") }
                .setLeftImage(R.drawable.app_icon_back)
                .setLeftClickListener{ finish() }
                .setRightTextStr(if (photoList.isEmpty()) "取消" else "确定（" + photoList.size + "/" + maxNum + "）")
                .setRightClickListener{
                    if (photoList.isEmpty()) {
                        finish()
                    } else {
                        finishAllWithResult(photoList as ArrayList<MediaData>)
                    }
                }
        }
    }

    private fun changeFragment(fragmentName: String, title: String?) {
        // check input
        val showFragment = getFragment(fragmentName)
        if (showFragment !== getCurrentFragment()) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            setAnimation(transaction)
            if (null == getCurrentFragment()) {
                transaction.add(R.id.content, showFragment!!, showFragment.javaClass.name).commitAllowingStateLoss()
            } else {
                // 已经加载了
                if (showFragment!!.isAdded) {
                    transaction.show(showFragment).hide(getCurrentFragment()!!).commitAllowingStateLoss()
                } else {
                    transaction.add(R.id.content, showFragment, showFragment.javaClass.name).hide(getCurrentFragment()!!).commitAllowingStateLoss()
                }
            }
            setCurrentFragment(showFragment)
        }
        changeTopBar(title)
    }

    private fun setAnimation(transaction: FragmentTransaction) {
        if (getCurrentFragment() is SelectPhotoFragment) {
            transaction.setCustomAnimations(R.anim.left_in, R.anim.right_out)
        } else {
            transaction.setCustomAnimations(R.anim.right_in, R.anim.left_out)
        }
    }

    override fun onBackPressed() {
//        if (getCurrentFragment() is SelectPhotoFragment) {
//            changeFragment(SelectPhotoFolderFragment::class.java.name, "相册选择")
//        } else if (getCurrentFragment() is SelectPhotoFolderFragment) {
//            finish()
//        }
        super.onBackPressed()
    }

}