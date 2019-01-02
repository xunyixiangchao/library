package com.lib.lib_core.comm.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import com.lib.lib_core.R

/**
 * 通知栏管理类
 * Created by lis on 2018/12/13.
 */
object NotifyHelper {
	var notification_download_progress_id = 400332

	//    private HashMap<Integer, Integer> pushMaps;//收到的推送

	/**
	 * 发送一个通知
	 *
	 * @param context
	 * @param drawableID 通知的icon
	 * @param title      通知的标题
	 * @param content    通知的内容
	 * @param intent     点击通知打开的intent
	 */
	fun notify(context: Context, drawableID: Int, title: String, content: String, intent: Intent) {
		notify(context, drawableID, title, content, getBroadcastIntent(context, intent))
	}

	fun notify(context: Context, drawableID: Int, title: String, content: String, intent: PendingIntent) {
		//        //获取状态通知栏管理
		//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		//        //实例化通知栏构造器NotificationCompat.Builder
		//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		//        //对Builder进行配置
		//
		//        mBuilder.setContentTitle(title) //设置通知栏标题
		//                .setContentText(content) //设置通知栏显示内容
		////                .setContentIntent(getDefaultIntent(context, intent, Notification.FLAG_UPDATE_CURRENT)) //设置通知栏点击意图
		//                .setContentIntent(intent) //设置通知栏点击意图
		////                .setNumber(2) //设置通知集合的数量
		//                .setTicker(content) //通知首次出现在通知栏，带上升动画效果的
		//                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		//                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
		//                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
		//                .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
		//                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
		//                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
		//                .setSmallIcon(drawableID);//设置通知小ICON
		//        mNotificationManager.notify(notification_request_code, mBuilder.build());
		notify(context, drawableID, title, content, content, true, intent)
	}

	/**
	 * @param context
	 * @param drawableID
	 * @param title
	 * @param content
	 * @param ticker     通知首次出现在通知栏，带上升动画效果的
	 * @param vibrate    是否震动
	 * @param intent
	 */
	fun notify(context: Context, drawableID: Int, title: String, content: String, ticker: String?, vibrate: Boolean, intent: PendingIntent) {
		//获取状态通知栏管理
		val mNotificationManager = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager
		//实例化通知栏构造器NotificationCompat.Builder
		val mBuilder = NotificationCompat.Builder(context)
		//对Builder进行配置

		mBuilder.setContentTitle(title) //设置通知栏标题
				.setContentText(content) //设置通知栏显示内容
				//                .setContentIntent(getDefaultIntent(context, intent, Notification.FLAG_UPDATE_CURRENT)) //设置通知栏点击意图
				.setContentIntent(intent) //设置通知栏点击意图
				//                .setNumber(1) //设置通知集合的数量
				//                .setTicker(ticker) //通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
				.setPriority(Notification.PRIORITY_MAX) //设置该通知优先级
				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				//                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
				//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setLargeIcon(BitmapFactory.decodeResource(context.resources, drawableID))
				.setSmallIcon(R.drawable.icon_clean_transparent)//设置通知小ICON

		if (!TextUtils.isEmpty(ticker)) {
			mBuilder.setTicker(ticker)
		}
		if (vibrate) {
			mBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
		}
		mNotificationManager.notify(notification_download_progress_id, mBuilder.build())
	}

	fun notifyClear(context: Context) {
		//获取状态通知栏管理
		val mNotificationManager = context.getSystemService(Activity.NOTIFICATION_SERVICE) as NotificationManager

		mNotificationManager.cancel(notification_download_progress_id)
	}


	/**
	 * Intent转换为普通点击的PendingIntent
	 *
	 * @param context
	 * @param intent
	 * @return
	 */
	fun getDefaultIntent(context: Context, intent: Intent): PendingIntent {
		return PendingIntent.getActivity(context, notification_download_progress_id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}

	/**
	 * Intent转换为广播的PendingIntent
	 *
	 * @param context
	 * @param intent
	 * @return
	 */
	private fun getBroadcastIntent(context: Context, intent: Intent): PendingIntent {
		return PendingIntent.getBroadcast(context, notification_download_progress_id, intent, PendingIntent.FLAG_UPDATE_CURRENT)
	}

	//    public static PendingIntent getDeaultRecerer(Context context) {
	//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notification_request_code++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	//    }
}
