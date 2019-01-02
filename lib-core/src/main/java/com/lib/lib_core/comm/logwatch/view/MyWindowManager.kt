package com.lib.lib_core.comm.logwatch

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.lib.lib_core.R
import com.lib.lib_core.comm.logwatch.ui.LogWatcherActivity
import com.lib.lib_core.comm.logwatch.view.FloatContentView
import com.lib.lib_core.comm.logwatch.view.FloatView

/**
 * Created by lis on 2018/12/13.
 */
class MyWindowManager {
	private var paramsView: WindowManager.LayoutParams? = null
	private var paramsContentView: WindowManager.LayoutParams? = null
	private var floatView: FloatView? = null
	private var floatContentView: FloatContentView? = null
	private var moveViewContent: String? = null

	private var isActivityOpen = false

	val isShow: Boolean
		get() = if (floatView != null) {
			floatView!!.getParent() != null
		} else {
			false
		}

	val isUpdate: Boolean
		get() = floatContentView != null

	/**
	 * 显示默认悬浮框
	 *
	 * @return
	 */
	// 悬浮框的类型
	// 悬浮框的行为
	// 悬浮框的对齐方式
	// 悬浮框的宽度
	// 悬浮框的高度
	// 横向位置
	// 纵向位置
	val view: FloatView
		get() {
			if (floatView == null) {
				floatView = FloatView(context!!)
				val mTextViewMove = floatView!!.findViewById(R.id.move) as TextView
				if (!TextUtils.isEmpty(moveViewContent)) {
					mTextViewMove.text = moveViewContent
				}
			}
			if (paramsView == null) {
				paramsView = WindowManager.LayoutParams()
				paramsView!!.type = WindowManager.LayoutParams.TYPE_PHONE
				paramsView!!.format = PixelFormat.RGBA_8888
				paramsView!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				paramsView!!.gravity = Gravity.LEFT or Gravity.TOP
				paramsView!!.width = floatView!!.mWidth
				paramsView!!.height = floatView!!.mHeight
				paramsView!!.x = displayWidth - floatView!!.mWidth
				paramsView!!.y = displayHeight / 2
			}
			return floatView as FloatView
		}

	/**
	 * 显示内容的悬浮框
	 *
	 * @return
	 */
	val contentView: View
		get() {
			if (floatContentView == null) {
				floatContentView = FloatContentView(context!!)
			}
			if (paramsContentView == null) {
				paramsContentView = WindowManager.LayoutParams()
				paramsContentView!!.type = WindowManager.LayoutParams.TYPE_PHONE
				paramsContentView!!.format = PixelFormat.RGBA_8888
				paramsContentView!!.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				paramsContentView!!.gravity = Gravity.LEFT or Gravity.TOP
				paramsContentView!!.width = displayWidth / 8 * 7
				paramsContentView!!.height = displayHeight / 8 * 7
				paramsContentView!!.x = (displayWidth - displayWidth / 8 * 7) / 2
				paramsContentView!!.y = (displayHeight - displayHeight / 8 * 7) / 2 - 50
			}
			return floatContentView!!
		}

	/**
	 * 显示悬浮框
	 */
	fun show(text: String) {
		moveViewContent = text
		floatView = view
		if (floatView!!.getParent() != null) {
			winManager!!.removeView(floatView)
		}
		winManager!!.addView(floatView, paramsView)
	}

	/**
	 * 显示悬浮框
	 */
	fun show() {
		floatView = view
		if (floatView!!.getParent() != null) {
			winManager!!.removeView(floatView)
		}
		winManager!!.addView(floatView, paramsView)
	}

	fun dismissFloat() {
		winManager!!.removeView(floatView)
		floatContentView = null
	}


	/**
	 * 点击悬浮框后的显示详细信息
	 */
	fun showContent() {
		val floatContentView = contentView
		if (floatContentView.parent != null) {
			winManager!!.removeView(floatContentView)
		}
		//		winManager.addView(floatContentView, paramsContentView);
		//		if (floatView != null) {
		//			winManager.removeView(floatView);
		//			floatView = null;
		//		}

		if (!isActivityOpen) {
			val intent = Intent(context, LogWatcherActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
			context!!.startActivity(intent)
		}
		isActivityOpen = !isActivityOpen
	}

	// 移动悬浮框
	fun move(view: View, delatX: Int, deltaY: Int) {
		if (view === floatView) {
			paramsView!!.x += delatX
			paramsView!!.y += deltaY
			winManager!!.updateViewLayout(view, paramsView)
		}
	}

	// 移除内容悬浮框
	fun dismiss() {
		winManager!!.removeView(floatContentView)
		floatContentView = null
	}

	fun back() {
		winManager!!.addView(view, paramsView)
		winManager!!.removeView(floatContentView)
		floatContentView = null
	}

	companion object {

		private var manager: MyWindowManager? = null
		private var winManager: WindowManager? = null
		private var context: Context? = null
		private var displayWidth: Int = 0
		private var displayHeight: Int = 0

		@Synchronized
		fun getInstance(context: Context): MyWindowManager {
			if (manager == null) {
				MyWindowManager.context = context
				winManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
				displayWidth = winManager!!.defaultDisplay.width
				displayHeight = winManager!!.defaultDisplay.height
				manager = MyWindowManager()
			}
			return manager as MyWindowManager
		}
	}

}
