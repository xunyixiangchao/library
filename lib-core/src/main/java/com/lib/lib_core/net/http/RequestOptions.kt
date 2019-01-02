package com.lib.lib_core.net.http

import java.util.*

/**
 * 请求参数
 * Created by lis on 2018/12/13.
 */
class RequestOptions {

	/**
	 * 设置超时时间，单位，秒
	 *
	 * @param timeout
	 */
	var timeout = 0
	private var tag: Any? = null
	var header: HashMap<String, String>? = null
	var requestType: String? = null

	var httpsCer: String? = null// https证书位置
	var httpsCerPassWord: String? = null
	var isToastDisplay = true
		private set

	var params = HashMap<String, String>()

	var isCancelIfActivityFinish = false

	object Mothed {
		val POST = "post"
		val GET = "get"

	}

	fun <T> getTag(): T? {
		return tag as T?
	}

	fun setTag(tag: Any) {
		this.tag = tag
	}

	fun toastDisplay(display: Boolean) {
		this.isToastDisplay = display
	}
}
