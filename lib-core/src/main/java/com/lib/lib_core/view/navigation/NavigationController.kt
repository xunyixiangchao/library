package work.dd.com.navigation

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import work.dd.com.mywindowmanager.R
import work.dd.com.utils.ViewUtil

/**
 * Created by lis on 2018/12/13.
 */
class NavigationController(private val mContext: Context, private val mNavigationListener: NavigationListener?)// bindNavigation(((Activity) context).getWindow().getDecorView());
	: NavigationListener {

	var mNavigationRootView: View? = null
	lateinit var mLeftLayout: LinearLayout
	lateinit var mCenterLayout: LinearLayout
	lateinit var mRightLayout: LinearLayout
	var title:String?=null
	var isHide = false
		private set
	private var rightButton: TextView? = null
	private var mNavigationSettingCallback: NavigationSettingCallback? = null

	/**
	 * 获取中间的按钮区域，需要通过findViewById获取具体按钮
	 *
	 * @return Modifier： Modified Date： Modify：
	 */
	val centerView: View
		get() = mCenterLayout

	/**
	 * 获取左边的按钮区域，需要通过findViewById获取具体按钮
	 *
	 * @return Modifier： Modified Date： Modify：
	 */
	val leftView: View
		get() = mLeftLayout

	/**
	 * 获取右边的按钮区域，需要通过findViewById获取具体按钮
	 *
	 * @return Modifier： Modified Date： Modify：
	 */
	val rightView: View
		get() = mRightLayout

	val height: Int
		get() {
			mNavigationRootView!!.measure(0, 0)
			return mNavigationRootView!!.measuredHeight
		}

	interface NavigationSettingCallback {
		/**
		 * 导航栏title变动的通知
		 *
		 * @param context
		 * @param title
		 */
		fun onNavigationTitleChanged(context: Context, title: String)
	}

	/**
	 * 绑定导航栏布局
	 *
	 * @param parentView Modifier： Modified Date： Modify：
	 */
	fun bindNavigation(parentView: View) {

		mNavigationRootView = parentView.findViewById(R.id.navigation_root_layout)
		if (mNavigationRootView == null) {
			throw NullPointerException("需要id为 navigation_root_layout 的布局")
		}
		ViewUtil.setViewVisibility(View.VISIBLE, mNavigationRootView!!)

		mLeftLayout = mNavigationRootView!!.findViewById(R.id.navigation_left_layout) as LinearLayout
		mCenterLayout = mNavigationRootView!!.findViewById(R.id.navigation_center_layout) as LinearLayout
		mRightLayout = mNavigationRootView!!.findViewById(R.id.navigation_right_layout) as LinearLayout
		mLeftLayout.gravity = Gravity.CENTER_VERTICAL
		mRightLayout.gravity = Gravity.CENTER_VERTICAL

		// 绑定布局之后，需要做初始化设置
		initNavigationSetting(getSetting())
	}

	/**
	 * 初始化setting
	 *
	 * @param setting Modifier： Modified Date： Modify：
	 */

	private fun initNavigationSetting(setting: NavigationSetting) {
		if (setting.backgroudDrawableResid !== -1) {
			setBackgroundResource(setting.backgroudDrawableResid)
		}

		if (setting.dividerResId !== -1) {
			val dividerView = mNavigationRootView!!.findViewById<View>(R.id.view_divider)
			dividerView.setBackgroundResource(setting.dividerResId)
		}
	}

	@JvmOverloads
	fun hideNavigation(hide: Boolean, anim: Boolean = false) {
		if (isHide == hide) {
			return
		}

		isHide = hide
		if (anim) {
			val valueAnimator: ValueAnimator

			//获取导航栏高度
//			val height = (mContext as BaseFrameActivity).getNavigationHeight()
			val height=0
			if (hide) {
				valueAnimator = ValueAnimator.ofInt(height, 0)
			} else {
				valueAnimator = ValueAnimator.ofInt(0, height)
			}
			valueAnimator.addUpdateListener { animation ->
				val animatedValue = animation.animatedValue as Int

				val params = mNavigationRootView!!.layoutParams as RelativeLayout.LayoutParams
				params.height = animatedValue
				mNavigationRootView!!.layoutParams = params
			}
			valueAnimator.interpolator = LinearInterpolator()
			valueAnimator.duration = 200
			valueAnimator.repeatCount = 0
			valueAnimator.start()
		} else {
			ViewUtil.setViewVisibility(if (hide) View.GONE else View.VISIBLE, this!!.mNavigationRootView!!)
		}
	}

	fun setDividerResource(color: Int) {
		val dividerView = mNavigationRootView!!.findViewById<View>(R.id.view_divider)
		dividerView.setBackgroundResource(color)
	}

	/**
	 * 隐藏导航栏地下的线
	 *
	 * @param hide
	 */
	fun hideNavigationLine(hide: Boolean) {
		val dividerView = mNavigationRootView!!.findViewById<View>(R.id.view_divider)
		dividerView.setVisibility(if (hide) View.GONE else View.VISIBLE)
	}

	/**
	 * 设置导航栏的中心标题
	 *
	 * @param title Modifier： Modified Date： Modify：
	 */
	@JvmOverloads
	fun setTitle(title: CharSequence) {
		if (TextUtils.isEmpty(title)) {
			return
		}
		// android:textColor="@color/color_global_colorblack3"
		// android:textSize="18sp"
		// TextView textView = new TextView(mContext);
		// textView.setText(title);
		this.title = title.toString()

		if (null != mNavigationSettingCallback) {
			mNavigationSettingCallback!!.onNavigationTitleChanged(mContext, title.toString())
		}

		val textView = View.inflate(mContext, R.layout.frame_navigation_title_layout, null) as TextView
		textView.maxEms = 10
		textView.text = title
		setTitle(textView)
	}

	/**
	 * 设置标题的res
	 *
	 * @param titleRes
	 */
	fun setTitle(@StringRes titleRes: Int) {
		setTitle(mContext.resources.getString(titleRes))
	}

	fun setTitle(view: View) {
		//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		//        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		//        mCenterLayout.setLayoutParams(lp);

		ViewUtil.setViewToGroup(mCenterLayout, view)
		//        if (mCenterLayout.getChildCount() > 0) {
		//            mCenterLayout.removeAllViews();
		//        }
		//        mCenterLayout.addView(view);
	}

	/**
	 * 设置左边的按钮
	 *
	 * @param title
	 * @param listener Modifier： Modified Date： Modify：
	 */
	fun setLeftButton(title: CharSequence, listener: View.OnClickListener) {
		val button = View.inflate(mContext, R.layout.frame_navigation_title_button_layout, null) as TextView
		button.text = title
		button.setOnClickListener(listener)
		setLeftButton(button)
	}

	fun setLeftButton(view: View) {
		hideLeftButton()
		mLeftLayout.addView(view)
	}

	fun hideLeftButton() {
		mLeftLayout.removeAllViews()
	}

	fun hideRightButton(hide: Boolean) {
		ViewUtil.setViewVisibility(if (hide) View.GONE else View.VISIBLE, mRightLayout)
	}

	/**
	 * 设置右边的按钮
	 *
	 * @param title
	 * @param listener Modifier： Modified Date： Modify：
	 */
	fun setRightButton(title: CharSequence, listener: View.OnClickListener) {
		rightButton = View.inflate(mContext, R.layout.frame_navigation_title_button_layout, null) as TextView
		//        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		rightButton!!.text = title
		rightButton!!.setOnClickListener(listener)
		setRightButton(rightButton!!)
	}

	fun getRightButton(): View? {
		return if (rightButton != null) {
			rightButton
		} else null
	}

	fun setRightButton(view: View) {
		ViewUtil.setViewToGroup(mRightLayout, view)
		//        hideRightButton();
		//        mRightLayout.addView(view);
	}

	fun setLeftButton(view: View, margin: Int) {
		var lp: LinearLayout.LayoutParams? = view.layoutParams as LinearLayout.LayoutParams
		if (null == lp) {
			lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		}
		lp.leftMargin = ViewUtil.dip2px(view.context, margin.toFloat())
		lp.rightMargin = ViewUtil.dip2px(view.context, margin.toFloat())
		view.layoutParams = lp

		setLeftButton(view)
	}

	fun setRightButton(view: View, margin: Int) {
		var lp: LinearLayout.LayoutParams? = view.layoutParams as LinearLayout.LayoutParams
		if (null == lp) {
			lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		}
		lp.leftMargin = ViewUtil.dip2px(view.context, margin.toFloat())
		lp.rightMargin = ViewUtil.dip2px(view.context, margin.toFloat())
		view.layoutParams = lp

		setRightButton(view)
	}

	fun setRightButton(resId: Int, listener: View.OnClickListener) {
		val view = View.inflate(mContext, R.layout.frame_navigation_title_image_layout, null)
		val rightButton = view.findViewById(R.id.navigation_image_layout) as ImageView
		rightButton.setImageResource(resId)
		rightButton.setOnClickListener(listener)
		setRightButton(view)
	}

	private fun hideRightButton() {
		mRightLayout.removeAllViews()
	}

	/**
	 * 设置导航栏的背景色
	 *
	 * @param color Modifier： Modified Date： Modify：
	 */
	fun setBackgroundColor(color: Int) {
		mNavigationRootView!!.setBackgroundColor(color)
	}

	/**
	 * 设置导航栏的背景色
	 *
	 * @param resId Modifier： Modified Date： Modify：
	 */
	fun setBackgroundResource(resId: Int) {
		mNavigationRootView!!.setBackgroundResource(resId)
	}

	fun setNavigationSettingCallback(callback: NavigationSettingCallback) {
		this.mNavigationSettingCallback = callback
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ichsy.public_libs.view.navigation.NavigationListener#popBack()
	 */
	override fun popBack() {
		if (checkListener()) {
			mNavigationListener!!.popBack()
		}
	}

	override fun pushActivity(intent: Intent) {
		if (checkListener()) {
			mNavigationListener!!.pushActivity(intent)
		}
	}

	override fun pushActivity(clz: Class<*>) {
		if (checkListener()) {
			mNavigationListener!!.pushActivity(clz)
		}
	}

	override fun pushActivity(clz: Class<*>, intent: Intent) {
		if (checkListener()) {
			mNavigationListener!!.pushActivity(clz, intent)
		}
	}

	fun checkListener(): Boolean {
		return if (mNavigationListener == null) {
			//        }
			//            throw new NullPointerException("navigationListener is null");
			false
		} else {
			true
		}
	}

	companion object {

		private var setting: NavigationSetting? = null

		fun getSetting(): NavigationSetting {
			if (null == setting) {
				setting = NavigationSetting()
			}
			return setting as NavigationSetting
		}
	}

}
/**
 * 隐藏导航栏
 *
 * @param hide Modifier： Modified Date： Modify：
 */
