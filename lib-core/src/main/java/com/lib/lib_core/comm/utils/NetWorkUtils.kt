package com.lib.lib_core.comm.utils

import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils

/**
 * 与网络相关
 * Created by lis on 2018/12/13.
 */
object NetWorkUtils {

	/**
	 * 获取当前网络类型
	 *
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */

	val NETTYPE_WIFI = 0x01
	val NETTYPE_CMWAP = 0x02
	val NETTYPE_CMNET = 0x03

	/**
	 * 检测网络是否可用
	 *
	 * @return
	 */
	fun isNetworkConnected(context: Context): Boolean {
		val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val ni = cm.activeNetworkInfo
		return ni != null && ni.isConnectedOrConnecting
	}

	fun getNetWorkTypeString(context: Context): String {
		val type = getNetworkType(context)

		when (type) {
			0 -> return "NONE"
			NETTYPE_WIFI -> return "WIFI"
			NETTYPE_CMWAP -> return "CMWAP"
			NETTYPE_CMNET -> return "CMNET"
			else -> return "UNKOWN"
		}

	}

	fun getNetworkType(context: Context): Int {
		var netType = 0
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networkInfo = connectivityManager.activeNetworkInfo ?: return netType
		val nType = networkInfo.type
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			val extraInfo = networkInfo.extraInfo
			if (!TextUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase() == "cmnet") {
					netType = NETTYPE_CMNET
				} else {
					netType = NETTYPE_CMWAP
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI
		}
		return netType
	}

}
