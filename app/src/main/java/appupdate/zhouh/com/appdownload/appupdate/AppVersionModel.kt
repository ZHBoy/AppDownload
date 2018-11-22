package appupdate.zhouh.com.appdownload.appupdate

import java.io.Serializable

/**
 * @作者 hao
 * @创建日期 2018/1/5 10:29
 * Description: 版本model
 */

class AppVersionModel : Serializable {

    /**
     * resUrl : string
     * updateInfo : string
     * verCode : 0
     * verForce : 0
     * verId : 0
     * verName : string
     */

    var resUrl: String? = null
    var updateInfo: String? = null
    var verCode: Int = 0
    var verForce: Int = 0//1强制 0不强制 4不弹框
    var verId: Int = 0
    var verName: String? = null
}
