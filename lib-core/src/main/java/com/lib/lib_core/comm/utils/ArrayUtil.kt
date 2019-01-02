package com.lib.lib_core.comm.utils

import java.util.*
import java.util.Map

/**
 * 处理array的工具类
 * Created by lis on 2018/12/13.
 */
object ArrayUtil {
	interface MapHandler<K, V> {
		fun onNext(key: K, value: V)
	}

	interface ListHandler<T> {
		fun onNext(position: Int, item: T)
	}

	/**
	 * 检查list,判断position是否越界
	 *
	 * @param categoryList
	 * @param position
	 * @return true:安全 false:不安全
	 */
	fun checkSafe(categoryList: List<*>, position: Int): Boolean {
		return !(categoryList == null || categoryList.isEmpty() || position >= categoryList.size || position < 0)
	}

	fun <T> getArrayObject(list: List<T>?, position: Int): T? {
		return if (null == list || position >= list.size) {
			null
		} else {
			list[position]
		}
	}

	/**
	 * 迭代列表
	 *
	 * @param list
	 */
	fun <T> iteratorList(list: List<T>?, handler: ListHandler<T>) {
		if (null == list || list.isEmpty()) return

		for (i in list.indices) {
			handler.onNext(i, list[i])
		}

	}

	//    public static <T> T get(List list, int position) {
	//        return null;
	//    }

	/**
	 * 获取map中的key和value
	 *
	 * @param map
	 * @param handler
	 * @param <K>
	 * @param <V>
	</V></K> */
	fun <K, V> arrayMap(map: HashMap<K, V>, handler: MapHandler<K, V>) {
		//        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();

		val it = map.entries.iterator()
		while (it.hasNext()) {
			val entry = it.next() as Map.Entry<*, *>
			val key = entry.key as K
			val value = entry.value as V
			handler.onNext(key, value)
		}
	}

	fun <T> getLastItem(data: List<T>?): T? {
		return if (data == null || data.isEmpty()) null else data[data.size - 1]
	}

	/**
	 * 数组转行成list，会做为空判断，如果为null，为了解决空指针，会new一个新的list
	 *
	 * @param a
	 * @param <T>
	 * @return
	</T> */
	fun <T> asList(a: Array<T>?): List<T> {
		return if (null == a) {
			ArrayList()
		} else {
			Arrays.asList(*a)
		}
	}

	/**
	 * 获取list的size，会对null进行判断
	 *
	 * @param list
	 * @return
	 */
	fun size(list: List<*>?): Int {
		return list?.size ?: 0
	}

	/**
	 * 判断两个list数据是否相同
	 *
	 * @param a
	 * @param b
	 * @param <T>
	 * @return
	</T> */
	fun <T : Comparable<T>> compare(a: List<T>?, b: List<T>?): Boolean {
		if (a == null || b == null) {
			return false
		}
		if (a.size != b.size) {
			return false
		}
		Collections.sort(a)
		Collections.sort(b)
		for (i in a.indices) {
			if (a[i] != b[i]) {
				return false
			}
		}
		return true
	}


}
