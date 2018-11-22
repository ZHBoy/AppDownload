package appupdate.zhouh.com.appdownload.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun s(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun l(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
