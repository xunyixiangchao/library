package com.lib.lib_core.comm.logwatch.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.lib.lib_core.comm.logwatch.InfoBean
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
class DAO(context: Context) {
	private var dbHelper: DBHelper? = null

	init {
		if (dbHelper == null) {
			dbHelper = DBHelper(context)
			if (db == null) {
				db = dbHelper!!.writableDatabase
				db = dbHelper!!.readableDatabase
			}
		}
	}

	/**
	 * insert
	 *
	 * @param name    类型名称
	 * @param content 详细数据
	 * @return
	 */
	fun insert(name: String, content: String): Long {
		val initValues = ContentValues()
		initValues.put(DBHelper.CLASSES_NAME, name)
		initValues.put(DBHelper.CLASSES_CONTENT, content)
		return db!!.insert(DBHelper.TESTER_TABLE_NAME, null, initValues)
	}

	/**
	 * delete
	 */
	fun delete(): Int {
		val sql = DBHelper.TESTER_TABLE_NAME
		return db!!.delete(sql, null, null)
	}

	/**
	 * delete (on the base of classesName)
	 *
	 * @param classesName 类型名称
	 * @return
	 */
	fun delete(classesName: String): Int {
		val sql = DBHelper.TESTER_TABLE_NAME
		return db!!.delete(sql, DBHelper.CLASSES_NAME + " = ?", arrayOf(classesName))
	}


	fun queryCateGory(): ArrayList<InfoBean> {
		val cursor: Cursor
		val mArrayList = ArrayList<InfoBean>()

		var bean = InfoBean()
		bean.classesName="全部"
		mArrayList.add(bean)
		try {
			val sql = "select * from " + DBHelper.TESTER_TABLE_NAME + " GROUP BY " + DBHelper.CLASSES_NAME
			cursor = db!!.rawQuery(sql, null)
			if (cursor.moveToFirst()) {
				do {
					bean = InfoBean()
					bean.classesName=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_NAME))
					bean.classesContent=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_CONTENT))
					mArrayList.add(bean)

				} while (cursor.moveToNext())
			}
			cursor.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return mArrayList
	}

	/**
	 * query
	 * 查询全部
	 *
	 * @return
	 */
	fun query(): ArrayList<InfoBean> {
		val cursor: Cursor
		val mArrayList = ArrayList<InfoBean>()
		try {
			val sql = "select _id," + DBHelper.CLASSES_NAME + "," + DBHelper.CLASSES_CONTENT + " from " + DBHelper.TESTER_TABLE_NAME
			cursor = db!!.rawQuery(sql, null)
			if (cursor.moveToFirst()) {
				do {
					val bean = InfoBean()
					bean.classesName=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_NAME))
					bean.classesContent=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_CONTENT))
					mArrayList.add(bean)

				} while (cursor.moveToNext())
			}
			cursor.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return mArrayList
	}

	fun query(page: Int, size: Int): ArrayList<InfoBean> {
		val cursor: Cursor
		val mArrayList = ArrayList<InfoBean>()
		try {
			val sql = "select _id," + DBHelper.CLASSES_NAME + "," + DBHelper.CLASSES_CONTENT + " from " + DBHelper.TESTER_TABLE_NAME + " ORDER BY _id DESC" + " limit " + size + " offset " + page
			cursor = db!!.rawQuery(sql, null)
			if (cursor.moveToFirst()) {
				do {
					val bean = InfoBean()
					bean.classesName=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_NAME))
					bean.classesContent=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_CONTENT))
					mArrayList.add(bean)

				} while (cursor.moveToNext())
			}
			cursor.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return mArrayList
	}

	/**
	 * 根据分类查询
	 *
	 * @param className 分类名称
	 * @return
	 */
	fun query(className: String, page: Int, size: Int): ArrayList<InfoBean> {
		val cursor: Cursor
		val mArrayList = ArrayList<InfoBean>()
		try {
			cursor = db!!.query(DBHelper.TESTER_TABLE_NAME, arrayOf(DBHelper.CLASSES_NAME, DBHelper.CLASSES_CONTENT), DBHelper.CLASSES_NAME + "=? " + " ORDER BY _id DESC limit " + size + " offset " + page, arrayOf(className), null, null, null)
			if (cursor.moveToFirst()) {
				do {
					val bean = InfoBean()
					bean.classesName=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_NAME))
					bean.classesContent=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_CONTENT))
					mArrayList.add(bean)

				} while (cursor.moveToNext())
			}
			cursor.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return mArrayList
	}


	/**
	 * 根据分类查询
	 *
	 * @param className 分类名称
	 * @return
	 */
	fun query(className: String): ArrayList<InfoBean> {
		val cursor: Cursor
		val mArrayList = ArrayList<InfoBean>()
		try {
			cursor = db!!.query(DBHelper.TESTER_TABLE_NAME, arrayOf(DBHelper.CLASSES_NAME, DBHelper.CLASSES_CONTENT), DBHelper.CLASSES_NAME + "=?", arrayOf(className), null, null, null)
			if (cursor.moveToFirst()) {
				do {
					val bean = InfoBean()
					bean.classesName=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_NAME))
					bean.classesContent=cursor.getString(cursor.getColumnIndex(DBHelper.CLASSES_CONTENT))
					mArrayList.add(bean)

				} while (cursor.moveToNext())
			}
			cursor.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return mArrayList
	}

	companion object {

		private var db: SQLiteDatabase? = null
	}

}
