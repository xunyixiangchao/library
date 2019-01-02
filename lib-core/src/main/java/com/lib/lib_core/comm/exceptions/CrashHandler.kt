package com.lib.lib_core.comm.exceptions

import android.content.Context
import android.os.Looper
import android.widget.Toast
import com.lib.lib_core.comm.utils.AppUtils
import com.lib.lib_core.comm.utils.FileUtil
import com.lib.lib_core.comm.utils.LogUtils
import work.dd.com.utils.ThreadPoolUtil
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * 处理Crash的handler类
 * Created by lis on 2018/12/13.
 */
class CrashHandler
// 用来存储设备信息和异常信息
// private Map<String, String> infos = new HashMap<String, String>();

// 用于格式化日期,作为日志文件名的一部分
// private DateFormat formatter = new
// SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

/**
 * 保证只有一个 CrashHandler 实例
 */
private constructor() : Thread.UncaughtExceptionHandler {

	// 程序的 Context 对象
	private var mContext: Context? = null
	private var mainClass: Class<*>? = null
	private var filterClassMap: HashMap<String, Boolean>? = null//过滤的class，在这些class中崩溃，不进行程序的重启
	private var currentActivityName: String? = null
	private var mCrashBasePath: String? = null
	private var extMessage: String? = null

	// 系统默认的 UncaughtException 处理类
	private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

	/**
	 * 设置过滤的崩溃类，防止在splash崩溃之后无限重启app
	 */
	fun setFilterClass(vararg filterClass: Class<*>): CrashHandler {
		if (null == filterClassMap) {
			filterClassMap = HashMap()
		}
		for (filterClazz in filterClass) {
			filterClassMap!![filterClazz.name] = true
		}
		return this
	}

	fun setExtMessage(extMessage: String): CrashHandler {
		this.extMessage = extMessage
		return this
	}

	/**
	 * 设置当前的activity
	 *
	 * @param context
	 */
	fun setCurrentActivityName(context: Context) {
		currentActivityName = context.javaClass.name
	}

	/**
	 * 初始化
	 *
	 * @param context
	 */
	fun init(context: Context, mainClass: Class<*>, crashBasePath: String) {
		this.mContext = context.applicationContext
		this.mainClass = mainClass
		this.mCrashBasePath = crashBasePath
		// 获取系统默认的 UncaughtException 处理器
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()

		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this)
	}

	override fun uncaughtException(thread: Thread, ex: Throwable) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler!!.uncaughtException(thread, ex)
		} else {
			try {
				Thread.sleep(3000)
			} catch (e: InterruptedException) {
				// Log.e(TAG, "error : ", e);
				e.printStackTrace()
			}

			Toast.makeText(mContext!!.applicationContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show()

			// 退出程序,注释下面的重启启动程序代码
			// android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(1);

			// 重新启动程序，注释上面的退出程序
			// if (mCla != null) {
			// Intent intent = new Intent();
			// intent.setClass(mContext, mCla);
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// mContext.startActivity(intent);
			// }

			android.os.Process.killProcess(android.os.Process.myPid())
			System.exit(1)

		}
	}

	/**
	 * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
	 *
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private fun handleException(ex: Throwable?): Boolean {
		if (ex == null) {
			return false
		}

		// saveCrashInfo2File(ex);
		// 使用 Toast 来显示异常信息
		ThreadPoolUtil.getInstance()!!.fetchData(Runnable {
			Looper.prepare()

			Toast.makeText(mContext!!.applicationContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_SHORT).show()

			val sb = StringBuilder()
			val writer = StringWriter()
			val printWriter = PrintWriter(writer)
			ex.printStackTrace(printWriter)
			var cause: Throwable? = ex.cause
			while (cause != null) {
				cause.printStackTrace(printWriter)
				cause = cause.cause
			}
			printWriter.close()
			val result = writer.toString()

			val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
			val date = sdf.format(Date(System.currentTimeMillis()))
			sb.append("崩溃时间：").append(date).append("\n")
			sb.append("崩溃版本：").append(AppUtils.getAppVersion(mContext!!)).append("\n")
			sb.append("附带信息：").append(extMessage).append("\n\n\n")
			sb.append(result)

			FileUtil.writeByBufferWriter(this!!.mCrashBasePath!!, "$date.txt", sb.toString(), false)

			LogUtils.e("crash", sb.toString())

			//检查如果在特殊的class，不进行app重启

			//app崩溃不重启了
			//                if (null == filterClassMap || null == filterClassMap.get(currentActivityName) || !filterClassMap.get(currentActivityName)) {
			//                    Intent intent = new Intent(mContext, mainClass);
			//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			//                    mContext.startActivity(intent);
			//                }

			Looper.loop()
		})

		// 收集设备参数信息
		// collectDeviceInfo(mContext);
		// 保存日志文件
		return true
	}

	companion object {
		// CrashHandler 实例
		private val INSTANCE = CrashHandler()

		/**
		 * 获取 CrashHandler 实例 ,单例模式
		 */
		fun instance(): CrashHandler {
			return INSTANCE
		}
	}

	//	public static void openErrorView(Context context, String errorInfo) {
	//		File file = new File(Environment.getExternalStorageDirectory().getPath(), "error.txt");
	//		FileUtil.saveInfo2File(file, errorInfo);
	//		openSystemBorwser(context, Uri.fromFile(file));
	//	}
	//
	//    public static void openSystemBorwser(Context context, Uri uri) {
	//        PackageManager pageManage = context.getPackageManager();
	//
	//        List<PackageInfo> packages = pageManage.getInstalledPackages(0);
	//        for (int i = 0; i < packages.size(); i++) {
	//            PackageInfo packageInfo = packages.get(i);
	//            String appName = packageInfo.applicationInfo.loadLabel(pageManage).toString();
	//            String pagName = packageInfo.packageName;
	//            if (appName.contains("浏览器") || appName.contains("互联网") || appName.toLowerCase().contains("browser")) {
	////				packageName = list.get(i).getPackageName();
	//
	////                String mainActivity = AppContextUtils.getMainActivity(context, packageName);
	//
	//                Intent intent = doStartApplicationWithPackageName(pageManage, pagName);
	//                if (null != intent) {
	//                    intent.setData(uri);
	//                    context.startActivity(intent);
	//                }
	//                break;
	//            }
	//        }
	//
	//
	////		String packageName = "";
	////		Intent intent = new Intent();
	////		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	////		intent.setAction("android.intent.action.VIEW");
	////
	////		List<AppInfo> list = AppContextUtils.getAllApps(context);
	////		for (int i = 0; i < list.size(); i++) {
	////			if (list.get(i).getAppName().contains("浏览器")) {
	////				packageName = list.get(i).getPackageName();
	////				break;
	////			}
	//    }
	//
	//    private static Intent doStartApplicationWithPackageName(PackageManager pageManage, String packagename) {
	//
	//        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
	//        PackageInfo packageinfo = null;
	//        try {
	//            packageinfo = pageManage.getPackageInfo(packagename, 0);
	//        } catch (PackageManager.NameNotFoundException e) {
	//            e.printStackTrace();
	//        }
	//        if (packageinfo == null) {
	//            return null;
	//        }
	//
	//        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
	//        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
	//        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	//        resolveIntent.setPackage(packageinfo.packageName);
	//
	//        // 通过getPackageManager()的queryIntentActivities方法遍历
	//        List<ResolveInfo> resolveinfoList = pageManage.queryIntentActivities(resolveIntent, 0);
	//
	//        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
	//        if (resolveinfo != null) {
	//            // packagename = 参数packname
	//            String packageName = resolveinfo.activityInfo.packageName;
	//            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
	//            String className = resolveinfo.activityInfo.name;
	//            // LAUNCHER Intent
	//            Intent intent = new Intent(Intent.ACTION_MAIN);
	//            intent.addCategory(Intent.CATEGORY_LAUNCHER);
	//
	//            // 设置ComponentName参数1:packagename参数2:MainActivity路径
	//            ComponentName cn = new ComponentName(packageName, className);
	//
	//            intent.setComponent(cn);
	//            return intent;
	////            context.startActivity(intent);
	//        }
	//        return null;
	//    }

	//		String mainActivity = AppContextUtils.getMainActivity(context, packageName);
	//
	//		if (mainActivity != null) {
	//			intent.setClassName(packageName, mainActivity);
	//			intent.setData(uri);
	//
	//			IchsyIntent ichsyIntent = new IchsyIntent(SystemBorwserUtils.class.getName(), intent, mainActivity);
	//			IntentBus.getInstance().startActivity(context, ichsyIntent);
	//		}
	//	}

}
