package com.lib.lib_core.comm.utils

import android.text.TextUtils
import com.google.gson.*
import java.lang.reflect.Type

/**
 * json处理工具类
 * Created by lis on 2018/12/13.
 */
object GsonHelper {
	private var mGson: Gson? = null

	fun build(): Gson? {
		synchronized(GsonHelper::class.java) {
			if (null == mGson) {
				mGson = com.google.gson.GsonBuilder().disableHtmlEscaping().registerTypeAdapter(Double::class.java, object : JsonSerializer<Double> {
					override fun serialize(src: Double?, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
						return if (src.toString() == src!!.toLong().toString()) {
							JsonPrimitive(src.toLong())
						} else {
							JsonPrimitive(src)
						}
					}
				}).create()
			}
			return mGson
		}
	}

	/**
	 * Json排版
	 *
	 * @param uglyJSONString
	 * @return
	 */
	fun formatter(uglyJSONString: String): String? {
		if (TextUtils.isEmpty(uglyJSONString)) {
			return null
		}

		var prettyJsonStr = uglyJSONString
		try {
			val gson = GsonBuilder().setPrettyPrinting().create()
			val jp = JsonParser()
			val je = jp.parse(uglyJSONString)
			prettyJsonStr = gson.toJson(je)
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return prettyJsonStr
	}

}
