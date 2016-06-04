package edc.test.page2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.os.IBinder;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCreatedCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edc.test.R;
import edc.test.main.MainActivity;
import edc.test.page3.MyClientManager;

/**
 * Created by zkshen on 2016/5/26.
 * 第二页的Fragment
 */
public class ControlFragment extends Fragment {

    private BluetoothAdapter mBluetoothAdapter = null;
    public BluetoothLeService0 mBluetoothLeService0 = null;
    public BluetoothLeService1 mBluetoothLeService1 = null;
    private BluetoothGattCharacteristic SwitchChara0;
    private BluetoothGattCharacteristic SwitchChara1;
    private Handler mHandler;
    private static ListView device_list;
    private List<String> DeviceList = new ArrayList<String>();
    private mListAdapter DeviceListAdapter;

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int REQUEST_SELECT_DEVICE = 1;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = new Handler();
        DeviceListAdapter = new mListAdapter(getActivity(), DeviceList);
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), "找不到蓝牙适配器", Toast.LENGTH_SHORT).show();
        }
        Intent gattServiceIntent1 = new Intent(getActivity(), BluetoothLeService1.class);
        getActivity().bindService(gattServiceIntent1, mServiceConnection1, Context.BIND_AUTO_CREATE);
        Intent gattServiceIntent0 = new Intent(getActivity(), BluetoothLeService0.class);
        getActivity().bindService(gattServiceIntent0, mServiceConnection0, Context.BIND_AUTO_CREATE);
        getActivity().registerReceiver(mGattUpdateReceiver, PublicFunctions.makeGattUpdateIntentFilter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_2, container, false);//关联布局文件
        Button SearchSwitch = (Button) rootView.findViewById(R.id.SearchSwitch);
        SearchSwitch.setOnClickListener(mClickListener2);
        device_list = (ListView) rootView.findViewById(R.id.device_list);
        device_list.setAdapter(DeviceListAdapter);
        device_list.setOnItemLongClickListener(mDeviceLongClickListener);
        return rootView;
    }

    private View.OnClickListener mClickListener2 = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.SearchSwitch:
                    if (!mBluetoothAdapter.isEnabled()) {
                        System.out.println("蓝牙未开启");
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }
                    else {
                        //Connect button pressed, open DeviceListActivity class, with popup windows that scan for devices
                        Intent newIntent = new Intent(getActivity(), DeviceListActivity.class);
                        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection0 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService0 = ((BluetoothLeService0.LocalBinder) service).getService();
            if (!mBluetoothLeService0.initialize()) {
                System.out.println("无法初始化蓝牙设备");
                getActivity().finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService0 = null;
        }
    };

    private final ServiceConnection mServiceConnection1 = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService1 = ((BluetoothLeService1.LocalBinder) service).getService();
            if (!mBluetoothLeService1.initialize()) {
                System.out.println("无法初始化蓝牙设备");
                getActivity().finish();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService1 = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (PublicFunctions.ACTION_GATT_CONNECTED.equals(action)) {
                DeviceDisplay();
            } else if (PublicFunctions.ACTION_GATT_DISCONNECTED.equals(action)) {

            } else if (PublicFunctions.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                SwitchChara0 = PublicFunctions.getSwitchChara(mBluetoothLeService0.getSupportedGattServices());  // 取出与开关有关的特征值
                if(SwitchChara0!=null){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService0.readCharacteristic(SwitchChara0);
                        }
                    }, 100);  // 延时100ms再读取数据，否则会出错
                    mBluetoothLeService0.setCharacteristicNotification(SwitchChara0, true);     // 使能Notification
                }
                SwitchChara1 = PublicFunctions.getSwitchChara(mBluetoothLeService1.getSupportedGattServices());  // 取出与开关有关的特征值
                if(SwitchChara1!=null){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mBluetoothLeService1.readCharacteristic(SwitchChara1);
                        }
                    }, 100);  // 延时100ms再读取数据，否则会出错
                    mBluetoothLeService1.setCharacteristicNotification(SwitchChara1, true);     // 使能Notification
                }
            } else if (PublicFunctions.ACTION_DATA_AVAILABLE.equals(action)) {
                String name = intent.getStringExtra(PublicFunctions.DEVICE_NAME);
                String state = intent.getStringExtra(PublicFunctions.DEVICE_STATE);
                RefreshDevice(name, state);
            }
        }
    };

    // 在page_2中列出所有蓝牙设备，在mGattUpdateReceiver中调用，永远将mBluetoothLeService0放在第一个
    private void DeviceDisplay() {
        String devicename;
        DeviceList.clear();
        if(mBluetoothLeService0.ConnectionState){
            devicename = mBluetoothLeService0.DeviceName;
            DeviceList.add(devicename);
        }
        if(mBluetoothLeService1.ConnectionState){
            devicename = mBluetoothLeService1.DeviceName;
            DeviceList.add(devicename);
        }
        DeviceListAdapter.notifyDataSetChanged();
    }

    // 刷新蓝牙开关列表
    private void RefreshDevice(String name, String state){
        View view;
        String text;
        sendDevices();
        switch (device_list.getCount()){
            case 1:
                view = device_list.getChildAt(0);
                text = ((TextView) view.findViewById(R.id.EquName)).getText().toString();
                if(text.equals(name)){
                    Switch aswitch = (Switch) view.findViewById(R.id.equ_switch);
                    if(state.equals("00")){
                        aswitch.setChecked(false);
                        setState(name, "off");
                    }else{
                        aswitch.setChecked(true);
                        setState(name, "on");
                    }
                }
                break;
            case 2:
                view = device_list.getChildAt(0);
                text = ((TextView) view.findViewById(R.id.EquName)).getText().toString();
                if(text.equals(name)){
                    Switch aswitch = (Switch) view.findViewById(R.id.equ_switch);
                    if(state.equals("00")){
                        aswitch.setChecked(false);
                        setState(name, "off");
                    }else{
                        aswitch.setChecked(true);
                        setState(name, "on");
                    }
                } else{
                    view = device_list.getChildAt(1);
                    text = ((TextView) view.findViewById(R.id.EquName)).getText().toString();
                    if(text.equals(name)){
                        Switch aswitch = (Switch) view.findViewById(R.id.equ_switch);
                        if(state.equals("00")){
                            aswitch.setChecked(false);
                            setState(name, "off");
                        }else {
                            aswitch.setChecked(true);
                            setState(name, "on");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    // 蓝牙开关列表适配器
    class mListAdapter extends BaseAdapter {
        Context context;
        List<String> devices;
        LayoutInflater inflater;

        public mListAdapter(Context context, List<String> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
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
            String device = devices.get(position);
            final TextView equName = ((TextView) vg.findViewById(R.id.EquName));
            equName.setText(device);
            final Switch aswitch = ((Switch) vg.findViewById(R.id.equ_switch));
            MySwitchListener mSwitchListener = new MySwitchListener(device);
            aswitch.setOnCheckedChangeListener(mSwitchListener);
            return vg;
        }
    }

    // 电器开关响应函数
    private class MySwitchListener implements CompoundButton.OnCheckedChangeListener {
        String thedevice;
        byte on = 0x01;
        byte off = 0x00;
        public MySwitchListener(String device){
            thedevice = device;
        }
        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if(isChecked) {
                if(thedevice.equals(mBluetoothLeService0.DeviceName)){
                    mBluetoothLeService0.writeCharacteristic(SwitchChara0, on);
                    setState(mBluetoothLeService0.DeviceName, "on");
                }else if(thedevice.equals(mBluetoothLeService1.DeviceName)){
                    mBluetoothLeService1.writeCharacteristic(SwitchChara1, on);
                    setState(mBluetoothLeService1.DeviceName, "on");
                }
            }else {
                if(thedevice.equals(mBluetoothLeService0.DeviceName)){
                    mBluetoothLeService0.writeCharacteristic(SwitchChara0, off);
                    setState(mBluetoothLeService0.DeviceName, "off");
                }else if(thedevice.equals(mBluetoothLeService1.DeviceName)){
                    mBluetoothLeService1.writeCharacteristic(SwitchChara1, off);
                    setState(mBluetoothLeService1.DeviceName, "off");
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(!mBluetoothLeService0.ConnectionState){            // 如果mBluetoothLeService0未占用则连接mBluetoothLeService0
                        mBluetoothLeService0.connect(deviceAddress);
                    }
                    else{                                                       // 否则使用mBluetoothLeService1
                        mBluetoothLeService1.connect(deviceAddress);
                    }
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getActivity(), "已开启蓝牙", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "未能开启蓝牙", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
            default:
                break;
        }
    }

    private AdapterView.OnItemLongClickListener mDeviceLongClickListener = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position){
                case 0:
                    if(mBluetoothLeService0.ConnectionState){
                        mBluetoothLeService0.disconnect();
                    }
                    else{
                        mBluetoothLeService1.disconnect();
                    }
                    break;
                case 1:
                    mBluetoothLeService1.disconnect();
                    break;
                default:
                    break;
            }
            DeviceDisplay();
            return true;
        }
    };

    public void setState(String name, String state){
        final String device = name;
        final String dstate = state;
        String userid = AVUser.getCurrentUser().getString("UserID");
        //Log.i(test,userid);
        AVQuery<AVObject> avQuery = new AVQuery<>("UserLog");
        avQuery.getInBackground(userid, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                Log.i("test", avObject.getString("User"));
                avObject.put(device, dstate);
                avObject.saveInBackground();
            }
        });
    }

    public static void sendDevices() {
        int i;
        int c = device_list.getCount();
        View view;
        String device;
        sendMessage(Integer.toString(c));
        for(i=0; i<c; i++) {
            view = device_list.getChildAt(i);
            device = ((TextView) view.findViewById(R.id.EquName)).getText().toString();
            sendMessage(device);
        }
        sendMessage("end");
    }

    public static void sendMessage(String message) {
        final String msgsend = message;
        // 与服务器连接
        MyClientManager.getInstance().getClient().open(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient client, AVIMException e) {
                if (e == null) {
                    // 创建与Jerry之间的对话
                    client.createConversation(Arrays.asList(AVUser.getCurrentUser().getString("User")+1), "DeviceList", null,
                            new AVIMConversationCreatedCallback() {
                                @Override
                                public void done(AVIMConversation conversation, AVIMException e) {
                                    if (e == null) {
                                        AVIMTextMessage msg = new AVIMTextMessage();
                                        msg.setText(msgsend);
                                        // 发送消息
                                        conversation.sendMessage(msg, new AVIMConversationCallback() {

                                            @Override
                                            public void done(AVIMException e) {
                                                if (e == null) {
                                                    Log.d("Update", "发送成功！");
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });
    }

    public void test(AVObject avObject) {
        avObject.put("test","test");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
        getActivity().unbindService(mServiceConnection0);
        mBluetoothLeService0.stopSelf();
        mBluetoothLeService0 = null;
        getActivity().unbindService(mServiceConnection1);
        mBluetoothLeService1.stopSelf();
        mBluetoothLeService1 = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, PublicFunctions.makeGattUpdateIntentFilter());
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
