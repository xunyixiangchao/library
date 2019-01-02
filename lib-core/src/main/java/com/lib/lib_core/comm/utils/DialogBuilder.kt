package com.lib.lib_core.comm.utils

import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import com.lib.lib_core.R
import work.dd.com.utils.ViewUtil

/**
 * Dialog的构建类
 * Created by lis on 2018/12/13.
 */
object DialogBuilder {

	/**
	 * 构建一个ProgressDialog
	 *
	 * @param context
	 * @param content 显示的内容
	 * @return
	 */
	fun buildProgressDialog(context: Context, content: String?): ProgressDialog {
		val dialog = ProgressDialog(context)
		if (!TextUtils.isEmpty(content)) {
			dialog.setMessage(content)
		}
		dialog.setCancelable(false)
		return dialog
	}

	/**
	 * 构建一个ProgressDialog
	 *
	 * @param context
	 * @param content 显示的内容
	 * @return
	 */
	fun buildProgressDialog(context: Context, content: String?, progress: Int): ProgressDialog {
		val dialog = buildProgressDialog(context, content)
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			dialog.setProgressNumberFormat("%1d M/%2d M")
		}
		dialog.progress = progress
		dialog.max = 0
		return dialog
	}

	/**
	 * 构建一个AlertDialog
	 *
	 * @param context
	 * @param title
	 * @param description
	 * @return
	 */
	fun buildAlertDialog(context: Context, title: String, description: CharSequence): AlertDialog.Builder {
		//        AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.MDDialogTheme);
		val dialog = AlertDialog.Builder(context)
		dialog.setTitle(title)
		if (!TextUtils.isEmpty(description)) {
			dialog.setMessage(description)
		}

		dialog.setCancelable(false)
		dialog.setPositiveButton("确定", null)
		return dialog
	}

	//    public static android.support.v7.app.AlertDialog.Builder buildMDAlertDialog(Context context, String title, CharSequence description) {
	//        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);
	//        dialog.setTitle(title);
	//        if (!TextUtils.isEmpty(description)) {
	////            builder.setMessage(description);
	//            dialog.setMessage(description);
	//        }
	//
	//        dialog.setCancelable(false);
	//        dialog.setNegativeButton("确定", null);
	//        return dialog;
	//    }

	/**
	 * 构建一个AlertDialog
	 *
	 * @param context
	 * @param title
	 * @param description
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	fun showAlertDialog(context: Context, title: String, description: String, okButton: String, cancelButton: String, listener: View.OnClickListener?) {
		var description = description
		//        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		val dialog = Dialog(context)
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

		val rootView = LayoutInflater.from(context).inflate(R.layout.dialog_base_layout, null)

		dialog.setContentView(rootView)
		//        dialog.setView(rootView);

		val titleView = rootView.findViewById(R.id.tv_dialog_title) as TextView
		val contentView = rootView.findViewById(R.id.tv_dialog_content) as TextView

		val okBt = rootView.findViewById(R.id.bt_dialog_button1) as TextView
		val cancelBt = rootView.findViewById(R.id.bt_dialog_button2) as TextView

		titleView.text = title
		if (!TextUtils.isEmpty(description)) {
			description = description.replace("\\r\\n", "<br />")
			contentView.text = Html.fromHtml(description)
		}

		if (TextUtils.isEmpty(okButton)) {
			okBt.visibility = View.GONE
		} else {
			okBt.visibility = View.VISIBLE
			okBt.text = okButton

			okBt.setOnClickListener { v ->
				listener?.onClick(v)

				if (dialog.isShowing) {
					dialog.dismiss()
				}
			}
			cancelBt.setOnClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
				}
			}
		}

		if (TextUtils.isEmpty(cancelButton)) {
			cancelBt.visibility = View.GONE
		} else {
			cancelBt.visibility = View.VISIBLE
			cancelBt.text = cancelButton
			cancelBt.setOnClickListener {
				if (dialog.isShowing) {
					dialog.dismiss()
				}
			}
		}

		if (!TextUtils.isEmpty(okButton) && !TextUtils.isEmpty(cancelButton)) {
			rootView.findViewById<View>(R.id.view_line).setVisibility(View.VISIBLE)
		} else {
			rootView.findViewById<View>(R.id.view_line).setVisibility(View.GONE)
		}

		dialog.setCancelable(false)
		//        builder.setPositiveButton(okButton, listener);
		//        builder.setNegativeButton(cancelButton, null);
		dialog.show()
		DialogBuilder.setDialogWindowAttr(dialog, ViewUtil.dip2px(context, 280F), ViewGroup.LayoutParams.WRAP_CONTENT)
	}

	@JvmOverloads
	fun setDialogWindowAttr(dlg: Dialog, width: Int, height: Int, gravity: Int = Gravity.CENTER): WindowManager.LayoutParams? {
		val window = dlg.window
		if (null != window) {
			val lp = window.attributes
			lp.gravity = gravity
			lp.width = width//宽高可设置具体大小
			lp.height = height
			dlg.window!!.attributes = lp
			return lp
		}
		return null

	}

	@JvmOverloads
	fun buildDialogFromBottom(context: Context, view: View? = null): Dialog {
		val mCameraDialog = Dialog(context, R.style.dialog_full_window_style)

		if (null != view) {
			mCameraDialog.setContentView(view)
		}
		val dialogWindow = mCameraDialog.window
		mCameraDialog.setCanceledOnTouchOutside(true)
		dialogWindow!!.setGravity(Gravity.BOTTOM)
		// 添加动画
		dialogWindow.setWindowAnimations(R.style.dialog_pop_from_bottom)

		//        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		//        lp.x = 0; // 新位置X坐标
		//        lp.y = -20; // 新位置Y坐标
		//        lp.width = context.getResources().getDisplayMetrics().widthPixels; // 宽度
		////      lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
		////      lp.alpha = 9f; // 透明度
		//        view.measure(0, 0);

		//        lp.height = view.getMeasuredHeight();
		//        lp.alpha = 9f; // 透明度
		//        dialogWindow.setAttributes(lp);
		//        WindowManager.LayoutParams lp = setDialogWindowAttr(mCameraDialog, context.getResources().getDisplayMetrics().widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
		val window = mCameraDialog.window
		if (null != window) {
			val lp = window.attributes
			//            lp.gravity = Gravity.CENTER;
			lp.width = context.resources.displayMetrics.widthPixels
			lp.height = WindowManager.LayoutParams.WRAP_CONTENT
			mCameraDialog.window!!.attributes = lp
		}
		//        if (lp != null) {
		//            lp.gravity = Gravity.BOTTOM;
		//        }

		mCameraDialog.show()
		return mCameraDialog
	}

	//    public static View getCloseMenuView(Context context, String title, View contentView) {
	//        View root = LayoutInflater.from(context).inflate(R.layout.dialog_listview_layout, null);
	//
	//        TextView titleView = (TextView) root.findViewById(R.id.tv_title);
	//        LinearLayout menusGroup = (LinearLayout) root.findViewById(R.id.group_menus);
	//
	//        titleView.setText(title);
	//
	//        menusGroup.removeAllViews();
	//
	//        menusGroup.addView(contentView);
	//
	//        return root;
	//    }
}
/**
 * 重新设置dialog的大小，如果不设置，dialog会特别小
 *
 * @param dlg
 * @param width
 * @param height
 *///    public static Dialog buildDialog(Context context) {
//        Dialog mCameraDialog = new Dialog(context, R.style.dialog_full_window_style);
//
//        mCameraDialog.setCanceledOnTouchOutside(true);
//
//        return mCameraDialog;
//    }
/**
 * 从底部弹出dialog
 */
