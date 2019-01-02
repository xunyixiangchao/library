package com.lib.lib_core.frame.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.lib.lib_core.R

/**
 * 二次封装带分页的adapter
 * Created by lis on 2018/12/13.
 */
abstract class BasePagingFrameAdapter<T>(context: Context) : BaseFrameAdapter<T>(context), AdapterPageChangedListener {
	private val LOADING_DELAY_TIME = 0// 每次获取数据，加一个delay延迟再返回给调用者(用于优化用户体验)

	protected val STATUS_LOADING = 1// 正在加载
	protected val STATUS_LOADING_NEED_TAP = STATUS_LOADING + 1// 点击加载下一页
	protected val STATUS_LOADING_END = STATUS_LOADING_NEED_TAP + 1// 加载完毕

	private var mLoadingStatus = STATUS_LOADING
	private var isLoadingNextPage = false// 当前是否在做下一次请求
	// private int proLoadPosition = 4;// 预加载，距离底部还剩几条数据的时候开始加载，默认是0
	/**
	 * 设置初始加载页面的页码
	 *
	 * @param startPage
	 */
	var startPage = 0
		set(startPage) {
			this.page = startPage
			field = startPage
		}// 初始化页码
	var page = 0
		private set

	private var pagingListener: PagingListener<T>? = null// 分页的listener

	// 通过判断pagingListener是否为空来定义有没有开启分页加载的开关，如果是分页加载，count+1，最后一个view为loadingview
	//如果有翻页并且list没数据，需要返回1，不然因为listview机制不会调用getView方法，会显示不出loading进度条
	override fun getCount(): Int {
		// 通过判断pagingListener是否为空来定义有没有开启分页加载的开关，如果是分页加载，count+1，最后一个view为loadingview
		if (super.getCount() !== 0 && pagingListener != null) {
			return super.getCount() + 1
		} else if (super.getCount() === 0 && pagingListener != null) {
			//如果有翻页并且list没数据，需要返回1，不然因为listview机制不会调用getView方法，会显示不出loading进度条
			return 1
		}
		return super.getCount()
	}

	override fun isEmpty(): Boolean {
		return data.size === 0
	}

	override fun getViewTypeCount(): Int {
		return super.getViewTypeCount() + 3
	}

	interface PagingListener<T> {

		/**
		 * 分页的监听，返回当前adapter和当前请求的页数(页数从0开始)
		 *
		 * @param adapter-
		 * @param page     下一页，page会自动计数
		 */
		fun onNextPageRequest(adapter: BasePagingFrameAdapter<T>, page: Int)
	}

	override fun getItemViewType(position: Int): Int {
		return if (position < count - 1 || pagingListener == null) {
			BaseAdapterViewType.VIEW_TYPE_CONTENT
		} else {
			when (mLoadingStatus) {
				STATUS_LOADING -> BaseAdapterViewType.VIEW_TYPE_LOADING
				STATUS_LOADING_NEED_TAP -> BaseAdapterViewType.VIEW_TYPE_LOADING_TAP_NEXT
				STATUS_LOADING_END -> BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE
				else -> BaseAdapterViewType.VIEW_TYPE_LOADING
			}
		}
	}

	override fun isEnabled(position: Int): Boolean {
		return getItemViewType(position) == BaseAdapterViewType.VIEW_TYPE_CONTENT
	}

	/**
	 * 创建loadingview的样式
	 *
	 * @param viewType VIEW_TYPE_LOADING, VIEW_TYPE_LOADING_COMPLETE,
	 * VIEW_TYPE_LOADING_TAP_NEXT
	 * @return
	 */
	protected fun onLoadingViewCrate(viewType: Int): View {
		when (viewType) {
			BaseAdapterViewType.VIEW_TYPE_LOADING -> return inflater.inflate(R.layout.adapter_loading_layout, null)
			BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE -> return inflater.inflate(R.layout.adapter_loading_complate_layout, null)
			BaseAdapterViewType.VIEW_TYPE_LOADING_TAP_NEXT -> return inflater.inflate(R.layout.adapter_loading_tap_next_layout, null)
			else -> return inflater.inflate(R.layout.adapter_loading_layout, null)
		}
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		var convertView = convertView
		val type = getItemViewType(position)
		when (type) {
			BaseAdapterViewType.VIEW_TYPE_CONTENT -> return super.getView(position, convertView, parent)
			BaseAdapterViewType.VIEW_TYPE_LOADING -> {
				if (convertView == null) {
					convertView = onLoadingViewCrate(type)
				}
				adapterLoadNextPage(page)
				return convertView
			}
			BaseAdapterViewType.VIEW_TYPE_LOADING_TAP_NEXT -> {
				if (convertView == null) {
					convertView = onLoadingViewCrate(type)
				}
				convertView.setOnClickListener {
					mLoadingStatus = STATUS_LOADING
					notifyDataSetChanged()
				}
				return convertView
			}
			BaseAdapterViewType.VIEW_TYPE_LOADING_COMPLETE -> {
				if (convertView == null) {
					// 总数据如果小于5条，就不显示已加载全部的view
					if (data.size > 5) {
						convertView = onLoadingViewCrate(type)
					} else {
						convertView = LinearLayout(context)
					}
				}
				return convertView
			}
			else -> return super.getView(position, convertView, parent)
		}
	}

	/**
	 * 本次加载分页请求完毕，可以加载下一次请求
	 */
	override fun mayHaveNextPage() {
		page++
		isLoadingNextPage = false
		mLoadingStatus = STATUS_LOADING
		notifyDataSetChanged()
	}

	/**
	 * 点击按钮加载下一页代替之前滑动到底部就自动加载
	 */
	override fun tapNextPage() {
		isLoadingNextPage = false
		mLoadingStatus = STATUS_LOADING_NEED_TAP
		notifyDataSetChanged()
	}

	override fun noMorePage() {
		isLoadingNextPage = false
		mLoadingStatus = STATUS_LOADING_END
		notifyDataSetChanged()
	}

	/**
	 * 同步两个adapter的数据和page
	 *
	 * @param data
	 * @param page
	 */
	fun syncAdapter(data: List<T>?, page: Int) {
		if (data == null) {
			return
		}
		super.resetData(data)
		// 更新page
		this.page = page
		notifyDataSetChanged()
	}

	/**
	 * 设置分页监听器
	 *
	 * @param listener
	 */
	fun setOnPagingListener(listener: PagingListener<T>) {
		this.pagingListener = listener
		// page++;
		if (count == 0) {
			adapterLoadNextPage(this.startPage)
		}
	}

	@Synchronized
	private fun adapterLoadNextPage(page: Int) {
		if (!isLoadingNextPage) {
			this.isLoadingNextPage = true
			Handler(Looper.getMainLooper()).postDelayed({ this@BasePagingFrameAdapter.pagingListener!!.onNextPageRequest(this@BasePagingFrameAdapter, page) }, LOADING_DELAY_TIME.toLong())
		}

	}

	/**
	 * 请求加载下一页
	 */
	fun requestNextPage() {
		adapterLoadNextPage(page)
	}
}
