package edc.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,OnPageChangeListener{

    // 底部菜单3个Linearlayout
    private LinearLayout ll_data;
    private LinearLayout ll_control;
    private LinearLayout ll_setting;

    // 底部菜单3个ImageView
    private ImageView iv_data;
    private ImageView iv_control;
    private ImageView iv_setting;

    // 底部菜单3个菜单标题
    private TextView tv_data;
    private TextView tv_control;
    private TextView tv_setting;

    // 中间内容区域
    private ViewPager viewPager;

    // ViewPager适配器ContentAdapter
    private ContentAdapter adapter;

    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        initView();
        // 初始化底部按钮事件
        initEvent();
    }

    private void initEvent() {
        // 设置按钮监听
        ll_data.setOnClickListener(this);
        ll_control.setOnClickListener(this);
        ll_setting.setOnClickListener(this);

        //设置ViewPager滑动监听
        viewPager.addOnPageChangeListener(this);
    }

    private void initView() {

        // 底部菜单3个Linearlayout
        this.ll_data = (LinearLayout) findViewById(R.id.ll_data);
        this.ll_control = (LinearLayout) findViewById(R.id.ll_control);
        this.ll_setting = (LinearLayout) findViewById(R.id.ll_setting);

        // 底部菜单3个ImageView
        this.iv_data = (ImageView) findViewById(R.id.iv_data);
        this.iv_control = (ImageView) findViewById(R.id.iv_control);
        this.iv_setting = (ImageView) findViewById(R.id.iv_setting);

        // 底部菜单3个菜单标题
        this.tv_data = (TextView) findViewById(R.id.tv_data);
        this.tv_control = (TextView) findViewById(R.id.tv_control);
        this.tv_setting = (TextView) findViewById(R.id.tv_setting);

        // 中间内容区域ViewPager
        this.viewPager = (ViewPager) findViewById(R.id.vp_content);

        // 适配器
        View page_1 = View.inflate(MainActivity.this, R.layout.page_1, null);
        View page_2 = View.inflate(MainActivity.this, R.layout.page_2, null);
        View page_3 = View.inflate(MainActivity.this, R.layout.page_3, null);

        views = new ArrayList<View>();
        views.add(page_1);
        views.add(page_2);
        views.add(page_3);

        this.adapter = new ContentAdapter(views);
        viewPager.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        // 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
        restartBotton();
        // ImageView和TetxView置为绿色，页面随之跳转
        switch (v.getId()) {
            case R.id.ll_data:
                iv_data.setImageResource(R.drawable.data_pressed);
                tv_data.setTextColor(0xff4a8522);
                viewPager.setCurrentItem(0);
            break;
            case R.id.ll_control:
                iv_control.setImageResource(R.drawable.control_pressed);
                tv_control.setTextColor(0xff4a8522);
                viewPager.setCurrentItem(1);
            break;
            case R.id.ll_setting:
                iv_setting.setImageResource(R.drawable.setting_pressed);
                tv_setting.setTextColor(0xff4a8522);
                viewPager.setCurrentItem(2);
            break;
            default:
            break;
        }

    }

    private void restartBotton() {
        // ImageView置为灰色
        iv_data.setImageResource(R.drawable.data_normal);
        iv_control.setImageResource(R.drawable.control_normal);
        iv_setting.setImageResource(R.drawable.setting_normal);
        // TextView置为白色
        tv_data.setTextColor(0xffcdcdcd);
        tv_control.setTextColor(0xffcdcdcd);
        tv_setting.setTextColor(0xffcdcdcd);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        restartBotton();
        //当前view被选择的时候,改变底部菜单图片，文字颜色
        switch (arg0) {
            case 0:
                iv_data.setImageResource(R.drawable.data_pressed);
                tv_data.setTextColor(0xff4a8522);
            break;
            case 1:
                iv_control.setImageResource(R.drawable.control_pressed);
                tv_control.setTextColor(0xff4a8522);
            break;
            case 2:
                iv_setting.setImageResource(R.drawable.setting_pressed);
                tv_setting.setTextColor(0xff4a8522);
            break;
            default:
            break;
        }

    }

}
