package com.lib.lib_core.comm.update

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.view.View
import com.lib.lib_core.R
import com.lib.lib_core.comm.bus.url.BusEventObserver
import com.lib.lib_core.comm.permission.PermissionManager
import com.lib.lib_core.comm.utils.*
import com.lib.lib_core.comm.view.dialog.UpdateDialog
import com.lib.lib_core.dao.SharedPreferencesProvider
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import work.dd.com.utils.ToastUtils

/**
 * 检测更新的工具类，负责执行检测更新
 * Created by lis on 2018/12/13.
 */
class UpdateHelper private constructor() {
	private val UPDATE_DOWNLOAD_STATUS_KEY = "update_files_download_status_version_"
	private val DOWNLOAD_STATUS_OK = "download_ok"
	private val DOWNLOAD_STATUS_NOT_OK = "download_not_ok"
	private var mSilentDownload = false
	private var mUpdateVo: UpdateVo? = null

	private var mBasePath: String? = null

	private val busEventObserver: BusEventObserver? = null

	/**
	 * 是否是通知栏进度显示
	 *
	 * @return
	 */
	val isNotifyProgressDialog: Boolean
		get() = mUpdateVo!!.updateStatus === UpdateVo.UpdateStatus.UPDATE

	/**
	 * 是否打开静默下载apk，如果打开，会在wifi下自动下载apk
	 *
	 * @param silent
	 */
	fun setAutoDownloadAPK(silent: Boolean) {
		mSilentDownload = silent
	}

	/**
	 * 执行update操作
	 *
	 * @param activity
	 * @param operator 操作update的抽象类
	 * @param listener update的监听类
	 */
	fun update(activity: Activity, operator: UpdateOperator?, listener: UpdateListener) {
		if (null == operator) {
			throw NullPointerException("operator must be not null")
		}
		val operation = UpdateOperation(activity, operator, listener)
		operation.execute()
	}

	fun setBasePath(path: String) {
		mBasePath = path
	}

	/**
	 * 执行检测更新的逻辑
	 *
	 * @param activity
	 * @param listener
	 */
	private fun invokeUpdate(activity: Activity, listener: UpdateListener) {
		when (mUpdateVo!!.updateStatus) {
			UpdateVo.UpdateStatus.FREE -> if (checkAPK(activity)) {
				showUpdateDialog(activity, listener)
			} else {
				listener.onUpdateSkip()
				autoSilentDownload(activity, null)
			}
			UpdateVo.UpdateStatus.UPDATE -> showUpdateDialog(activity, listener)
			UpdateVo.UpdateStatus.FORCE -> showUpdateDialog(activity, listener)
			UpdateVo.UpdateStatus.SILENCE, UpdateVo.UpdateStatus.NONE -> listener.onUpdateSkip()
		}
	}

