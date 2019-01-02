package com.lib.lib_core.comm.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lib.lib_core.R

/**
 * Created by lis on 2018/12/13.
 */
class UpdateDialog : Dialog {

	constructor(context: Context) : super(context) {}

	constructor(context: Context, theme: Int) : super(context, theme) {}

	class Builder(private val context: Context) {
		private var isForce: Boolean = false
		private var title: String? = null
		private var message: String? = null
		private var confirmText: String? = null
		private var confirmListener: View.OnClickListener? = null

		fun setTitle(title: String): Builder {
			this.title = title
			return this
		}

		fun setMessage(message: String): Builder {
			this.message = message
			return this
		}


		fun setConfirmButton(confirmText: String, confirmListener: View.OnClickListener): Builder {
			this.confirmText = confirmText
			this.confirmListener = confirmListener
			return this
		}

		fun setForce(isForce: Boolean): Builder {
			this.isForce = isForce
			return this
		}

		fun create(): UpdateDialog {
			val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

			val dialog = UpdateDialog(context, R.style.dialog)
			dialog.setCancelable(false)

			val layout = inflater.inflate(R.layout.frame_dialog_update_layout, null)

			(layout.findViewById(R.id.tv_title) as TextView).text = title
			(layout.findViewById(R.id.tv_content) as TextView).text = message
			(layout.findViewById(R.id.tv_confirm) as TextView).text = confirmText
			layout.findViewById<ImageView>(R.id.iv_cancel).setVisibility(if (isForce) View.GONE else View.VISIBLE)
			if (confirmListener != null) {
				layout.findViewById<ImageView>(R.id.iv_cancel).setOnClickListener(View.OnClickListener { v ->
					dialog.dismiss()
					confirmListener!!.onClick(v)
				})
				layout.findViewById<TextView>(R.id.tv_confirm).setOnClickListener(View.OnClickListener { v ->
					dialog.dismiss()
					confirmListener!!.onClick(v)
				})
			}
			dialog.setContentView(layout)
			return dialog
		}
	}
}



