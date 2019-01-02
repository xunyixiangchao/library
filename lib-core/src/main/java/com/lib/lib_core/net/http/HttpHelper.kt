package com.lib.lib_core.net.http

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.lib.lib_core.comm.logwatch.LogWatcher
import com.lib.lib_core.comm.utils.GsonHelper
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.net.http.handler.DataSenderFilter
import com.lib.lib_core.net.http.handler.RequestHandler
import com.lib.lib_core.net.http.handler.TransformFilter
import work.dd.com.utils.ThreadPoolUtil
import java.lang.ref.WeakReference
import java.util.*

/**
 * Http请求的工具类
 * Created by lis on 2018/12/13.
 */
class HttpHelper(val context: Context) : HttpRequestInterface {

	private val mRequestTaskList: MutableList<RequestHandler>

	var options: RequestOptions? = null

	private var mCanceledUrl: String? = null

	val httpContext: HttpContext
		get() = HttpContext()

	init {
		mRequestTaskList = ArrayList<RequestHandler>()
		onTaskListFiliter(mRequestTaskList)
		mRequestTaskList.add(TransformFilter())
		mRequestTaskList.add(DataSenderFilter())
	}

	// public void doPost(String url, RequestParams params, Object target,
	// Class<?> responseClass, RequestListener listener) {
	// doPost(url, params, target, responseClass, mRequestTaskList, listener);
	// }

	override fun doPost(url: String, params: Any, responseClass: Class<*>, listener: RequestListener) {
		doPost(url, params, responseClass, mRequestTaskList, listener)
	}

	/**
	 * @param url
	 * @param params
	 * @param responseClass
	 * @param taskList
	 * @param listener
	 */
	fun doPost(url: String, params: Any, responseClass: Class<*>, taskList: List<RequestHandler>, listener: RequestListener?) {
		LogUtils.i(HttpHelper.TAG, "request url:$url")
		LogWatcher.getInstance().putRequestInfo("[request: $url]")

		if (url == mCanceledUrl) {
			mCanceledUrl = ""
		}
		val handler = Handler(Looper.getMainLooper())

		val httpContext = httpContext
		httpContext.context = WeakReference(context)
		httpContext.requestObject = params
		httpContext.options = options
		// httpContext.setTag(requestTarget);
		// if (mTimeOut != 0) {
		// httpContext.setTimeOut(mTimeOut);
		// }
		httpContext.responseClass = responseClass
		listener?.onHttpRequestBegin(url)

		val task = Runnable {
			val requestTime = System.currentTimeMillis()
			//				try {
			for (i in taskList.indices) {
				val requstHandler = taskList[i]
				LogUtils.i(TAG, "HttpUtil: request executed:" + requstHandler.javaClass.simpleName)
				requstHandler.onRequest(url, httpContext, listener!!, handler)
			}
			for (i in taskList.indices.reversed()) {
				val requstHandler = taskList[i]
				LogUtils.i(TAG, "HttpUtil: response executed:" + requstHandler.javaClass.simpleName)
				requstHandler.onResponse(url, httpContext, listener!!, handler)
			}
			//				} catch (Exception e) {
			//					e.printStackTrace();
			//				} finally {
			httpContext.requestTime = System.currentTimeMillis() - requestTime
			handler.post {
				if (!TextUtils.isEmpty(mCanceledUrl) && mCanceledUrl == url) {// 本次请求已经被取消
					listener?.onHttpRequestCancel(url, httpContext)
					LogUtils.i(HttpHelper.TAG, "request data:" + httpContext.request)
					LogUtils.i(HttpHelper.TAG, "response data (Canceled!) :[cost:" + httpContext.requestTime + "ms ]" + httpContext.response)

					LogWatcher.getInstance().putRequestInfo("[request: " + url + "]\n\n" + GsonHelper.formatter(httpContext.request!!))
					LogWatcher.getInstance().putRequestInfo(
							"[response (Canceled!) : " + url + "]\n[cost:" + httpContext.requestTime + "ms ]\n\n" + GsonHelper.formatter(httpContext.response!!))
				} else {
					if (listener != null) {
						if (httpContext.requestObject != null) {
							listener.onHttpRequestSuccess(url, httpContext)
						} else {
							listener.onHttpRequestFailed(url, httpContext)
						}
						listener.onHttpRequestComplete(url, httpContext)
					}
					LogUtils.i(HttpHelper.TAG, "request data:" + httpContext.request)
					LogUtils.i(HttpHelper.TAG, "response data:[cost:" + httpContext.requestTime + "ms ]" + httpContext.response)
					LogWatcher.getInstance().putRequestInfo("[request: " + url + "]\n\n" + GsonHelper.formatter(httpContext.request!!))
					LogWatcher.getInstance().putRequestInfo(
							"[response: " + url + "]\n[cost:" + httpContext.requestTime + "ms ]\n\n" + GsonHelper.formatter(httpContext.response!!))
				}
			}
		}
//			}
		ThreadPoolUtil.getInstance()!!.fetchData(task)
	}

	@Deprecated("")

	/**
	 * 不要使用本方法！
	 */
	override fun post(url: String, params: Any, responseClass: Class<*>, listener: RequestListener) {

	}

	/**
	 * 取消某个http请求
	 *
	 * @param url
	 */
	fun cancel(url: String) {
		mCanceledUrl = url
	}

	/**
	 * 可以在http请求的队列中添加任意新的队列任务
	 *
	 * @param mRequestTaskList Modifier： Modified Date： Modify：
	 */
	fun onTaskListFiliter(mRequestTaskList: MutableList<RequestHandler>) {
		if (testDataFilter != null)
			mRequestTaskList.add(testDataFilter!!)
	}

	companion object {
		val TAG = "HttpHelper"

		/** */
		private var testDataFilter: RequestHandler? = null

		fun enableTestData(testDataFilter: RequestHandler) {
			HttpHelper.testDataFilter = testDataFilter
		}
	}

}
