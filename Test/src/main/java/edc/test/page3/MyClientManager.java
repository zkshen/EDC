package edc.test.page3;

import android.text.TextUtils;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理client
 */
public class MyClientManager {
    public static final String KEY_UPDATED_AT = "updatedAt";
    private static MyClientManager myClientManager;
    private volatile AVIMClient mClient;
    private String clientId;


    public synchronized static MyClientManager getInstance() {
        if (myClientManager == null) {
            myClientManager = new MyClientManager();
        }
        return myClientManager;
    }

    private MyClientManager() {

    }

    public void open(String clientId, final AVIMClientCallback callback) {
        this.clientId = clientId;
        mClient = AVIMClient.getInstance(clientId);
        mClient.open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (callback != null) {
                    callback.done(avimClient, e);
                }
            }

        });
    }

    public AVIMClient getMyClient() {
        return mClient;
    }

    public String getClientId() {
        if (TextUtils.isEmpty(clientId)) {
            throw new IllegalStateException("Please call MyClientManager.open first");
        }
        return clientId;
    }

    public AVIMClient getClient() {
        return mClient;
    }

    public AVIMConversationQuery getConversationQuery() {
        return mClient.getQuery();
    }
}
