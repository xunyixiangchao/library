package com.lib.lib_core.comm.bus.url

/**
 * Created by lis on 2018/12/13.
 */
interface BusEventObserver {

	/**
	 * 当收到event事件，会执行此函数
	 *
	 * @param event   event事件
	 * @param message 事件所带的参数
	 */
	fun onBusEvent(event: String, message: Any?)
}
