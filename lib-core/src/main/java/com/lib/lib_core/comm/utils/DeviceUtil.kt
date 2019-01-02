package com.lib.lib_core.comm.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.lib.lib_core.comm.permission.PermissionManager
import com.lib.lib_core.dao.SharedPreferencesProvider
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * 获取设备信息的工具类
 * Created by lis on 2018/12/13.
 */
object DeviceUtil {
	val CACHE_DEVICE_KEY = "CACHE_DEVICE_KEY"
	private var windows_size_height = 0
	private var windows_size_width = 0

	val deviceVersion: Int
		get() = Build.VERSION.SDK_INT

	//    private String intToIp(int i) {
	//        return (i & 0xFF) + "." +
	//                ((i >> 8) & 0xFF) + "." +
	//                ((i >> 16) & 0xFF) + "." +
	//                (i >> 24 & 0xFF);
	//    }


	val model: String
		get() = android.os.Build.MODEL

	/**
	 * 打开或者关闭输入法
	 *
	 * @param context
	 * @param focusView 焦点View
	 * @param hide      是否显示
	 * @author LiuYuHang
	 * @date 2014年9月28日
	 */
	fun hidKeyBoard(context: Context, focusView: View?, hide: Boolean) {
		if (focusView == null) {
			return
		}
		val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

		if (hide) {
			// 强制隐藏键盘
			imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
		} else {
			//            focusView.requestFocus();
			//            imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);

			if (focusView is EditText) {
				focusView.setSelection(0, focusView.length())
			}

			val handler = Handler(Looper.getMainLooper())
			handler.postDelayed({
				val inputManager = focusView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				inputManager?.showSoftInput(focusView, 0)
			}, 200)


		}
	}

	/**
	 * 获取运营商名字
	 */
	fun getOperatorName(context: Context): String {
		//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		val operator = getOperator(context)
		if (operator != null) {
			when (operator) {
				"46000", "46002" ->
					// operatorName="中国移动";
					return "中国移动"
			//                signalTextView.setText("中国移动");
			// Toast.makeText(this, "此卡属于(中国移动)",
			// Toast.LENGTH_SHORT).show();
				"46001" ->
					// operatorName="中国联通";
					//                signalTextView.setText("中国联通");
					return "中国联通"
			// Toast.makeText(this, "此卡属于(中国联通)",
			// Toast.LENGTH_SHORT).show();
				"46003" ->
					// operatorName="中国电信";
					//                signalTextView.setText("中国电信");
					return "中国电信"
			}// Toast.makeText(this, "此卡属于(中国电信)",
			// Toast.LENGTH_SHORT).show();
		}
		return "UnKnown"
	}

	/**
	 * 获取运营商code
	 *
	 * @return
	 */
	fun getOperator(context: Context): String? {
		val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
				?: return null
		return telephonyManager.simOperator

		//        String operator = telephonyManager.getSimOperator();
		//        if (operator != null) {
		//            if (operator.equals("46000") || operator.equals("46002")) {
		//                // operatorName="中国移动";
		//                return "中国移动";
		////                signalTextView.setText("中国移动");
		//                // Toast.makeText(this, "此卡属于(中国移动)",
		//                // Toast.LENGTH_SHORT).show();
		//            } else if (operator.equals("46001")) {
		//                // operatorName="中国联通";
		////                signalTextView.setText("中国联通");
		//                return "中国联通";
		//                // Toast.makeText(this, "此卡属于(中国联通)",
		//                // Toast.LENGTH_SHORT).show();
		//            } else if (operator.equals("46003")) {
		//                // operatorName="中国电信";
		////                signalTextView.setText("中国电信");
		//                return "中国电信";
		//                // Toast.makeText(this, "此卡属于(中国电信)",
		//                // Toast.LENGTH_SHORT).show();
		//            }
		//        }
		//        return "UnKnown";
	}


	/**
	 * 拨打电话
	 *
	 * @param context
	 * @param phoneNumber
	 */
//	fun call(context: Context, phoneNumber: String) {
//		val builder = DialogBuilder.buildAlertDialog(context, "", "是否拨打电话 $phoneNumber")
//
//		builder.setNegativeButton("取消", null)
//		builder.setPositiveButton("拨打", DialogInterface.OnClickListener { dialog, which ->
//			val intent = Intent(Intent.ACTION_DIAL)
//			val data = Uri.parse("tel:$phoneNumber")
//			intent.data = data
//			context.startActivity(intent)
//		})
//
//		builder.show()
//	}

