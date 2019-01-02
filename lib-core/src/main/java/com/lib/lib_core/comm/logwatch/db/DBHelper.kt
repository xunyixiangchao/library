package com.lib.lib_core.comm.logwatch.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by lis on 2018/12/13.
 */
class DBHelper : SQLiteOpenHelper {

	constructor(context: Context) : super(context, DB_NAME, null, 4) {}

	constructor(context: Context, version: Int) : super(context, DB_NAME, null, version) {}

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL("create table " + TESTER_TABLE_NAME + " ("
				+ _ID + " integer primary key AUTOINCREMENT,"
				+ CLASSES_NAME + " text, "
				+ CLASSES_CONTENT + " text ) ")
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists $TESTER_TABLE_NAME")
			onCreate(db)
		}
	}

	companion object {

		internal val DB_NAME = "Tester.db"

		internal val TESTER_TABLE_NAME = "info_tester"

		internal val _ID = "_id"
		internal val CLASSES_NAME = "classes_name"//分类名称
		internal val CLASSES_CONTENT = "content"//具体内容
	}

}