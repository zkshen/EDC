package edc.test.page3;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

/**
 * 用户的抽象
 */
public class MyUser extends AVUser {
    public static final String TAG = "MyUser";
    public static final String USERNAME = "username";
    public static final String LOCATION = "location";
    
    public static MyUser getCurrentUser() {
        return getCurrentUser(MyUser.class);
    }

    public static boolean isLogin() {
        return (AVUser.getCurrentUser() != null);
    }


    public static void signUpByNameAndPwd(String name, String password, String userid, SignUpCallback callback) {
        AVUser user = new AVUser();
        String username = name.substring(0,name.length()-1);
        user.setUsername(name);
        user.setPassword(password);
        user.put("UserID", userid);
        user.put("User",username);
        user.signUpInBackground(callback);
    }

    public static void uploadPhoneNumber(String phone) {
        AVUser currentUser = AVUser.getCurrentUser();
        String objectId = currentUser.getObjectId();
        AVObject post = AVObject.createWithoutData("_User", objectId);
        post.put("mobilePhoneNumber", phone);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.i("LeanCloud", "Save successfully.");
                } else {
                    Log.e("LeanCloud", e.getMessage());
                }
            }
        });
    }

    public static String getPhoneNumber() {
        AVUser currentUser = AVUser.getCurrentUser();
        if (currentUser != null)
            return currentUser.getMobilePhoneNumber();
        else
            return "";
    }

    public static void uploadLocation() {

    }
}
