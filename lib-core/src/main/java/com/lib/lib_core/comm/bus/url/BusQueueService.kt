package com.lib.lib_core.comm.bus.url

import android.content.Context
import android.text.TextUtils
import com.lib.lib_core.comm.utils.LogUtils
import work.dd.com.utils.ThreadPoolUtil
import java.util.*

/**
 * 负责处理和分发总线事务的服务类
 * Created by lis on 2018/12/13.
 */
class BusQueueService {

	private val mMessageQueue: HashMap<String, MutableList<BusEventObject>>?//消息队列

	init {
		mMessageQueue = HashMap()
	}

	fun addQueue(key: String, eventObject: BusEventObject?) {
		if (null == eventObject) return

		var observers: MutableList<BusEventObject>? = mMessageQueue!![key] as MutableList<BusEventObject>?

		if (observers == null) {
			observers = ArrayList()
			mMessageQueue[key] = observers
		}
		observers.add(eventObject)

		LogUtils.i("busevent", String.format("add event to bus,key is:%s; targetClass is :%s;", key, eventObject.observer))

		//为了防止内存溢出，handler再太多的情况下会进行自动清理
		if (observers.size >= MAX_HANDLER_SIZE) {
			observers.removeAt(0)
		}
	}

	fun removeQueue(key: String, observer: BusEventObserver?) {
		if (null == observer) return
		if (null == mMessageQueue) return

		if (mMessageQueue.containsKey(key)) {
			val eventObjects = mMessageQueue[key]

			for (eventObject in eventObjects!!) {
				if (eventObject.observer === observer) {
					eventObjects.remove(eventObject)
					break
				}
			}

			//如果当前事件已经没有注册的地方，可以释放掉该key的所有内存
			if (eventObjects.size == 0) {
				removeQueueForKey(key)
			}

			LogUtils.i("busevent", String.format("remove event to bus, key: %s; targetClass is:%s", key, observer))
		}
	}

	fun removeQueueForKey(key: String) {
		if (null == mMessageQueue) return
		mMessageQueue.remove(key)
	}

	/**
	 * 根据类名，删除注册的总线
	 *
	 * @param clazz
	 */
	fun removeQueueByClassName(clazz: Any) {
		for ((busKey, busEventObjectList) in mMessageQueue!!) {

			val busEventObjectIterator = busEventObjectList.iterator()
			while (busEventObjectIterator.hasNext()) {
				val busEventObject = busEventObjectIterator.next()
				if (!TextUtils.isEmpty(busEventObject.classNameTarget) && busEventObject.classNameTarget.equals(clazz.javaClass.name)) {
					LogUtils.i("busevent", "removeQueueByClassName, key is: " + busKey + " activityTag is: " + busEventObject.classNameTarget)
					busEventObjectIterator.remove()
				}
			}

		}
	}

	/**
	 * 分发消息
	 *
	 * @param key
	 * @param message
	 */
	fun distributeEvent(receiveContext: Context?, key: String, message: Any) {
		if (mMessageQueue!!.size == 0) return

		val observers = mMessageQueue[key]
		if (observers == null || observers.isEmpty()) {
			return
		}

		//给订阅者发送信息到倒着发送，保证最后注册的订阅者优先收到订阅消息
		for (i in observers.indices.reversed()) {
			val eventObject = observers[i]

			if (receiveContext != null) {
				if (eventObject.classNameTarget.equals(receiveContext.javaClass.name)) {
					continue
				}
			}

			//            eventObject.classNameTag

			val observer = eventObject.observer

			//            Handler handler =new Handler() {
			//                @Override
			//                public void handleMessage(Message msg) {
			//                    super.handleMessage(msg);
			//                    LogUtils.i("lyh", "runnable run");
			//                    observer.onBusEvent(key, message);
			//                }
			//            };
			val runnable = Runnable { observer.onBusEvent(key, message) }

			if (eventObject.onMainThread) {
				ThreadPoolUtil.runOnMainThread(runnable)
				//                Handler handler = new Handler(Looper.getMainLooper());
				//                handler.post(runnable);
				//                handler.sendEmptyMessage(0);
				//                observer.onBusEvent(key, message);
			} else {
				ThreadPoolUtil.getInstance()!!.fetchData(runnable)
			}
		}


	}

	companion object {

		private val MAX_HANDLER_SIZE = 30
	}
}
