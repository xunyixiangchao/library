package com.lib.lib_core.comm.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.support.v4.content.FileProvider
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import java.io.File
import java.io.UnsupportedEncodingException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
object AppUtils {

	private val TAG = AppUtils::class.java.simpleName

	/**
	 * SDK 版本号
	 *
	 * @return
	 */
	val sdkVersion: String
		get() {
			var sdk = Build.VERSION.SDK
			if (TextUtils.isEmpty(sdk)) {
				sdk = "sdk"
			}
			return sdk
		}

	/**
	 * android 版本号
	 *
	 * @return
	 */
	val systemVersion: String
		get() {
			var system = Build.VERSION.RELEASE
			if (TextUtils.isEmpty(system)) {
				system = "os_info"
			}
			return system
		}

	/**
	 * 手机版本号
	 *
	 * @return
	 */
	val modelVersion: String
		get() {
			var model = Build.MODEL
			if (TextUtils.isEmpty(model)) {
				model = "model"
			}
			return model
		}

	/**
	 * 获取产品名
	 *
	 * @return
	 */
	val product: String
		get() = "huijiayou_android"

	/**
	 * 操作系统
	 *
	 * @return
	 */
	val os: String
		get() = "android"

	val isFlym3: Boolean
		get() {
			if (Build.DEVICE == "mx2" || Build.DEVICE == "mx3") {
				return true
			} else if (Build.DEVICE == "mx" || Build.DEVICE == "m9") {
				return false
			}
			return false
		}

	val isMi3OrMi4OS: Boolean
		get() = "cancro" == Build.DEVICE

	/**
	 * 获取手机ip地址
	 *
	 * @return
	 */
	// if (!inetAddress.isLoopbackAddress() && inetAddress
	// instanceof Inet6Address) {
	val phoneIp: String
		get() {
			try {
				val en = NetworkInterface.getNetworkInterfaces()
				while (en.hasMoreElements()) {
					val intf = en.nextElement()
					val enumIpAddr = intf.inetAddresses
					while (enumIpAddr.hasMoreElements()) {
						val inetAddress = enumIpAddr.nextElement()
						if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
							return inetAddress.getHostAddress().toString()
						}
					}
				}
			} catch (e: Exception) {
			}

