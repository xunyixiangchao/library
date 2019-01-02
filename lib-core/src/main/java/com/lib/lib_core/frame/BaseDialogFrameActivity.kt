package com.lib.lib_core.frame

import com.lib.lib_core.comm.view.dialog.DialogHUB

/**
 * 带有自定义DialogHub的activity基类
 * Created by lis on 2018/12/17.
 */
abstract class BaseDialogFrameActivity : BaseFrameActivity() {

	override fun getNavigationHeight(): Int {
		return 0
	}
	// 进度条
	protected var mDialogHUB: DialogHUB? = null

	val dialogHUB: DialogHUB
		get() {
			if (mDialogHUB == null) {
				mDialogHUB = initDialogHub()
			}
			return mDialogHUB!!
		}

	/**
	 * 绑定ProgressHub
	 *
	 * @return
	 */
	private fun initDialogHub(): DialogHUB {
		val dialogHUB = DialogHUB()
		dialogHUB.bindDialog(this)
		return dialogHUB
	}
}