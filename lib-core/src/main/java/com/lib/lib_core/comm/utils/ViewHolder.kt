package com.lib.lib_core.comm.utils

import android.util.SparseArray
import android.view.View

/**
 * 通用的adapter抽象类
 * Created by lis on 2018/12/13.
 */
object ViewHolder {

	/**
	 * 用法简单介绍 if (convertView == null) {<br></br>
	 * convertView = LayoutInflater.from(context).inflate(R.layout.banana_phone,
	 * parent, false);<br></br>
	 * }<br></br>
	 * ImageView bananaView = ViewHolder.get(convertView, R.id.banana);<br></br>
	 * TextView phoneView = ViewHolder.get(convertView, R.id.phone);<br></br>
	 *
	 * @param view
	 * @param id
	 * @return
	 */
	operator fun <T : View> get(view: View, id: Int): T? {
		var viewHolder: SparseArray<View>? = view.tag as SparseArray<View>
		if (viewHolder == null) {
			viewHolder = SparseArray()
			view.tag = viewHolder
		}
		var childView: View? = viewHolder.get(id)
		if (childView == null) {
			childView = view.findViewById(id)
			viewHolder.put(id, childView)
		}
		return childView as T?
	}
}