	/**
	 * 获取状态栏高度
	 *
	 * @param activity
	 * @return > 0 success; <= 0 fail
	 */
	fun getStatusHeight(activity: Activity): Int {
		var statusHeight = 0
		val localRect = Rect()
		activity.window.decorView.getWindowVisibleDisplayFrame(localRect)
		statusHeight = localRect.top
		if (0 == statusHeight) {
			val localClass: Class<*>
			try {
				localClass = Class.forName("com.android.internal.R\$dimen")
				val localObject = localClass.newInstance()
				val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString())
				statusHeight = activity.resources.getDimensionPixelSize(i5)
			} catch (e: Exception) {
				e.printStackTrace()
			}

		}
		return statusHeight
	}

	/**
	 * 获取屏幕高度
	 *
	 * @param context
	 * @return
	 */
	fun getWindowHeight(context: Context): Int {
		if (windows_size_height == 0) {
			//            DisplayMetrics dm = new DisplayMetrics();
			//            getWindowManager(context).getDefaultDisplay().getMetrics(dm);
			windows_size_height = getDisplayMetrics(context).heightPixels
		}
		return windows_size_height
	}

	/**
	 * 获取屏幕宽度
	 *
	 * @param context
	 * @return
	 */
	fun getWindowWidth(context: Context): Int {
		if (windows_size_width == 0) {
			//            DisplayMetrics dm = new DisplayMetrics();
			//            getWindowManager(context).getDefaultDisplay().getMetrics(dm);
			windows_size_width = getDisplayMetrics(context).widthPixels
		}
		return windows_size_width
	}

	fun getDisplayMetrics(context: Context): DisplayMetrics {
		val dm = DisplayMetrics()
		getWindowManager(context).defaultDisplay.getMetrics(dm)
		return dm

	}

	fun getWindowManager(context: Context): WindowManager {
		return context.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
	}

	/**
	 * 获得状态栏高度
	 */
	fun getStateBarHeight(context: Context): Int {
		var c: Class<*>? = null
		var obj: Any? = null
		var field: java.lang.reflect.Field? = null
		var x = 0
		var statusBarHeight = 0
		try {
			c = Class.forName("com.android.internal.R\$dimen")
			obj = c!!.newInstance()
			field = c.getField("status_bar_height")
			x = Integer.parseInt(field!!.get(obj).toString())
			statusBarHeight = context.resources.getDimensionPixelSize(x)
			return statusBarHeight
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return statusBarHeight
	}

	/**
	 * 获得底部NavigationBar高度
	 */
	fun getNavigationBarHeight(context: Context): Int {
		var height = 0
		try {
			val resources = context.resources
			val resourcesId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
			height = resources.getDimensionPixelSize(resourcesId)
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return height
	}

	/**
	 * 通过class名找到activity完整路径
	 *
	 * @return Modifier： Modified Date： Modify：
	 */
	fun getActivityByClassName(context: Context, className: String): String? {
		try {
			val packageInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
			if (packageInfo.activities != null) {
				for (ai in packageInfo.activities) {
					if (ai.name.endsWith(className)) {
						return ai.name
					}
				}
			}
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
			return null
		}

		return null
	}

	/**
	 * 获取手机mac地址
	 *
	 * @param context
	 * @return
	 */

	fun getMac(context: Context): String {
		var macAddress = ""
		try {
			val wifiMgr = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
			val info = wifiMgr?.connectionInfo
			if (null != info) {
				if (!TextUtils.isEmpty(info.macAddress))
					macAddress = info.macAddress.replace(":", "")
			}
		} catch (e: Exception) {
			macAddress = "mac"
			e.printStackTrace()
		}

		return macAddress
	}

	fun getIMEI(context: Context): String? {
		if (PermissionManager.checkPermission(context, Manifest.permission.READ_PHONE_STATE)) {
			val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
			return tm.deviceId
		} else {
			return null
		}
	}

	fun getDeviceId(context: Context): String? {
		var deviceId = getIMEI(context)
		if (!TextUtils.isEmpty(deviceId)) {
			return deviceId
		}
		//        deviceId = getMac(context);
		//        if (!TextUtils.isEmpty(deviceId)) {
		//            return deviceId;
		//        }

		val provider = SharedPreferencesProvider().getProvider(context)
		deviceId = provider.getCache(CACHE_DEVICE_KEY)
		if (!TextUtils.isEmpty(deviceId)) {
			return deviceId
		} else {
			deviceId = "LE" + System.currentTimeMillis() + Random().nextInt(10)
			provider.putCache(CACHE_DEVICE_KEY, deviceId)
			return deviceId
		}
	}

	fun getIp(context: Context): String? {
		return getIPAddress(context)
	}

	fun getIPAddress(context: Context): String? {
		val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
		if (info != null && info.isConnected) {
			if (info.type == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
				try {
					//Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
					val en = NetworkInterface.getNetworkInterfaces()
					while (en.hasMoreElements()) {
						val intf = en.nextElement()
						val enumIpAddr = intf.inetAddresses
						while (enumIpAddr.hasMoreElements()) {
							val inetAddress = enumIpAddr.nextElement()
							if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
								return inetAddress.getHostAddress()
							}
						}
					}
				} catch (e: SocketException) {
					e.printStackTrace()
				}

			} else if (info.type == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
				val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
				val wifiInfo = wifiManager.connectionInfo
				val ip = wifiInfo.ipAddress
				//                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
				return (ip and 0xFF).toString() + "." +
						(ip shr 8 and 0xFF) + "." +
						(ip shr 16 and 0xFF) + "." +
						(ip shr 24 and 0xFF)
			}
		} else {
			//当前无网络连接,请在设置中打开网络
		}
		return null
	}

	fun getChannel(context: Context): String {
		return "1"
	}

	/**
	 * 获取channelid，会先从sd卡查找，如果找不到，会从配置文件中查找，并且保存到sd卡
	 *
	 * @param context
	 * @return
	 */
	fun getChannel(context: Context, path: String): String {
		//        String channelFilePath = FileUtil.getSdCardPath() + "/" + path;
		//        String channelFileName = "channel.txt";
		val channel: String

		//        if (FileUtil.isSdCardExist()) {
		//            channel = FileUtil.readByBufferReader(channelFilePath, channelFileName);
		//        }

		//        if (TextUtils.isEmpty(channel)) {

		channel = ChannelUtil.getChannel(context, "developer")!!

		//            FileUtil.writeByBufferWriter(channelFilePath, channelFileName, channel, false);
		//        }
		return channel
	}
}