package com.lib.lib_core.comm.logwatch.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.ClipboardManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lib.lib_core.R
import com.lib.lib_core.comm.logwatch.InfoBean
import com.lib.lib_core.comm.logwatch.LogWatcher
import com.lib.lib_core.comm.logwatch.db.DAO
import com.lib.lib_core.comm.utils.ViewHolder
import com.lib.lib_core.frame.BaseFrameActivity
import com.lib.lib_core.frame.adapter.BasePagingFrameAdapter
import work.dd.com.utils.ToastUtils
import java.util.*

/**
 * logwatch点击之后的UI
 * Created by lis on 2018/12/13.
 */
class LogWatcherActivity : BaseFrameActivity() {

	private var mCurrentType: String? = null

	/**
	 * 初始化布局，设置的view
	 *
	 * @return Modifier： Modified Date： Modify：
	 */
	protected override fun onLayoutInflate(): Int {
		return R.layout.activity_logwatcher_layout
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		navigationController.setBackgroundColor(Color.WHITE)
		navigationController.setDividerResource(android.R.color.darker_gray)
		navigationController.setTitle("LogWatcher")

		navigationController.setRightButton("清除", View.OnClickListener {
			val dao = DAO(context)
			dao.delete()

			val listView = findViewById(R.id.lv_main) as ListView

			if (null != listView.adapter) {
				(listView.adapter as LogWatcherAdapter).clearAdapter()
			}
		})

		initView()

	}

	protected override fun onResume() {
		super.onResume()
		LogWatcher.getInstance().dismiss()
	}

	protected override fun onPause() {
		super.onPause()
		LogWatcher.getInstance().show()
	}


	private fun initView() {
		val headerView = findViewById(R.id.ll_header) as LinearLayout
		val listView = findViewById(R.id.lv_main) as ListView

		val adapter = LogWatcherAdapter(this)

		val pagingListener = object : BasePagingFrameAdapter.PagingListener<InfoBean> {

			override fun onNextPageRequest(adapter: BasePagingFrameAdapter<InfoBean>, page: Int) {
				val dao = DAO(activity)
				val query: ArrayList<InfoBean>?

				val size = 50

				if (TextUtils.isEmpty(mCurrentType) || "全部" == mCurrentType) {
					query = dao.query(page, size)
				} else {
					query = dao.query(mCurrentType!!, page, size)
				}
				if (null != query) {
					adapter.addData(query)
					if (query!!.size < size) {
						adapter.noMorePage()
					} else {
						adapter.mayHaveNextPage()
					}
				} else {
					adapter.noMorePage()
				}
			}
		}


		val headerList = DAO(activity).queryCateGory()
		mCurrentType = headerList.get(0).classesName

		for (infoBean in headerList) {
			val headerItem = Button(activity)

			headerItem.setText(infoBean.classesName)

			headerItem.setOnClickListener(View.OnClickListener { v ->
				if ((v as Button).text.toString() == mCurrentType) {
					return@OnClickListener
				}

				mCurrentType = infoBean.classesName
				adapter.clearAdapter()
				adapter.setOnPagingListener(pagingListener)
			})

			headerView.addView(headerItem)
		}

		listView.setAdapter(adapter)

		adapter.setOnPagingListener(pagingListener)

		listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			val item = adapter.getItem(position)

			val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			//                        ClipData myClip = ClipData.newPlainText("text", text);
			myClipboard.text = item!!.classesContent

			ToastUtils.showMessage(activity, "复制成功")
		}

		//        listView.setSelection(adapter.getCount() - 1);
	}

	private inner class LogWatcherAdapter(context: Context) : BasePagingFrameAdapter<InfoBean>(context) {

		/**
		 * 在adapter的getView中，首次创建view，初始化并返回创建的布局
		 *
		 * @param position
		 * @param inflater
		 * @param parent
		 * @return
		 */
		override fun onViewCreate(position: Int, inflater: LayoutInflater, parent: ViewGroup): View {
			return inflater.inflate(R.layout.adapter_logwatcher_item, parent, false)
		}

		/**
		 * 在adapter的getView中，每次滑动listView会循环执行本方法
		 *
		 * @param position
		 * @param item
		 * @param convertView
		 */
		override fun onViewAttach(position: Int, item: InfoBean, convertView: View) {
			val contentTextView = ViewHolder.get<TextView>(convertView, R.id.tv_content)

			if (contentTextView != null) {
				contentTextView.setText(item.classesContent)
			}
		}
	}
}