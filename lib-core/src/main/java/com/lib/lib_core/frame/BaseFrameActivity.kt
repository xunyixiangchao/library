package com.lib.lib_core.frame

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import com.lib.lib_core.R
import com.lib.lib_core.comm.bus.url.BusEventObserver
import com.lib.lib_core.comm.bus.url.BusManager
import com.lib.lib_core.comm.bus.url.UrlParser
import com.lib.lib_core.comm.exceptions.CrashHandler
import com.lib.lib_core.comm.permission.PermissionManager
import com.lib.lib_core.comm.utils.DeviceUtil
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.comm.utils.NavigationUtil
import com.lib.lib_core.frame.interfaces.FragmentInterface
import work.dd.com.navigation.NavigationController
import work.dd.com.navigation.NavigationListener

/**
 * 基础框架层父类
 * Created by lis on 2018/12/13.
 */
abstract class BaseFrameActivity : FragmentActivity(), NavigationListener, FragmentInterface {

	//    public static final String BUNDLE_INTENT_SECOND_ACTION_EVENT = "BUNDLE_INTENT_SECOND_ACTION_EVENT";
	//    public static final String BUNDLE_INTENT_FIRST_ACTION_EVENT_RUN = "BUNDLE_INTENT_FIRST_ACTION_EVENT_RUN";

	//    public static final String CACHE_ACTIVITY_DATA_SAVER = "CACHE_ACTIVITY_DATA_SAVER";

	// 导航栏
	lateinit var navigationController: NavigationController
	private var mMainHandler: Handler? = null
	private var mLayoutInflater: LayoutInflater? = null

	protected var mContentView: FrameLayout? = null
	var isActivityFinish = false
		private set
	open fun getNavigationHeight(): Int {
		return 0
	}
	private val activityEventObserver = object : BusEventObserver {

		override fun onBusEvent(event: String, message: Any?) {
			if (EVENT_FINISH_ACTIVITY == event) {
				if (null != message) {
					val className = message as String?
					if (activity.javaClass.name == className) {
						finish()
					}
				}
			} else if (EVENT_PUSH_HOMEPAGE == event) {
				//当前页面的activity类名
				val currentActivityClassName = activity.javaClass.name
				//主页面的类名
				val mainActivityClass = message as String?

				if (currentActivityClassName != mainActivityClass) {
					finish()
				}
			}
		}
	}

	val context: Context
		get() = this

	val activity: Activity
		get() = this

	private val `object` = Any()

	val fragmentss: List<Fragment>?
		get() {
			val fm = supportFragmentManager

			return if (fm.isDestroyed) {
				null
			} else fm.fragments
		}

	/**
	 * 获取主线程的handler
	 *
	 * @return
	 */
	val mainHandler: Handler
		get() {
			if (null == mMainHandler) {
				mMainHandler = Handler(Looper.getMainLooper())
			}
			return mMainHandler!!
		}

	/**
	 * 初始化布局，设置的view
	 *
	 * @return Modifier： Modified Date： Modify：
	 */
	protected abstract fun onLayoutInflate(): Int


	//    private HashMap<String, Object> activitySelfDataMap;//私有存储的数据

	//    /**
	//     * 持久存储数据
	//     *
	//     * @param key
	//     * @param value
	//     * @param <Value>
	//     */
	//    public <Value extends Serializable> void put(@NonNull String key, @NonNull Value value) {
	////        checkNotNull(key);
	////        checkNotNull(value);
	//        checkDataMap();
	//        activitySelfDataMap.put(key, value);
	//    }

	//    /**
	//     * 获取持久数据
	//     *
	//     * @param key
	//     * @param <Value>
	//     * @return
	//     */
	//    public <Value extends Serializable> Value get(@NonNull String key) {
	////        checkNotNull(key);
	//        checkDataMap();
	//        return (Value) activitySelfDataMap.get(key);
	//    }
	//
	//    public void checkDataMap() {
	//        if (null == activitySelfDataMap) {
	//            activitySelfDataMap = new HashMap<>();
	//        }
	//    }

