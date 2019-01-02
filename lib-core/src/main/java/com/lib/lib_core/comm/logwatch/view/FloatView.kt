package com.lib.lib_core.comm.logwatch.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.TextView
import com.lib.lib_core.R
import com.lib.lib_core.comm.logwatch.MyWindowManager

/**
 * Created by lis on 2018/12/13.
 */
class FloatView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

	private val mTextMove: TextView
	private val manager: MyWindowManager
	var mWidth: Int = 0
	var mHeight: Int = 0
	private var preX: Int = 0
	private var preY: Int = 0
	private var downX: Int = 0
	private var downY: Int = 0
	private var x: Int = 0
	private var y: Int = 0
	private var isMove: Boolean = false

	init {
		// 填充布局，并添加至
		LayoutInflater.from(context).inflate(R.layout.logwatch_float_view, this)
		mTextMove = findViewById(R.id.move) as TextView
		// 宽高
		mWidth = mTextMove.layoutParams.width
		mHeight = mTextMove.layoutParams.height
		manager = MyWindowManager.getInstance(context)
	}

	override fun onTouchEvent(event: MotionEvent): Boolean {
		when (event.action) {
			MotionEvent.ACTION_DOWN -> {
				preX = event.rawX.toInt()
				preY = event.rawY.toInt()
				downX = event.rawX.toInt()
				downY = event.rawY.toInt()
				isMove = false
			}
			MotionEvent.ACTION_MOVE -> {
				x = event.rawX.toInt()
				y = event.rawY.toInt()
				manager.move(this, x - preX, y - preY)
				preX = x
				preY = y
			}
			MotionEvent.ACTION_UP -> {
				val currentX = event.rawX.toInt()
				val currentY = event.rawY.toInt()
				val limitX = if (downX - currentX > 0) downX - currentX else currentX - downX
				val limitY = if (downY - currentY > 0) downY - currentY else currentY - downY
				isMove = !(limitX < 5 || limitY < 5)
				if (!isMove) {// 是否是移动，主要是区分click
					manager.showContent()
				}
			}
		}
		return super.onTouchEvent(event)
	}
}
