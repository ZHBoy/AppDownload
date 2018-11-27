package appupdate.zhouh.com.appdownload.appupdate

import android.app.Activity
import android.view.View
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
            UpdateDialog(context, appVersionModel).show()
        } else {
            AppUpdateUtil.removeOldApk(context)
        }//没有新版本:删掉老apk
    }


}
