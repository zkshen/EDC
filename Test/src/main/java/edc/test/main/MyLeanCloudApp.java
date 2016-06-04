package edc.test.main;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import edc.test.page2.ControlFragment;

/**
 * Created by zkshen on 2016/5/28.
 */
public class MyLeanCloudApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"u38RhgINiywjgPLfGI2cbmH1-gzGzoHsz","AcFxHaSaoWD9yXa6HeJIhq5G");
        AVIMMessageManager.registerMessageHandler(AVIMTypedMessage.class, new CustomMessageHandler());
    }

    public static class CustomMessageHandler extends AVIMMessageHandler {
        //接收到消息后的处理逻辑
        @Override
        public void onMessage(AVIMMessage message,AVIMConversation conversation,AVIMClient client){
            if(message instanceof AVIMTextMessage){
                Log.d("Tom & Jerry", ((AVIMTextMessage) message).getText());
                switch (((AVIMTextMessage) message).getText()){
                    case "RefreshDevice":
                        ControlFragment.sendDevices();
                }
            }
        }

        public void onMessageReceipt(AVIMMessage message,AVIMConversation conversation,AVIMClient client){

        }
    }
}
