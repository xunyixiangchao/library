package com.lib.lib_core.comm.update

import com.lib.lib_core.comm.utils.FileUtil
import com.lib.lib_core.net.http.downloader.HttpDownloader
import java.io.File

/**
 * 检测更新 - 下载文件的工具类
 * Created by lis on 2018/12/13.
 */
object UpdateDownloadProvider {
	val FILE_DOWNLOAD_PATH = "/release/"

	interface DownloadListener {
		/**
		 * 文件准备下载
		 *
		 * @param url
		 */
		fun onDownloadStart(url: String)

		/**
		 * 文件正在下载
		 *
		 * @param max
		 * @param current
		 */
		fun onDownloadProgress(max: Int, current: Int)

		/**
		 * 文件下载完成
		 *
		 * @param path
		 */
		fun onDownloadSuccess(path: String)

		fun onDownloadFailed(message: String)
	}

	/**
	 * 执行检测更新的文件下载
	 *
	 * @param url
	 * @param listener
	 */
	fun httpDownload(url: String, path: String, listener: DownloadListener) {
		listener.onDownloadStart(url)

		val lastProgressPosition = intArrayOf(-1)

		HttpDownloader.addDownloaderTask(url, path + FILE_DOWNLOAD_PATH, object : HttpDownloader.HttpDownloaderListener {
			override fun onDownloadFailure(message: String) {
				listener.onDownloadFailed("下载失败: $message")
			}

			override fun onDownloadProgress(max: Long, position: Long, progress: Int) {
				val maxKB = max.toInt() / 1024
				val progressKB = position.toInt() / 1024

				val pro = (progressKB.toFloat() / maxKB.toFloat() * 100).toInt()

				if (lastProgressPosition[0] != pro) {
					listener.onDownloadProgress(maxKB, progressKB)
				}
				lastProgressPosition[0] = pro
			}

			override fun onDownloadSuccess(filePath: String, fileName: String) {
				listener.onDownloadSuccess(filePath + fileName)
			}
		})

	}

	/**
	 * 执行检测更新的文件下载
	 *
	 * @param url
	 * @param listener
	 */
	//    public static void download(String url, final DownloadListener listener) {
	//        listener.onDownloadStart(url);
	//
	////        HttpDownloader.addDownloaderTask(url, FILE_DOWNLOAD_PATH);
	//
	//        File savePath = new File(FILE_DOWNLOAD_PATH);// saveFile
	//        DownLoadTask task = new DownLoadTask();
	//        task.setId(url);
	//        task.setDlSavePath(savePath.getPath());
	//        task.setUrl(url);
	//        DownLoadManager.getInstance().addDLTask(task, new DownLoadListener() {
	//            @Override
	//            public void onDownLoadStart(DownLoadTask task) {
	//            }
	//
	//            @Override
	//            public void onDownLoadUpdated(DownLoadTask task, long finishedSize, long trafficSpeed) {
	//                int maxKB = (int) task.getDlTotalSize() / 1024;
	//                int progressKB = (int) finishedSize / 1024;
	//                listener.onDownloadProgress(maxKB, progressKB);
	//            }
	//
	//            @Override
	//            public void onDownLoadPaused(DownLoadTask task) {
	//
	//            }
	//
	//            @Override
	//            public void onDownLoadResumed(DownLoadTask task) {
	//
	//            }
	//
	//            @Override
	//            public void onDownLoadSuccess(DownLoadTask task) {
	//                listener.onDownloadSuccess(task.getDlSavePath());
	//            }
	//
	//            @Override
	//            public void onDownLoadCanceled(DownLoadTask task) {
	//
	//            }
	//
	//            @Override
	//            public void onDownLoadFailed(DownLoadTask task) {
	//                listener.onDownloadFailed("下载失败");
	//            }
	//
	//            @Override
	//            public void onDownLoadRetry(DownLoadTask task) {
	//
	//            }
	//        });
	//    }


	/**
	 * 获取更新文件的File
	 *
	 * @param vo Update实体类
	 * @return 返回更新的文件保存地址的File文件
	 */
	fun getDownloadFile(basePath: String, vo: UpdateVo): File {
		val path = basePath + FILE_DOWNLOAD_PATH
		val name = FileUtil.getFileNameByUrl(vo.url)
		return File(path, name)
	}
}
