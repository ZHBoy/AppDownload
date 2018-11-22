package appupdate.zhouh.com.appdownload.appupdate

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import appupdate.zhouh.com.appdownload.R
import appupdate.zhouh.com.appdownload.utils.AppUpdateUtil
import appupdate.zhouh.com.appdownload.utils.SharedPreferencesUtils
import appupdate.zhouh.com.appdownload.utils.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File
import android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale


/**
 * Created by hao on 2017/6/12.
 */

class AppUpdatePopupWindow(private val conentView: View, width: Int, height: Int) : PopupWindow(conentView, width, height) {

    private var rl_update_content: RelativeLayout? = null
    private var rl_update_progress: RelativeLayout? = null

    private var my_progress: ProgressBar? = null

    private var tv_progress: TextView? = null

    private var isBindService: Boolean = false

    private var mContext: Activity? = null

    private val conn = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DownloadService.DownloadBinder
            val downloadService = binder.service

            //接口回调，下载进度
            downloadService.setOnProgressListener { fraction ->
                Log.i("zhou", "下载进度：" + (fraction * 100).toInt())
                //                    bnp.setProgress((int)(fraction * 100));、1
                val p = (fraction * 100).toInt()
                my_progress!!.progress = p

                tv_progress!!.text = (if (p >= 100) 100 else p).toString() + "%"

                //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                if (fraction == DownloadService.UNBIND_SERVICE && isBindService) {
                    mContext!!.unbindService(this)
                    isBindService = false
                    Log.i("zhou", "下载完成！")
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }
    }

    fun setPWDataView(context: Activity, appVersionModel: AppVersionModel) {
        mContext = context

        val rl_root_view = conentView.findViewById<RelativeLayout>(R.id.rl_root_view)
        val view_line = conentView.findViewById<View>(R.id.view_line)
        val tv_tip_ok = conentView.findViewById<TextView>(R.id.tv_tip_ok)
        val tv_tip_content = conentView.findViewById<TextView>(R.id.tv_tip_content)
        val tv_tip_cancel = conentView.findViewById<TextView>(R.id.tv_tip_cancel)
        rl_update_progress = conentView.findViewById(R.id.rl_update_progress)
        rl_update_content = conentView.findViewById(R.id.rl_update_content)
        my_progress = conentView.findViewById(R.id.my_progress)
        tv_progress = conentView.findViewById(R.id.tv_progress)

        if (appVersionModel.verForce == 1) {
            tv_tip_content.text = "对不起，老版本已经不提供支持，请点击马上更新。"

            view_line.visibility = View.GONE
            tv_tip_cancel.visibility = View.GONE

        } else {
            tv_tip_content.text = appVersionModel.updateInfo
        }

        conentView.setOnTouchListener { v, event ->
            //强制更新点击外面无反应
            appVersionModel.verForce == 1
        }

        tv_tip_ok.setOnClickListener { it ->
            RxPermissions(context as FragmentActivity)
                    .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe { // will emit 2 Permission objects
                        permission ->
                        if (permission.granted) {
                            val path = SharedPreferencesUtils.get(context, Constant.SP_DOWNLOAD_PATH, "")
                            val fileName = File(path)

                            if (fileName != null && fileName.exists() && fileName.isFile) {
                                AppUpdateUtil.installApk(context, Uri.parse("file://" + fileName.toString()))
                                if (appVersionModel.verForce == 1) {
                                    dismissPopupWindow()
                                }

                            } else {
                                isBindService = AppUpdateUtil.bindsService(context, appVersionModel.resUrl, conn)
                                ToastUtils.s(context, "新版本正在下载")
                                rl_update_progress!!.visibility = View.VISIBLE
                                rl_update_content!!.visibility = View.GONE

                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // Denied permission without ask never again
                        } else {
                            // Denied permission with ask never again
                            // Need to go to the settings
                        }
                    }
//            RxPermissions(conte
            tv_tip_cancel.setOnClickListener { dismissPopupWindow() }
            rl_root_view.setOnClickListener { dismissPopupWindow() }
        }
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    fun showPopupWindow(parent: View) {
        if (!this.isShowing) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.CENTER, 0, 0)
        } else {
            dismissPopupWindow()
        }
    }

    /**
     * 隐藏popupWindow
     *
     */
    fun dismissPopupWindow() {
        this.dismiss()
    }

}

