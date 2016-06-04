package edc.test.page3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import edc.test.R;

public class SignUpActivity extends Activity {
    private EditText username;
    private EditText pwd;
    private EditText pwd2;
    private int username_checked = 0;
    private int pwd_checked = 0;
    private int pwd2_checked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        setContentView(R.layout.activity_sign_up);
        username = (EditText) findViewById(R.id.RegUsername);
        pwd = (EditText) findViewById(R.id.RegPwd);
        pwd2 = (EditText) findViewById(R.id.RegPwdAgain);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str = username.getText().toString();
                    if (str.length() <= 0) {
                        Toast.makeText(SignUpActivity.this, "账户名不得为空", Toast.LENGTH_SHORT).show();
                        username_checked = 0;
                    } else {
                        username_checked = 1;
                    }
                }
            }
        });
        pwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    //You must get username before inputting pwd
                    if (username_checked == 0) {
                        username.requestFocus();
                        pwd.clearFocus();
                    }
                } else {

                    if (username_checked == 1) {
                        String str = pwd.getText().toString();

                        //Check limits
                        if (str.length() < 6) {
                            Toast.makeText(SignUpActivity.this, "密码不得少于6位", Toast.LENGTH_SHORT).show();
                            pwd_checked = 0;
                        }

                        //Checked
                        else {
                            pwd_checked = 1;
                        }
                    }
                }
            }
        });
        pwd2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //You must get pwd before inputting pwd2
                    if (pwd_checked == 0) {
                        pwd.requestFocus();
                        pwd2.clearFocus();
                    }
                }
            }
        });

        //Check the password again when press the Enter
        pwd2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String str = pwd2.getText().toString();

                    //Check limits
                    if (!str.equals(pwd.getText().toString())) {
                        Toast.makeText(SignUpActivity.this, "两次密码输入不同", Toast.LENGTH_SHORT).show();
                        pwd2_checked = 0;
                    }

                    //Checked
                    else {
                        pwd2_checked = 1;
                    }
                    return true;
                }
                return false;

            }
        });

        pwd2.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (pwd2.getText().toString().equals(pwd.getText().toString())) {
                    pwd2_checked = 1;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pwd2.getText().toString().equals(pwd.getText().toString())) {
                    pwd2_checked = 1;
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Called when the user_item clicks the RegCommit button
     */

    public void regCommit(View view) {
        if (username_checked == 1 && pwd_checked == 1 && pwd2_checked == 1) {
            final AVObject UserLog = new AVObject("UserLog");// 构建对象
            UserLog.put("User", username.getText().toString());
            UserLog.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        String userid;
                        userid = UserLog.getObjectId();
                        MyUser.signUpByNameAndPwd(username.getText().toString() + "1", pwd.getText().toString(), userid, new SignUpCallback() {
                            public void done(AVException e) {
                                if (filterException(e)) {

                                }
                            }
                        });
                        MyUser.signUpByNameAndPwd(username.getText().toString() + "0", pwd.getText().toString(), userid, new SignUpCallback() {
                            public void done(AVException e) {
                                if (filterException(e)) {

                                }
                            }
                        });
                        Toast.makeText(SignUpActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 失败的话，请检查网络环境以及 SDK 配置是否正确
                    }
                }
            });
        } else {
            Toast.makeText(this, "注册信息不完整", Toast.LENGTH_SHORT).show();
        }
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
