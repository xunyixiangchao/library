package com.lib.lib_core.net.http.handler

import android.os.Handler
import com.lib.lib_core.net.http.HttpContext
import com.lib.lib_core.net.http.RequestListener

/**
 * Created by lis on 2018/12/13.
 */
interface RequestHandler {

	/**
	 * 发送请求要处理的业务
	 *
	 * @param url         请求的url
	 * @param httpContext 请求的上下文，保存了请求的所有参数
	 * @param listener    Modifier： Modified Date： Modify：
	 */
	fun onRequest(url: String, httpContext: HttpContext, listener: RequestListener, handler: Handler)

	/**
	 * 接收到请求的时候要处理的业务
	 *
	 * @param url         请求的url
	 * @param httpContext 请求的上下文，保存了请求的所有参数
	 * @param listener    回调
	 * @param handler     在主线程中回调
	 * Modifier： Modified Date： Modify：
	 */
	fun onResponse(url: String, httpContext: HttpContext, listener: RequestListener, handler: Handler)
}
