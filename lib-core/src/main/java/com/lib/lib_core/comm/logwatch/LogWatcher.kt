package com.lib.lib_core.comm.logwatch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.lib.lib_core.comm.logwatch.db.DAO

/**
 * Created by lis on 2018/12/13.
 */
class LogWatcher {
	private var windowManager: MyWindowManager? = null
	private var dao: DAO? = null
	private var context: Context? = null

	@JvmOverloads
	fun init(context: Context, title: String = getApplicationName(context)) {
		this.context = context
		if (windowManager == null) {
			windowManager = MyWindowManager.getInstance(context)
		}

		if (!windowManager!!.isShow) {

			//权限判断
			if (Build.VERSION.SDK_INT >= 23) {
				if (!Settings.canDrawOverlays(context.applicationContext)) {
					//启动Activity让用户授权
					val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)

					if (context !is Activity) {
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					}
					context.startActivity(intent)
				} else {
					//执行6.0以上绘制代码
					windowManager!!.show(title)// 如果需要修改悬浮框名称，则直接添加参数
				}
			} else {
				//执行6.0以下绘制代码
				windowManager!!.show(title)// 如果需要修改悬浮框名称，则直接添加参数
			}
			// finish();
		}
	}

	fun show() {
		if (null != windowManager) {
			windowManager!!.show()
		}
	}

	fun dismiss() {
		if (null != windowManager) {
			windowManager!!.dismissFloat()
		}
	}

	fun getApplicationName(mContext: Context): String {
		var packageManager: PackageManager? = null
		var applicationInfo: ApplicationInfo?
		try {
			packageManager = mContext.applicationContext.packageManager
			applicationInfo = packageManager!!.getApplicationInfo(mContext.packageName, 0)
		} catch (e: PackageManager.NameNotFoundException) {
			applicationInfo = null
		}

		return packageManager!!.getApplicationLabel(applicationInfo) as String
	}

	fun putRequestInfo(request: String) {
		putMessage("请求", request)
	}

	fun putMessage(message: String) {
		putMessage("消息", message)
	}

	/**
	 * 放入一组message
	 *
	 * @param message
	 */
	fun putMessage(vararg message: String) {
		val sb = StringBuffer()
		for (i in message.indices) {
			sb.append(message[i] + "\n")
		}
		putMessage(sb.toString())
	}

	/**
	 * 添加带有target标签的消息
	 *
	 * @param tag
	 * @param message
	 */
	fun putMessage(tag: String, message: String) {
		if (context == null)
			return

		if (dao == null) {
			dao = DAO(context!!)
		}
		dao!!.insert(tag, message)// 第一个参数类型名称，第二个参数是详细信息
	}

	companion object {

		private var instance: LogWatcher? = null

		fun getInstance(): LogWatcher {
			if (instance == null)
				instance = LogWatcher()
			return instance as LogWatcher
		}
	}

}