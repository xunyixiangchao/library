package com.lib.lib_core.frame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by lis on 2018/12/13.
 */
interface BaseFrameAdapterDrawer<T> {

	/**
	 * 在adapter的getView中，首次创建view，初始化并返回创建的布局
	 *
	 * @param position
	 * @param inflater
	 * @param parent
	 * @return
	 */
	fun onViewCreate(position: Int, inflater: LayoutInflater, parent: ViewGroup): View

	/**
	 * 在adapter的getView中，每次滑动listView会循环执行本方法
	 *
	 * @param position
	 * @param item
	 * @param convertView
	 */
	fun onViewAttach(position: Int, item: T, convertView: View)

}
