package com.lib.lib_core.comm.update

/**
 * 检测更新的回调
 * Created by lis on 2018/12/13.
 */
interface UpdateListener {

	/**
	 * 检测更新业务准备期，还未进行检测更新逻辑处理
	 */
	fun onUpdatePre()

	/**
	 * 检测更新业务执行完毕，本次不需要更新或者用户点击下次更新
	 */
	fun onUpdateSkip()

	/**
	 * 检测更新业务执行完毕，用户点击立即更新并且apk文件已经下载文笔
	 *
	 * @param path 下载好的apk路径（SD卡绝对路径）
	 */
	fun onUpdateCompleteAndInstall(path: String)
}
