package cn.yue.base.common.utils.device

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.util.Xml
import cn.yue.base.common.utils.Utils
import cn.yue.base.common.utils.Utils.getContext
import cn.yue.base.common.utils.app.IntentUtils
import cn.yue.base.common.utils.constant.StringUtils
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * 介绍：手机相关工具类
 * 作者：luobiao
 * 邮箱：luobiao@imcoming.cn
 * 时间：2017/2/23.
 */
object PhoneUtils {

    /**
     * 判断设备是否是手机
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    val isPhone: Boolean
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm != null && tm.phoneType != TelephonyManager.PHONE_TYPE_NONE
        }

    /**
     * 获取IMEI码
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
     *
     * @return IMEI码
     */
    @JvmStatic
    val imei: String?
        @SuppressLint("HardwareIds", "MissingPermission")
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm?.deviceId
        }

    /**
     * 获取IMSI码
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
     *
     * @return IMSI码
     */
    @JvmStatic
    val imsi: String?
        @SuppressLint("HardwareIds", "MissingPermission")
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm?.subscriberId
        }

    /**
     * 获取移动终端类型
     *
     * @return 手机制式
     *
     *  * [TelephonyManager.PHONE_TYPE_NONE] : 0 手机制式未知
     *  * [TelephonyManager.PHONE_TYPE_GSM] : 1 手机制式为GSM，移动和联通
     *  * [TelephonyManager.PHONE_TYPE_CDMA] : 2 手机制式为CDMA，电信
     *  * [TelephonyManager.PHONE_TYPE_SIP] : 3
     *
     */
    @JvmStatic
    val phoneType: Int
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm?.phoneType ?: -1
        }

    /**
     * 判断sim卡是否准备好
     *
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic
    val isSimCardReady: Boolean
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm != null && tm.simState == TelephonyManager.SIM_STATE_READY
        }

    /**
     * 获取Sim卡运营商名称
     *
     * 中国移动、如中国联通、中国电信
     *
     * @return sim卡运营商名称
     */
    @JvmStatic
    val simOperatorName: String?
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm?.simOperatorName
        }

    /**
     * 获取Sim卡运营商名称
     *
     * 中国移动、如中国联通、中国电信
     *
     * @return 移动网络运营商名称
     */
    @JvmStatic
    val simOperatorByMnc: String?
        get() {
            val tm = Utils.getContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val operator = tm?.simOperator ?: return null
            when (operator) {
                "46000", "46002", "46007" -> return "中国移动"
                "46001" -> return "中国联通"
                "46003" -> return "中国电信"
                else -> return operator
            }
        }

    /**
     * 获取手机状态信息
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_PHONE_STATE"/>`
     *
     * @return DeviceId(IMEI) = 99000311726612<br></br>
     * DeviceSoftwareVersion = 00<br></br>
     * Line1Number =<br></br>
     * NetworkCountryIso = cn<br></br>
     * NetworkOperator = 46003<br></br>
     * NetworkOperatorName = 中国电信<br></br>
     * NetworkType = 6<br></br>
     * honeType = 2<br></br>
     * SimCountryIso = cn<br></br>
     * SimOperator = 46003<br></br>
     * SimOperatorName = 中国电信<br></br>
     * SimSerialNumber = 89860315045710604022<br></br>
     * SimState = 5<br></br>
     * SubscriberId(IMSI) = 460030419724900<br></br>
     * VoiceMailNumber = *86<br></br>
     */
    @JvmStatic
    val phoneStatus: String
        @SuppressLint("MissingPermission")
        get() {
            val tm = Utils.getContext()
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            var str = ""
            str += "DeviceId(IMEI) = " + tm.deviceId + "\n"
            str += "DeviceSoftwareVersion = " + tm.deviceSoftwareVersion + "\n"
            str += "Line1Number = " + tm.line1Number + "\n"
            str += "NetworkCountryIso = " + tm.networkCountryIso + "\n"
            str += "NetworkOperator = " + tm.networkOperator + "\n"
            str += "NetworkOperatorName = " + tm.networkOperatorName + "\n"
            str += "NetworkType = " + tm.networkType + "\n"
            str += "PhoneType = " + tm.phoneType + "\n"
            str += "SimCountryIso = " + tm.simCountryIso + "\n"
            str += "SimOperator = " + tm.simOperator + "\n"
            str += "SimOperatorName = " + tm.simOperatorName + "\n"
            str += "SimSerialNumber = " + tm.simSerialNumber + "\n"
            str += "SimState = " + tm.simState + "\n"
            str += "SubscriberId(IMSI) = " + tm.subscriberId + "\n"
            str += "VoiceMailNumber = " + tm.voiceMailNumber + "\n"
            return str
        }

    /**
     * 跳至拨号界面
     *
     * @param phoneNumber 电话号码
     */
    @JvmStatic
    fun dial(phoneNumber: String) {
        Utils.getContext().startActivity(IntentUtils.getDialIntent(phoneNumber))
    }

    /**
     * 拨打电话
     *
     * 需添加权限 `<uses-permission android:name="android.permission.CALL_PHONE"/>`
     *
     * @param phoneNumber 电话号码
     */
    @JvmStatic
    fun call(phoneNumber: String) {
        Utils.getContext().startActivity(IntentUtils.getCallIntent(phoneNumber))
    }

    /**
     * 跳至发送短信界面
     *
     * @param phoneNumber 接收号码
     * @param content     短信内容
     */
    @JvmStatic
    fun sendSms(phoneNumber: String, content: String) {
        Utils.getContext().startActivity(IntentUtils.getSendSmsIntent(phoneNumber, content))
    }

    /**
     * 发送短信
     *
     * 需添加权限 `<uses-permission android:name="android.permission.SEND_SMS"/>`
     *
     * @param phoneNumber 接收号码
     * @param content     短信内容
     */
    @JvmStatic
    fun sendSmsSilent(phoneNumber: String, content: String) {
        if (StringUtils.isEmpty(content)) return
        val sentIntent = PendingIntent.getBroadcast(Utils.getContext(), 0, Intent(), 0)
        val smsManager = SmsManager.getDefault()
        if (content.length >= 70) {
            val ms = smsManager.divideMessage(content)
            for (str in ms) {
                smsManager.sendTextMessage(phoneNumber, null, str, sentIntent, null)
            }
        } else {
            smsManager.sendTextMessage(phoneNumber, null, content, sentIntent, null)
        }
    }

    /**
     * 获取手机联系人
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>`
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_CONTACTS"/>`
     *
     * @return 联系人链表
     */
    // 1.获取内容解析者
    // 2.获取内容提供者的地址:com.android.contacts
    // raw_contacts表的地址 :raw_contacts
    // view_data表的地址 : data
    // 3.生成查询地址
    // 4.查询操作,先查询raw_contacts,查询contact_id
    // projection : 查询的字段
    // 5.解析cursor
    // 6.获取查询的数据
    // cursor.getString(cursor.getColumnIndex("contact_id"));//getColumnIndex
    // : 查询字段在cursor中索引值,一般都是用在查询字段比较多的时候
    // 判断contact_id是否为空
    //null   ""
    // 7.根据contact_id查询view_data表中的数据
    // selection : 查询条件
    // selectionArgs :查询条件的参数
    // sortOrder : 排序
    // 空指针: 1.null.方法 2.参数为null
    // 8.解析c
    // 9.获取数据
    // 10.根据类型去判断获取的data1数据并保存
    // 电话
    // 姓名
    // 11.添加到集合中数据
    // 12.关闭cursor
    // 12.关闭cursor
    @JvmStatic
    val allContactInfo: List<HashMap<String, String>>
        get() {
            SystemClock.sleep(3000)
            val list = ArrayList<HashMap<String, String>>()
            val resolver = Utils.getContext().contentResolver
            val raw_uri = Uri.parse("content://com.android.contacts/raw_contacts")
            val date_uri = Uri.parse("content://com.android.contacts/data")
            val cursor = resolver.query(raw_uri, arrayOf("contact_id"), null, null, null)
            try {
                while (cursor!!.moveToNext()) {
                    val contact_id = cursor.getString(0)
                    if (!StringUtils.isEmpty(contact_id)) {
                        val c = resolver.query(date_uri, arrayOf("data1", "mimetype"), "raw_contact_id=?",
                                arrayOf(contact_id), null)
                        val map = HashMap<String, String>()
                        while (c!!.moveToNext()) {
                            val data1 = c.getString(0)
                            val mimetype = c.getString(1)
                            if (mimetype == "vnd.android.cursor.item/phone_v2") {
                                map["phone"] = data1
                            } else if (mimetype == "vnd.android.cursor.item/name") {
                                map["name"] = data1
                            }
                        }
                        list.add(map)
                        c.close()
                    }
                }
            } finally {
                cursor!!.close()
            }
            return list
        }

    /**
     * 打开手机联系人界面点击联系人后便获取该号码
     *
     * 参照以下注释代码
     */
    @JvmStatic
    fun getContactNum() {
        Log.d("tips", "U should copy the following code.")
        /*
    Intent intent = new Intent();
    intent.setAction("android.intent.action.PICK");
    intent.setType("vnd.android.cursor.dir/phone_v2");
    startActivityForResult(intent, 0);

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri uri = data.getData();
            String num = null;
            // 创建内容解析者
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                num = cursor.getString(cursor.getColumnIndex("data1"));
            }
            cursor.close();
            num = num.replaceAll("-", "");//替换的操作,555-6 -> 5556
        }
    }
    */
    }

    /**
     * 获取手机短信并保存到xml中
     *
     * 需添加权限 `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>`
     *
     * 需添加权限 `<uses-permission android:name="android.permission.READ_SMS"/>`
     */
    @JvmStatic
    fun getAllSMS() {
        // 1.获取短信
        // 1.1获取内容解析者
        val resolver = Utils.getContext().contentResolver
        // 1.2获取内容提供者地址   sms,sms表的地址:null  不写
        // 1.3获取查询路径
        val uri = Uri.parse("content://sms")
        // 1.4.查询操作
        // projection : 查询的字段
        // selection : 查询的条件
        // selectionArgs : 查询条件的参数
        // sortOrder : 排序
        val cursor = resolver.query(uri, arrayOf("address", "date", "type", "body"), null, null, null)
        // 设置最大进度
        val count = cursor!!.count//获取短信的个数
        // 2.备份短信
        // 2.1获取xml序列器
        val xmlSerializer = Xml.newSerializer()
        try {
            // 2.2设置xml文件保存的路径
            // os : 保存的位置
            // encoding : 编码格式
            xmlSerializer.setOutput(FileOutputStream(File("/mnt/sdcard/backupsms.xml")), "utf-8")
            // 2.3设置头信息
            // standalone : 是否独立保存
            xmlSerializer.startDocument("utf-8", true)
            // 2.4设置根标签
            xmlSerializer.startTag(null, "smss")
            // 1.5.解析cursor
            while (cursor.moveToNext()) {
                SystemClock.sleep(1000)
                // 2.5设置短信的标签
                xmlSerializer.startTag(null, "sms")
                // 2.6设置文本内容的标签
                xmlSerializer.startTag(null, "address")
                val address = cursor.getString(0)
                // 2.7设置文本内容
                xmlSerializer.text(address)
                xmlSerializer.endTag(null, "address")
                xmlSerializer.startTag(null, "date")
                val date = cursor.getString(1)
                xmlSerializer.text(date)
                xmlSerializer.endTag(null, "date")
                xmlSerializer.startTag(null, "type")
                val type = cursor.getString(2)
                xmlSerializer.text(type)
                xmlSerializer.endTag(null, "type")
                xmlSerializer.startTag(null, "body")
                val body = cursor.getString(3)
                xmlSerializer.text(body)
                xmlSerializer.endTag(null, "body")
                xmlSerializer.endTag(null, "sms")
                println("address:$address   date:$date  type:$type  body:$body")
            }
            xmlSerializer.endTag(null, "smss")
            xmlSerializer.endDocument()
            // 2.8将数据刷新到文件中
            xmlSerializer.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getAndroidId(): String? {
        return Settings.System.getString(getContext().contentResolver, Settings.Secure.ANDROID_ID)
    }
}