			return ""
		}

	/**
	 * 安装 App
	 *
	 * @param context
	 * @param apkPath
	 */
	fun installApplication(context: Context, apkPath: String) {
		context.startActivity(getInstallApplicationIntent(context, apkPath))
	}

	/**
	 * 获取安装app的intent
	 *
	 * @param apkPath
	 * @return
	 */
	fun getInstallApplicationIntent(context: Context, apkPath: String): Intent {
		val installIntent = Intent(Intent.ACTION_VIEW)

		if (Build.VERSION.SDK_INT >= 24) {
			val apkUri = FileProvider.getUriForFile(context, getAppPackage(context) + ".fileprovider", File(apkPath))
			installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
			installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive")
		} else {
			installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			installIntent.setDataAndType(Uri.parse("file://$apkPath"), "application/vnd.android.package-archive")
		}
		return installIntent
	}

	/**
	 * 方法描述：判断某一应用是否正在运行
	 * Created by cafeting on 2017/2/4.
	 *
	 * @param context     上下文
	 * @param packageName 应用的包名
	 * @return true 表示正在运行，false 表示没有运行
	 */
	fun isAppRunning(context: Context, packageName: String): Boolean {
		val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val list = am.getRunningTasks(100)
		if (list.size <= 0) {
			return false
		}
		for (info in list) {
			if (info.baseActivity.packageName == packageName) {
				return true
			}
		}
		return false
	}

	/**
	 * 获取已安装应用的 uid，-1 表示未安装此应用或程序异常
	 *
	 * @param context
	 * @param packageName
	 * @return
	 */
	fun getPackageUid(context: Context, packageName: String): Int {
		try {
			val applicationInfo = context.packageManager.getApplicationInfo(packageName, 0)
			if (applicationInfo != null) {
				return applicationInfo.uid
			}
		} catch (e: Exception) {
			return -1
		}

		return -1
	}

	/**
	 * 判断某一 uid 的程序是否有正在运行的进程，即是否存活
	 * Created by cafeting on 2017/2/4.
	 *
	 * @param context 上下文
	 * @param uid     已安装应用的 uid
	 * @return true 表示正在运行，false 表示没有运行
	 */
	fun isProcessRunning(context: Context, uid: Int): Boolean {
		val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val runningServiceInfos = am.getRunningServices(200)
		if (runningServiceInfos.size > 0) {
			for (appProcess in runningServiceInfos) {
				if (uid == appProcess.uid) {
					return true
				}
			}
		}
		return false
	}

	@SuppressLint("MissingPermission")
			/**
	 * 获取设备唯一标识（稍微加过密）
	 *
	 * @param context
	 * @return
	 */
	fun getDeviceUuidFactory(context: Context): String {
		val PREFS_FILE = "device_id.xml"
		val PREFS_DEVICE_ID = "device_id"
		var deviceUuid: UUID? = null
		var uniqueId = ""
		if (deviceUuid == null) {
			synchronized(AppUtils::class.java) {
				if (deviceUuid == null) {
					val prefs = context.getSharedPreferences(PREFS_FILE, 0)
					val id = prefs.getString(PREFS_DEVICE_ID, null)
					if (id != null) {
						uniqueId = id

					} else {
						val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
						var deviceId: String
						try {
							if ("9774d56d682e549c" != androidId) {
								deviceUuid = UUID.nameUUIDFromBytes(androidId.toByteArray(charset("utf-8")))
							} else {
								deviceId = (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).deviceId
								if (TextUtils.isEmpty(deviceId)) {
									deviceId = DeviceUtil.getMac(context)
								}
								val tmSerial = "" + (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simSerialNumber
								deviceUuid = UUID(androidId.hashCode().toLong(), deviceId.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())
							}
							uniqueId = deviceUuid!!.toString()
						} catch (e: UnsupportedEncodingException) {
							throw RuntimeException(e)
						}

					}
					prefs.edit().putString(PREFS_DEVICE_ID, uniqueId).commit()
				}
			}
		}
		return uniqueId

	}

	fun quitApp() {
		System.exit(0)
	}

	/**
	 * App版本号
	 *
	 * @return
	 */
	fun getAppVersion(context: Context): String {
		var version: String
		val pm = context.packageManager
		try {
			val packInfo = pm.getPackageInfo(context.packageName, 0)
			version = packInfo.versionName
		} catch (e: PackageManager.NameNotFoundException) {
			version = "version"
			e.printStackTrace()
		}

		return version
	}

	/**
	 * 获取app的versionCode
	 *
	 * @param context
	 * @return
	 */
	fun getAppVersionCode(context: Context): Int {
		try {
			val pm = context.packageManager

			val pinfo = pm.getPackageInfo(context.packageName, PackageManager.GET_CONFIGURATIONS)
			val versionName = pinfo.versionName

			return pinfo.versionCode
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}

		return 0
	}

	fun getAppPackage(context: Context): String {
		return context.packageName
		//        try {
		//            String pkName = this.getPackageName();
		//            String versionName = this.getPackageManager().getPackageInfo(
		//                    pkName, 0).versionName;
		//            int versionCode = this.getPackageManager()
		//                    .getPackageInfo(pkName, 0).versionCode;
		//            return pkName + "   " + versionName + "  " + versionCode;
		//        } catch (Exception e) {
		//        }
		//        return null;
	}

	fun getAndroidId(context: Context): String {
		return Settings.Secure.getString(context.contentResolver, "android_id")
	}

	/**
	 * 获取屏幕宽高信息
	 *
	 * @param activity
	 * @return DisplayMetrics
	 */
	fun getScreenDisplay(activity: Activity): DisplayMetrics {
		val dm = DisplayMetrics()
		activity.windowManager.defaultDisplay.getMetrics(dm)
		return dm
	}

	/**
	 * 获取屏幕宽高信息
	 *
	 * @param context
	 * @return DisplayMetrics
	 */
	fun getScreenDisplay(context: Context): DisplayMetrics {

		return context.resources.displayMetrics
	}

	/**
	 * 获取屏幕尺寸
	 *
	 * @return
	 */
	fun getScreen(activity: Activity): String {
		val dm = getScreenDisplay(activity)
		activity.windowManager.defaultDisplay.getMetrics(dm)
		var screen = dm.widthPixels.toString() + "x" + dm.heightPixels
		if (TextUtils.isEmpty(screen)) {
			screen = "screen"
		}
		return screen
	}

	@SuppressLint("MissingPermission")
			/**
	 * 手机运营商
	 *
	 * @return
	 */
	fun getOp(context: Context): String? {
		val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
		var providersName: String? = null
		val IMSI = tm.subscriberId
		try {
			// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
			if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
				providersName = "中国移动"
			} else if (IMSI.startsWith("46001")) {
				providersName = "中国联通"
			} else if (IMSI.startsWith("46003")) {
				providersName = "中国电信"
			} else {
				providersName = "op"
			}

		} catch (e: Exception) {
			e.printStackTrace()
		}

		if (TextUtils.isEmpty(providersName)) {
			providersName = "op"
		}
		return providersName
	}

	@SuppressLint("MissingPermission")
			/**
	 * 获取手机号(一般拿不到)
	 *
	 * @param context
	 * @return
	 */
	fun getNativePhoneNumber(context: Context): String? {
		val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
		var NativePhoneNumber: String? = null
		NativePhoneNumber = tm.line1Number
		return NativePhoneNumber
	}

	/**
	 * 获取网络状态
	 *
	 * @return
	 */
	fun getNetType(context: Context): String {
		val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo = connMgr.activeNetworkInfo ?: return "net_type"
		val nType = networkInfo.type
		return if (nType == ConnectivityManager.TYPE_WIFI) {

			"wifi"

		} else {
			"cellular"
		}
	}

	/**
	 * 魅族的SmartBar
	 *
	 * @return
	 */
	fun hasSmartBar(): Boolean {
		try {
			// 新型号可用反射调用Build.hasSmartBar()
			val method = Class.forName("android.os.Build").getMethod("hasSmartBar")
			return (method.invoke(null) as Boolean)
		} catch (e: Exception) {
		}

		// 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
		if (Build.DEVICE == "mx2") {
			return true
		} else if (Build.DEVICE == "mx" || Build.DEVICE == "m9") {
			return false
		}

		return false
	}

	fun setConfigValue(key: String, value: String, context: Context) {
		val preferences = context.getSharedPreferences("setting_config", Context.MODE_PRIVATE)
		preferences.edit().putString(key, value).commit()
	}

	fun getConfigValue(key: String, context: Context): String {
		val preferences = context.getSharedPreferences("setting_config", Context.MODE_PRIVATE)
		return preferences.getString(key, "")
	}

	fun isAppExist(context: Context): Boolean {
		val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val runningTasks = am.getRunningTasks(20)
		var numActivities = 0
		for (runningTaskInfo in runningTasks) {

			if (runningTaskInfo.topActivity.packageName.contains("com.jiayou.qianheshengyun.app")) {
				LogUtils.i(TAG, "发现栈中有本应用activity")
				numActivities += runningTaskInfo.numActivities
			}
			LogUtils.i(TAG, "runningTaskInfo.topActivity.getClassName()：：" + runningTaskInfo.topActivity.className)
		}
		LogUtils.i(TAG, "numActivities：：$numActivities")
		return numActivities > 1
	}

	/**
	 * 屏幕是否是亮的
	 */
	fun isScreenOn(context: Context): Boolean {
		val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
		return pm.isScreenOn
	}

	fun isAppOnForeground(context: Context): Boolean {
		// Returns a list of application processes that are running on the
		// device

		val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val packageName = context.packageName

		val appProcesses = activityManager.runningAppProcesses ?: return false

		for (appProcess in appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName == packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true
			}
		}
		return false
	}

	fun isBackground(context: Context): Boolean {
		val activityManager = context
				.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val appProcesses = activityManager
				.runningAppProcesses
		for (appProcess in appProcesses) {
			if (appProcess.processName == context.packageName) {
				/*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
				Log.i(context.packageName, "此appimportace ="
						+ appProcess.importance
						+ ",context.getClass().getName()="
						+ context.javaClass.name)
				if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i(context.packageName, "处于后台" + appProcess.processName)
					return true
				} else {
					Log.i(context.packageName, "处于前台" + appProcess.processName)
					return false
				}
			}
		}
		return false
	}

	internal fun isTopActivity(activity: Activity, packageName: String): Boolean {
		//        String packageName = "xxxxx";
		val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val tasksInfo = activityManager.getRunningTasks(1)
		if (tasksInfo.size > 0) {
			println("---------------包名-----------" + tasksInfo[0].topActivity.packageName)
			//应用程序位于堆栈的顶层
			if (packageName == tasksInfo[0].topActivity.packageName) {
				return true
			}
		}
		return false
	}

	//    /**
	//     * 该应用是否正在运行///会有版本问题
	//     */
	//    @Deprecated
	//    public static boolean isTopApp(Context context, String packageName) {
	//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	//        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	//        if (!tasks.isEmpty()) {
	//            ComponentName topActivity = tasks.get(0).topActivity;
	//            if (topActivity.getPackageName().equals(packageName)) {
	//                return true;
	//            }
	//        }
	//        return false;
	//    }

	/**
	 * 判断最顶层的 Activity是不是需要的
	 */
	fun isTopActivity(context: Context, className: String): Boolean {
		val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val tasks = am.getRunningTasks(1)
		if (!tasks.isEmpty()) {
			val topActivity = tasks[0].topActivity
			LogUtils.i(TAG, "topActivity=" + topActivity.className)
			LogUtils.i(TAG, "className=$className")
			if (topActivity.className == className) {
				return true
			}
		}
		return false
	}

	/**
	 * 判断应用前台应用中是否包含顶部activity是否是 className
	 *
	 * @return
	 */
	fun isTopActivityInProscenium(context: Context, className: String): Boolean {
		LogUtils.i(TAG, "className=$className")
		val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val tasks = am.getRunningTasks(2)
		for (runningTaskInfo in tasks) {
			LogUtils.i(TAG, "runningTaskInfo=" + runningTaskInfo.topActivity.className)
			if (runningTaskInfo.topActivity.className == className) {
				return true
			}
		}
		return false
	}

	// 判断是否安装某个客户端
	fun AHAappInstalledOrNot(context: Context, packageName: String): Boolean {
		val pm = context.packageManager
		var app_installed = false
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
			app_installed = true
		} catch (e: PackageManager.NameNotFoundException) {
			app_installed = false
		}

		return app_installed
	}

	fun isFirstRun(context: Context): Boolean {
		var isFirstRun = false
		val isFirstRunStr = context.getSharedPreferences("configure", Context.MODE_PRIVATE).getString("apkVersion", "")
		isFirstRun = getAppVersion(context) != isFirstRunStr
		context.getSharedPreferences("configure", Context.MODE_PRIVATE).edit().putString("apkVersion", getAppVersion(context)).commit()
		return isFirstRun
	}

	fun isExistGuidePage(context: Context, currentGuideVersion: String): Boolean {
		val pageUrl = "yindaoye01_" + currentGuideVersion.replace(".", "_")
		return context.resources.getIdentifier(pageUrl, "drawable", context.applicationInfo.packageName) != 0
	}

	fun isFirstRunCurrentVersionGuidePage(context: Context): Boolean {
		val lastGuideVersion = context.getSharedPreferences("configure", Context.MODE_PRIVATE).getString("apkVersion", "")
		val currentGuideVersion = getAppVersion(context)
		if (currentGuideVersion == lastGuideVersion) {
			return false
		}
		if (isExistGuidePage(context, currentGuideVersion)) {
			context.getSharedPreferences("configure", Context.MODE_PRIVATE).edit().putString("apkVersion", currentGuideVersion).apply()
			return true
		}
		context.getSharedPreferences("configure", Context.MODE_PRIVATE).edit().putString("apkVersion", lastGuideVersion).apply()
		return false
	}

	/**
	 * 获取当前context的进程
	 *
	 * @param context
	 * @return
	 */
	fun getCurProcessName(context: Context): String {
		val pid = android.os.Process.myPid()
		val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		for (appProcess in activityManager.runningAppProcesses) {
			if (appProcess.pid == pid) {
				return appProcess.processName
			}
		}
		return ""
	}
}
