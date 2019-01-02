package com.lib.lib_core.comm.utils

import android.content.Context
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import android.text.TextUtils
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by lis on 2018/12/13.
 */
object ChannelUtil {

	private val CHANNEL_KEY = "cztchannel"
	private val CHANNEL_VERSION_KEY = "cztchannel_version"
	private var mChannel: String? = null
	/**
	 * 返回市场。  如果获取失败返回defaultChannel
	 * @param context
	 * @param defaultChannel
	 * @return
	 */
	@JvmOverloads
	fun getChannel(context: Context, defaultChannel: String = ""): String? {
		//内存中获取
		if (!TextUtils.isEmpty(mChannel)) {
			return mChannel
		}
		//sp中获取
		mChannel = getChannelBySharedPreferences(context)
		if (!TextUtils.isEmpty(mChannel)) {
			return mChannel
		}
		//从apk中获取
		mChannel = getChannelFromApk(context, CHANNEL_KEY)
		if (!TextUtils.isEmpty(mChannel)) {
			//保存sp中备用
			saveChannelBySharedPreferences(context, mChannel!!)
			return mChannel
		}
		//全部获取失败
		return defaultChannel
	}

	/**
	 * 从apk中获取版本信息
	 * @param context
	 * @param channelKey
	 * @return
	 */
	fun getChannelFromApk(context: Context, channelKey: String): String {
		//从apk包中获取
		val appinfo = context.applicationInfo
		val sourceDir = appinfo.sourceDir
		//默认放在meta-inf/里， 所以需要再拼接一下
		val key = "META-INF/$channelKey"
		var ret = ""
		var zipfile: ZipFile? = null
		try {
			zipfile = ZipFile(sourceDir)
			val entries = zipfile.entries()
			while (entries.hasMoreElements()) {
				val entry = entries.nextElement() as ZipEntry
				val entryName = entry.name
				if (entryName.startsWith(key)) {
					ret = entryName
					break
				}
			}
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}
		val split = ret.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		var channel = ""
		if (split != null && split.size >= 2) {
			channel = ret.substring(split[0].length + 1)
		}
		return channel
	}

	/**
	 * 本地保存channel & 对应版本号
	 * @param context
	 * @param channel
	 */
	private fun saveChannelBySharedPreferences(context: Context, channel: String) {
		val sp = PreferenceManager.getDefaultSharedPreferences(context)
		val editor = sp.edit()
		editor.putString(CHANNEL_KEY, channel)
		editor.putInt(CHANNEL_VERSION_KEY, getVersionCode(context))
		editor.commit()
	}

	/**
	 * 从sp中获取channel
	 * @param context
	 * @return 为空表示获取异常、sp中的值已经失效、sp中没有此值
	 */
	private fun getChannelBySharedPreferences(context: Context): String {
		val sp = PreferenceManager.getDefaultSharedPreferences(context)
		val currentVersionCode = getVersionCode(context)
		if (currentVersionCode == -1) {
			//获取错误
			return ""
		}
		val versionCodeSaved = sp.getInt(CHANNEL_VERSION_KEY, -1)
		if (versionCodeSaved == -1) {
			//本地没有存储的channel对应的版本号
			//第一次使用  或者 原先存储版本号异常
			return ""
		}
		return if (currentVersionCode != versionCodeSaved) {
			""
		} else sp.getString(CHANNEL_KEY, "")
	}

	/**
	 * 从包信息中获取版本号
	 * @param context
	 * @return
	 */
	private fun getVersionCode(context: Context): Int {
		try {
			return context.packageManager.getPackageInfo(context.packageName, 0).versionCode
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
		}

		return -1
	}
}
/**
 * 返回市场。  如果获取失败返回""
 * @param context
 * @return
 */