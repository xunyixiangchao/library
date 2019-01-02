package com.lib.lib_core.net.http

import android.content.Context
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
class HttpContext {
	//    private final Object lock1 = new Object();
	//    private final Object lock2 = new Object();
	var requestTime: Long = 0
	var context: WeakReference<Context>? = null
	/**
	 * 请求的数据string
	 */
	/**
	 * 网络超时时间
	 */
	//	private int timeOut;

	/**
	 * 每次网络请求的唯一标示 用于缓存key
	 */
	// private String uuid;
	var request: String? = null
	var requestObject: Any? = null
	var responseClass: Class<*>? = null
	var httpCode: Int = 0
	/**
	 * 返回的数据string
	 */
	var code: Int = 0
	var message: String? = null
	var isRequestSuccess = false//标志本次请求是否成功，因为直接判断返回的responseObject有可能不准确，所以单独用一个新的变量去记录
	//    public String message;
	var response: String? = null
	private var responseObject: Any? = null

	//	private boolean isCache;
	//	private Object tag;

	var options: RequestOptions? = null

	var params: HashMap<String, Any>? = HashMap()
		get() {
			if (null == field) {
				this.params = HashMap()
			}
			return field
		}

	var responseTimeStamp: Long = 0


	fun <T> getResponseObject(): T? {
		return responseObject as T?
	}

	fun setResponseObject(responseObject: Any) {
		this.responseObject = responseObject
	}
}
