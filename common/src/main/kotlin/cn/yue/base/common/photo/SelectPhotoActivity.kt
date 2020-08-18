package cn.yue.base.common.photo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseFragment
import cn.yue.base.common.activity.BaseFragmentActivity
import cn.yue.base.common.photo.data.MediaVO
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.*

/**
 * Description :
 * Created by yue on 2019/3/11
 */
@Route(path = "/common/selectPhoto")
class SelectPhotoActivity : BaseFragmentActivity() {
    var maxNum = 1
        private set
    private var photoList: MutableList<MediaVO> = ArrayList()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            maxNum = intent.getIntExtra("maxNum", 1)
            val defaultList: List<Uri>? = intent.getParcelableArrayListExtra("photos")
            if (defaultList != null) {
                for (uri in defaultList) {
                    val mediaVO = MediaVO()
                    mediaVO.uri = uri
                    photoList.add(mediaVO)
                }
            }
        }
        changeFragment(SelectPhotoFragment::class.java.name, "最近照片")
    }

    private fun changeTopBar(title: String?) {
        if (getCurrentFragment() is SelectPhotoFolderFragment) {
            getTopBar().setCenterTextStr(title)
                    .setLeftImage(R.drawable.app_icon_back)
                    .setLeftTextStr("")
                    .setLeftClickListener{ finish() }
                    .setRightTextStr("取消")
                    .setRightClickListener{ finish() }
        } else if (getCurrentFragment() is SelectPhotoFragment) {
            getTopBar().setLeftTextStr("相册")
                    .setLeftImage(R.drawable.app_icon_back)
                    .setLeftClickListener{ changeFragment(SelectPhotoFolderFragment::class.java.name, "相册选择") }
                    .setCenterTextStr(title)
                    .setRightTextStr(if (photoList.isEmpty()) "取消" else "确定（" + photoList.size + "/" + maxNum + "）")
                    .setRightClickListener{
                        if (photoList.isEmpty()) {
                            finish()
                        } else {
                            finishAllWithResult(photoList as ArrayList<MediaVO>)
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

    fun changeToSelectPhotoFragment(folderId: String?, name: String?) {
        changeFragment(SelectPhotoFragment::class.java.name, name)
        (getCurrentFragment() as SelectPhotoFragment).refresh(folderId)
    }

    private val fragmentNames = arrayOf(SelectPhotoFragment::class.java.name, SelectPhotoFolderFragment::class.java.name)
    private val fragments: MutableMap<String?, BaseFragment?> = HashMap()
    private fun getFragment(fragmentName: String): BaseFragment? {
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

    fun getPhotoList(): MutableList<MediaVO> {
        return photoList
    }

    fun setPhotoList(photoList: MutableList<MediaVO>) {
        this.photoList = photoList
    }

    override fun onBackPressed() {
        if (getCurrentFragment() is SelectPhotoFragment) {
            changeFragment(SelectPhotoFolderFragment::class.java.name, "相册选择")
        } else if (getCurrentFragment() is SelectPhotoFolderFragment) {
            finish()
        }
    }

    private fun finishAllWithResult(selectList: ArrayList<MediaVO>) {
        val intent = Intent()
        val uris = ArrayList<Uri?>()
        for ((_, _, uri) in selectList) {
            uris.add(uri)
        }
        intent.putParcelableArrayListExtra("photos", uris)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}