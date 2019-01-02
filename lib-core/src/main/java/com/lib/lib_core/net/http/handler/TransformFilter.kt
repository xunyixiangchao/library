package com.lib.lib_core.net.http.handler

import android.os.Handler
import com.lib.lib_core.comm.utils.GsonHelper
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.net.http.HttpContext
import com.lib.lib_core.net.http.HttpHelper
import com.lib.lib_core.net.http.RequestListener

/**
 * 类型转换的过滤器
 * Created by lis on 2018/12/13.
 */
class TransformFilter : RequestHandler {
	/*
     * (non-Javadoc)
     *
     * @see
     * com.ichsy.libs.core.net.http.handler.RequestHandler#onRequest(java.lang
     * .String, com.ichsy.libs.core.net.http.HttpContext,
     * com.ichsy.libs.core.net.http.RequestListener)
     */
	override fun onRequest(url: String, httpContext: HttpContext, listener: RequestListener, handler: Handler) {
		if (null == httpContext.requestObject) {
			httpContext.request=""
		} else {
			httpContext.request= GsonHelper.build()!!.toJson(httpContext.requestObject)
		}
		//		httpContext.setRequest(JsonHelper.obj2Json(httpContext.getRequestObject()));
	}

	/*
     * (non-Javadoc)
     *
     * @see
     * com.ichsy.libs.core.net.http.handler.RequestHandler#onResponse(java.lang
     * .String, com.ichsy.libs.core.net.http.HttpContext,
     * com.ichsy.libs.core.net.http.RequestListener)
     */
	override fun onResponse(url: String, httpContext: HttpContext, listener: RequestListener, handler: Handler) {
		var baseResponse: Any? = null
		try {
			baseResponse = GsonHelper.build()!!.fromJson(httpContext.response, httpContext.responseClass)
			httpContext.isRequestSuccess = true
			//			baseResponse = (Object) JsonHelper.json2Obj(httpContext.getResponse(), httpContext.getResponseClass());
		} catch (e: Exception) {
			e.printStackTrace()
			httpContext.isRequestSuccess = false

			LogUtils.e(HttpHelper.TAG, "HttpUtil: TransformFilter Error------")
			LogUtils.e(HttpHelper.TAG, "{JSON}  " + httpContext.response + "\n")
			//            LogUtils.e(HttpHelper.TAG, ExceptionUtil.getException(e));
			LogUtils.e(HttpHelper.TAG, e.message!!)
		}

		httpContext.setResponseObject(baseResponse!!)
	}

}
