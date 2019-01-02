package com.lib.lib_core.net.http

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Created by lis on 2018/12/13.
 */
enum class OkHttpClientHelper {

	INSTANCE;

	fun getOkHttpClient(): OkHttpClient? {
		if (okHttpClient == null) {
			okHttpClient = OkHttpClient.Builder()
					.connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
					.writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
					.readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
					.build()
		}
		return okHttpClient
	}

	companion object {
		private val TIME_OUT = 10
		private var okHttpClient: OkHttpClient? = null
	}
}
