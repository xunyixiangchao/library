package com.lib.lib_core.comm.bus.url.filter

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Pair
import android.view.View
import com.lib.lib_core.comm.bus.url.UrlParser
import com.lib.lib_core.comm.utils.ArrayUtil
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.comm.utils.NavigationUtil
import work.dd.com.utils.ToastUtils
import java.util.*

/**
 * 总线中控制intent跳转的逻辑处理
 * Created by lis on 2018/12/13.
 */
class IntentFilter : BusBaseFilter() {

	override fun onAction(context: Context, params: HashMap<String, String?>, attachMap: HashMap<String, Any?>) {
		var params = params
		var intent: Intent? = null
		if (null == attachMap || null == (attachMap[PARAMS_ATTACH_INTENT_KEY] as Intent)) {
			intent = Intent()
		}

		var className: String? = null//目标activity
		if (params == null) {
			if (intent != null) {
				className = intent.component!!.className
			}
			params = HashMap()
		} else {
			val nativeName = params[UrlParser.SCHEME_RULE_NATIVE]
			params.remove(UrlParser.SCHEME_RULE_NATIVE)

			//className从路由表获取
//			val routeClassName = UrlRoute.getInstance().getClassName(nativeName)
//			if (TextUtils.isEmpty(routeClassName)) {
//				className = nativeName
//			} else {
//				className = routeClassName
//			}
			className = nativeName!!
		}

		if (intent != null) {
			className = intent.component!!.className
		}

		if (context !is Activity) {
			if (intent != null) {
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
			}
		}

		ArrayUtil.arrayMap(params, object : ArrayUtil.MapHandler<String, String?> {

			override fun onNext(key: String, value: String?) {
				Log.v("intent", "key:$key value:$value")
				//拼装总线url后面的参数到intent参数中
				if (intent != null) {
					intent.putExtra(key, value)
				}
			}
		})

		val fromActivity = context.javaClass.name

		LogUtils.i("urlparser", msg = "处理intent的filter，intent is:$intent\nfrom = $fromActivity\nto = $className")

		//传递来源
		intent!!.putExtra(INTENT_BUNDLE_FROM_ACTIVITY, fromActivity)

		//判断是否忽略多次打开的延迟
		val quickOpen = intent.getStringExtra("quickOpen")

		/* 最短时间校验，验证两次打开activity的时间，如果太短不做任何操作，可以防止重复点击的问题 */
		if (null != currentIntent && currentIntent!!.intentClass == className && "true" != quickOpen) {

			val currentTime = System.currentTimeMillis()
			val intentTime = currentIntent!!.time

			if (currentTime - intentTime < 400) {
				LogUtils.i("intentfilter", "currentTime - intentTime: " + (currentTime - intentTime))
				return
			}
		}

		currentIntent = IntentClassFilter()
		currentIntent!!.intentClass = className
		currentIntent!!.time = System.currentTimeMillis()

		onPushActivity(context, fromActivity, className!!, intent, attachMap)
	}

	inner class IntentClassFilter {
		var intentClass: String? = null
		var time: Long = 0
	}

	//    private UrlParser.UrlParserFilter urlParserFilter;

	override fun filterWhat(): Array<String> {
		return arrayOf(UrlParser.SCHEME_RULE_NATIVE)
	}

	protected fun onPushActivity(context: Context, fromActivity: String, toActivity: String, finalIntent: Intent, attachMap: HashMap<String, Any?>) {
		Log.v("urlparser", "cant find bus_mapping file, start activity: $toActivity")

		//        if (null != urlParserFilter) {
		//            urlParserFilter.onFilter(context, attachMap);
		//        }

		finalIntent.setClassName(context, toActivity)
		pushActivity(context, finalIntent, attachMap)
	}

	/**
	 * 打开activity的逻辑
	 *
	 * @param context
	 * @param finalIntent
	 */
	fun pushActivity(context: Context, finalIntent: Intent, attachMap: HashMap<String, Any?>) {
		val isActivity = context is Activity
		try {
			val resultCode = finalIntent.getIntExtra(NavigationUtil.INTENT_BUNDLE_ACTIVITY_REQUEST_CODE, ACTIVITY_RESULT_CODE_DEFAULT)
			//            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, shareView, "shareName");

			if (isActivity) {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && null != attachMap && null != attachMap[PARAMS_BUNDLE_TRANSITION]) {
					val transitionMap = attachMap[PARAMS_BUNDLE_TRANSITION] as HashMap<String, View>

					val pairs = arrayOfNulls<Pair<View, String>>(transitionMap.size)

					var i = 0
					for ((key, value) in transitionMap) {
						pairs[i] = Pair.create(value, key)
						i++
					}
					val options = ActivityOptions.makeSceneTransitionAnimation(context as Activity, *pairs)

					if (resultCode != ACTIVITY_RESULT_CODE_DEFAULT) {
						context.startActivityForResult(finalIntent, resultCode, options.toBundle())
					} else {
						context.startActivity(finalIntent, options.toBundle())
					}
				} else {
					if (resultCode != ACTIVITY_RESULT_CODE_DEFAULT) {
						(context as Activity).startActivityForResult(finalIntent, resultCode)
					} else {
						context.startActivity(finalIntent)
					}
				}
			} else {
				context.startActivity(finalIntent)
			}

		} catch (e: Exception) {
			e.printStackTrace()
			ToastUtils.showMessage(context, "启动页面发生错误：" + e.message)
		}

	}

	companion object {
		val INTENT_BUNDLE_FROM_ACTIVITY = "INTENT_BUNDLE_FROM_ACTIVITY"
		val PARAMS_ATTACH_INTENT_KEY = "PARAMS_ATTACH_INTENT_KEY"
		val PARAMS_ATTACH_RUNNABLE_KEY = "PARAMS_ATTACH_RUNNABLE_KEY"
		var currentIntent: IntentClassFilter? = null

		val PARAMS_BUNDLE_TRANSITION = "params_bundle_transition"

		//默认的resultCode
		val ACTIVITY_RESULT_CODE_DEFAULT = Activity.RESULT_OK
	}

	//    public void setUrlParserFilter(UrlParser.UrlParserFilter urlParserFilter) {
	//        this.urlParserFilter = urlParserFilter;
	//    }

}
