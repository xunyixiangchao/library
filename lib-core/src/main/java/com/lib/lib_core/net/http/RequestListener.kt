package com.lib.lib_core.net.http

/**
 * 请求网络的回调
 * Created by lis on 2018/12/13.
 */
class RequestListener {

	/**
	 * 请求开始的时候
	 *
	 * @param url
	 * @author LiuYuHang
	 * @date 2014年11月14日
	 */
	fun onHttpRequestBegin(url: String) {}

	/**
	 * 请求之后会回调的方法
	 *
	 * @param url
	 * @param httpContext
	 * @author LiuYuHang
	 * @date 2014年11月14日
	 */
	fun onHttpRequestSuccess(url: String, httpContext: HttpContext) {}

	/**
	 * 网络请求超时的回调
	 *
	 * @param url
	 * @param httpContext
	 */
	fun onHttpRequestTimeOut(url: String, httpContext: HttpContext) {

	}

	/**
	 * 请求失败回调的接口
	 *
	 * @param url
	 * @author LiuYuHang
	 * @date 2014年11月14日
	 */
	fun onHttpRequestFailed(url: String, httpContext: HttpContext) {}

	/**
	 * 请求结束之后（不管成功或者失败，都会执行本方法）
	 *
	 * @param url
	 * @param httpContext 请求回来的对象
	 * @author LiuYuHang
	 * @date 2014年11月14日
	 */
	fun onHttpRequestComplete(url: String, httpContext: HttpContext) {}

	/**
	 * 调用 [HttpHelper.cancel]之后，会回调本方法
	 *
	 * @param url
	 * @param httpContext
	 * @author LiuYuHang
	 * @date 2014年11月21日
	 */
	fun onHttpRequestCancel(url: String, httpContext: HttpContext) {}
}
