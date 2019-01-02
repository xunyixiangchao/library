package work.dd.com.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by lis on 2018/12/13.
 */
class ThreadPoolUtil private constructor() {

	private var mService: ExecutorService? = null

	//工作线程，次要优先级
	private val mWordThreadService: ExecutorService? = null

	//后台线程，低优先级
	private val mBackGroundThreadService: ExecutorService? = null

	init {
		mService = Executors.newFixedThreadPool(THREAD_COUNT)
	}

	fun fetchData(request: Runnable) {
		mService!!.submit(request)
	}

	companion object {
		private val THREAD_COUNT = 15

		private var instance: ThreadPoolUtil? = null

		fun getInstance(): ThreadPoolUtil? {
			if (instance == null) {
				synchronized(ThreadPoolUtil::class.java) {
					if (instance == null) {
						instance = ThreadPoolUtil()
					}
				}

			}
			return instance
		}

		/**
		 * 检测当前是否在主线程
		 *
		 * @return
		 */
		val isMainThread: Boolean
			get() = Looper.myLooper() == Looper.getMainLooper()

		/**
		 * 检测并在主线程中执行 runnable
		 *
		 * @param runnable
		 */
		fun runOnMainThread(runnable: Runnable) {
			if (ThreadPoolUtil.isMainThread) {
				runnable.run()
			} else {
				mainHandle.post(runnable)
			}
		}

		/**
		 * 获取当前app的主handler
		 *
		 * @return
		 */
		val mainHandle: Handler
			get() = Handler(Looper.getMainLooper())
	}
}
