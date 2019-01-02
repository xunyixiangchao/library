package com.lib.lib_core.comm.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import java.util.*

/**
 * 运行时权限管理类
 * Created by lis on 2018/12/13.
 */
class PermissionManager private constructor() {

	private val requestPermissionQueue: HashMap<String, PermissionRequestObject>?

	interface PermissionHandler {
		/**
		 * 用户通过了所有申请权限
		 */
		fun onAllPermissionGranted()

		/**
		 * 用户有拒绝部分权限
		 *
		 * @param permission 被拒绝的权限
		 */
		fun onPermissionDenied(permission: Array<String>)

		/**
		 * 所有权限已经都永久拒绝
		 */
		fun onAllPermissionDenied()

		//        /**
		//         * 用户有拒绝部分权限
		//         */
		//        void onPermissionDenied();
		//        /**
		//         * @param shouldShowRequestPermissionRationale true:表明用户没有彻底禁止弹出权限请求
		//         *                                             false:表明用户已经彻底禁止弹出权限请求
		//         * @param permission
		//         */
		//        void onShowRequestPermissionRationale(boolean shouldShowRequestPermissionRationale, String permission);
	}

	inner class PermissionRequestObject {
		var activityClassName: String? = null
		var permission: String? = null
		var handler: PermissionHandler? = null
	}

	init {
		requestPermissionQueue = HashMap()
	}

	//    public void findPermission(final Activity activity, final String[] permissions, String permission, int[] grantResults) {
	//        for (int i = 0; i < permissions.length; i++) {
	//            if (permission.equals(permissions[i])) {
	//                if (PermissionManager.isDenied(grantResults[i])) {
	//                    boolean shouldShow = PermissionManager.shouldShowRequestPermissionRationale(activity, permissions[i]);
	//                    if (shouldShow) {
	//                        AlertDialog.Builder dialog = DialogBuilder.buildAlertDialog(activity, "提示", "请允许我们获得您的电话权限，拒绝后将无法正常支付，客官请三思。");
	//                        dialog.setPositiveButton("去允许", new DialogInterface.OnClickListener() {
	//                            @Override
	//                            public void onClick(DialogInterface dialog, int which) {
	//                                PermissionManager.requestPermission(activity, permissions);
	//                            }
	//                        });
	//
	//                        dialog.setNegativeButton("取消", null);
	//                        dialog.show();
	//                    } else {
	//                        AlertDialog.Builder dialog = DialogBuilder.buildAlertDialog(activity, "提示", "获取电话权限失败，无法正常支付，您可前往应用权限设置中打开权限");
	//                        dialog.setNegativeButton("我知道了", null);
	//                        dialog.show();
	//                    }
	//
	//                } else {
	//                    requestPayInfo("unionpay");
	//                }
	//            }
	//        }
	//    }

	/**
	 * 一条龙式权限申请处理
	 *
	 * @param activity
	 * @param permissions
	 * @param permissionHandler
	 */
	fun checkAndRequestPermission(activity: Activity, permissions: Array<String>, permissionHandler: PermissionHandler) {

		val deniedPermissions = checkPermission(activity, permissions)
		if (0 == deniedPermissions.size) {
			permissionHandler.onAllPermissionGranted()
		} else {
			requestPermission(activity, deniedPermissions)

			val permissionRequestObject = PermissionRequestObject()
			//            permissionRequestObject.activityClassName = activity.getClass().getName();
			permissionRequestObject.handler = permissionHandler
			//            requestPermissionQueue.add(permissionRequestObject);
			requestPermissionQueue!![activity.javaClass.name] = permissionRequestObject
		}

	}


	fun onRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		var permissionRequestObject: PermissionRequestObject? = null
		if (null == requestPermissionQueue || null == (requestPermissionQueue[activity.javaClass.name])) {
			return
		}
		permissionRequestObject = requestPermissionQueue!![activity.javaClass.name]
		val deniedPermission = ArrayList<String>()
		var tipsCount = 0
		for (i in grantResults.indices) {
			if (isDenied(grantResults[i])) {
				val isTip = PermissionManager.shouldShowRequestPermissionRationale(activity, permissions[i])

				if (isTip) {//表明用户没有彻底禁止弹出权限请求
					deniedPermission.add(permissions[i])
					//                    PermissionManager.requestPermission(getActivity(), permissions);
				} else {//表明用户已经彻底禁止弹出权限请求
					//   PermissionMonitorService.start(this);//这里一般会提示用户进入权限设置界面
					tipsCount++
				}
			}
		}

		val size = deniedPermission.size
		if (size > 0 && tipsCount != permissions.size) {
			permissionRequestObject!!.handler!!.onPermissionDenied(deniedPermission.toTypedArray())
		} else if (size > 0 && tipsCount == permissions.size) {
			permissionRequestObject!!.handler!!.onAllPermissionDenied()
		} else {
			permissionRequestObject!!.handler!!.onAllPermissionGranted()
		}
		requestPermissionQueue.remove(activity.javaClass.name)
	}

	companion object {
		//PackageManager.PERMISSION_GRANTED
		//PackageManager.PERMISSION_DENIED
		private var instance: PermissionManager? = null

		fun getInstance(): PermissionManager {
			if (null == instance) {
				instance = PermissionManager()
			}
			return instance as PermissionManager
		}

		val defaultPermission: Array<String>
			get() = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE)

		/**
		 * 检查权限是否通过
		 *
		 * @param context
		 * @param permission
		 * @return
		 */
		fun checkPermission(context: Context, permission: String): Boolean {
			return if (Build.VERSION.SDK_INT < 23) {
				//M以下版本直接成功
				true
			} else {
				context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
			}
		}

		/**
		 * 批量检查权限是否申请通过，如果有一个未授权，就返回false
		 *
		 * @param context
		 * @param permissions
		 * @return 返回未授权的权限
		 */
		fun checkPermission(context: Context, permissions: Array<String>): Array<String> {
			val deniedPermissions = ArrayList<String>()

			for (permission in permissions) {
				if (!checkPermission(context, permission)) {
					deniedPermissions.add(permission)
				}
			}
			return deniedPermissions.toTypedArray()
		}

		/**
		 * 权限是否被拒绝
		 *
		 * @param result
		 * @return
		 */
		fun isDenied(result: Int): Boolean {
			return result == PackageManager.PERMISSION_DENIED
		}

		/**
		 * 检查权限是否被永久拒绝
		 *
		 * @return true:表明用户没有彻底禁止弹出权限请求 false:表明用户已经彻底禁止弹出权限请求
		 */
		fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
			return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
		}

		@JvmOverloads
		fun requestPermission(context: Context, permission: Array<String>, requestCode: Int = 0) {
			if (context is Activity) {
				//            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
				//            }

				ActivityCompat.requestPermissions(context, permission, requestCode)
			}
		}

		/**
		 * 检测并且发送权限申请，如果权限已经申请通过，不执行任何方法
		 *
		 * @param activity
		 * @param permissions
		 */
		fun checkAndRequestPermission(activity: Activity, permissions: Array<String>) {
			if (0 != checkPermission(activity, permissions).size) {
				requestPermission(activity, permissions)
			}
		}
	}


}