	/*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
	override fun onCreate(savedInstanceState: Bundle?) {
		requestWindowFeature(Window.FEATURE_NO_TITLE)

		super.onCreate(savedInstanceState)

		//        if (null != savedInstanceState) {
		//            checkDataMap();
		//            LogUtils.i("frame", "savedInstanceState begin loading");
		//            String cacheString = savedInstanceState.getString(CACHE_ACTIVITY_DATA_SAVER);
		//            activitySelfDataMap = ObjectUtils.hex2Object(cacheString, HashMap.class);
		//            LogUtils.i("frame", "savedInstanceState begin loading end");
		//        }

		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

		super.setContentView(R.layout.frame_root_layout)
		//        mLayoutTitle = (LinearLayout) findViewById(id.navigation_root_layout);
		val childId = onLayoutInflate()

		//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//        toolbar.setTitle("这里是Title");
		//        toolbar.setSubtitle("这里是子标题");
		//        toolbar.setLogo(R.drawable.icon);
		//        setSupportActionBar(toolbar);

		// 侵入式状态栏
		//		if (VERSION.SDK_INT >= 19) {
		//			View navigationView = findViewById(id.navigation_root_layout);
		//			// navigationView.setPadding(navigationView.getPaddingLeft(),
		//			// DeviceUtil.getStatusHeight(this),
		//			// navigationView.getPaddingRight(),
		//			// navigationView.getPaddingBottom());
		//			// 透明状态栏
		//			// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//			// // 透明导航栏
		//			// //
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//
		//			Window window = getWindow();
		//			/*
		//			 * window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
		//			 * , WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//			 */
		//			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		//			window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		//
		//			int statusBarHeight = DeviceUtil.getStatusHeight(this);
		//			navigationView.setPadding(0, statusBarHeight, 0, 0);
		//		}

		mContentView = findViewById(R.id.view_root_layout) as FrameLayout
		//        mContentView.removeAllViews();

		if (childId > 0) {
			val childView = LayoutInflater.from(this).inflate(childId, null)
			mContentView!!.addView(childView)
		}

		// 初始化并绑定导航栏
		this.navigationController = initNavigationController(window.decorView)

