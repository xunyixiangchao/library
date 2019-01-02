package com.lib.lib_core.frame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
abstract class BaseFrameAdapter<T>
//    private boolean isDataEmpty = true;

(val context: Context) : BaseAdapter(), BaseFrameAdapterDrawer<T> {
	var inflater: LayoutInflater
		protected set
	private var list: MutableList<T>? = null

	private var mAdapterListener: AdapterListener? = null

	val data: List<T>
		get() {
			if (null == list) {
				list = ArrayList()
			}
			return list!!
		}

	init {
		// iflater写在这里，是为了优化不用创建多个LayoutInflater
		this.inflater = LayoutInflater.from(context)
	}

	/**
	 * 重新设置数据，一般是用于刷新adapter
	 *
	 * @param data
	 */
	fun resetData(data: List<T>?) {
		var data = data
		//        if (null == data || data.isEmpty()) {
		//            return;
		//        }

		//解决空指针
		if (null == data) {
			data = ArrayList()
		}

		//防止data数据被clean掉，需要重新复制保存
		val tempData = ArrayList(data)
		if (null == list) {
			list = ArrayList()
		} else {
			list!!.clear()
		}
		list!!.addAll(tempData)
		notifyDataSetChanged()
	}

	/**
	 * 追加数据，一般是用于分页
	 *
	 * @param data
	 */
	fun addData(data: List<T>?) {
		if (null == data || data.isEmpty()) {
			return
		}

		if (list == null) {
			list = ArrayList()
		}
		list!!.addAll(data)
		notifyDataSetChanged()

		if (null != mAdapterListener) {
			mAdapterListener!!.onDataBindComplete(list!!.size == 0)
		}
	}

	/**
	 * 清空数据
	 */
	fun clearData() {
		if (null == list) {
			list = ArrayList()
		} else {
			list!!.clear()
		}
		notifyDataSetChanged()
	}

	/**
	 * 清空adapter中的数据
	 */
	fun clearAdapter() {
		clearData()
		notifyDataSetChanged()
	}

	fun addData(data: Array<T>?) {
		if (null == data) {
			return
		}
		addData(Arrays.asList(*data))
	}

	override fun getCount(): Int {
		if (null == list) {
			if (null != mAdapterListener) {
				mAdapterListener!!.onBeginBindData()
			}
			return 0
		} else {
			return list!!.size
		}
	}

	override fun getItem(position: Int): T? {
		return if (list == null || position == list!!.size) {
			null
		} else list!![position]
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		var convertView = convertView
		if (convertView == null) {
			convertView = onViewCreate(position, inflater, parent)
		}
		onViewAttach(position, this!!.getItem(position)!!, convertView)
		return convertView
	}

	fun setAdapterListener(listener: AdapterListener) {
		this.mAdapterListener = listener
	}
}