	/**
	 * 显示检测更新dialog
	 *
	 * @param activity
	 * @param listener
	 */
	fun showUpdateDialog(activity: Activity, listener: UpdateListener) {
		val confirmText: String

		if (mUpdateVo!!.updateStatus === UpdateVo.UpdateStatus.FREE) {
			confirmText = "免流量更新"
		} else {
			confirmText = "马上更新"
		}

		/*

        new LeDialog(context).show(new LeDialog.UiBuilder() {
            @Override
            public View onViewCreate(LayoutInflater inflater) {
                return inflater.inflate(com.ichsy.core_library.R.layout.frame_dialog_update_layout, null);
            }

            @Override
            public void onViewDraw(final Dialog dialog, View view) {
                ((TextView) view.findViewById(com.ichsy.core_library.R.id.tv_title)).setText(mUpdateVo.title);
                ((TextView) view.findViewById(com.ichsy.core_library.R.id.tv_content)).setText(mUpdateVo.description);
                ((TextView) view.findViewById(com.ichsy.core_library.R.id.tv_confirm)).setText(confirmText);

                view.findViewById(R.id.iv_cancel).setVisibility(mUpdateVo.updateStatus == UpdateVo.UpdateStatus.FORCE
                        ? View.GONE : View.VISIBLE);

                ViewHelper.get(context).id(com.ichsy.core_library.R.id.iv_cancel).listener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        listener.onUpdateSkip();
                    }
                });
                ViewHelper.get(context).id(com.ichsy.core_library.R.id.tv_confirm).listener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (checkAPK(context)) {
                            listener.onUpdateCompleteAndInstall(UpdateDownloadProvider.getDownloadFile(mBasePath, mUpdateVo).getPath());
                        } else {
                            downloadUpdateFiles(context, false, listener);
                        }
                    }
                });
            }
        });

        */

		val builder = UpdateDialog.Builder(activity)
		builder.setForce(mUpdateVo!!.updateStatus === UpdateVo.UpdateStatus.FORCE)
				.setMessage(mUpdateVo!!.description).setTitle(mUpdateVo!!.title)
				.setConfirmButton(confirmText, View.OnClickListener { v ->
					if (v.id == R.id.iv_cancel) {
						listener.onUpdateSkip()
					} else if (v.id == R.id.tv_confirm) {
						//权限SD卡权限
						val isGranted = PermissionManager.checkPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)

						if (isGranted) {
							//                                if (checkAPK(activity)) {
							//                                    listener.onUpdateCompleteAndInstall(UpdateDownloadProvider.getDownloadFile(mBasePath, mUpdateVo).getPath());
							//                                } else {
							downloadUpdateFiles(activity, false, listener)
							//                                }
						} else {
							val dialog = DialogBuilder.buildAlertDialog(activity, "权限错误", "获取SD卡权限失败，无法下载更新文件")
							dialog.setPositiveButton("我知道了", DialogInterface.OnClickListener { dialog, which -> AppUtils.quitApp() })
							dialog.show()
						}


					}
				})
		builder.create().show()

