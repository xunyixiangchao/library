package com.lib.lib_core.comm.update

import android.support.annotation.DrawableRes
import java.io.Serializable

/**
 * 检测更新的实体类
 * Created by lis on 2018/12/13.
 */
class UpdateVo : Serializable {

	/**
	 * 更新版本号
	 */
	var version: String = ""

	/**
	 * 更新标题
	 */
	var title: String= ""

	/**
	 * 更新描述
	 */
	var description: String= ""

	/**
	 * 文件的大小
	 */
	var size: Long = 0

	/**
	 * 更新地址
	 */
	var url: String= ""

	@DrawableRes
	var iconResId: Int = 0

	/**
	 * 更新状态 - 也就是是否需要更新
	 */
	var updateStatus: UpdateStatus

	enum class UpdateStatus {
		UPDATE, //需要更新
		FORCE, //强制更新
		FREE, //免流量更新
		NONE, //不需要更新
		SILENCE
		//静默更新
	}

	constructor() {
		this.updateStatus = UpdateStatus.NONE
	}

	constructor(version: String, title: String, description: String, url: String, size: Long, status: UpdateStatus, @DrawableRes iconResId: Int) {
		this.version = version
		this.title = title
		this.description = description
		this.url = url
		this.size = size
		this.updateStatus = status
		this.iconResId = iconResId
	}

}
