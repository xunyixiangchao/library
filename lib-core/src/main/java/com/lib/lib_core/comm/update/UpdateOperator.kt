package com.lib.lib_core.comm.update

import android.text.TextUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 检测更新的操作类，处理检测更新中用户的操作
 * Created by lis on 2018/12/13.
 */
abstract class UpdateOperator {

	/**
	 * 执行检测更新的网络请求，返回网络请求之后的数据（注意：这个函数在子线程中）
	 *
	 * @return
	 */
	abstract fun onUpdateRequest(): String


	/**
	 * 解析服务端返回的网络请求数据，返回检测更新的实体类
	 *
	 * @param updateJson
	 * @return
	 */
	abstract fun parserUpdateJson(updateJson: String): UpdateVo

	fun onUpdateRequestHeader(): String? {
		return null
	}

	fun onUpdateRequestParams(): String? {
		return null
	}

	fun doRequest(url: String, header: String, body: String): String? {
		var result: String? = null

		var response: Response? = null
		try {
			val finalUrl: String
			if (TextUtils.isEmpty(body)) {
				finalUrl = url
			} else {
				finalUrl = url + body
			}
			val builder = OkHttpClient.Builder()
					.connectTimeout(4, TimeUnit.SECONDS)
					.writeTimeout(4, TimeUnit.SECONDS)
					.readTimeout(4, TimeUnit.SECONDS)
			//                    .cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));

			val request = Request.Builder().url(finalUrl).get().build()
			//            OkHttpClient okHttpClient = new OkHttpClient();
			val okHttpClient = builder.build()

			response = okHttpClient.newCall(request).execute()
			if (response!!.isSuccessful()) {
				result = response!!.body().string()
			} else {
				result = response!!.toString()
			}
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
			if (null != response) {
				response!!.close()
			}
		}
		return result
	}
}
