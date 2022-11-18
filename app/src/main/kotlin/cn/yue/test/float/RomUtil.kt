package cn.yue.test.float

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


/**
 * Description :
 * Created by yue on 2022/7/15
 */

object RomUtil {
    private const val TAG = "RomUtil"
    private const val ROM_MIUI = "MIUI"
    private const val ROM_EMUI = "EMUI"
    private const val ROM_FLYME = "FLYME"
    private const val ROM_OPPO = "OPPO"
    private const val ROM_SMARTISAN = "SMARTISAN"
    private const val ROM_VIVO = "VIVO"
    private const val ROM_QIKU = "QIKU"
    const val ROM_LENOVO = "LENOVO"
    const val ROM_SAMSUNG = "SAMSUNG"
    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private const val KEY_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"
    private const val KEY_VERSION_GIONEE = "ro.gn.sv.version"
    private const val KEY_VERSION_LENOVO = "ro.lenovo.lvp.version"
    private const val KEY_VERSION_FLYME = "ro.build.display.id"
    private const val KEY_EMUI_VERSION_CODE = "ro.build.hw_emui_api_level"
    private const val KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code"
    private const val KEY_MIUI_HANDY_MODE_SF = "ro.miui.has_handy_mode_sf"
    private const val KEY_MIUI_REAL_BLUR = "ro.miui.has_real_blur"
    private const val KEY_FLYME_PUBLISHED = "ro.flyme.published"
    private const val KEY_FLYME_FLYME = "ro.meizu.setupwizard.flyme"
    private const val KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon"
    private const val KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme"
    private const val KEY_FLYME_PUBLISH_FALG = "ro.flyme.published"
    private const val KEY_VIVO_OS_NAME = "ro.vivo.os.name"
    private const val KEY_VIVO_OS_VERSION = "ro.vivo.os.version"
    private const val KEY_VIVO_ROM_VERSION = "ro.vivo.rom.version"
    fun isEmui(): Boolean {
        return check(ROM_EMUI)
    }

    fun isMiui(): Boolean {
        return check(ROM_MIUI)
    }

    fun isVivo(): Boolean {
        return check(ROM_VIVO)
    }

    fun isOppo(): Boolean {
        return check(ROM_OPPO)
    }

    fun isFlyme(): Boolean {
        return check(ROM_FLYME)
    }

    fun isQiku(): Boolean {
        return check(ROM_QIKU) || check("360")
    }

    fun isSmartisan(): Boolean {
        return check(ROM_SMARTISAN)
    }

    private var sName: String? = null
    fun getName(): String? {
        if (sName == null) {
            check("")
        }
        return sName
    }

    private var sVersion: String? = null
    fun getVersion(): String? {
        if (sVersion == null) {
            check("")
        }
        return sVersion
    }

    fun check(rom: String): Boolean {
        if (sName != null) {
            return sName == rom
        }
        if (!TextUtils.isEmpty(getProp(KEY_VERSION_MIUI).also {
                sVersion = it
            })) {
            sName = ROM_MIUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_EMUI).also {
                sVersion = it
            })) {
            sName = ROM_EMUI
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_OPPO).also {
                sVersion = it
            })) {
            sName = ROM_OPPO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_VIVO).also {
                sVersion = it
            })) {
            sName = ROM_VIVO
        } else if (!TextUtils.isEmpty(getProp(KEY_VERSION_SMARTISAN).also {
                sVersion = it
            })) {
            sName = ROM_SMARTISAN
        } else {
            sVersion = Build.DISPLAY
            if (sVersion!!.toUpperCase().contains(ROM_FLYME)) {
                sName = ROM_FLYME
            } else {
                sVersion = Build.UNKNOWN
                sName = Build.MANUFACTURER.toUpperCase()
            }
        }
        return sName == rom
    }

    fun getProp(name: String): String? {
        var line: String? = null
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $name")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read prop $name", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return line
    }
}