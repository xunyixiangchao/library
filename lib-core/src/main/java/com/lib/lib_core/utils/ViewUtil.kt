package work.dd.com.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.EditText
import android.widget.ListView

/**
 * Created by lis on 2018/12/13.
 */
object ViewUtil {


	// 状态栏高度
	private var statusBarHeight = 0
	// 屏幕像素点
	private val screenSize = Point()

	/**
	 * 单位换算
	 *
	 * @param context
	 * @param dipValue
	 * @return
	 * @author LiuYuHang
	 * @date 2014年10月23日
	 */
	fun dip2px(context: Context?, dipValue: Float): Int {
		if (context == null)
			return 0
		val scale = context.resources.displayMetrics.density
		return (dipValue * scale + 0.5f).toInt()
	}

	fun getListViewHeight(listView: ListView): Int {
		val listAdapter = listView.adapter ?: return 0

		var totalHeight = 0
		for (i in 0 until listAdapter.count) {
			val listItem = listAdapter.getView(i, null, listView)
			listItem.measure(0, 0)
			totalHeight += listItem.measuredHeight
		}
		totalHeight += listView.dividerHeight * (listAdapter.count - 1) +
				listView.paddingBottom + listView.paddingTop

		return totalHeight
	}

	/**
	 * 单位换算
	 *
	 * @param context
	 * @param pxValue
	 * @return
	 * @author LiuYuHang
	 * @date 2014年10月23日
	 */
	fun px2dip(context: Context, pxValue: Float): Int {
		val scale = context.resources.displayMetrics.density
		return (pxValue / scale + 0.5f).toInt()
	}

	/**
	 * 批量设置View的显示和隐藏
	 *
	 * @param visibility Modifier： Modified Date： Modify：
	 */
	fun setViewVisibility(visibility: Int, vararg views: View) {
		setViewVisibility(visibility, null, *views)
	}

	/**
	 * 批量设置View的显示和隐藏
	 *
	 * @param visibility
	 * @param animation  使用动画
	 * @param views
	 */
	fun setViewVisibility(visibility: Int, animation: Animation?, vararg views: View) {
		if (views == null || views.size == 0)
			return
		for (v in views) {
			if (null == v || v.visibility == visibility) {
				continue
			}

			val runnable = Runnable {
				if (animation == null) {
					v.visibility = visibility
				} else {
					v.visibility = visibility
					v.startAnimation(animation)

				}
			}

			ThreadPoolUtil.runOnMainThread(runnable)
		}
	}

	fun swapView(from: View, to: View) {
		setViewVisibility(View.VISIBLE, to)
		setViewVisibility(View.GONE, from)
	}

	/**
	 * edittext和button做绑定
	 *
	 * @param editText
	 * @param btn
	 */
	fun bindButton(editText: EditText, btn: View) {
		btn.isEnabled = getText(editText).length > 0
		editText.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

			}

			override fun afterTextChanged(s: Editable) {
				btn.isEnabled = s.length > 0
			}
		})
	}

	/**
	 * 从editText中获取里面的文本内容
	 *
	 * @param editText
	 * @return
	 */
	fun getText(editText: EditText): String {
		return if (editText!=null || !TextUtils.isEmpty(editText.text)) "" else editText.text.toString().trim { it <= ' ' }
	}

	/**
	 * 检查editText内容是否为空，如果为空，提示信息，
	 *
	 * @param editText
	 * @param emptyMessage 如果内容为空，弹出的提示
	 * @return true:检测通过，不为空，false:内容为空
	 */
	fun checkEditText(editText: EditText, emptyMessage: String): Editable? {
		val text: String
		if (TextUtils.isEmpty(editText.text)) {
			ToastUtils.showMessage(editText.context, emptyMessage)
		}
		return editText.text
	}

	/**
	 * view设置到group中
	 *
	 * @param viewGroup
	 * @param child
	 */
	fun setViewToGroup(viewGroup: ViewGroup, child: View) {
		if (viewGroup.childCount > 0) {
			viewGroup.removeAllViews()
		}
		viewGroup.addView(child)
	}

	//    public void resetLayoutByDeviceWidth(Context context, View view) {
	//        view.getLayoutDirection()
	//
	//    }


	interface ViewMovedListener {
		fun moveUp()

		fun moveDown()
	}

	/**
	 * 监听listview的上下滑动
	 *
	 * @param touchView
	 * @param movedListener
	 */
	fun setOnScrollerListener(touchView: ListView, movedListener: ViewMovedListener) {
		touchView.setOnTouchListener(object : View.OnTouchListener {

			internal var height = 50
			internal var offsetUp = height * 1.0f
			internal var offsetDown = 10.0f
			internal var oY = 0f
			internal var nY = 0f

			@SuppressLint("ClickableViewAccessibility")
			override fun onTouch(v: View, event: MotionEvent): Boolean {
				when (event.action) {
					MotionEvent.ACTION_DOWN -> oY = event.rawY
					MotionEvent.ACTION_MOVE -> {
						if (oY == 0f) {
							oY = event.rawY
						}
						nY = event.rawY
						if (nY - oY <= -offsetUp) {
							if (touchView.firstVisiblePosition > 0) {
								movedListener.moveUp()
							}
						}// && pagerCanScroll
						if (nY - oY >= offsetDown) {
							movedListener.moveDown()
						}
					}
					MotionEvent.ACTION_UP ->
						// oY = event.getRawY();
						oY = 0f
					else -> {
					}
				}
				return false
			}
		})
	}

	/**
	 * 获得到ListView滚动的距离
	 *
	 * @return
	 */
	fun getScrollY(listView: ListView): Int {
		val c = listView.getChildAt(0) ?: return 0
		val firstVisiblePosition = listView.firstVisiblePosition
		val top = c.top
		return -top + firstVisiblePosition * c.height
	}

	// 获取屏幕像素点
	fun getScreenSize(context: Activity?): Point {

		if (context == null) {
			return screenSize
		}
		val wm = context
				.getSystemService(Context.WINDOW_SERVICE) as WindowManager
		if (wm != null) {
			val mDisplayMetrics = DisplayMetrics()
			val diplay = wm.defaultDisplay
			if (diplay != null) {
				diplay.getMetrics(mDisplayMetrics)
				val W = mDisplayMetrics.widthPixels
				val H = mDisplayMetrics.heightPixels
				if (W * H > 0 && (W > screenSize.x || H > screenSize.y)) {
					screenSize.set(W, H)
				}
			}
		}
		return screenSize
	}

	// 获取状态栏高度
	fun getStatusBarHeight(context: Context): Int {
		if (statusBarHeight <= 0) {
			val frame = Rect()
			(context as Activity).window.decorView.getWindowVisibleDisplayFrame(frame)
			statusBarHeight = frame.top
		}
		if (statusBarHeight <= 0) {
			try {
				val c = Class.forName("com.android.internal.R\$dimen")
				val obj = c.newInstance()
				val field = c.getField("status_bar_height")
				val x = Integer.parseInt(field.get(obj).toString())
				statusBarHeight = context.resources.getDimensionPixelSize(x)

			} catch (e1: Exception) {
				e1.printStackTrace()
			}

		}
		return statusBarHeight
	}
}