package appupdate.zhouh.com.appdownload.appupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow

import appupdate.zhouh.com.appdownload.R
import appupdate.zhouh.com.appdownload.utils.AppUpdateUtil

/**
 * @作者 hao
 * @创建日期 2018/1/5 11:10
 * Description: 版本更新封装
 */
object UpdateController {

    /**
     * 检查版本--模拟一个网络请求 获得一个实体AppVersionModel
     */
    fun getAppVersion(context: Activity, viewRoot: View) {
        val appVersionModel = AppVersionModel()
        appVersionModel.resUrl = "https://count.liqucn.com/d.php?id=38489&urlos=android&from_type=web"
        appVersionModel.verForce = 0
        appVersionModel.updateInfo = "我是一个新版本，更新吧。"

        if (appVersionModel.verForce != 4) {
            tipVersionUpdate(context, viewRoot, appVersionModel)
        } else {
            AppUpdateUtil.removeOldApk(context)
        }//没有新版本:删掉老apk
    }

    fun tipVersionUpdate(context: Activity, viewRoot: View, appVersionModel: AppVersionModel) {

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.app_update_pop, null)

        contentView.isFocusable = true
        contentView.isFocusableInTouchMode = true

        val appUpdatePopupWindow = AppUpdatePopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        appUpdatePopupWindow.isFocusable = true
        // 菜单背景色
        appUpdatePopupWindow.setBackgroundDrawable(null)

        appUpdatePopupWindow.setPWDataView(context, appVersionModel)

        appUpdatePopupWindow.setOnDismissListener {
            if (appVersionModel.verForce == 1) {
                appUpdatePopupWindow.showAtLocation(viewRoot, Gravity.CENTER, 0, 0)
                val intent = Intent(Intent.ACTION_MAIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK //如果是服务里调用，必须加入new task标识
                intent.addCategory(Intent.CATEGORY_HOME)
                context.startActivity(intent)
            }
        }
        appUpdatePopupWindow.showPopupWindow(viewRoot)
    }
}