		BusManager.default.register(EVENT_FINISH_ACTIVITY, activityEventObserver)
		BusManager.default.register(EVENT_PUSH_HOMEPAGE, activityEventObserver)
	}

	/**
	 * 获取传递过来的参数
	 *
	 * @param name
	 * @return
	 */
	fun getStringExtra(name: String): String? {
		val intent = intent
		var result: String? = null
		return if (null == intent) {
			null
		} else if (!TextUtils.isEmpty(intent.getStringExtra(name))) {
			intent.getStringExtra(name)
		} else if (intent.getIntExtra(name, -100) != -100) {
			intent.getIntExtra(name, -100).toString() + ""
		} else {
			result
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		if (null != activityEventObserver) {
			BusManager.getInstance().unRegister(EVENT_FINISH_ACTIVITY, activityEventObserver)
			BusManager.getInstance().unRegister(EVENT_PUSH_HOMEPAGE, activityEventObserver)
		}
		BusManager.getInstance().onActivityDestroy(this)

		isActivityFinish = true
	}

	/**
	 * 初始化NavigationController
	 *
	 * @return 返回初始化的初始化NavigationController控制器
	 */
	fun initNavigationController(view: View): NavigationController {
		val navigationController = NavigationController(this, this)
		navigationController.bindNavigation(view)

		val imageButton = LayoutInflater.from(this).inflate(R.layout.frame_navigation_title_left_button, null)
		imageButton.setOnClickListener(View.OnClickListener { navigationController.popBack() })
		val leftButton = imageButton.findViewById<ImageView>(R.id.view_navigation_left_button)
		leftButton.setOnClickListener(View.OnClickListener { navigationController.popBack() })

		navigationController.setLeftButton(imageButton)

		return navigationController
	}


	/*
     * (non-Javadoc)
     *
     * @see android.app.Activity#setContentView(android.view.View)
     */
	@SuppressLint("ResourceType")
	override fun setContentView(child: Int) {
		// super.setContentView(view);
		super.setContentView(R.layout.frame_root_layout)
		mContentView!!.removeAllViews()
		//        mContentView.addView(view);

		mContentView = findViewById(R.id.view_root_layout) as FrameLayout
		//        mContentView.removeAllViews();

		if (child > 0) {
			mContentView!!.addView(LayoutInflater.from(this).inflate(child, null))
		}
		// 初始化并绑定导航栏
		this.navigationController = initNavigationController(window.decorView)
	}


	override fun onResume() {
		super.onResume()
		CrashHandler.instance().setCurrentActivityName(this)
	}

	override fun getLayoutInflater(): LayoutInflater? {
		if (null == mLayoutInflater) {
			mLayoutInflater = super.getLayoutInflater()
		}
		return mLayoutInflater
	}

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param intent
	 */
	override fun pushActivity(intent: Intent) {
		//        String uri = UrlParser.getInstance().buildUri(UrlParser.SCHEME_RULE_NATIVE);
		//        UrlParser.getInstance().parser(this, uri, intent);
		NavigationUtil.navigationTo(this, intent)
	}

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param clz 跳转的activity类名
	 */
	override fun pushActivity(clz: Class<*>) {
		//        String uri = UrlParser.getInstance().buildUri(UrlParser.SCHEME_RULE_NATIVE, clz.getName());
		//        UrlParser.getInstance().parser(this, uri);
		NavigationUtil.navigationTo(this, clz)
	}

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param clz 跳转的activity类名
	 */
	override fun pushActivity(clz: Class<*>, intent: Intent) {
		//        String uri = UrlParser.getInstance().buildUri(UrlParser.SCHEME_RULE_NATIVE, clz.getName());
		//        UrlParser.getInstance().parser(this, uri, intent);

		NavigationUtil.navigationTo(this, clz, intent)
	}

	/*
     * (non-Javadoc)
     *
     * @see com.ichsy.public_libs.view.navigation.NavigationListener#popBack()
     */
	override fun popBack() {
		val cost = onPressBackMenu()
		LogUtils.i("frame", "popBack cost is: " + cost + " activity is:" + javaClass.name)
		if (!cost) {
			onBackPressed()
		}
	}

	/**
	 * 用户关闭页面的监听，返回true表示被消费，false表示未被消费
	 *
	 * @return
	 */
	protected fun onPressBackMenu(): Boolean {
		return false
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		return keyCode == KeyEvent.KEYCODE_BACK && onPressBackMenu() || super.onKeyDown(keyCode, event)
	}

	override fun finish() {
		super.finish()

		//页面关闭的时候会默认隐藏键盘
		DeviceUtil.hidKeyBoard(applicationContext, mContentView, true)
	}

	override fun getFragments(): List<Fragment>? {
		val fm = supportFragmentManager

		return if (fm.isDestroyed) {
			null
		} else fm.fragments
	}

	override fun <T : View?> findViewById(id: Int): T {
		return if (mContentView == null) {
			super.findViewById<View>(id) as T
		} else {
			mContentView!!.findViewById<View>(id) as T
		}
	}

	@JvmOverloads
	fun replaceFragment(viewId: Int, fragment: Fragment, selfNavigationController: Boolean = false): Fragment {
		return replaceFragment(viewId, fragment, fragment.javaClass.simpleName, selfNavigationController)
	}

	fun replaceFragment(@IdRes viewId: Int, fragment: Fragment, fragmentTag: String, selfNavigationController: Boolean): Fragment {
		return replaceFragment(supportFragmentManager, viewId, fragment, fragmentTag, selfNavigationController)
	}

	/**
	 * 替换布局为fragment，fragment必须为BaseFrameFragment的子类，在fragment中将可以控制父布局的部分控件，如导航条
	 *
	 * @param fm
	 * @param viewId                   布局id
	 * @param fragment
	 * @param fragmentTag              fragment的tag，用来查找fragment
	 * @param selfNavigationController 是否使用自己的navigation
	 */
	override fun replaceFragment(fm: FragmentManager, @IdRes viewId: Int, fragment: Fragment, fragmentTag: String, selfNavigationController: Boolean): Fragment {
		var fragment = fragment
		synchronized(`object`) {
			//        FragmentManager fm = getSupportFragmentManager();
			val ft = fm.beginTransaction()

			val fragments = getFragments()

			if (null != fragments) {
				for (frag in fragments) {
					if (null == frag) continue
					ft.hide(frag)
				}
			}

			//        String fragmentTag = fragment.getClass().getSimpleName();
			//        Fragment cache = fm.findFragmentByTag(fragmentTag);
			val cache = findFragmentByTag(fm, fragmentTag)

			//        boolean isNewAddFragment = false;

			if (null == cache) {
				if (fragment is BaseFrameFragment) {
					if (selfNavigationController) {
						(fragment as BaseFrameFragment).navigationController=null
					} else {
						(fragment as BaseFrameFragment).navigationController=navigationController
					}
					//                ((BaseFrameFragment) fragment).onLazyOnResume();
				}
				fm.executePendingTransactions()
				if (fragment.isAdded) {
					ft.show(fragment)
				} else {
					//                isNewAddFragment = true;
					ft.add(viewId, fragment, fragmentTag)
				}
			} else {
				fragment = cache
				ft.show(fragment)
			}
			ft.commitAllowingStateLoss()

			if (null != cache) {
				if (fragment is BaseFrameFragment) {
					val cacheFragment = cache as BaseFrameFragment?
					if (null == cacheFragment!!.navigationController) {
						if (null != cacheFragment.activity) {
							val className = cacheFragment.activity!!.javaClass.name
							onActivityError("关闭页面 $className 原因：null == cacheFragment.navigationController")
							BusManager.default.postEvent(EVENT_FINISH_ACTIVITY, className)
						}
					} else {
						//                    if (isNewAddFragment) {
						cacheFragment.onLazyOnResume(this)
						//                    }
					}
				}
			}
			return fragment
		}
	}

	override fun findFragmentByTag(fm: FragmentManager, fragmentTag: String): Fragment {
		val fragments = getFragments()
		if (null != fragments) {
			for (fragment in fragments) {
				if (null == fragment) continue
				if (fragmentTag == fragment.tag) {
					return fragment
				}
			}
		}
		return fm.findFragmentByTag(fragmentTag)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
		super.onActivityResult(requestCode, resultCode, data)

		val fragments = getFragments()

		if (null != fragments) {
			for (frag in fragments) {
				if (null == frag) continue
				frag.onActivityResult(requestCode, resultCode, data)
			}
		}

	}


	protected fun onActivityError(message: String) {

	}

	//    /**
	//     * 检查是否可以进行下一次跳转
	//     *
	//     * @return
	//     */
	//    private Intent getNextIntent() {
	//        Bundle intentExt = getIntent().getExtras();
	//        if (null != intentExt) {
	//            return intentExt.getParcelable(BUNDLE_INTENT_SECOND_INTENT);
	//        }
	//        return null;
	//    }

	/**
	 * 检测并跳转到下个intent(如果有)
	 */
	protected fun pushNextIntent() {
		val intentExt = intent.extras
		if (null != intentExt) {
			//            intentExt.getParcelable(BUNDLE_INTENT_SECOND_INTENT);
			//执行登陆之后的后续逻辑
			val parcelableIntent = intentExt.getParcelable<Intent>(BUNDLE_INTENT_SECOND_INTENT)
			if (null != parcelableIntent) {
				pushActivity(parcelableIntent)
			} else {
				val schemeUrl = intentExt.getString(BUNDLE_INTENT_SECOND_SCHEME_URL)
				UrlParser.instance.parser(this, schemeUrl)
			}

			//            String actionEvent = intentExt.getString(BUNDLE_INTENT_SECOND_ACTION_EVENT);
			//            if (ObjectUtils.isNotNull(actionEvent)) {
			//                parcelableIntent.putExtra(BUNDLE_INTENT_FIRST_ACTION_EVENT_RUN, parcelableIntent);
			//                setResult(Activity.RESULT_OK, parcelableIntent);
			//            }

		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (null != getFragments()) {
			for (fragment in getFragments()!!) {
				fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
			}
		}
		PermissionManager.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults)
	}

	companion object {
		val EVENT_FINISH_ACTIVITY = "EVENT_FINISH_ACTIVITY"
		val EVENT_PUSH_HOMEPAGE = "EVENT_PUSH_HOMEPAGE"

		val BUNDLE_INTENT_SECOND_INTENT = "BUNDLE_INTENT_SECOND_INTENT"
		val BUNDLE_INTENT_SECOND_SCHEME_URL = "BUNDLE_INTENT_SECOND_SCHEME_URL"
	}

}
/**
 * 替换布局为fragment，fragment必须为BaseFrameFragment的子类，在fragment中将可以控制父布局的部分控件，如导航条
 *
 * @param viewId   布局id
 * @param fragment
 */