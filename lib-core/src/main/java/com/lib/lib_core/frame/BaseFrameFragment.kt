package com.lib.lib_core.frame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lib.lib_core.R
import com.lib.lib_core.comm.bus.url.BusManager
import com.lib.lib_core.comm.utils.DeviceUtil
import com.lib.lib_core.comm.view.dialog.DialogHUB
import work.dd.com.navigation.NavigationController
import work.dd.com.navigation.NavigationListener
import work.dd.com.utils.ViewUtil

/**
 * Created by lis on 2018/12/13.
 */
abstract class BaseFrameFragment : Fragment(), NavigationListener {
	private val BUNDLE_IS_FRAGMENT_RECOVERED = "BUNDLE_IS_FRAGMENT_RECOVERED"//这个fragment是否是从内存中恢复的
	private val BUNDLE_HAS_SELF_NAVIGATION = "BUNDLE_HAS_SELF_NAVIGATION"//这个fragment是否是从内存中恢复的

	var title: String? = null
	// 导航栏
	/**
	 * 设置fragment的导航栏
	 *
	 * @param controller
	 */
	var navigationController: NavigationController? = null

	protected var isSelfNavigationController: Boolean = false///是否是自己的导航栏


	//    //懒加载
	//    private boolean isFragmentFirstVisible = true;
	//    private boolean isVisible;
	//    private boolean isCreate = false;

	// 用作统计的code
	//	private String mPageCode;

	protected lateinit var rootView: View
	private var mContentView: FrameLayout? = null
	private var fragmentDialogHUB: DialogHUB? = null
	var isFragmentCreate = false

	/**
	 * 获取fragment的DialogHub，因为布局的原因，fragment要有自己的dialog
	 *
	 * @return
	 */
	val dialogHUB: DialogHUB
		get() {
			if (null == fragmentDialogHUB) {
				fragmentDialogHUB = DialogHUB()
				fragmentDialogHUB!!.bindDialog(this!!.activity!!, rootView)
			}
			return fragmentDialogHUB as DialogHUB
		}

	/**
	 * 初始化布局，设置的viewID
	 *
	 * @return 返回layout文件的id Modifier： Modified Date： Modify：
	 */
	protected abstract fun onLayoutInflate(): Int

	/**
	 * fragment的懒加载生命周期，进入当前fragment才会调用，类似activity的 [Activity.onCreate]
	 *
	 * @param view
	 * @author LiuYuHang
	 * @date 2015年1月15日
	 */
	protected abstract fun onLazyCreate(view: View)

	/**
	 * fragment的懒加载生命周期，进入当前fragment才会调用，类似activity的 [Activity.onResume]
	 *
	 * @author LiuYuHang
	 * @date 2015年1月15日
	 */
	abstract fun onLazyOnResume(context: Context)


	//    protected abstract void onLazyResume();

	/*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		//        LogUtils.v("fragment", "onViewCreated");
		//        if (isFragmentFirstVisible && isVisible) {
		//            onLazyInit();
		//        }
		//        isFragmentFirstVisible = false;

		//        if (!isFragmentCreate()) {
		//            setFragmentCreate(true);
		onLazyCreate(view)
		//        }

	}

	//    @Override
	//    public void setUserVisibleHint(boolean isVisibleToUser) {
	//        LogUtils.v("fragment", "setUserVisibleHint");
	//        super.setUserVisibleHint(isVisibleToUser);
	//        this.isVisible = isVisibleToUser;
	//        if (isVisibleToUser) {
	//            LogUtils.v("fragment", "setUserVisibleHint:" + isFragmentFirstVisible);
	//            if (!isFragmentFirstVisible) {
	//                onLazyInit();
	//            }
	//        }
	//    }

	//    private void onLazyInit() {
	//        if (!isCreate) {
	//            onLazyCreate(view);
	//            isCreate = true;
	//        }
	//        onLazyResume();
	//    }

	//    @Override
	//    public void setUserVisibleHint(boolean isVisibleToUser) {
	//        super.setUserVisibleHint(isVisibleToUser);
	//        if (getUserVisibleHint()) {
	//            if (!isCreate) {
	//                onLazyCreate();
	//            }
	//            onLazyResume();
	//        }
	//    }

	/**
	 * 用户点击返回键的处理事件
	 *
	 * @return 返回按钮是否已经被消费，true为消费，false为没有消费
	 */
	fun onPressBackMenu(): Boolean {
		return false
	}

