package edc.test.page3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import edc.test.R;
import edc.test.main.Constants;
import edc.test.main.MainActivity;

public class LoginActivity extends Activity {

    private static final String TAG = "Login";

    protected EditText txtUserName;

    protected EditText txtPwd;

    public static void goLoginActivityFromActivity(Activity fromActivity) {
        Intent intent = new Intent(fromActivity, LoginActivity.class);
        fromActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_login);
        txtUserName = (EditText) findViewById(R.id.userName);
        txtPwd = (EditText) findViewById(R.id.pwd);
    }


    public void login(View view) {
        final String username = txtUserName.getText().toString().trim();
        final String pwd = txtPwd.getText().toString().trim();
        MyUser.logInInBackground(username, pwd, new LogInCallback<MyUser>() {
            public void done(MyUser user, AVException e) {
                if (e == null && user != null) {
                    updateUserInfo(user);
                } else if (user == null) {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                }
            }
        }, MyUser.class);
    }

    public void updateUserInfo(final MyUser user) {
        AVInstallation installation = AVInstallation.getCurrentInstallation();
        if (installation != null) {
            user.put(Constants.INSTALLATION, installation);
            user.put("num", 1);
            user.saveInBackground(new SaveCallback() {
                public void done(AVException e) {
                    if (filterException(e)) {
                        //根据用户名生成一个Client
                        MyClientManager.getInstance().open(user.getUsername(), new AVIMClientCallback() {
                            @Override
                            public void done(AVIMClient avimClient, AVIMException e) {
                                if (filterException(e)) {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    protected boolean filterException(Exception e) {
        if (e != null) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}
