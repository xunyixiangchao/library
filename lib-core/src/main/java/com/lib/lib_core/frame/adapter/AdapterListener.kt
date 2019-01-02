package com.lib.lib_core.frame.adapter

/**
 * Created by lis on 2018/12/13.
 */
interface AdapterListener {

	/**
	 * 准备给adapter绑定数据
	 */
	fun onBeginBindData()

	/**
	 * adapter已经绑定数据
	 *
	 * @param empty 数据是否为空
	 */
	fun onDataBindComplete(empty: Boolean)
}