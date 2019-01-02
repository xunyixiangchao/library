package com.lib.lib_core.comm.utils

import android.text.TextUtils
import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.util.*

/**
 *  处理url链接的工具类
 * Created by lis on 2018/12/13.
 */
object UrlUtil {

	/**
	 * 校验并调整url
	 *
	 * @param url
	 * @return
	 */
	fun url(url: String): String {
		return if (url.contains("://")) {
			url
		} else {
			"http://$url"
		}
	}

	//    public static String getUrlFileName(String url) {
	//        if (TextUtils.isEmpty(url)) {
	//            return "";
	//        } else {
	//            return url.substring(url.lastIndexOf("/") + 1, url.length());
	//        }
	//    }

	fun isUrl(url: String): Boolean {
		return if (TextUtils.isEmpty(url)) false else url.startsWith("http://") || url.startsWith("https://")

	}

	fun getUrl(url: String): String? {
		if (!TextUtils.isEmpty(url)) {
			val urls = url.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
			for (s in urls) {
				if (isUrl(s)) return s
			}
		}

		return null
	}

	fun getUrlQuery(url: String): HashMap<String, String?> {
		var resultMap = HashMap<String, String?>()
		if (!TextUtils.isEmpty(url) && url.startsWith("http")) {
			var uri: URI? = null
			try {
				uri = URI.create(url)
			} catch (e: Exception) {
				e.printStackTrace()
			}

			if (null == uri) return resultMap
			//            String query = uri.getQuery(); //from=AActivity&to=BActivity
			resultMap = UrlUtil.parserQuery(uri.query)
		}
		return resultMap
	}

	//    /**
	//     * 判断url是否带有某参数
	//     *
	//     * @param url
	//     * @return
	//     */
	//    public static boolean hasParams(String url, String key) {
	//
	//    }

	@Deprecated("")
	fun getParams(url: String, key: String): String? {
		val params = UrlUtil.parserQuery(url)
		return params[key]
	}

	/**
	 * 重新构建url的参数 加上&或者?
	 * ##未测试
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	fun getAppendString(url: String, params: String): String {
		val hasParams = url.contains("?")

		return (if (hasParams) "&" else "?") + params
	}


	/**
	 * 把http后面的参数处理为HashMap
	 *
	 * @param query
	 * @return
	 */
	fun parserQuery(query: String): HashMap<String, String?> {
		val map = HashMap<String, String?>()
		if (TextUtils.isEmpty(query)) return map

		LogUtils.i("urlparser", "parserQuery query:$query")
		val group = query.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
		if (group.size == 0) return map

		for (item in group) {
			//            url=http://m.leyou.com.cn/gain/web_gain?id=104
			val splitIndex = item.indexOf("=")
			if (splitIndex < 0 || splitIndex + 1 > item.length) {
				continue
			}
			val key = item.substring(0, splitIndex)
			var value: String? = null
			try {
				value = URLDecoder.decode(item.substring(splitIndex + 1, item.length), "utf-8")
			} catch (e: UnsupportedEncodingException) {
				e.printStackTrace()
			}

			LogUtils.i("urlparser", "current filter params: key:$key  value:$value")
			var keys =if (TextUtils.isEmpty(key)) "" else key
			map[keys] = if (TextUtils.isEmpty(value)) "" else value
		}
		return map
	}

	/**
	 * 去掉http连接中的指定参数
	 *
	 * @param url
	 * @param key
	 * @return
	 */
	fun removeParams(url: String, key: String): String {
		val params = UrlUtil.getUrlQuery(url)
		if (params == null || params.size == 0 || !params.containsKey(key)) {
			return url
		}

		val value = params[key]
		val replaceValue = key + "=" + if (TextUtils.isEmpty(value)) "" else value
		if (params.size == 1) {
			return url.replace("?$replaceValue", "")
		}

		val i = url.indexOf(replaceValue)
		if (i <= 0) {
			return url
		}

		val mark = url.substring(i - 1, i)
		return if (mark == "?") {
			url.replace("$replaceValue&", "")
		} else if (mark == "&") {
			url.replace("&$replaceValue", "")
		} else {
			url.replace(replaceValue, "")
		}
	}

	fun appendParams(url: String, key: String, value: String): String {
		var url = url
		url = removeParams(url, key)

		if (url.endsWith("?")) {
			url = url.substring(0, url.indexOf("?"))
		}
		return if (TextUtils.isEmpty(value)) {
			url
		} else url + (if (url.contains("?")) "&" else "?") + key + "=" + value
	}

}
