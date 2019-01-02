package work.dd.com.navigation

import android.content.Intent

/**
 * 导航栏监听器
 * Created by lis on 2018/12/13.
 */
interface NavigationListener {

	/**
	 * 返回
	 */
	fun popBack()

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param intent
	 */
	fun pushActivity(intent: Intent)

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param clz 跳转的activity类名
	 */
	fun pushActivity(clz: Class<*>)

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param clz 跳转的activity类名
	 */
	fun pushActivity(clz: Class<*>, intent: Intent)
}
