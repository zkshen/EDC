package edc.test.main;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by zkshen on 2016/5/28.
 */
public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"u38RhgINiywjgPLfGI2cbmH1-gzGzoHsz","AcFxHaSaoWD9yXa6HeJIhq5G");
    }
}
