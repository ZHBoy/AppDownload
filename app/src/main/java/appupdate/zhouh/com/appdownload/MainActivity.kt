package appupdate.zhouh.com.appdownload

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import appupdate.zhouh.com.appdownload.appupdate.UpdateController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvClick.setOnClickListener {
            UpdateController.getAppVersion(this, rootView)
        }
    }

}
