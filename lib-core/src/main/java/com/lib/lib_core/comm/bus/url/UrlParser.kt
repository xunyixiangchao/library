package com.lib.lib_core.comm.bus.url

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.lib.lib_core.comm.bus.url.filter.BusBaseFilter
import com.lib.lib_core.comm.bus.url.filter.IntentFilter
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.comm.utils.UrlUtil
import java.net.URI
import java.util.*

/**
 * URL格式解析工具类
 * Created by lis on 2018/12/13.
 */
class UrlParser private constructor() {

	private val filters: HashMap<String, BusBaseFilter>?
	private var specialMap: HashMap<String, String>? = null

	init {
		//初始化过滤器的过滤类型
		filters = HashMap()
	}

	/**
	 * 给url总线添加可处理的逻辑
	 */
	fun registerFilter(filter: BusBaseFilter) {
		for (s in filter.filterWhat()) {
			filters!![s] = filter
		}
		LogUtils.v("filter", "给url添加处理类型:" + Arrays.toString(filter.filterWhat()))
	}

	/**
	 * 批量添加事件
	 *
	 * @param filter
	 */
	fun registerFilters(vararg filter: BusBaseFilter) {
		for (busBaseFilter in filter) {
			registerFilter(busBaseFilter)
		}
	}

	/**
	 * 添加特殊的url集合，转换成规范的url
	 *
	 * @param key   特殊的url
	 * @param value 转换后的url
	 */
	fun putSpacialMap(key: String, value: String) {
		if (null == specialMap) {
			specialMap = HashMap()
		}
		specialMap!![key] = value
	}

	/**
	 * 获取规范的url前缀
	 *
	 * @param schemeType
	 * @param param      目标参数，SCHEME_RULE_NATIVE 为要跳转的activity类名<br></br>
	 * SCHEME_RULE_WEB 为url地址<br></br>
	 * SCHEME_RULE_ACTION 为具体事件
	 * @return leyou://intent?<br></br>leyou://web?<br></br>leyou://action?<br></br>
	 */
	@JvmOverloads
	fun buildUri(schemeType: String, param: String? = null): String {
		return "leyou://" + schemeType + if (TextUtils.isEmpty(param)) "" else "?$schemeType=$param"
	}

	/**
	 * 解析总线逻辑
	 *
	 * @param context
	 * @param url     处理当前scheme url，并返回是否处理成功<br></br>
	 *
	 *
	 * url分成3部分，第一部分是固定的<leyou:></leyou:>//>，第二部分是跳转逻辑，表示跳转到页面(intent)还是网页(web)还是事件(action)，后边会跟上参数<br></br>
	 * eg. leyou://native?from=AActivity&to=BActivity
	 * @param intent  附带的intent参数
	 * @return 返回是否解析成功，如果解析失败，可能为一个普通url
	 *
	 */
	fun parser(context: Context, url: String): Boolean {
		return parser(context, url, null)
	}

	@JvmOverloads
	fun parser(context: Context, url: String, intent: Intent?): Boolean {
		val attachMap = HashMap<String, Any?>()
		attachMap[IntentFilter.PARAMS_ATTACH_INTENT_KEY] = intent
		return parser(context, url, attachMap)
	}

