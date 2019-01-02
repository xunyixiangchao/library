package com.lib.lib_core.comm.logwatch.view

import android.content.Context
import android.text.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lib.lib_core.R
import com.lib.lib_core.comm.logwatch.InfoBean
import com.lib.lib_core.comm.utils.ViewHolder
import com.lib.lib_core.frame.adapter.BaseFrameAdapter

/**
 * Created by lis on 2018/12/13.
 */
class ContentAdapter(context: Context) : BaseFrameAdapter<InfoBean>(context) {

	override fun onViewCreate(position: Int, inflater: LayoutInflater, parent: ViewGroup): View {
		return inflater.inflate(R.layout.logwatch_float_content_view_item, null)
	}

	override fun onViewAttach(position: Int, item: InfoBean, convertView: View) {
		val packageName = ViewHolder.get<TextView>(convertView, R.id.float_content_view_package_name)
		packageName!!.setText(item.classesContent)

		packageName.setOnClickListener(View.OnClickListener {
			val myClipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
			//                        ClipData myClip = ClipData.newPlainText("text", text);
			myClipboard.text = item.classesContent
		})
	}

}