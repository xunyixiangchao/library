package work.dd.com.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.Gravity
import android.widget.Toast

/**
 * Created by lis on 2018/12/13.
 */
object ToastUtils {

	private val handler = Handler(Looper.getMainLooper())
	private var toast: Toast? = null
	private val synObj = Any()

	/***
	 * Toast发送消息，默认Toast.LENGTH_LONG
	 *
	 * @param msg 消息内容
	 * @author 时培飞
	 * Create at 2015-5-28 下午3:16:02
	 */
	fun showMessageLong(act: Context, msg: String) {
		showMessage(act, msg, Toast.LENGTH_LONG)
	}

	/***
	 * Toast发送消息，默认Toast.LENGTH_LONG
	 *
	 * @author 时培飞
	 * Create at 2015-5-28 下午3:17:10
	 */
	fun showMessageLong(act: Context, msg: Int) {
		showMessage(act, msg, Toast.LENGTH_LONG)
	}

	/***
	 * Toast发送消息
	 *
	 * @param msg 发送内容
	 * @param len 显示时长
	 * Create at 2015-5-28 下午3:17:31
	 * @author 时培飞
	 */
	@JvmOverloads
	fun showMessage(act: Context, msg: Int, len: Int = Toast.LENGTH_SHORT) {
		handler.post {
			synchronized(synObj) {
				if (toast != null) {
					toast!!.cancel()
					toast!!.setText(msg)
					toast!!.duration = len
				} else {
					toast = Toast.makeText(act, msg, len)
				}
				toast!!.setGravity(Gravity.CENTER, 0, 0)
				toast!!.show()
			}
		}
	}

	/***
	 * Toast发送消息
	 *
	 * @param msg 发送内容
	 * @param len 显示时长
	 * Create at 2015-5-28 下午3:17:31
	 * @author 时培飞
	 */
	@JvmOverloads
	fun showMessage(act: Context?, msg: String, len: Int = Toast.LENGTH_SHORT) {
		if (!TextUtils.isEmpty(msg) && null != act) {

			//            ThreadPoolUtil.runOnMainThread(new Runnable() {
			//                @Override
			//                public void run() {
			//                    Toast.makeText(act, msg, len).show();
			//                }
			//            });
			val handler = Handler(Looper.getMainLooper())
			handler.post {
				val toast = Toast.makeText(act, msg, len)
				toast.setGravity(Gravity.CENTER, 0, 0)
				toast.show()
			}
		}
		//        new Thread(new Runnable() {
		//            public void run() {
		//                handler.post(new Runnable() {
		//                    @Override
		//                    public void run() {
		//                        synchronized (synObj) {
		//                            if (toast != null) {
		//                                toast.cancel();
		//                                toast.setText(msg);
		//                                toast.setDuration(len);
		//                            } else {
		//                                toast = Toast.makeText(act, msg, len);
		//                            }
		//                            toast.setGravity(Gravity.BOTTOM, 0, 0);
		//                            toast.show();
		//                        }
		//                    }
		//                });
		//            }
		//        }).start();
	}

	/**
	 * 关闭当前Toast
	 *
	 * @author 时培飞
	 * Create at 2015-5-28 下午3:18:31
	 */
	fun cancelCurrentToast() {
		if (toast != null) {
			toast!!.cancel()
		}
	}
}
/***
 * Toast发送消息，默认Toast.LENGTH_SHORT
 *
 * @param msg 消息内容
 * Create at 2015-5-28 下午3:15:26
 * @author 时培飞
 */
/***
 * Toast发送消息，默认Toast.LENGTH_SHORT
 *
 * 发送整形数据 Create at 2015-5-28 下午3:16:48
 * @author 时培飞
 */
