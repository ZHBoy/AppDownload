package appupdate.zhouh.com.appdownload.appupdate

import android.Manifest
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import appupdate.zhouh.com.appdownload.R
import appupdate.zhouh.com.appdownload.utils.AppUpdateUtil
import appupdate.zhouh.com.appdownload.utils.SharedPreferencesUtils
import appupdate.zhouh.com.appdownload.utils.ToastUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import java.io.File

/**
 *@author : HaoBoy
 *@date : 2018/11/27
 *@description :版本更新提示框
 **/

class UpdateDialog(context: Context?, appVersionModel: AppVersionModel) : Dialog(context, R.style.ActionSheetDialogStyle) {

    private var progressBar: ProgressBar? = null

    private var tvProgress: TextView? = null

    private var rlProgress: RelativeLayout? = null

    private var isBindService: Boolean = false

    init {
        this.setCancelable(false)
        this.setCanceledOnTouchOutside(false)
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_app_update, null, false)

        this.setContentView(contentView)
        val params = window.attributes
        params.width = context!!.resources.displayMetrics.widthPixels / 100 * 75
        params.height = context!!.resources.displayMetrics.heightPixels / 100 * 55
        window.attributes = params

        val tvOk = contentView.findViewById<TextView>(R.id.tvOk)
        val tvContent = contentView.findViewById<TextView>(R.id.tvContent)
        val tvCancel = contentView.findViewById<TextView>(R.id.tvCancel)
        rlProgress = contentView.findViewById(R.id.rlProgress)
        val llMustUpdate = contentView.findViewById<LinearLayout>(R.id.llMustUpdate)
        val llNotMustUpdate = contentView.findViewById<LinearLayout>(R.id.llNotMustUpdate)

        progressBar = contentView.findViewById(R.id.progressBar)
        tvProgress = contentView.findViewById(R.id.tvProgress)

        //设置文本内容
        tvContent.text = appVersionModel.updateInfo

        //是强制更新
        if (appVersionModel.verForce == 1) {
            llMustUpdate.visibility = View.VISIBLE
        } else {
            llNotMustUpdate.visibility = View.VISIBLE
        }

        //点击取消
        tvCancel.setOnClickListener { dismiss() }
        //点击升级
        tvOk.setOnClickListener { it ->
            RxPermissions(context as FragmentActivity)
                    .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe { // will emit 2 Permission objects
                        permission ->
                        if (permission.granted) {
                            val path: String = SharedPreferencesUtils.get(context, Constant.SP_DOWNLOAD_PATH, "")
                            val fileName = File(path)

                            if (fileName != null && fileName.exists() && fileName.isFile) {
                                AppUpdateUtil.installApk(context, Uri.parse("file://" + fileName.toString()))
                            } else {
                                isBindService = AppUpdateUtil.bindsService(context, appVersionModel.resUrl, conn)
                                ToastUtils.l(context, "新版本正在下载")
                                rlProgress!!.visibility = View.VISIBLE
                            }
                        } else if (permission.shouldShowRequestPermissionRationale) {
                        } else {
                        }
                    }
        }
    }

    private val conn = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as DownloadService.DownloadBinder
            val downloadService = binder.service

            //接口回调，下载进度
            downloadService.setOnProgressListener { fraction ->
                Log.i("zhou", "下载进度：" + (fraction * 100).toInt())
                //                    bnp.setProgress((int)(fraction * 100));、1
                val p = (fraction * 100).toInt()
                progressBar!!.progress = p

                tvProgress!!.text = (if (p >= 100) 100 else p).toString() + "%"

                //判断是否真的下载完成进行安装了，以及是否注册绑定过服务
                if (fraction == DownloadService.UNBIND_SERVICE && isBindService) {
                    context!!.unbindService(this)
                    isBindService = false
                    Log.i("zhou", "下载完成！")
                    rlProgress!!.visibility = View.GONE
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {

        }

    }

}