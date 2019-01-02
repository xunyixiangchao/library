package com.lib.lib_core.dao

import android.content.Context
import android.text.TextUtils
import com.lib.lib_core.comm.utils.GsonHelper
import java.lang.reflect.Type

/**
 * DAO的基类，负责处理DAO的公共逻辑
 * Created by lis on 2018/12/13.
 */
abstract class BaseProvider {
	var context: Context? = null
		private set
	var uid: String? = null
		private set

	/**
	 * 增
	 *
	 * @param key
	 * @param value
	 * @return 是否成功
	 */
	protected abstract fun add(key: String, value: String): Boolean

	/**
	 * 删
	 *
	 * @param key
	 * @return 是否成功
	 */
	protected abstract fun delete(key: String): Boolean

	/**
	 * 改
	 *
	 * @param key
	 * @param value
	 * @return 是否成功
	 */
	protected abstract fun update(key: String, value: String): Boolean

	/**
	 * 查
	 *
	 * @param key
	 * @return 获取的数据
	 */
	protected abstract fun find(key: String): String?


	/**
	 * 初始化
	 *
	 * @param context
	 * @param uid
	 */
	protected fun init(context: Context, uid: String) {
		this.context = context
		this.uid = uid
	}

	fun getProvider(context: Context, uid: String): Provider {
		return if (TextUtils.isEmpty(uid)) {
			getProvider(context)
		} else {
			Provider(context, this, uid)
		}
	}

	/**
	 * 获取默认的provider
	 *
	 * @param context
	 * @return
	 */
	fun getProvider(context: Context): Provider {
		return Provider(context, this, DEFAULT_UID_KEY)
	}

	inner class Provider(context: Context?, private val dao: BaseProvider, uid: String) {

		init {
			if (null == context) {
				throw NullPointerException("context must be no null")
			}
			this.dao.init(context.applicationContext, uid)
		}

		/**
		 * 保存缓存数据
		 *
		 * @param key
		 * @param value
		 * @return
		 */
		@Synchronized
		fun putCache(key: String, value: Any): Boolean {
			val data = dao.find(key)

			val valueString: String
			if (value is String) {
				valueString = value
			} else {
				valueString = GsonHelper.build()!!.toJson(value)
			}

			return if (null != data) {
				dao.update(key, valueString)
			} else {
				dao.add(key, valueString)
			}
		}

		/**
		 * 获取缓存数据
		 *
		 * @param key
		 * @return
		 */
		fun getCache(key: String): String? {
			return dao.find(key)
		}

		/**
		 * 获取缓存数据
		 *
		 * @param key
		 * @return
		 */
		fun <T> getCache(key: String, clz: Class<T>): T? {
			val valueString = dao.find(key)
			return if (TextUtils.isEmpty(valueString)) {
				null
			} else {
				GsonHelper.build()!!.fromJson(valueString, clz)
			}
			//            return dao.find(key);
		}

		/**
		 * 获取缓存数据
		 *
		 * @param key
		 * @return
		 */
		fun <T> getCache(key: String, type: Type): T? {
			val valueString = dao.find(key)
			return if (TextUtils.isEmpty(valueString)) {
				null
			} else {
				GsonHelper.build()!!.fromJson(valueString, type)
			}
			//            return dao.find(key);
		}
	}

	companion object {
		private val DEFAULT_UID_KEY = "global"
	}

}