	/*
     * (non-Javadoc)
     *
     * @see android.app.Activity#findViewById(int)
     */
	fun findViewById(id: Int): View {
		return mContentView!!.findViewById(id)
	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		rootView = LayoutInflater.from(activity).inflate(R.layout.frame_root_layout, null, false)

		//		mPageCode = initPageCode();
		mContentView = rootView.findViewById<View>(R.id.view_root_layout) as FrameLayout

		//        mContentView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, 400));

		//        mContentView.setBackgroundColor(Color.RED);

		mContentView!!.removeAllViews()

		val childId = onLayoutInflate()
		if (childId != 0) {
			val childView: View
			//			if (this instanceof PluginBaseFragment) {// 判断当前是不是插件fragment
			//				childView = MyResources.getResource(getClass()).inflate(getActivity(), childId, container, false);
			//			} else {
			childView = View.inflate(activity, childId, null)
			//			}
			mContentView!!.addView(childView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
		}


		//如果自己是重新恢复，并且没有自己的navigation，才需要走navigation的逻辑判断
		var isFragmentRecover = false
		var isHasSelfNavigationController = false

		if (null != savedInstanceState) {
			isFragmentRecover = savedInstanceState.getBoolean(BUNDLE_IS_FRAGMENT_RECOVERED, false)
			isHasSelfNavigationController = savedInstanceState.getBoolean(BUNDLE_HAS_SELF_NAVIGATION, false)
		}

		if (!isFragmentRecover || isHasSelfNavigationController || null == navigationController) {
			//如果没有给他设置过NavigationController，初始化一个新的，因为有可能每个fragment都有自己的导航栏
			if (navigationController == null) {
				isSelfNavigationController = true
				if (null != activity as BaseFrameActivity) {
					(activity as BaseFrameActivity).navigationController.hideNavigation(true)
					//                // 初始化并绑定导航栏
					navigationController = (activity as BaseFrameActivity).initNavigationController(rootView)
				}
			} else {
				isSelfNavigationController = false
				ViewUtil.setViewVisibility(View.GONE, rootView.findViewById<View>(R.id.navigation_root_layout))
			}
		}
		return rootView
	}

	override fun onSaveInstanceState(outState: Bundle) {
		//保存fragment状态
		outState.putSerializable(BUNDLE_IS_FRAGMENT_RECOVERED, true)
		outState.putSerializable(BUNDLE_HAS_SELF_NAVIGATION, isSelfNavigationController)
		super.onSaveInstanceState(outState)
	}

	override fun onResume() {
		super.onResume()
		//        onLazyOnResume(this);

		val activity = activity
		if (null != activity) {
			onLazyOnResume(activity)
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		BusManager.default.onActivityDestroy(this)
	}

	//    public Fragment replaceFragment(int viewId, Fragment fragment, boolean selfNavigationController) {
	//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
	//        if (fragment instanceof BaseFrameFragment && !selfNavigationController) {
	//            ((BaseFrameFragment) fragment).setNavigationController(navigationController);
	//        }
	//        transaction.replace(viewId, fragment, fragment.getClass().getSimpleName());
	//        transaction.commitAllowingStateLoss();
	//        return fragment;
	//    }

	/**
	 * 替换布局为fragment，fragment必须为BaseFrameFragment的子类，在fragment中将可以控制父布局的部分控件，如导航条
	 *
	 * @param viewId                   布局id
	 * @param fragment
	 * @param selfNavigationController 是否使用自己的navigation
	 */
	//    public synchronized Fragment replaceFragment(int viewId, Fragment fragment, boolean selfNavigationController) {
	//        BaseFrameActivity activity = (BaseFrameActivity) getActivity();
	//        return activity.replaceFragment(activity.getSupportFragmentManager(), viewId, fragment, fragment.getClass().getSimpleName(), selfNavigationController);
	//    }
	@Synchronized
	fun replaceFragment(viewId: Int, fragment: Fragment, selfNavigationController: Boolean): Fragment {
		var fragment = fragment
		val fm = childFragmentManager
		val ft = fm.beginTransaction()

		val fragments = fm.fragments
		if (null != fragments) {
			for (frag in fragments) {
				if (null == frag) {
					continue
				}
				ft.hide(frag)
			}
		}

		val fragmentTag = fragment.javaClass.simpleName
		val cache = fm.findFragmentByTag(fragmentTag)

		if (null == cache) {
			if (fragment is BaseFrameFragment) {
				if (selfNavigationController) {
					fragment.navigationController = null
				} else {
					fragment.navigationController = navigationController
				}
				//                ((BaseFrameFragment) fragment).onLazyOnResume();
			}

			if (fragment.isAdded) {
				ft.show(fragment)
			} else {
				ft.add(viewId, fragment, fragmentTag)
			}
		} else {
			fragment = cache
			ft.show(fragment)
		}
		ft.commitAllowingStateLoss()

		//        if (null != cache) {
		//            if (fragment instanceof BaseFrameFragment && null != getActivity()) {
		//                ((BaseFrameFragment) cache).onLazyOnResume(getActivity());
		//            }
		//        }
		return fragment
	}

	/*
     * (non-Javadoc)
     *
     * @see com.ichsy.public_libs.view.navigation.NavigationListener#popBack()
     */
	override fun popBack() {
		if (null != activity) {
			DeviceUtil.hidKeyBoard(activity!!, mContentView, true)
			activity!!.finish()
		}

	}

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param intent
	 */
	override fun pushActivity(intent: Intent) {
		if (null == activity) return
		(activity as BaseFrameActivity).pushActivity(intent)
	}

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param clz 跳转的activity类名
	 */
	override fun pushActivity(clz: Class<*>) {
		if (null == activity) return
		(activity as BaseFrameActivity).pushActivity(clz)
	}

	/**
	 * 通过总线逻辑跳转页面
	 *
	 * @param clz 跳转的activity类名
	 */
	override fun pushActivity(clz: Class<*>, intent: Intent) {
		if (null == activity) return
		(activity as BaseFrameActivity).pushActivity(clz, intent)

	}
}
