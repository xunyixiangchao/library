package com.lib.lib_core.dao

import android.app.Activity
import android.content.SharedPreferences

/**
 * SharedPreferences的DAO存储过程
 * Created by lis on 2018/12/13.
 */
class SharedPreferencesProvider : BaseProvider() {
	private val SP_FILE_NAME = "cache_user_data_"

	protected override fun add(key: String, value: String): Boolean {
		val editor = getSP(this!!.uid!!).edit()
		editor.putString(key, value)
		return editor.commit()
	}

	protected override fun delete(key: String): Boolean {
		val editor = getSP(this!!.uid!!).edit()
		editor.putString(key, "")
		return editor.commit()
	}

	protected override fun update(key: String, value: String): Boolean {
		val editor = getSP(this!!.uid!!).edit()
		editor.putString(key, value)
		return editor.commit()
	}

	protected override fun find(key: String): String {
		val sp = getSP(this!!.uid!!)
		return sp.getString(key, "")
	}

	/**
	 * 根据用户的uid获取自己的缓存
	 *
	 * @param uid
	 * @return
	 */
	private fun getSP(uid: String): SharedPreferences {
		val spName = SP_FILE_NAME + uid
		return context!!.getSharedPreferences(spName, Activity.MODE_PRIVATE)
	}

}
