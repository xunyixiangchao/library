package com.lib.lib_core.comm.helper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import work.dd.com.utils.ThreadPoolUtil
import java.util.concurrent.ExecutionException

/**
 * 图片下载的中转类
 * Created by lis on 2018/12/17.
 */
class ImageHelper(
		//    private static ImageHelper instance;

		private val context: Context) {
	private var url: String? = null
	private var defaultImageId: Int = 0
	private var listener: ImageLoaderListener? = null
	private var isCenterCrop = true
	private lateinit var transformations: Array<BitmapTransformation?>
	private var isCache = true
	private var asBitmap: Boolean = false

	/**
	 * 加载图片
	 *
	 * @param url            图片url
	 * @param placeHolderRid 占位图
	 * @return
	 */
	fun load(url: String, placeHolderRid: Int): ImageHelper {
		this.url = url
		this.defaultImageId = placeHolderRid
		return this
	}

	fun load(url: String, placeHolderRid: Int, isCache: Boolean): ImageHelper {
		this.url = url
		this.defaultImageId = placeHolderRid
		this.isCache = isCache
		return this

	}

	fun listener(listener: ImageLoaderListener): ImageHelper {
		this.listener = listener
		return this
	}

	fun centerCrop(centerCrop: Boolean): ImageHelper {
		this.isCenterCrop = centerCrop
		return this
	}

	fun asBitmap(asBitmap: Boolean): ImageHelper {
		this.asBitmap = asBitmap
		return this
	}

	fun transform(transformations: Array<BitmapTransformation?>): ImageHelper {
		this.transformations = transformations
		return this
	}

	/**
	 * 加载图片到view上
	 *
	 * @param rid view的id
	 */
	fun into(rid: Int) {
		val imageView = (context as Activity).findViewById<View>(rid) as ImageView
		into(imageView)
	}

	fun into(imageView: ImageView): TextView? {
		val request = Glide.with(context.applicationContext).load(url)
		if (isCache) {
			request.diskCacheStrategy(DiskCacheStrategy.ALL)
			request.skipMemoryCache(false)
		} else {
			request.diskCacheStrategy(DiskCacheStrategy.NONE)
			request.skipMemoryCache(true)
		}
		request.crossFade()
				.error(defaultImageId)
				.placeholder(defaultImageId)

		if (listener != null) {
			request.listener(object : RequestListener<String, GlideDrawable> {
				override fun onException(e: Exception, model: String, target: Target<GlideDrawable>, isFirstResource: Boolean): Boolean {
					listener!!.onLoadError()
					return false
				}

				override fun onResourceReady(resource: GlideDrawable, model: String, target: Target<GlideDrawable>, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
					listener!!.onLoadOk()
					return false
				}
			})
		}

		if (isCenterCrop) {
			request.centerCrop()
		}

		if (transformations != null) {
			request.transform(*transformations!!)
		}
		if (asBitmap) {
			request.asBitmap().into(imageView)
		} else {
			request.into(imageView)
		}

		return null
	}

	interface ImageDownloadCallback {
		fun onImageDownload(drawable: Bitmap?)
	}

	interface ImageLoaderListener {
		fun onLoadOk()

		fun onLoadError()
	}

	companion object {

		fun with(context: Context): ImageHelper {
			return ImageHelper(context)
		}

		fun downloadFromUrl(context: Context, url: String, width: Int, height: Int, callback: ImageDownloadCallback) {
			ThreadPoolUtil.getInstance()!!.fetchData(Runnable {
				try {
					val myBitmap = Glide.with(context)
							.load(url)
							.asBitmap()
							.centerCrop()
							.into(width, height)
							.get()
					//                    bottomLayout.setBackgroundDrawable(new BitmapDrawable(myBitmap));
					callback.onImageDownload(myBitmap)
				} catch (e: InterruptedException) {
					callback.onImageDownload(null)
					e.printStackTrace()
				} catch (e: ExecutionException) {
					callback.onImageDownload(null)
					e.printStackTrace()
				}
			})
		}
	}
}
