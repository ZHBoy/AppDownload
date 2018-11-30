# AppDownload
适配8.0，有下载进度的Android版本更新库。


版本更新涉及到几个问题，文件下载，下载进度，更新逻辑的控制（强制更新，正常更新以及无更新）等。本项目只是做了简单的实现，可根据公司产品要求和UI设计，自行拓展。

注意的地方：
1.AndroidManifest.xml中的权限注册，以及动态权限申请。（如果动态权限未申请到，请在设置中打开）

2.利用Android安装程序，7.0及以上会有问题，要添加fileProvider注册
 <!-- 7.0文件访问-->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="appupdate.zhouh.com.appdownload.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />

        </provider>
