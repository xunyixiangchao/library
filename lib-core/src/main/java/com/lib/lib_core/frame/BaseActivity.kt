package com.lib.lib_core.frame

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import butterknife.ButterKnife
import com.lib.lib_core.R
import com.lib.lib_core.comm.bus.url.BusEventObserver
import com.lib.lib_core.comm.bus.url.UrlParser
import com.lib.lib_core.comm.helper.FrameUiHelper
import com.lib.lib_core.comm.utils.LogUtils
import work.dd.com.dialog.DialogUiBuilder
import work.dd.com.utils.ViewUtil

/**
 * Created by lis on 2018/12/17.
 */
abstract class BaseActivity : BaseDialogFrameActivity(), BusEventObserver {

	/**
	 * 获取顶部导航栏的高度
	 *
	 * @return
	 */
	override fun getNavigationHeight(): Int {
		return ViewUtil.dip2px(this, 42f) + 1
	}

	private val observer = FrameEventObserver()

	override fun onBusEvent(event: String, message: Any?) {
	}
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		ButterKnife.bind(this)

		LogUtils.i("url_parser_debug", "parser url is: leyou://native?native=" + javaClass.name)

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val window = getWindow()
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
			window.statusBarColor = resources.getColor(R.color.color_white)
			window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
		}

		//        String intentTitle = get

		//        if (enableSlider()) {
		//            SlidrConfig config = new SlidrConfig.Builder()
		//                    .primaryColor(getResources().getColor(R.color.colorPrimary))
		//                    .sensitivity(1f)
		//                    .scrimColor(Color.BLACK)
		//                    .scrimStartAlpha(0.8f)
		//                    .scrimEndAlpha(0f)
		//                    .velocityThreshold(2400)
		//                    .distanceThreshold(0.25f)
		//                    .edge(true | false)
		//                    .edgeSize(0.18f) // The % of the screen that counts as the edge, default 18%
		////                                .listener(new SlidrListener(){...})
		//                    .build();
		//            Slidr.attach(this, config);
		//        }

		//        mainHandler = new Handler(Looper.getMainLooper());

		dialogHUB.setDialogBackground(Color.WHITE)
		dialogHUB.setMarginTopAndBottom(getNavigationHeight(), 0)

		navigationController.setBackgroundColor(resources.getColor(R.color.color_white))
		navigationController.setDividerResource(R.color.color_line)
		//        mContentView.setBackgroundColor(getResources().getColor(R.color.le_bg_white));
		mContentView!!.setBackgroundResource(R.color.base_background)
		//        mContentView.setBackgroundResource(R.color.le_color_background);
		//        mContentView.setBackgroundResource(R.color.background_material_light);

		//        dialogHUB.setMarginTopAndBottom(0, (int) getResources().getDimension(R.dimen.navigation_bottom_height));

		val dialogUiBuilder = FrameUiHelper.instance.getDialogUiBuilder(context)
		dialogHUB.setProgressUiBuilder(dialogUiBuilder)

		dialogHUB.setMessageViewUiBuilder(object : DialogUiBuilder {

			override fun onViewCreate(inflater: LayoutInflater): View {
				return inflater.inflate(R.layout.activity_empty_view_layout, null)
			}

			override fun onViewDraw(view: View, message: String?) {
				val iconView = view.findViewById(R.id.imageview_icon) as ImageView
				val messageView = view.findViewById(R.id.textview_message) as TextView

				val resId = onSetErrorViewImageRes()

				if (resId != 0) {
					iconView.setImageResource(resId)
				}
				messageView.text = message
			}
		})

		dialogHUB.setNetErrorUiBuilder(object : DialogUiBuilder {
			override fun onViewCreate(inflater: LayoutInflater): View {
				return inflater.inflate(R.layout.view_net_bad_layout, null)
			}

			override fun onViewDraw(view: View, message: String?) {}

		})

//		BusManager.getInstance().register(EventKeys.EVENT_LOGIN_ON_LOG, observer)
//		BusManager.getInstance().register(EventKeys.EVENT_LOGIN_OUT_LOG, observer)
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			//            DebugHelper.Instance.getDebugHelper().tryOpen(this, "leyou://native?native=com.capelabs.leyou.ui.activity.debug.DebugActivity");
//			DebugHelper.Instance.getDebugHelper().tryOpen({
//				UrlParser.getInstance().parser(getActivity(), "leyou://native?native=com.capelabs.leyou.ui.activity.debug.DebugActivity")
//				null
//			})
		}
		return super.onKeyDown(keyCode, event)
	}

	/**
	 * 返回错误页面的ErrorImage
	 *
	 * @return
	 */
	protected fun onSetErrorViewImageRes(): Int {
		return R.drawable.public_empty_pic
	}

	protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
		super.onActivityResult(requestCode, resultCode, data)
//		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
	}

	protected override fun onDestroy() {
		super.onDestroy()

//		BusManager.getInstance().unRegister(EventKeys.EVENT_LOGIN_ON_LOG, observer)
//		BusManager.getInstance().unRegister(EventKeys.EVENT_LOGIN_OUT_LOG, observer)
	}

	fun pushActivity(className: String) {
		UrlParser.instance.parser(context, "leyou://native?native=$className")
	}

	/**
	 * 重写此方法可以启用或者禁用滑动关闭
	 *
	 * @return
	 */
	@Deprecated("")
	fun enableSlider(): Boolean {
		return true
	}

	//是否登录
	private inner class FrameEventObserver : BusEventObserver {
		override fun onBusEvent(event: String, message: Any?) {
		}

		/**
		 * 当收到event事件，会执行此函数
		 *
		 * @param event   event事件
		 * @param message 事件所带的参数
		 */
//		fun onBusEvent(event: String, message: Any) {
//			if (event == EventKeys.EVENT_LOGIN_ON_LOG) {
//				if (!isActivityFinish()) {
//					if (message as Boolean) {
//						onUserLogin()
//					}
//				}
//			} else if (event == EventKeys.EVENT_LOGIN_OUT_LOG) {
//				if (!isActivityFinish()) {
//					if (!(message as Boolean)) {
//						onUserLogout()
//					}
//				}
//			}
//		}
//	}
//
//	fun onBusEvent(event: String, message: Any) {
//
//	}

//	fun onUserLogin() {
//		val fragments = getFragments()
//		if (ObjectUtils.isNotNull(fragments)) {
//			for (fragment in fragments) {
//				if (ObjectUtils.isNull(fragment)) {
//					continue
//				}
//				if (fragment is UserLoginStatusHandler) {
//					(fragment as UserLoginStatusHandler).onUserLogin()
//				}
//			}
//		}
//	}

//	fun onUserLogout() {
//		val fragments = getFragments()
//		if (ObjectUtils.isNotNull(fragments)) {
//			for (fragment in fragments) {
//				if (ObjectUtils.isNull(fragment)) {
//					continue
//				}
//				if (fragment is UserLogoutStatusHandler) {
//					(fragment as UserLogoutStatusHandler).onUserLogout()
//				}
//			}
//		}
//	}

//	fun finishWebView() {
//
//	}

	}
}