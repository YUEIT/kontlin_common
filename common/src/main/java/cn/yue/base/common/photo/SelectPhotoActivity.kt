package cn.yue.base.common.photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import cn.yue.base.common.R
import cn.yue.base.common.activity.BaseActivity
import cn.yue.base.common.activity.PermissionCallBack
import cn.yue.base.common.image.ImageLoader
import cn.yue.base.common.utils.app.RunTimePermissionUtil
import cn.yue.base.common.utils.code.ThreadPoolUtils
import cn.yue.base.common.utils.debug.LogUtils
import cn.yue.base.common.widget.recyclerview.CommonAdapter
import cn.yue.base.common.widget.recyclerview.CommonViewHolder
import com.alibaba.android.arouter.facade.annotation.Route
import java.io.File
import java.util.*

/**
 * Description :
 * Created by yue on 2018/11/19
 */

@Route(path = "/common/selectPhoto")
class SelectPhotoActivity : BaseActivity() {

    private val PAGE_COUNT = 50
    private var adapter: CommonAdapter<String>? = null
    private val photoList:MutableList<String> = ArrayList<String>()
    private var page: Int = 0
    private var isCanLoadMore = true

    override val layoutId: Int
        get() = R.layout.fragment_select_photo

    private var handler = Handler(Handler.Callback { msg ->
        if (msg.what == 101) {
            val addList = msg.obj as List<String>
            if (addList == null || addList.isEmpty()) {
                isCanLoadMore = false
            } else {
                isCanLoadMore = true
                photoList.addAll(addList)
                adapter!!.setList(photoList)
            }
        }
        false
    })

    private val select = ArrayList<String>()
    private val crop = false
    private var targetUri: Uri? = null
    private var photoPath: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPhotoError()
    }

    override fun initView() {
        findViewById<View>(R.id.backView).setOnClickListener { finish() }
        val confirm = findViewById<TextView>(R.id.confirmTV)
        confirm.setOnClickListener { finishAllWithResult(select) }
        val photoRV = findViewById<RecyclerView>(R.id.photoRV)
        photoRV.layoutManager = GridLayoutManager(this@SelectPhotoActivity, 5)
        photoRV.adapter = object : CommonAdapter<String>(this@SelectPhotoActivity, photoList) {

            override fun getLayoutIdByType(viewType: Int): Int {
                return R.layout.item_select_photo
            }

            override fun bindData(holder: CommonViewHolder<String>, position: Int, s: String) {
                val photoIV = holder.getView<ImageView>(R.id.photoIV)
                val checkBox = holder.getView<CheckBox>(R.id.photoCB)
                if (position == 0) {
                    photoIV!!.setBackgroundColor(Color.parseColor("#cccccc"))
                    photoIV.setOnClickListener { toCamera() }
                    checkBox!!.visibility = View.GONE
                    checkBox.setOnCheckedChangeListener(null)
                } else {
                    ImageLoader.getLoader().loadImage(photoIV, s)
                    photoIV!!.setOnClickListener(null)
                    checkBox!!.visibility = View.VISIBLE
                    checkBox.setOnCheckedChangeListener { buttonView, isChecked -> addSelectList(s, isChecked) }
                }
            }
        }
        photoRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val layoutManager = recyclerView!!.layoutManager as GridLayoutManager
                LogUtils.e("" + layoutManager.findLastVisibleItemPosition())
                if (photoList.size - 5 <= layoutManager.findLastVisibleItemPosition() && isCanLoadMore) {
                    isCanLoadMore = false
                    getPhotoList()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        photoList.add(0, "camera")
        RunTimePermissionUtil.requestPermissions(this@SelectPhotoActivity, RunTimePermissionUtil.REQUEST_CODE, object : PermissionCallBack {
            override fun requestSuccess(permission: String) {
                getPhotoList()
            }

            override fun requestFailed(permission: String) {

            }
        }, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun getPhotoList() {
        val threadPoolUtils = ThreadPoolUtils(ThreadPoolUtils.Type.SingleThread, 1)
        threadPoolUtils.execute( Runnable {
            val list = PhotoUtils.getPhotosByPage(this@SelectPhotoActivity, page, PAGE_COUNT)
            handler.sendMessage(Message.obtain(handler, 101, list))
            page++
        })
    }

    private fun addSelectList(s: String, checked: Boolean) {
        if (checked) {
            for (str in select) {
                if (str == s) {
                    return
                }
            }
            select.add(s)
        } else {
            select.remove(s)
        }
    }

    private fun finishAllWithResult(selectList: ArrayList<String>) {
        val intent = Intent()
        intent.putStringArrayListExtra("photos", selectList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private val REQUEST_CODE_CAMERA = 102
    private val REQUEST_CODE_PHOTO_CROP = 103

    private fun toCamera() {
        RunTimePermissionUtil.requestPermissions(this, RunTimePermissionUtil.REQUEST_CODE, object : PermissionCallBack {
            override fun requestSuccess(permission: String) {
                val cachePath = Environment.getExternalStorageDirectory().toString() + File.separator + "cache" + File.separator
                val tempFile = File(cachePath, UUID.randomUUID().toString() + ".jpg")
                photoPath = tempFile.absolutePath
                targetUri = Uri.fromFile(tempFile)

                if (targetUri != null) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
                    //intent.putExtra("return-data", false);//若为false则表示不返回数据
                    startActivityForResult(intent, REQUEST_CODE_CAMERA)
                }
            }

            override fun requestFailed(permission: String) {

            }
        }, Manifest.permission.CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        LogUtils.i("$requestCode,$resultCode")
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (crop) {
                val intent = Intent("com.android.camera.action.CROP")
                intent.setDataAndType(targetUri, "image/*")
                intent.putExtra("crop", "true")//可裁剪
                //intent.putExtra("aspectX", 2);
                //intent.putExtra("aspectY", 1);
                intent.putExtra("scale", false)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, targetUri)
                //intent.putExtra("return-data", false);//若为false则表示不返回数据
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
                intent.putExtra("noFaceDetection", true)
                startActivityForResult(intent, REQUEST_CODE_PHOTO_CROP)
            } else {
                val temp = ArrayList<String>()
                temp.add(photoPath!!)
                finishAllWithResult(temp)
            }
        }
        if (requestCode == REQUEST_CODE_PHOTO_CROP) {
            val temp = ArrayList<String>()
            temp.add(photoPath!!)
            finishAllWithResult(temp)
        }

    }

    private fun initPhotoError() { // android 7.0系统解决拍照的问题
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            builder.detectFileUriExposure()
        }
    }

}
