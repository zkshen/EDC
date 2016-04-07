package edc.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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



    private ImageView ble_start;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice mBluetoothDevice = null;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_SELECT_DEVICE = 1;
    public BluetoothLeService mBluetoothLeService = null;
    private boolean mConnected = false;
    private List<BluetoothGattService> BleServices;
    private mListAdapter equlistAdapter;
    private List<String> equList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控件
        initView();
        // 初始化底部按钮事件
        setViewPager();
        initEvent();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "找不到蓝牙适配器", Toast.LENGTH_SHORT).show();
        }
        // 启动蓝牙服务
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    void initEvent(){
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
        this.ble_start = (ImageView) findViewById(R.id.ble_start);
    }

    private void setViewPager(){
        List<View> views;
        ContentAdapter adapter;
        //equList =
        equlistAdapter = new mListAdapter(this, equList);
        // 中间内容区域ViewPager
        this.viewPager = (ViewPager) findViewById(R.id.vp_content);
        // 适配器
        View page_1 = View.inflate(MainActivity.this, R.layout.page_1, null);

        View page_2 = View.inflate(MainActivity.this, R.layout.page_2, null);
        ListView device_list = (ListView) page_2.findViewById(R.id.device_list);
        device_list.setAdapter(equlistAdapter);

        View page_3 = View.inflate(MainActivity.this, R.layout.page_3, null);
        views = new ArrayList<View>();
        views.add(page_1);
        views.add(page_2);
        views.add(page_3);
        adapter = new ContentAdapter(views);
        viewPager.setAdapter(adapter);
    }
    // 在page_2中列出所有蓝牙设备，在mGattUpdateReceiver中调用
    private void ControlDisplay(){
        String uuid;
        String equname;
        equList.clear();
        BleServices = mBluetoothLeService.getSupportedGattServices();
        for (BluetoothGattService gattService : BleServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                equname = SampleGattAttributes.searchname(uuid);
                System.out.println(uuid);
                if(equname != null) {
                    equList.add(equname);
                }
            }
        }
        equlistAdapter.notifyDataSetChanged();
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
            case R.id.disconnect:
                mBluetoothLeService.disconnect();
                equList.clear();
                equlistAdapter.notifyDataSetChanged();
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
                    mBluetoothLeService.connect(deviceAddress);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "已开启蓝牙", Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(this, "未能开启蓝牙", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                System.out.println("无法初始化蓝牙设备");
                finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                ControlDisplay();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService.stopSelf();
        mBluetoothLeService = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    class mListAdapter extends BaseAdapter {
        Context context;
        List<String> charas;
        LayoutInflater inflater;

        public mListAdapter(Context context, List<String> charas) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.charas = charas;
        }

        @Override
        public int getCount() {
            return charas.size();
        }

        @Override
        public Object getItem(int position) {
            return charas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;
            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.equ_list, null);
            }
            String chara = charas.get(position);

            final TextView equName = ((TextView) vg.findViewById(R.id.EquName));

            equName.setText(chara);

            return vg;
        }
    }
}
