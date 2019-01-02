package com.lib.lib_core.comm.utils

import android.text.TextUtils
import android.util.Log
import com.lib.lib_core.comm.logwatch.LogWatcher

/**
 * log工具类
 * Created by lis on 2018/12/13.
 */
object LogUtils {
	var VERBOSE = 5
	var DEBUG = 4
	var INFO = 3
	var WARN = 2
	var ERROR = 1

	//    public static int LOG_LEVEL = 6;
	private var logLevel = 6

	fun setLogLevel(level: Int) {
		logLevel = level
	}

	fun v(tag: String, msg: String) {
		if (TextUtils.isEmpty(msg)) return
		if (logLevel > VERBOSE) {
			Log.v(tag, msg)
			LogWatcher.getInstance().putMessage("log", "v\n$msg")
		}
	}

	fun d(tag: String, msg: String) {
		if (TextUtils.isEmpty(msg)) return
		if (logLevel > DEBUG) {
			Log.d(tag, msg)
			LogWatcher.getInstance().putMessage("log", "d\n$msg")
		}
	}

	fun i(tag: String, msg: String) {
		if (TextUtils.isEmpty(msg)) return
		if (logLevel > INFO) {
			Log.i(tag, msg)
			LogWatcher.getInstance().putMessage("log", "i\n$msg")
		}
	}

	fun w(tag: String, msg: String) {
		if (TextUtils.isEmpty(msg)) return
		if (logLevel > WARN) {
			Log.w(tag, msg)
			LogWatcher.getInstance().putMessage("log", "w\n$msg")
		}
	}

	fun e(tag: String, msg: String) {
		if (TextUtils.isEmpty(msg)) return
		if (logLevel > ERROR) {
			Log.e(tag, msg)
			LogWatcher.getInstance().putMessage("log", "e\n$msg")
		}
	}

}
