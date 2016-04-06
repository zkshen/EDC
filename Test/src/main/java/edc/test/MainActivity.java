package edc.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private ImageView ble_start;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_SELECT_DEVICE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        initView();
        // 初始化底部按钮事件
        initEvent();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "找不到蓝牙适配器", Toast.LENGTH_SHORT).show();
        }
    }

    private void initEvent() {
        // 设置按钮监听
        ll_data.setOnClickListener(this);
        ll_control.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        //设置ViewPager滑动监听
        viewPager.addOnPageChangeListener(this);
        ble_start.setOnClickListener(this);
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
        this.ble_start = (ImageView) findViewById(R.id.ble_start);
    }
    @Override
    public void onClick(View v) {
        // ImageView和TetxView置为绿色，页面随之跳转
        switch (v.getId()) {
            case R.id.ll_data:
                restartBotton();
                iv_data.setImageResource(R.drawable.data_pressed);
                tv_data.setTextColor(0xff4a8522);
                viewPager.setCurrentItem(0);
                break;
            case R.id.ll_control:
                restartBotton();
                iv_control.setImageResource(R.drawable.control_pressed);
                tv_control.setTextColor(0xff4a8522);
                viewPager.setCurrentItem(1);
                break;
            case R.id.ll_setting:
                restartBotton();
                iv_setting.setImageResource(R.drawable.setting_pressed);
                tv_setting.setTextColor(0xff4a8522);
                viewPager.setCurrentItem(2);
                break;
            case R.id.ble_start:
//                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//                if(adapter != null){
//                    Toast.makeText(this, "已找到蓝牙适配器", Toast.LENGTH_SHORT).show();
//                    // 如果蓝牙未开启，则请求开启蓝牙
//                    if(!adapter.isEnabled()){
//                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                        startActivity(intent);
//                    }
//                    Set<BluetoothDevice> devices = adapter.getBondedDevices();
//                    if(devices.size() > 0){
//                        for(Iterator iterator = devices.iterator();iterator
//                                .hasNext();){
//                            BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
//                            System.out.println(bluetoothDevice.getAddress());
//                        }
//                    }
//                }
//                else{
//                    Toast.makeText(this, "找不到蓝牙适配器", Toast.LENGTH_SHORT).show();
//                }
                if (!mBluetoothAdapter.isEnabled()) {
                    System.out.println("蓝牙未开启");
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
                else {
                    //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
                    Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mBluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    System.out.println(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
