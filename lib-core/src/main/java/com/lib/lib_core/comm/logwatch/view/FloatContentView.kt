package com.lib.lib_core.comm.logwatch.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.lib.lib_core.R
import com.lib.lib_core.comm.logwatch.InfoBean
import com.lib.lib_core.comm.logwatch.MyWindowManager
import com.lib.lib_core.comm.logwatch.db.DAO
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
class FloatContentView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) : LinearLayout(mContext, attrs), View.OnClickListener {

	var mWidth: Int = 0
	var mHeight: Int = 0
	private val manager: MyWindowManager
	private var adapter: ContentAdapter? = null
	private val mBtnClose: Button
	private val mBtnBack: Button
	private val mListView: ListView
	private var dao: DAO? = null
	private val mHorizontalLayout: LinearLayout
	private val mTextClear: TextView

	init {
		LayoutInflater.from(mContext).inflate(R.layout.logwatch_float_content_view, this)
		manager = MyWindowManager.getInstance(mContext)
		val params = findViewById<View>(R.id.content).layoutParams
		mWidth = params.width
		mHeight = params.height

		mBtnClose = findViewById(R.id.bt_close) as Button
		mBtnBack = findViewById(R.id.bt_back) as Button
		mListView = findViewById(R.id.list) as ListView
		mHorizontalLayout = findViewById(R.id.horizontal_view) as LinearLayout
		mTextClear = findViewById(R.id.clear) as TextView

		mBtnClose.setOnClickListener(this)
		mBtnBack.setOnClickListener(this)
		mTextClear.setOnClickListener(this)
		if (dao == null) {
			dao = DAO(mContext)
		}
		mArrayList = dao!!.query()//始终读取数据库里的内容
		val list = ArrayList<String>()
		var flag = false
		for (j in mArrayList!!.indices) {
			if (j == 0) {
				list.add(mArrayList!![0].classesName!!)
			}
			for (i in list.indices) {
				if (list[i] == mArrayList!![j].classesName) {
					flag = true
					break
				} else {
					flag = false
				}
			}
			if (!flag) {
				list.add(mArrayList!![j].classesName!!)
			}
		}
		mHorizontalLayout.removeAllViews()
		if (list.size != 0) {
			val view = LinearLayout.inflate(mContext, R.layout.logwatch_content_btn, null)
			val mTextName = view.findViewById(R.id.text) as TextView
			mTextName.text = "全部"
			mHorizontalLayout.addView(view)
			mTextName.setOnClickListener {
				mArrayList!!.clear()
				mArrayList = dao!!.query()
				setData(mContext)
			}
		}
		for (i in list.indices) {
			val view1 = LinearLayout.inflate(mContext, R.layout.logwatch_content_btn, null)
			val mTextName1 = view1.findViewById(R.id.text) as TextView
			mTextName1.text = list[i]
			mHorizontalLayout.addView(view1)
			mTextName1.setOnClickListener {
				val classesName = mTextName1.text.toString()
				mArrayList!!.clear()
				mArrayList = dao!!.query(classesName)
				setData(mContext)
			}
		}
		setData(mContext)
	}

	private fun setData(context: Context) {
		adapter = ContentAdapter(context)
		adapter!!.addData(mArrayList)
		mListView.setAdapter(adapter)
		mListView.setSelection(mArrayList!!.size - 1)
	}

	override fun onClick(v: View) {
		if (v === mBtnClose) {
			manager.dismiss()
		} else if (v === mBtnBack) {
			manager.back()
		} else if (v === mTextClear) {
			if (dao == null) {
				dao = DAO(mContext)
			}
			dao!!.delete()
			mArrayList!!.clear()
			setData(mContext)
			mHorizontalLayout.removeAllViews()
		}
	}

	companion object {
		private var mArrayList: ArrayList<InfoBean>? = null
	}

}

