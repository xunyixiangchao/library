package com.lib.lib_core.comm.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.lib.lib_core.comm.bus.url.UrlParser
import com.lib.lib_core.comm.bus.url.filter.IntentFilter
import com.lib.lib_core.frame.BaseFrameActivity
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
object NavigationUtil {
	val INTENT_BUNDLE_ACTIVITY_REQUEST_CODE = "INTENT_BUNDLE_ACTIVITY_REQUEST_CODE"

	/**
	 * 跳转到某个页面
	 *
	 * @param context
	 * @param intent
	 */
	fun navigationTos(context: Context, intent: Intent, transitions: Any?) {
		val uri = UrlParser.instance.buildUri(UrlParser.SCHEME_RULE_NATIVE)

		val attachMap = HashMap<String, Any?>()
		attachMap[IntentFilter.PARAMS_ATTACH_INTENT_KEY] = intent
		attachMap[IntentFilter.PARAMS_BUNDLE_TRANSITION] = transitions
		UrlParser.instance.parser(context, uri, attachMap)
	}


	fun navigationTo(context: Context, intent: Intent) {
		navigationTos(context, intent, null)
	}

	/**
	 * 跳转到某个页面
	 */
	fun navigationTo(context: Context, clz: Class<*>) {
		//        String uri = UrlParser.getInstance().buildUri(UrlParser.SCHEME_RULE_NATIVE, clz.getName());
		//        UrlParser.getInstance().parser(context, uri);

		val intent = Intent(context, clz)
		navigationTo(context, intent)
	}

	/**
	 * 跳转到某个页面
	 */
	fun navigationTo(context: Context, clz: Class<*>, intent: Intent) {
		//        String uri = UrlParser.getInstance().buildUri(UrlParser.SCHEME_RULE_NATIVE, clz.getName());
		//        UrlParser.getInstance().parser(context, uri, intent);

		intent.setClass(context, clz)
		navigationTo(context, intent)
	}

	fun navigationToForResult(context: Context, intent: Intent, requestCode: Int) {
		val uri = UrlParser.instance.buildUri(UrlParser.SCHEME_RULE_NATIVE)
		intent.putExtra(INTENT_BUNDLE_ACTIVITY_REQUEST_CODE, requestCode)
		UrlParser.instance.parser(context, uri, intent)
	}

	fun navigationToForResult(context: Context, clz: Class<*>, requestCode: Int) {
		val intent = Intent(context, clz)
		navigationToForResult(context, intent, requestCode)
	}

	fun navigationToForResult(context: Context, clz: Class<*>, intent: Intent, requestCode: Int) {
		intent.setClass(context, clz)
		navigationToForResult(context, intent, requestCode)
	}

	/**
	 * 跳转到某页面，然后在这个页面关闭的时候，打开第二个页面
	 *
	 * @param context
	 * @param intent
	 * @param secondIntent
	 */
	fun navigationNext(context: Context, intent: Intent, secondIntent: Intent) {
		val bundle = Bundle()
		bundle.putParcelable(BaseFrameActivity.BUNDLE_INTENT_SECOND_INTENT, secondIntent)
		intent.putExtras(bundle)
		NavigationUtil.navigationTo(context, intent)
	}

	//    /**
	//     * 跳转到某页面，然后在这个页面关闭的时候，执行事件
	//     *
	//     * @param context
	//     * @param intent
	//     * @param actionEvent
	//     */
	//    public static void navigationNextAndRunAction(Context context, Intent intent, String actionEvent) {
	//        Bundle bundle = new Bundle();
	//        bundle.putString(BaseFrameActivity.BUNDLE_INTENT_SECOND_ACTION_EVENT, actionEvent);
	////        bundle.putParcelable(BaseFrameActivity.BUNDLE_INTENT_SECOND_ACTION_EVENT, actionEvent);
	//        intent.putExtras(bundle);
	//        NavigationUtil.navigationTo(context, intent);
	//    }

	/**
	 * 跳转到某页面，然后在这个页面关闭的时候，打开第二个页面
	 *
	 * @param context
	 * @param intent
	 * @param secondScheme
	 */
	fun navigationNext(context: Context, intent: Intent, secondScheme: String) {
		intent.putExtra(BaseFrameActivity.BUNDLE_INTENT_SECOND_SCHEME_URL, secondScheme)
		NavigationUtil.navigationTo(context, intent)
	}
}