package edc.test.page3;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import edc.test.R;

/**
 * Created by zkshen on 2016/5/26.
 * 第三页Fragment
 */
public class SettingFragment extends Fragment {

    private Button LogIn;
    private Button SignUp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_3, container, false);//关联布局文件
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LogIn = (Button) view.findViewById(R.id.login);
        LogIn.setOnClickListener(mClickListener3);
        SignUp = (Button) view.findViewById(R.id.signup);
        SignUp.setOnClickListener(mClickListener3);
    }

    private View.OnClickListener mClickListener3 = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent intent;
            switch (v.getId()){
                case R.id.login:
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.signup:
                    intent = new Intent(getActivity(), SignUpActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
}
