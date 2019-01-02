package com.lib.lib_core.comm.utils

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.lib.lib_core.config.CoreConfig
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Created by lis on 2018/12/13.
 */
object FileUtil {
	private val TAG = FileUtil::class.java.simpleName

	private val FOLDER_NAME = "/leyou/"

	/**
	 * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
	 *
	 * @return
	 */
	val isSdCardExist: Boolean
		get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

	/**
	 * 获取SD卡根目录路径
	 *
	 * @return
	 */
	val sdCardPath: String
		get() = if (isSdCardExist) {
			Environment.getExternalStorageDirectory().absolutePath
		} else {
			""
		}

	private fun imageFolder(): String {
		val folder = Environment.getExternalStorageDirectory().toString() + FOLDER_NAME + "image/"
		mkDir(folder)
		return folder
	}

	private fun mkDir(folder: String): Boolean {
		val file = File(folder)

		return file.exists() || file.mkdirs()
	}


	fun saveBitmap(mBitmap: Bitmap, path: String): Boolean {
		val f = File(path)
		try {
			f.createNewFile()
		} catch (e: IOException) {
			return false
		}

		val fOut: FileOutputStream
		try {
			fOut = FileOutputStream(f)
		} catch (e: FileNotFoundException) {
			e.printStackTrace()
			return false
		}

		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
		try {
			fOut.flush()
		} catch (e: IOException) {
			e.printStackTrace()
			return false
		}

		try {
			fOut.close()
		} catch (e: IOException) {
			e.printStackTrace()

		}

		return true
	}

	/**
	 * 用bufferReader读取文件
	 *
	 * @param path
	 * @param fileName
	 * @return
	 */
	fun readByBufferReader(path: String, fileName: String): String? {
		val file = File(path, fileName)
		if (!file.exists()) return null
		try {
			val br = BufferedReader(FileReader(file))
			var line: String
			val sb = StringBuilder()
			while ((br.readLine()) != null) {
				//                System.out.println("readline:" + readline);
				sb.append(br.readLine())
			}
			br.close()
			//            System.out.println("读取成功：" + sb.toString());
			return sb.toString()
		} catch (e: Exception) {
			e.printStackTrace()
		}

		return null
	}