		//        //需要更新，选择更新模式
		//        AlertDialog.Builder builder = DialogBuilder.buildAlertDialog(context, mUpdateVo.title, mUpdateVo.description);
		//
		//        if (mUpdateVo.updateStatus == UpdateVo.UpdateStatus.FREE) {
		//            builder.setNegativeButton("免流量安装", new DialogInterface.OnClickListener() {
		//                @Override
		//                public void onClick(DialogInterface dialog, int which) {
		//                    listener.onUpdateCompleteAndInstall(UpdateDownloadProvider.getDownloadFile(mBasePath, mUpdateVo).getPath());
		//                }
		//            });
		//        } else {
		//            builder.setNegativeButton("马上更新", new DialogInterface.OnClickListener() {
		//                @Override
		//                public void onClick(DialogInterface dialog, int which) {
		//                    if (checkAPK(context)) {
		//                        listener.onUpdateCompleteAndInstall(UpdateDownloadProvider.getDownloadFile(mBasePath, mUpdateVo).getPath());
		//                    } else {
		//                        downloadUpdateFiles(context, false, listener);
		//                    }
		//                }
		//            });
		//        }
		//
		//        if (mUpdateVo.updateStatus != UpdateVo.UpdateStatus.FORCE) {
		//            builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
		//                @Override
		//                public void onClick(DialogInterface dialog, int which) {
		//                    listener.onUpdateSkip();
		//                }
		//            });
		//        }
		//        builder.show();
	}

	interface UpgradeInterface {
		fun onUpgradeStart()
	}

	/**
	 * 下载更新文件
	 *
	 * @param activity
	 * @param silent   是否显示进度条
	 * @param listener
	 */
	fun downloadUpdateFiles(activity: Activity, silent: Boolean, listener: UpdateListener?) {
		//        LogUtils.i("update", "downloadUpdateFiles...");
		//        final ProgressDialog downloadProgressDialog = DialogBuilder.buildProgressDialog(activity, "正在下载更新\n" + mUpdateVo.description, 0);
		//
		//        if (null == busEventObserver) {
		//            busEventObserver = new BusEventObserver() {
		//                @Override
		//                public void onBusEvent(String event, Object message) {
		//                    DownloaderService.UpdateDownloadStatus updateDownloadStatus = (DownloaderService.UpdateDownloadStatus) message;
		//
		//                    if (updateDownloadStatus.status == DownloaderService.EVENT_UPDATE_DOWNLOAD_START) {
		//                        if (isNotifyProgressDialog()) {
		//                            //如果是普通更新，跳过页面在后台更新
		//                            listener.onUpdateSkip();
		//                        } else {
		//                            if (!silent && null != downloadProgressDialog) {
		//                                downloadProgressDialog.show();
		//                            }
		//                        }
		//
		//                    } else if (updateDownloadStatus.status == DownloaderService.EVENT_UPDATE_DOWNLOAD_PROGRESS) {
		//                        LogUtils.v("update", "download total-->>>" + updateDownloadStatus.max + "  current-->>>"
		//                                + updateDownloadStatus.current + " (progress: " + updateDownloadStatus.progress + "%)");
		//
		//                        if (isNotifyProgressDialog()) {
		////                            int pro = (int) (((float) updateDownloadStatus.current / (float) updateDownloadStatus.max) * 100);
		////                        NotifyHelper.notify(context, R.drawable.ic_launcher, "下载更新", "正在下载: " + pro, new Intent());
		//
		//                            PendingIntent emptyIntent = NotifyHelper.getDefaultIntent(activity, new Intent());
		//                            NotifyHelper.notify(activity, mUpdateVo.iconResId, "下载更新", "正在下载: " + updateDownloadStatus.progress + "%", null, false, emptyIntent);
		//
		////                            //获取状态通知栏管理
		////                            NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(Activity.NOTIFICATION_SERVICE);
		////                            //实例化通知栏构造器NotificationCompat.Builder
		////                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
		////                            //对Builder进行配置
		////                            mBuilder.setContentTitle("下载更新") //设置通知栏标题
		////                                    .setContentText("正在下载: " + updateDownloadStatus.progress + "%") //设置通知栏显示内容
		//////                .setContentIntent(getDefaultIntent(context, intent, Notification.FLAG_UPDATE_CURRENT)) //设置通知栏点击意图
		////                                    .setContentIntent(NotifyHelper.getDefaultIntent(activity, new Intent())) //设置通知栏点击意图
		//////                .setNumber(2) //设置通知集合的数量
		//////                                .setTicker(content) //通知首次出现在通知栏，带上升动画效果的
		////                                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
		////                                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
		////                                    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
		////                                    .setOngoing(true)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
		//////                                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
		////                                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
		////                                    .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
		////                            mNotificationManager.notify(NotifyHelper.notification_request_code, mBuilder.build());
		//
		//                        } else {
		//                            if (null != downloadProgressDialog) {
		//                                downloadProgressDialog.setMax(updateDownloadStatus.max);
		//                                downloadProgressDialog.setProgress(updateDownloadStatus.current);
		//                            }
		//                        }
		//
		//                    } else if (updateDownloadStatus.status == DownloaderService.EVENT_UPDATE_DOWNLOAD_SUCCESS) {
		//                        BusManager.getInstance().unRegister(DownloaderService.EVENT_UPDATE_STATUS, busEventObserver);
		//
		//                        setAPKDownloadStatus(activity, DOWNLOAD_STATUS_OK);
		//
		//                        if (isNotifyProgressDialog()) {
		//                            /**
		//                             * *******
		//                             * 下载完成，点击安装**********
		//                             **/
		////                            Uri uri = Uri.fromFile(new File(updateDownloadStatus.filePath));
		////                            Intent intent = new Intent(Intent.ACTION_VIEW);
		////                            intent.setDataAndType(uri, "application/vnd.android.package-archive");
		////                        pendingIntent = PendingIntent.getActivity(UpdateService.this, 0, intent, 0);
		//
		////                            PendingIntent defaultIntent = NotifyHelper.getDefaultIntent(activity, AppUtils.getInstallApplicationIntent(activity, updateDownloadStatus.filePath));
		////                            NotifyHelper.notify(activity, mUpdateVo.iconResId, "下载完成", "更新包下载完成，请点击安装", defaultIntent);
		//                            NotifyHelper.notifyClear(activity);
		//                            AppUtils.installApplication(activity, updateDownloadStatus.filePath);
		//                        } else {
		//                            if (null != downloadProgressDialog) {
		//                                downloadProgressDialog.dismiss();
		//                            }
		//                            if (null != listener) {
		//                                listener.onUpdateCompleteAndInstall(updateDownloadStatus.filePath);
		//                            }
		//                        }
		//
		//
		//                    } else if (updateDownloadStatus.status == DownloaderService.EVENT_UPDATE_DOWNLOAD_FAILED) {
		//                        BusManager.getInstance().unRegister(DownloaderService.EVENT_UPDATE_STATUS, busEventObserver);
		//
		//                        setAPKDownloadStatus(activity, DOWNLOAD_STATUS_NOT_OK);
		//                        LogUtils.i("update", "error: " + updateDownloadStatus.message);
		//
		//                        if (isNotifyProgressDialog()) {
		//                            NotifyHelper.notify(activity, mUpdateVo.iconResId, "下载失败", "下载更新文件失败", new Intent());
		//                        } else {
		//                            if (null != downloadProgressDialog && downloadProgressDialog.isShowing()) {
		//                                downloadProgressDialog.dismiss();
		//                            }
		//                            AlertDialog.Builder dialog = DialogBuilder.buildAlertDialog(activity, "错误", "网络不佳，请重试");
		//                            dialog.setPositiveButton("重试", new DialogInterface.OnClickListener() {
		//                                @Override
		//                                public void onClick(DialogInterface dialog, int which) {
		//                                    downloadUpdateFiles(activity, silent, listener);
		//                                }
		//                            });
		//                            dialog.setNegativeButton("退出", new DialogInterface.OnClickListener() {
		//                                @Override
		//                                public void onClick(DialogInterface dialog, int which) {
		//                                    AppUtils.quitApp();
		//                                }
		//                            });
		//                            dialog.show();
		//                        }
		//
		//                    }
		//                }
		//            };
		//        }
		////
		//        Intent intent = new Intent(activity, DownloaderService.class);
		//        intent.putExtra(DownloaderService.INTENT_UPDATE_OBJECT, mUpdateVo);
		//        intent.putExtra(DownloaderService.INTENT_UPDATE_BASE_PATH, mBasePath);
		//
		//        BusManager.getInstance().register(DownloaderService.EVENT_UPDATE_STATUS, busEventObserver);
		//        activity.startService(intent);

		val downloadProgressDialog = DialogBuilder.buildProgressDialog(activity, null, 0)
		downloadProgressDialog.setTitle("正在下载安装包")
		val url = mUpdateVo!!.url
		val apkPath = mBasePath + "release/" + FileUtil.getFileNameByUrl(url)
		FileDownloader.getImpl().create(url).setPath(apkPath).setListener(object : FileDownloadListener() {
			protected override fun pending(baseDownloadTask: BaseDownloadTask, i: Int, i1: Int) {
				LogUtils.i("update", "downloadUpdateFiles...")

				if (isNotifyProgressDialog) {
					//如果是普通更新，跳过页面在后台更新
					ToastUtils.showMessage(activity, "安装包正在后台下载中...")
					listener!!.onUpdateSkip()
				} else {
					if (!silent) {
						downloadProgressDialog.show()
					}
				}
			}

			protected override fun connected(task: BaseDownloadTask, etag: String, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
				super.connected(task, etag, isContinue, soFarBytes, totalBytes)
				downloadProgressDialog.setMax(totalBytes)
			}

			protected override fun progress(baseDownloadTask: BaseDownloadTask, current: Int, max: Int) {
				val progress = (current.toFloat() / max.toFloat() * 100).toInt()
				LogUtils.v("update", "download total-->>>$max  current-->>>$current (progress: $progress%)")
				if (isNotifyProgressDialog) {
					val emptyIntent = NotifyHelper.getDefaultIntent(activity, Intent())
					NotifyHelper.notify(activity, mUpdateVo!!.iconResId, "下载安装包", "正在下载: $progress%", null, false, emptyIntent)
				} else {
					//                    downloadProgressDialog.setMax(max);
					downloadProgressDialog.setProgress(current)
					downloadProgressDialog.setProgressNumberFormat(String.format("%.2f M/%.2f M", current.toFloat() / 1024f / 1024f, max.toFloat() / 1024f / 1024f))
				}
			}

			protected override fun completed(baseDownloadTask: BaseDownloadTask) {
				LogUtils.v("update", "download complete")
				setAPKDownloadStatus(activity, DOWNLOAD_STATUS_OK)
				if (isNotifyProgressDialog) {
					//** 下载完成，点击安装 **/
					NotifyHelper.notifyClear(activity)
					//                    AppUtils.installApplication(activity, baseDownloadTask.getTargetFilePath());
				} else {
					downloadProgressDialog.dismiss()
				}
				if (null != listener) {
					listener!!.onUpdateCompleteAndInstall(baseDownloadTask.getTargetFilePath())
				}
			}

			protected override fun paused(baseDownloadTask: BaseDownloadTask, i: Int, i1: Int) {

			}

			protected override fun error(baseDownloadTask: BaseDownloadTask, throwable: Throwable) {
				LogUtils.v("update", "download error:" + throwable.message)

				setAPKDownloadStatus(activity, DOWNLOAD_STATUS_NOT_OK)

				if (isNotifyProgressDialog) {
					NotifyHelper.notify(activity, mUpdateVo!!.iconResId, "下载失败", "下载安装包失败", Intent())
				} else {
					if (downloadProgressDialog.isShowing()) {
						downloadProgressDialog.dismiss()
					}
					val dialog = DialogBuilder.buildAlertDialog(activity, "错误", "网络不佳，请重试")
					dialog.setPositiveButton("重试", DialogInterface.OnClickListener { dialog, which -> downloadUpdateFiles(activity, silent, listener) })
					dialog.setNegativeButton("退出", DialogInterface.OnClickListener { dialog, which -> AppUtils.quitApp() })
					dialog.show()
				}

			}

			protected override fun warn(baseDownloadTask: BaseDownloadTask) {

			}
		}).start()
	}

	//    private BusEventObserver updateStatusObserver = new BusEventObserver() {
	//
	//        @Override
	//        public void onBusEvent(String event, Object message) {
	//            if (DownloaderService.EVENT_UPDATE_DOWNLOAD_START.equals(event)) {
	//
	//            } else if (DownloaderService.EVENT_UPDATE_DOWNLOAD_PROGRESS.equals(event)) {
	//
	//            } else if (DownloaderService.EVENT_UPDATE_DOWNLOAD_SUCCESS.equals(event)) {
	//
	//            } else if (DownloaderService.EVENT_UPDATE_DOWNLOAD_FAILED.equals(event)) {
	//
	//            }
	//        }
	//    };

	/**
	 * 检测apk是否已经下载完毕
	 *
	 * @return 返回是否下载完毕并且大小正确
	 */
	fun checkAPK(context: Context): Boolean {
		val apk = UpdateDownloadProvider.getDownloadFile(this!!.mBasePath!!, this!!.mUpdateVo!!)

		LogUtils.i("update", "check download apk...")
		LogUtils.i("update", "apk.exists(): " + apk.exists())
		LogUtils.i("update", "isAPKDownloadOk(context): " + isAPKDownloadOk(context))
		LogUtils.i("update", "apk.length(): " + apk.length() + "  vo.size: " + mUpdateVo!!.size)

		return apk.exists() && isAPKDownloadOk(context)
	}

	/**
	 * 判断apk是否下载完毕
	 *
	 * @return
	 */
	fun isAPKDownloadOk(context: Context): Boolean {
		val provider = SharedPreferencesProvider()
		val status = provider.getProvider(context).getCache(UPDATE_DOWNLOAD_STATUS_KEY + mUpdateVo!!.version)
		return DOWNLOAD_STATUS_OK == status
	}

	/**
	 * 设置apk下载状态
	 *
	 * @param context
	 * @param status
	 */
	fun setAPKDownloadStatus(context: Context, status: String) {
		val provider = SharedPreferencesProvider()
		provider.getProvider(context).putCache(UPDATE_DOWNLOAD_STATUS_KEY + mUpdateVo!!.version, status)
	}

	/**
	 * 满足以下条件才可以开启静默下载
	 * 1:当前是wifi状态，静默下载
	 * 2:当前没有下载完成的完整文件（比对文件大小）
	 * 3:已经开启了静默下载
	 *
	 * @param activity
	 * @param listener
	 */
	fun autoSilentDownload(activity: Activity, listener: UpdateListener?) {
		if (mSilentDownload && !checkAPK(activity) && NetWorkUtils.getNetworkType(activity) === NetWorkUtils.NETTYPE_WIFI) {
			downloadUpdateFiles(activity, true, listener)
		}
	}

	/**
	 * 操作检测更新逻辑的异步类
	 *
	 *
	 * Created by liuyuhang on 16/5/3.
	 */
	private inner class UpdateOperation(private val activity: Activity, private val operator: UpdateOperator, private val listener: UpdateListener) : AsyncTask<Void, Void, UpdateVo>() {

		override fun onPreExecute() {
			super.onPreExecute()
			this.listener.onUpdatePre()
		}

		override fun doInBackground(vararg params: Void): UpdateVo {
			val url = operator.onUpdateRequest()
			val header = operator.onUpdateRequestHeader()
			val param = operator.onUpdateRequestParams()

			val result = operator.doRequest(url, header!!, param!!)
			//发送更新请求
			return operator.parserUpdateJson(result!!)
		}

		override fun onPostExecute(updateObject: UpdateVo) {
			super.onPostExecute(updateObject)
			//            UpdateVo.UpdateStatus status = updateObject.updateStatus;

			mUpdateVo = updateObject
			if (null == mUpdateVo) {
				mUpdateVo = UpdateVo()
			}

			invokeUpdate(activity, listener)

			//            if (status == UpdateVo.UpdateStatus.NONE) {
			//                //不需要更新
			//                listener.onUpdateSkip();
			//            } else {
			//                //需要更新，选择更新模式
			//                AlertDialog.Builder builder = DialogBuilder.buildAlertDialog(context, updateObject.title, updateObject.description);
			//
			//                if (checkAPK(updateObject)) {
			//                    builder.setNegativeButton("马上更新（免流量）", new DialogInterface.OnClickListener() {
			//                        @Override
			//                        public void onClick(DialogInterface dialog, int which) {
			////                        downloadUpdateFiles(context, updateObject.url, false, listener);
			//                            listener.onUpdateCompleteAndInstall(UpdateDownloadProvider.getDownloadFile(updateObject).getPath());
			//                        }
			//                    });
			//                } else {
			//                    builder.setNegativeButton("马上更新", new DialogInterface.OnClickListener() {
			//                        @Override
			//                        public void onClick(DialogInterface dialog, int which) {
			//                            downloadUpdateFiles(context, updateObject.url, false, listener);
			////                          listener.onUpdateComplete();
			//                        }
			//                    });
			//                }
			//
			//                if (status != UpdateVo.UpdateStatus.FORCE) {//如果是强制更新，不需要显示取消按钮（也就是下次再说）
			//                    builder.setPositiveButton("下次再说", new DialogInterface.OnClickListener() {
			//                        @Override
			//                        public void onClick(DialogInterface dialog, int which) {
			//                            listener.onUpdateSkip();
			//
			//                            //静默更新的逻辑
			//                            autoSilentDownload(context, updateObject, listener);
			//                        }
			//                    });
			//                }
			//
			//                builder.show();
			//            }
		}
	}

	companion object {
		private var instance: UpdateHelper? = null

		fun getInstance(): UpdateHelper {
			if (null == instance) {
				instance = UpdateHelper()
			}
			return instance as UpdateHelper
		}
	}
}