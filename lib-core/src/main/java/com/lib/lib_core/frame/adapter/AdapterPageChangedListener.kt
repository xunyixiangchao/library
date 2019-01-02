package com.lib.lib_core.frame.adapter

/**
 * 监听adapter分页的接口
 * Created by lis on 2018/12/13.
 */
interface AdapterPageChangedListener {

	/**
	 * 下面还有数据，可以继续翻页
	 */
	fun mayHaveNextPage()

	/**
	 * 点击按钮加载下一页代替之前滑动到底部就自动加载
	 */
	fun tapNextPage()

	/**
	 * 已加载完全部数据，停止分页
	 */
	fun noMorePage()
}
