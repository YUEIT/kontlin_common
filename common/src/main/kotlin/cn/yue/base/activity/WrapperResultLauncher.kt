package cn.yue.base.activity

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import cn.yue.base.router.FRouter
import cn.yue.base.router.RouterCard

/**
 * Description :
 * Created by yue on 2023/7/21
 */
class WrapperResultLauncher(
	val context: Any,
	val launcher: ActivityResultLauncher<Intent>
)

fun WrapperResultLauncher.launch(
	path: String,
	toActivity: String? = null,
	block: ((routerCard: RouterCard) -> Unit)? = null
) {
	val route = FRouter.instance.build(path)
	block?.invoke(route)
	route.navigation(this, 0, toActivity)
}