package com.lib.lib_core.comm.bus.url

import android.content.Context

/**
 * 总线业务逻辑
 * Created by lis on 2018/12/13.
 */
class BusManager private constructor() {

	private val mService: BusQueueService


	init {
		mService = BusQueueService()
	}

	/**
	 * 注册总线事件，在类销毁的时候，需要调用unRegister()方法反注册事件，防止可能发生的oom
	 *
	 * @param key      event事件key
	 * @param observer
	 */
	fun register(key: String, observer: BusEventObserver) {
		unRegister(key, observer)

		mService.addQueue(key, BusEventObject(observer, true))
	}

	/**
	 * 批量注册事件
	 *
	 * @param observer
	 * @param keys
	 */
	fun register(observer: BusEventObserver, vararg keys: String) {
		if (null != keys) {
			for (key in keys) {
				register(key, observer)
			}
		}
	}

	/**
	 * 注册总线事件，会在主线程中收到event事件，在类销毁的时候，需要调用unRegister()方法反注册事件，防止可能发生的oom
	 *
	 * @param key      event事件key
	 * @param observer
	 */
	fun registerOnMainThread(key: String, observer: BusEventObserver) {
		unRegister(key, observer)
		mService.addQueue(key, BusEventObject(observer, true))
	}

	/**
	 * 注册总线事件，会在子线程中收到event事件，在类销毁的时候，需要调用unRegister()方法反注册事件，防止可能发生的oom
	 *
	 * @param key      event事件key
	 * @param observer
	 */
	fun registerOnThread(key: String, observer: BusEventObserver) {
		unRegister(key, observer)
		mService.addQueue(key, BusEventObject(observer, false))
	}

	/**
	 * 注册之后的事件在使用完或者不再使用的时候，需要进行反注册，防止可能发生的oom
	 *
	 * @param key
	 */
	fun unRegister(key: String, observer: BusEventObserver) {
		mService.removeQueue(key, observer)
	}

	/**
	 * 监听activity关闭，会自定反注册部分event事件
	 *
	 * @param clazz
	 */
	fun onActivityDestroy(clazz: Any) {

		if (clazz is BusEventObserver) {
			mService.removeQueueByClassName(clazz)
		}

	}

	//    /**
	//     * 反注册该key所有的事件
	//     *
	//     * @param key
	//     */
	//    public void unRegisterKey(String key) {
	//        if (mMessageQueue == null) return;
	//        mMessageQueue.remove(key);
	//    }

	/**
	 * 分发事件，所有注册该事件的类都可以收到此事件和所传参数（message）
	 *
	 * @param key
	 * @param message
	 */
	fun postEvent(key: String, message: Any) {
		postEvent(null, key, message)
	}


	/**
	 * 分发事件，所有注册该事件的类都可以收到此事件和所传参数（message）
	 *
	 * @param receiveContext 限制接收到总线小心的context
	 * @param key
	 * @param message
	 */
	fun postEvent(receiveContext: Context?, key: String, message: Any) {
		mService.distributeEvent(receiveContext, key, message)
	}

	companion object {
		private var instance: BusManager? = null

		/**
		 * 使用getDefault()代替
		 */
		@Deprecated("")
		@Synchronized
		fun getInstance(): BusManager {
			//        synchronized (BusManager.class) {
			if (null == instance) {
				instance = BusManager()
			}
			//        }
			return instance as BusManager
		}

		//        synchronized (BusManager.class) {
		//        }
		val default: BusManager
			@Synchronized get() {
				if (null == instance) {
					instance = BusManager()
				}
				return instance!!
			}
	}

}
