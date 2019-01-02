package com.lib.lib_core.comm.bus.url.filter

import android.content.Context
import java.util.*

/**
 * BusFilter的基类，抽象了重要方法
 * Created by lis on 2018/12/13.
 */
abstract class BusBaseFilter {


	/**
	 * 初始化并声明可处理的filter类型
	 *
	 * @return filterWhat[]，是个数组
	 */
	abstract fun filterWhat(): Array<String>


	/**
	 * 具体处理事件的方法
	 *
	 * @param context
	 * @param params    携带参数，注意params有可能为null，需要做判断
	 * @param attachMap 携带的参数
	 * @return
	 */
	abstract fun onAction(context: Context, params: HashMap<String, String?>, attachMap: HashMap<String, Any?>)


}