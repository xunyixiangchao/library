package com.lib.lib_core.net.http

/**
 * Created by lis on 2018/12/13.
 */
interface HttpRequestInterface {

	/**
	 * 发送post请求
	 *
	 * @param url
	 * @param params
	 * @param listener Modifier： Modified Date： Modify：
	 */
	fun doPost(url: String, params: Any, responseClass: Class<*>, listener: RequestListener)

	fun post(url: String, params: Any, responseClass: Class<*>, listener: RequestListener)
}