	fun parser(context: Context, url: String, attachMap: HashMap<String, Any?>): Boolean {
		var url = url
		if (null == filters || TextUtils.isEmpty(url)) {
			//            if(urlParserCallback)
			return false
		}

		if (null != specialMap && !specialMap!!.isEmpty()) {
			val value = specialMap!![url]
			if (!TextUtils.isEmpty(value)) {
				url = value!!
				LogUtils.i("lyh", "url replace: $url")
			}
		}

		val uri: URI
		try {
			uri = URI.create(url)
		} catch (e: Exception) {
			LogUtils.i("urlparser", "url parser 失败，原因：" + e.message)
			e.printStackTrace()
			return false
		}

		val host = uri.host
		var path = uri.path

		var query: String? = null
		val queryIndex = url.indexOf("?")
		if (queryIndex >= 0) {
			query = url.substring(queryIndex + 1, url.length)
		}

		LogUtils.i("urlparser", "uri:" + uri.toString())
		LogUtils.i("urlparser", "query:" + query!!)
		LogUtils.i("urlparser", "current filter host is:$host")
		LogUtils.i("urlparser", "current filter path is:$path")

		val filter: BusBaseFilter?
		var queryMap = UrlUtil.parserQuery(query)
		LogUtils.i("urlparser", "queryMap size: " + queryMap.size)

		//首先判断是商品还是其他
		val keyWord = "forapp/"
		if (url.contains(keyWord)) {
			//其他
			query = url.substring(url.lastIndexOf(keyWord) + keyWord.length, url.length)
			queryMap = UrlUtil.parserQuery(query)
			LogUtils.i("urlparser", "le query:$query")

			filter = filters[host]
		} else {
			//商品
			if (!TextUtils.isEmpty(path)) {
				//                queryMap = new HashMap<>();
				queryMap.put(URL_PATH_PARAMS_KEY, path.substring(path.lastIndexOf("/") + 1, path.length))

				val pathLastIndex = path.lastIndexOf("/")
				if (pathLastIndex != 0 && pathLastIndex != -1) {
					path = path.substring(0, pathLastIndex)
				}
			}
			LogUtils.i("urlparser", "filters.get(host + path): $host$path")
			filter = filters[host + path]

			//            if (filter instanceof IntentFilter) {
			//                ((IntentFilter)filter).setUrlParserFilter(urlParserFilter);
			//            }

		}

		if (null == filter) {
			return false
		} else {
			filter!!.onAction(context, queryMap, attachMap)
			return true
		}
	}

	companion object {
		//    public static final String AES_KEY_WORD_1 = "lEyOu";
		//    public static final String AES_KEY_WORD_2 = "85861200";
		//    public static final String AES_KEY_WORD = AES_KEY_WORD_1 + AES_KEY_WORD_2;

		//    private IUrlParserCallback urlParserCallback;

		private val SCHEME_START = "leyou"

		val SCHEME_RULE_NATIVE = "native"
		val SCHEME_RULE_WEB = "web"
		val SCHEME_RULE_ACTION = "action"

		val URL_PATH_PARAMS_KEY = "_url_path"
		//    public static final String URL_PATH_PARAMS_TIME = "_url_time";


		private var INSTANCE: UrlParser? = null

		val instance: UrlParser
			get() {
				if (null == INSTANCE) {
					INSTANCE = UrlParser()
				}
				return INSTANCE as UrlParser
			}
	}

	//    private List<UrlParserFilter> intentFilters;
	//
	//    public void addIntentFilter(UrlParserFilter filter, Class<?> hookClass) {
	//        if (null == intentFilters) {
	//            intentFilters = new ArrayList<>();
	//        }
	//        intentFilters.add(filter);
	//    }
	//
	//    public interface UrlParserFilter {
	//        /**
	//         * fillter拦截时候的处理
	//         * @param context
	//         * @param attachMap
	//         */
	//        void onIntentFilter(Context context, HashMap<String, Object> attachMap);
	//    }

	//    public void setUrlParserCallback(IUrlParserCallback urlParserCallback) {
	//        this.urlParserCallback = urlParserCallback;
	//    }

}
/**
 * 获取规范的url前缀
 *
 * @param schemeType
 * @return leyou://native?
 * <br></br>leyou://web?
 * <br></br>leyou://action?
 * <br></br>
 *///    /**
//     * 同步解析url
//     *
//     * @param context
//     * @param url
//     * @return
//     */
//    public synchronized boolean syncParser(Context context, String url) {
//        return parser(context, url);
//    }
/**
 * 解析总线逻辑
 *
 * @param context
 * @param url     处理当前scheme url，并返回是否处理成功<br></br>
 *
 *
 * url分成3部分，第一部分是固定的<leyou:></leyou:>//>，第二部分是跳转逻辑，表示跳转到页面(intent)还是网页(web)还是事件(action)，后边会跟上参数<br></br>
 * eg. leyou://intent?from=AActivity&to=BActivity
 * @return 返回是否解析成功，如果解析失败，可能为一个普通url
 */
