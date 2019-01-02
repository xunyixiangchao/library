package com.lib.lib_core.frame.interfaces

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

/**
 * fragment相关的接口
 * Created by lis on 2018/12/13.
 */
interface FragmentInterface {

	/**
	 * 获取当前页面包含的所有fragment
	 *
	 * @return
	 */
	 fun getFragments(): List<Fragment>?

	/**
	 * 根据fragment的tag，获取他的fragment
	 *
	 * @param fm
	 * @param fragmentTag
	 * @return
	 */
	fun findFragmentByTag(fm: FragmentManager, fragmentTag: String): Fragment

	/**
	 * 替换布局为fragment，fragment必须为BaseFrameFragment的子类，在fragment中将可以控制父布局的部分控件，如导航条
	 *
	 * @param fm
	 * @param viewId                   布局id
	 * @param fragment
	 * @param fragmentTag              fragment的tag，用来查找fragment
	 * @param selfNavigationController 是否使用自己的navigation
	 */
	fun replaceFragment(fm: FragmentManager, @IdRes viewId: Int, fragment: Fragment, fragmentTag: String, selfNavigationController: Boolean): Fragment
}
