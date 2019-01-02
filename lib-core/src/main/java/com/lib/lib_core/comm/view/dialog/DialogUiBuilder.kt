package work.dd.com.dialog

import android.view.LayoutInflater
import android.view.View

/**
 * 页面的builder
 * Created by lis on 2018/12/13.
 */
interface DialogUiBuilder {

	/**
	 * 页面初始化
	 *
	 * @return
	 */
	fun onViewCreate(inflater: LayoutInflater): View

	/**
	 * 页面内容绘制
	 */
	fun onViewDraw(view: View, message: String?)


}
