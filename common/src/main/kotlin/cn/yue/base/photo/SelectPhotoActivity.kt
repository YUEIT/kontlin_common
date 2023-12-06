package cn.yue.base.photo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.yue.base.activity.BaseFragment
import cn.yue.base.activity.BaseFragmentActivity
import cn.yue.base.R
import cn.yue.base.photo.data.MediaData
import cn.yue.base.photo.data.MediaType
import cn.yue.base.utils.code.getParcelableArrayListExt
import cn.yue.base.utils.code.getString
import cn.yue.base.widget.viewpager.SampleTabStrip2
import com.alibaba.android.arouter.facade.annotation.Route

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

    private lateinit var viewPager: ViewPager2
    private lateinit var tabs: SampleTabStrip2

    override fun getContentViewLayoutId(): Int {
        return R.layout.activity_select_photo
    }
    
    private fun initBundle() {
        if (intent != null) {
            maxNum = intent.getIntExtra("maxNum", 1)
            val defaultList = intent.getParcelableArrayListExt("uris", Uri::class)
            defaultList?.forEach {
                val mediaVO = MediaData()
                mediaVO.uri = it
                photoList.add(mediaVO)
            }
            val defaultMediaList = intent.getParcelableArrayListExt("medias", MediaData::class)
            if (defaultMediaList != null) {
                photoList.addAll(defaultMediaList)
            }
            mediaType = MediaType.valueOf(intent.getIntExtra("mediaType", MediaType.ALL.value))
            isPreview = intent.getBooleanExtra("isPreview", false)
        }
    }

    private fun initTopBar() {
        getTopBar().setLeftImage(R.drawable.app_icon_back)
            .setLeftClickListener{ finish() }
            .setRightTextStr(if (photoList.isEmpty()) {
                R.string.app_cancel.getString()
            } else {
                "${R.string.app_confirm.getString()}(" + photoList.size + "/" + maxNum + ")"
            })
            .setRightClickListener{
                if (photoList.isEmpty()) {
                    finish()
                } else {
                    finishAllWithResult(photoList as ArrayList<MediaData>)
                }
            }
    }

    override fun initView() {
        super.initView()
        initBundle()
        initTopBar()
        if (tabTitle.isEmpty()) {
            tabTitle.add(R.string.app_photos_folder_select.getString())
            tabTitle.add(R.string.app_photos_folder_nearly.getString())
        } else {
            tabTitle[0] = R.string.app_photos_folder_select.getString()
            tabTitle[1] = R.string.app_photos_folder_nearly.getString()
        }
        viewPager = findViewById(R.id.viewPager)
        val adapter = object : FragmentStateAdapter(this), SampleTabStrip2.LayoutTabProvider {
    
            override fun getItemCount(): Int {
                return 2
            }
    
            override fun createTabView(): View {
                return View.inflate(this@SelectPhotoActivity, R.layout.item_select_photo_title, null)
            }
    
            override fun bindTabView(view: View, position: Int, selectPosition: Int) {
                view.findViewById<TextView>(R.id.tv_tab_item).apply {
                    if (position == selectPosition) {
                        setTextColor(Color.parseColor("#333333"))
                    } else {
                        setTextColor(Color.parseColor("#999999"))
                    }
                    text = tabTitle[position]
                }
            }
    
            override fun createFragment(position: Int): Fragment {
                return if (position == 0) {
                    getFragment(SelectPhotoFolderFragment::class.java.name)
                } else {
                    getFragment(SelectPhotoFragment::class.java.name)
                }
            }
        }
        viewPager.adapter = adapter
        tabs = findViewById(R.id.tabs)
        tabs.setViewPager(viewPager)
        viewPager.currentItem = 1
    }

    fun changeToSelectPhotoFragment(folderId: String?, name: String?) {
        val fragment = getFragment(fragmentNames[1])
        if (fragment is SelectPhotoFragment) {
            fragment.refresh(folderId)
        }
        tabTitle[1] = name ?: ""
        val tabItem = (tabs.layoutManager as LinearLayoutManager).findViewByPosition(1)
        tabItem?.findViewById<TextView>(R.id.tv_tab_item)?.text = name
        viewPager.currentItem = 1
    }

    private val tabTitle = arrayListOf<String>()
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
                .setRightTextStr(R.string.app_cancel.getString())
                .setRightClickListener{ finish() }
        } else if (getCurrentFragment() is SelectPhotoFragment) {
            getTopBar().setCenterTextStr(title)
                .setCenterImage(R.drawable.app_icon_search)
                .setCenterClickListener {
                    changeFragment(
	                    SelectPhotoFolderFragment::class.java.name,
                        R.string.app_photos_folder_select.getString())
                }
                .setLeftImage(R.drawable.app_icon_back)
                .setLeftClickListener{ finish() }
                .setRightTextStr(
                    if (photoList.isEmpty()) {
                        R.string.app_cancel.getString()
                    } else {
                        "${R.string.app_confirm.getString()}（" + photoList.size + "/" + maxNum + "）"
                    }
                )
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
                if (showFragment.isAdded) {
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