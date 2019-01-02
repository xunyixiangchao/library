package com.lib.lib_core.comm.bus.url

/**
 * 注册在消息总线的event事件
 * Created by lis on 2018/12/13.
 */
class BusEventObject(var observer: BusEventObserver, var onMainThread: Boolean) {
	var classNameTarget: String//记录是哪个class的

	init {

		this.classNameTarget = observer.javaClass.name
	}

}