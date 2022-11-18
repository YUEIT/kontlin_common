package cn.yue.test.mvp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import cn.yue.base.common.widget.TopBar
import cn.yue.base.middle.mvp.components.binding.BaseHintBindFragment
import cn.yue.test.ComposeActivity
import cn.yue.test.R
import cn.yue.test.databinding.FragmentTestHintBinding
import cn.yue.test.float.FloatingWindowService
import com.alibaba.android.arouter.facade.annotation.Route
import java.util.regex.Pattern


/**
 * Description :
 * Created by yue on 2021/11/12
 */

@Route(path = "/app/testHint")
class TestHintFragment : BaseHintBindFragment<FragmentTestHintBinding>() {

    private val activityResultContract = object : ActivityResultContract<Intent, Bundle?>(){

        override fun createIntent(context: Context, input: Intent): Intent {
            //这个intent由resultLauncher调用launch方法时传入
            return input
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Bundle? {
            if(resultCode == Activity.RESULT_OK) {
                return intent?.extras
            }
            return null
        }
    }

    override fun getContentLayoutId(): Int {
        return R.layout.fragment_test_hint
    }

    override fun initTopBar(topBar: TopBar) {
        super.initTopBar(topBar)
        topBar.setBackgroundColor(Color.RED)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        binding.tv.setOnClickListener {
//            if (SettingsCompat.canDrawOverlays(mActivity)) {
//                mActivity.startService(Intent(mActivity, FloatingWindowService::class.java))
//            }
            registerForActivityResult(activityResultContract) {
                Log.d("luo", ": success")
            }.launch(Intent(mActivity, ComposeActivity::class.java))
        }
        binding.tvUp.setOnClickListener {
            mActivity.stopService(Intent(mActivity, FloatingWindowService::class.java))
        }
        binding.tvDrop.setOnLongClickListener {
            // Create a new ClipData.
            // This is done in two steps to provide clarity. The convenience method
            // ClipData.newPlainText() can create a plain text ClipData in one step.

            // Create a new ClipData.Item from the ImageView object's tag.
            val item = ClipData.Item(binding.tvDrop.text)

            // Create a new ClipData using the tag as a label, the plain text MIME type, and
            // the already-created item. This creates a new ClipDescription object within the
            // ClipData and sets its MIME type to "text/plain".
            val dragData = ClipData(
                binding.tvDrop.text,
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )

            // Instantiate the drag shadow builder.
            val myShadow = object : View.DragShadowBuilder(it) {

                private val shadow = ColorDrawable(Color.LTGRAY)

                override fun onProvideShadowMetrics(
                    outShadowSize: Point?,
                    outShadowTouchPoint: Point?
                ) {

                    // Set the width of the shadow to half the width of the original View.
//                    val width: Int = view.width / 2
//
//                    // Set the height of the shadow to half the height of the original View.
//                    val height: Int = view.height / 2
//
//                    // The drag shadow is a ColorDrawable. This sets its dimensions to be the
//                    // same as the Canvas that the system provides. As a result, the drag shadow
//                    // fills the Canvas.
//                    shadow.setBounds(0, 0, width, height)
//
//                    // Set the size parameter's width and height values. These get back to
//                    // the system through the size parameter.
//                    outShadowSize?.set(width, height)
//
//                    // Set the touch point's position to be in the middle of the drag shadow.
//                    outShadowTouchPoint?.set(width / 2, height / 2)
                    super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint)
                }

                override fun onDrawShadow(canvas: Canvas?) {
                    super.onDrawShadow(canvas)
                }
            }

            // Start the drag.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.startDragAndDrop(
                    dragData,  // The data to be dragged
                    myShadow,  // The drag shadow builder
                    null,      // No need to use local data
                    0          // Flags (not currently used, set to 0)
                )
            }