	/**
	 * 用BufferWriter写文件
	 *
	 * @param path     路径
	 * @param fileName 名字
	 * @param text     内容
	 * @param isAppend 复写还是追加
	 * @return
	 */
	fun writeByBufferWriter(path: String, fileName: String, text: String, isAppend: Boolean): Boolean {
		var bw: BufferedWriter? = null
		try {
			File(path).mkdirs()
			val file = File(path, fileName)
			//第二个参数意义是说是否以append方式添加内容
			bw = BufferedWriter(FileWriter(file, isAppend))
			bw.write(text)
			bw.flush()
			return true
		} catch (e: Exception) {
			e.printStackTrace()

		} finally {
			if (null != bw) {
				try {
					bw.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}
		return false
	}

	/**
	 * 保存Bitmap
	 *
	 * @param bitmap
	 * @param tempFile
	 */
	fun saveBitmap(bitmap: Bitmap, tempFile: File) {
		var fOut: FileOutputStream? = null
		try {
			fOut = FileOutputStream(tempFile)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
			fOut.flush()
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
			if (fOut != null) {
				try {
					fOut.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}

	}

	fun readPictureDegree(path: String): Int {
		var degree = 0
		try {
			val exifInterface = ExifInterface(path)
			val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
			when (orientation) {
				ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
				ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
				ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
			}
		} catch (e: IOException) {
			e.printStackTrace()
			return -1
		}

		return degree
	}

	fun rotaingImageView(angle: Int, bitmap: Bitmap?): Bitmap? {
		var bitmap: Bitmap? = bitmap ?: return null
		// 旋转图片 动作
		val matrix = Matrix()
		matrix.postRotate(angle.toFloat())
		// 创建新的图片
		bitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
		return bitmap
	}

	@Throws(Exception::class)
	fun readInputStream(inStream: InputStream): ByteArray {
		val buffer = ByteArray(1024)
		var len = -1
		val outStream = ByteArrayOutputStream()

		while ((inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, inStream.read(buffer))
		}

		val data = outStream.toByteArray()
		outStream.close()
		inStream.close()

		return data
	}

	/**
	 * @param filePath
	 * @return byte[]
	 * @throws FileNotFoundException
	 */
	@Throws(FileNotFoundException::class)
	fun readFileToBytes(filePath: String): ByteArray {
		var fileBytes: ByteArray? = null
		var `in`: FileInputStream? = null
		try {
			val file = File(filePath)
			val length = file.length().toInt()
			fileBytes = ByteArray(length)
			`in` = FileInputStream(filePath)
			`in`.read(fileBytes, 0, length)
			`in`.close()
			`in` = null
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
			if (null != `in`) {
				try {
					`in`.close()
					`in` = null
				} catch (ex: Exception) {
				}

			}
		}
		return fileBytes!!
	}

	fun getBitmapFromBytes(bytes: ByteArray?, opts: BitmapFactory.Options?): Bitmap? {
		return if (bytes != null) {
			if (opts != null) {
				BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
			} else {
				BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
			}
		} else null

	}

	/**
	 * 保存一个字符串到文件, 如果文件存在，则清除之前的数据，保存新的数据
	 *
	 * @param str
	 * @param path
	 */
	fun saveString(str: String, path: String): Boolean {
		val f = File(path)
		if (!f.parentFile.exists())
			f.mkdirs()
		if (f.exists())
			f.delete()
		try {
			f.createNewFile()
		} catch (e: IOException) {
			return false
		}

		var fOut: FileOutputStream? = null
		try {
			fOut = FileOutputStream(f)
			fOut.write(str.toByteArray())
			fOut.flush()

		} catch (e: FileNotFoundException) {
			e.printStackTrace()
			return false
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
			if (fOut != null) {
				try {
					fOut.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}

		return true
	}

	/**
	 * 获取String
	 *
	 * @param path
	 * @return
	 */
	fun getString(path: String?): String? {
		if (path == null)
			return null
		val f = File(path)
		if (!f.exists())
			return null
		var br: BufferedReader? = null
		try {
			br = BufferedReader(FileReader(f))
			var temp: String? = null
			val sb = StringBuffer()
			temp = br.readLine()
			while (temp != null) {
				sb.append(temp)
				temp = br.readLine()
			}
			LogUtils.i(TAG, "url=" + sb.toString())
			return sb.toString()
		} catch (e: FileNotFoundException) {
			e.printStackTrace()
			return null
		} catch (e: IOException) {
			e.printStackTrace()
			return null
		} finally {
			if (br != null) {
				try {
					br.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}
	}

	/**
	 * 下载文件 语音、图片
	 *
	 * @param urlPath
	 * @return
	 */
	fun getFileByte(urlPath: String): ByteArray? {
		var `in`: InputStream? = null
		var result: ByteArray? = null
		try {
			val url = URL(urlPath)
			val httpURLconnection = url.openConnection() as HttpURLConnection
			httpURLconnection.doInput = true
			httpURLconnection.connect()
			if (httpURLconnection.responseCode == 200) {
				`in` = httpURLconnection.inputStream
				result = readAll(`in`!!)
				`in`.close()
			} else {
				Log.e(TAG, "下载文件失败，状态码是：" + httpURLconnection.responseCode)
			}
		} catch (e: Exception) {
			Log.e(TAG, "下载文件失败，原因是：" + e.toString())
			e.printStackTrace()
		} finally {
			if (`in` != null) {
				try {
					`in`.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}

			}
		}
		return result
	}

	@Throws(Exception::class)
	fun readAll(`is`: InputStream): ByteArray {
		val baos = ByteArrayOutputStream(1024)
		val buf = ByteArray(1024)
		var c = `is`.read(buf)
		while (-1 != c) {
			baos.write(buf, 0, c)
			c = `is`.read(buf)
		}
		baos.flush()
		baos.close()
		return baos.toByteArray()
	}


	fun saveInfo2File(file: File, content: String): Boolean {
		LogUtils.e(CoreConfig.LOG_TAG, content)
		try {
			if (!file.exists()) {
				file.createNewFile()
			}
			val fw = FileWriter(file.absoluteFile)
			val bw = BufferedWriter(fw)
			bw.write(content)
			bw.close()
		} catch (e: IOException) {
			e.printStackTrace()
		}

		LogUtils.e(CoreConfig.LOG_TAG, file.path)
		return true
	}

	/**
	 * 递归删除文件和文件夹
	 *
	 * @param file 要删除的根目录
	 */
	fun deleteFile(file: File) {
		if (!file.exists()) {
		} else {
			if (file.isFile) {
				file.deleteOnExit()
				return
			}
			if (file.isDirectory) {
				val childFile = file.listFiles()
				if (childFile == null || childFile.size == 0) {
					file.deleteOnExit()
					return
				}
				for (f in childFile) {
					deleteFile(f)
				}
				file.deleteOnExit()
			}
		}
	}

	fun getFileNameByUrl(url: String): String? {
		if (isEmpty(url)) {
			return null
		}
		val index = url.lastIndexOf('?')
		val index2 = url.lastIndexOf("/")
		return if (index > 0 && index2 >= index) {
			UUID.randomUUID().toString()
		} else url.substring(index2 + 1, if (index < 0) url.length else index)

	}

	fun isEmpty(input: String?): Boolean {
		if (input == null || "" == input)
			return true

		for (i in 0 until input.length) {
			val c = input[i]
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false
			}
		}
		return true
	}

	/**
	 * 将二维码图片保存到文件夹
	 *
	 * @param context
	 * @param bmp
	 */
	fun saveImageToGallery(context: Context, bmp: Bitmap) {
		// 首先保存图片
		val externalStorageState = Environment.getExternalStorageState()
		//判断sd卡是否挂载
		if (externalStorageState == Environment.MEDIA_MOUNTED) {
			/*外部存储可用，则保存到外部存储*/
			//创建一个文件夹
			val appDir = File(Environment.getExternalStorageDirectory(), "syy")
			//如果文件夹不存在
			if (!appDir.exists()) {
				//则创建这个文件夹
				appDir.mkdir()
			}
			//将bitmap保存
			save(context, bmp, appDir)
		} else {
			//外部不可用，将图片保存到内部存储中，获取内部存储文件目录
			val filesDir = context.filesDir
			//保存
			save(context, bmp, filesDir)
		}
	}

	private fun save(context: Context, bmp: Bitmap, appDir: File) {
		//命名文件名称
		val fileName = System.currentTimeMillis().toString() + ".jpg"
		//创建图片文件，传入文件夹和文件名
		val imagePath = File(appDir, fileName)
		try {
			//创建文件输出流，传入图片文件，用于输入bitmap
			val fos = FileOutputStream(imagePath)
			//将bitmap压缩成png，并保存到相应的文件夹中
			drawBg4Bitmap(Color.WHITE, bmp).compress(Bitmap.CompressFormat.PNG, 100, fos)
			//冲刷流
			fos.flush()
			//关闭流
			fos.close()
		} catch (e: FileNotFoundException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.contentResolver,
					imagePath.absolutePath, fileName, null)
		} catch (e: FileNotFoundException) {
			e.printStackTrace()
		}

		// 最后通知图库更新
		context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imagePath.absolutePath)))
	}

	fun drawBg4Bitmap(color: Int, orginBitmap: Bitmap): Bitmap {
		val paint = Paint()
		paint.color = color
		val bitmap = Bitmap.createBitmap(orginBitmap.width,
				orginBitmap.height, orginBitmap.config)
		val canvas = Canvas(bitmap)
		canvas.drawRect(0f, 0f, orginBitmap.width.toFloat(), orginBitmap.height.toFloat(), paint)
		canvas.drawBitmap(orginBitmap, 0f, 0f, paint)
		return bitmap
	}
}
