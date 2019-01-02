package com.lib.lib_core.comm.helper

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.lib.lib_core.R
import work.dd.com.dialog.DialogUiBuilder
import work.dd.com.utils.ViewUtil
import java.util.*

/**
 * 协助baseActivity和BaseFragment
 * Created by liuyuhang on 2017/6/19.
 */
class FrameUiHelper {
//    var customIconUrl: String? = null

	companion object INSTANCE {
		val animationIds = intArrayOf(R.drawable.le_loading_anim, R.drawable.le_loading_anim2, R.drawable.le_loading_anim3)

		val instance = FrameUiHelper()

		/**
		 * 本次app的动画id
		 */
		var currentLoadingAnimationId = -1
	}

	/**
	 * 获取dialog的UiBuilder
	 */
	fun getDialogUiBuilder(context: Context): DialogUiBuilder {

		val uiBuilder = object : DialogUiBuilder {
			/**
			 * 页面初始化

			 * @return
			 */
			override fun onViewCreate(inflater: LayoutInflater): View {
//                if (hasCustomIcon) {
				return inflater.inflate(R.layout.activity_frame_custom_loading_layout, null, false)
//                } else {
//                    return inflater.inflate(R.layout.activity_loading_animation_layout, null)
//                }
			}

			/**
			 * 页面内容绘制
			 */
			override fun onViewDraw(view: View, message: String?) {
				val userIconUrl = ""
				val hasCustomIcon =false

				val customLoadingView = view.findViewById<View>(R.id.group_custom_loading)
				val loadingView = view.findViewById<ImageView>(R.id.view_loading)
				if (currentLoadingAnimationId == -1) {
					currentLoadingAnimationId = Random().nextInt(animationIds.size)
				}

				if (hasCustomIcon) {
                    ViewUtil.swapView(loadingView, customLoadingView)

                    val animView = customLoadingView.findViewById<ImageView>(R.id.iv_background)
                    val loadImage = customLoadingView.findViewById(R.id.iv_image) as ImageView
                    val beeView = customLoadingView.findViewById(R.id.iv_bee) as ImageView

//                    ImageHelper.with(context).load(userIconUrl, R.drawable.index_baby_userpic).transform(GlideCircleTransform(context)).into(loadImage)

                    val scaleAnim = ObjectAnimator.ofFloat(animView, "rotation", 0f, 359f)
                    scaleAnim.interpolator = LinearInterpolator()
                    scaleAnim.duration = 1500
                    scaleAnim.repeatCount = -1
                    scaleAnim.start()

                    val transAnim = ObjectAnimator.ofFloat(beeView, "translationY", 0.0f, 10.0f, 0f)
                    transAnim.duration = 500
                    transAnim.repeatCount = -1
                    transAnim.start()
				} else {
                    ViewUtil.swapView(customLoadingView, loadingView)
                    val v = loadingView.findViewById(R.id.view_loading) as ImageView
                    v.setImageResource(animationIds[currentLoadingAnimationId])
                    (v.drawable as AnimationDrawable).start()
				}
			}

		}

//        if (hasCustomIcon) {
//            uiBuilder = object : DialogUiBuilder {
//                /**
//                 * 页面初始化
//
//                 * @return
//                 */
//                override fun onViewCreate(inflater: LayoutInflater?): View {
////                return R.layout.activity_frame_custom_loading_layout
//                    return inflater!!.inflate(R.layout.activity_frame_custom_loading_layout, null, false)
//                }
//
//                /**
//                 * 页面内容绘制
//                 */
//                override fun onViewDraw(view: View?, message: String?) {
//                    val animView = view?.findViewById(R.id.iv_background)
//
//                    val scaleAnim = ObjectAnimator.ofFloat(animView, "rotation", 0f, 359f)
//                    scaleAnim.interpolator = LinearInterpolator()
//                    scaleAnim.duration = 1000
//                    scaleAnim.repeatCount = -1
//                    scaleAnim.start()
//
////                val scaleAnim: ScaleAnimation = ScaleAnimation(0f, 36f0, 0f, 0f)
//
//                }
//
//            }
//        } else {
//            uiBuilder = object : DialogUiBuilder {
//
//                override fun onViewCreate(inflater: LayoutInflater): View {
//                    return inflater.inflate(R.layout.activity_loading_animation_layout, null)
//                    //                    return inflater.inflate(LeDialogImpl.Companion.getProgressDialog(), null);
//                }
//
//                override fun onViewDraw(view: View, message: String) {
//                    val loadingView = view.findViewById(R.id.view_loading)
//                    (loadingView.background as AnimationDrawable).start()
//                    //                    LeDialogImpl.Companion.onProgressDialogCreate(view, message);
//                }
//            }
//        }
		return uiBuilder
	}
}