            true
        }

        binding.etReceived.setOnDragListener { v, event ->
            Log.d("luo", "initView: ${event.action}")
            when(event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data.
                    if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        // As an example of what your application might do, applies a blue color tint

                        // Invalidate the view to force a redraw in the new tint.
                        v.invalidate()

                        // Returns true to indicate that the Vie w can accept the dragged data.
                        true
                    } else {
                        // Returns false to indicate that, during the current drag and drop operation,
                        // this View will not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v?.setBackgroundColor(Color.BLUE)
                    // Returns true; the value is ignored.
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    // Ignore the event.
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v?.setBackgroundColor(Color.GRAY)
                    // Returns true; the value is ignored.
                    true
                }
                DragEvent.ACTION_DROP -> {
                    // Gets the item containing the dragged data.
                    val item: ClipData.Item = event.clipData.getItemAt(0)

                    // Gets the text data from the item.
                    val dragData = item.text

                    // Displays a message containing the dragged data.

                    // Turns off any color tints.
                    (v as? TextView)?.apply {
                        editableText.insert(selectionStart, dragData)
                    }

                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    v?.setBackgroundColor(Color.GRAY)
                    // Returns true; the value is ignored.
                    true
                }
                else -> {
                    // An unknown action type was received.
                    Log.e("DragDrop Example", "Unknown action type received by View.OnDragListener.")
                    false
                }
            }
        }

    }

    fun loadHook(str: String): String {
        return "未被拦截${str}"
    }

    //<copy><copy></copy></copy>
    fun parseCopy(content: String) {

        //        String reg = "\\[[a-zA-Z0-9\\u4e00-\\u9fa5]+\\]";//校验表情正则
        val reg = "<copy>.*?</copy>" //校验表情正则
        val pattern = Pattern.compile(reg)
        val matcher = pattern.matcher(content)
        var startIndex = 0
        val stringArray = arrayListOf<String>()
        while (matcher.find()) {
            val matchStr = matcher.group() //获取匹配到的字符串
            val start = matcher.start() //匹配到字符串的开始位置
            val end = matcher.end() //匹配到字符串的结束位置
            Log.d("luo", "parseCopy: $matchStr , $start $end")
            if (start > startIndex) {
                val beforeString = content.substring(startIndex, start)
                stringArray.add(beforeString)
            }
            val matchString = content.substring(start, end)
            stringArray.add(matchString.replace("<copy>", "").replace("</copy>", ""))
            startIndex = end
        }
        if (startIndex < content.length) {
            val matchString = content.substring(startIndex, content.length)
            stringArray.add(matchString)
        }
        val stringBuilder = StringBuilder()
        for (s in stringArray) {
            stringBuilder.append(s)
        }
        Log.d("luo", "parseCopy: $stringBuilder")
    }

    //maxTop ; maxEnd
    private fun startAnim() {
        val addTop = 500
        val maxEnd = 500
        val location = IntArray(2)
        binding.tvUp.getLocationInWindow(location)
        val pointX: Int = location[0] - binding.tvUp.width / 2
        val pointY: Int = location[1] - binding.tvUp.height / 2
        val animatorSet = AnimatorSet()
        val animX =
            ObjectAnimator.ofFloat(binding.tvUp, "x", pointX.toFloat(), (pointX + addTop).toFloat())
        val animY =
            ObjectAnimator.ofFloat(binding.tvUp, "y", pointX.toFloat(), (pointY + addTop).toFloat())
        animatorSet.interpolator = AnticipateInterpolator()
        animatorSet.duration = 2000
        animatorSet.playTogether(animX, animY)
        animatorSet.start()
    }

    val currentPosition = FloatArray(2)
    private fun test() {
        val location = IntArray(2)
        location[0] = binding.tvUp.x.toInt()
        location[1] = binding.tvUp.y.toInt()
        Log.d("luo", "test: ${location[0]} ${location[1]}")
        val endLocation = IntArray(2)
        endLocation[0] = location[0] + 500
        endLocation[1] = location[1] - 500
        val path = Path()
        path.moveTo(location[0].toFloat(), location[1].toFloat())
        path.quadTo(
            (location[0] + endLocation[0]).toFloat() / 2, endLocation[1].toFloat() - 300,
            endLocation[0].toFloat(), endLocation[1].toFloat()
        )
        val pathMeasure = PathMeasure(path, false)
        val valueAnimator = ValueAnimator.ofFloat(0f, pathMeasure.length)
        valueAnimator.interpolator = TimeInterpolator {
            //0 - 1
            it
        }
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            pathMeasure.getPosTan(value, currentPosition, null)
            Log.d("luo", "test: ${currentPosition[0]} ${currentPosition[1]}")
            binding.tvUp.x = currentPosition[0]
            binding.tvUp.y = currentPosition[1]
        }
        valueAnimator.duration = 2000
        valueAnimator.start()

    }

}