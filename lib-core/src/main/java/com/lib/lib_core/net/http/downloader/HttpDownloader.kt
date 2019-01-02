package com.lib.lib_core.net.http.downloader

import android.text.TextUtils
import com.lib.lib_core.comm.utils.FileUtil
import com.lib.lib_core.comm.utils.GsonHelper
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.net.http.OkHttpClientHelper
import okhttp3.*
import work.dd.com.utils.ThreadPoolUtil
import java.io.*
import java.nio.channels.FileChannel

/**
 * 基于okhttp的文件下载工具类
 *
 * Created by lis on 2018/12/13.
 */
object HttpDownloader {
	interface HttpDownloaderListener {
		fun onDownloadFailure(message: String)

		fun onDownloadProgress(max: Long, position: Long, progress: Int)

		fun onDownloadSuccess(filePath: String, fileName: String)
	}

	//    //每次下载需要新建新的Call对象
	//    private static Call newCall(String url, long startPoints) {
	//        Request request = new Request.Builder()
	//                .url(url)
	//                .header("RANGE", "bytes=" + startPoints + "-")//断点续传要用到的，指示下载的区间
	//                .build();
	//        return new OkHttpClient().newCall(request);
	//    }

	/**
	 * @param url
	 * @param filePath 绝对路径，不需要后缀名
	 * @param listener
	 */
	fun addDownloaderTask(url: String, filePath: String, listener: HttpDownloaderListener) {


		val requestBuilder = Request.Builder().url(url)

		val fileName = FileUtil.getFileNameByUrl(url)
		//读取断点续传位置
		val extJson = FileUtil.readByBufferReader(filePath, fileName + "_extTmp")
		if (!TextUtils.isEmpty(extJson)) {
			val extVo = GsonHelper.build()!!.fromJson(extJson, DownloaderExtVo::class.java)
			if (null != extVo) {
				//                startPoint = extVo.current;
				//                requestBuilder.header("RANGE", "bytes=" + extVo.current + "-");
			}
		}
		//        Request request = new Request.Builder().addHeader("Range", "").url(url).build();

		OkHttpClientHelper.INSTANCE.getOkHttpClient()!!.newCall(requestBuilder.build()).enqueue(object : Callback {

			override fun onFailure(call: Call, e: IOException) {
				listener.onDownloadFailure(e.message!!)
			}

			@Throws(IOException::class)
			override fun onResponse(call: Call, response: Response) {
				val fileName = FileUtil.getFileNameByUrl(url)

				var `is`: InputStream? = null
				val buffer = ByteArray(2048)
				var len: Int
				val fos: FileOutputStream? = null

				val body = response.body()
				val `in` = body.byteStream()
				var channelOut: FileChannel? = null
				// 随机访问文件，可以指定断点续传的起始位置
				var randomAccessFile: RandomAccessFile? = null

				var sum: Long = 0
				val total = response.body().contentLength()

				try {
					`is` = response.body().byteStream()
					if (!validateFile(url, filePath, response.body())) {
						val file = File(filePath, fileName + "_tmp")

						File(filePath).mkdirs()


						//                        fos = new FileOutputStream(extFile);
						//                        fos.write(buffer, 0, );
						//                        fos.flush();

						//                        fos = new FileOutputStream(file);
						//                        long sum = 0;
						//                        while ((len = is.read(buf)) != -1) {
						//                            fos.write(buf, 0, len);
						//                            sum += len;
						//                            final int progress = (int) (sum * 1.0f / total * 100);
						//                            final long finalSum = sum;
						//                            ThreadPoolUtil.runOnMainThread(new Runnable() {
						//                                @Override
						//                                public void run() {
						//                                    listener.onDownloadProgress(total, finalSum, progress);
						//                                }
						//                            });
						//                        }
						//                        fos.flush();

						val startPoint = 0L

						randomAccessFile = RandomAccessFile(file, "rwd")
						//Chanel NIO中的用法，由于RandomAccessFile没有使用缓存策略，直接使用会使得下载速度变慢，亲测缓存下载3.3秒的文件，用普通的RandomAccessFile需要20多秒。
						channelOut = randomAccessFile.channel
						// 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
						val mappedBuffer = channelOut!!.map(FileChannel.MapMode.READ_WRITE, startPoint, body.contentLength())
						//                        byte[] buffer = new byte[1024];
						//                        int len;
						while ((`in`.read(buffer)) != -1) {
							mappedBuffer.put(buffer, 0, `in`.read(buffer))
							sum += `in`.read(buffer).toLong()
							val finalLen = sum
							val progress = (finalLen * 1.0f / response.body().contentLength() * 100).toInt()
							ThreadPoolUtil.runOnMainThread(Runnable {
								//                                    int progress = (int) (finalLen * 1.0f / response.body().contentLength() * 100);
								listener.onDownloadProgress(total, finalLen, progress)
							})
						}

						file.renameTo(File(filePath, fileName))
					} else {
						LogUtils.i("downloader", "file is exits... ")
					}
					ThreadPoolUtil.runOnMainThread(Runnable { listener.onDownloadSuccess(filePath, fileName!!) })
				} catch (e: Exception) {
					e.printStackTrace()
					listener.onDownloadFailure(e.message!!)

					val extVo = DownloaderExtVo()
					extVo.current = sum
					extVo.max = total
					FileUtil.writeByBufferWriter(filePath, fileName + "_extTmp", GsonHelper.build()!!.toJson(extVo), false)

				} finally {
					try {
						`is`?.close()
						fos?.close()
						`in`.close()
						channelOut?.close()
						randomAccessFile?.close()
					} catch (e: IOException) {
						e.printStackTrace()
					}

				}
			}

		})

	}

	/**
	 * 检查文件是否下载完毕
	 *
	 * @param url
	 * @param filePath
	 * @param body
	 * @return
	 */
	private fun validateFile(url: String, filePath: String, body: ResponseBody): Boolean {
		val fileName = FileUtil.getFileNameByUrl(url)
		val file = File(filePath, fileName)
		return file.length() == body.contentLength()
	}


}
