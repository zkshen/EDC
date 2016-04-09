package edc.test;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.IntentFilter;

import java.util.Formatter;
import java.util.List;

/**
 * Created by zkshen on 2016/4/9.
 */
public class PublicFunctions {

    public final static String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final static String DEVICE_STATE = "DEVICE_STATE";
    public final static String DEVICE_NAME = "DEVICE_NAME";

    public static String displayData(byte[] value, byte len) {
        String txt = new String(value, 0, len - 1);
        boolean isPrint = isAsciiPrintable(txt);

        if (!isPrint || len <= 4)
            txt = BytetohexString(value, len);
        return txt;
    }

    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (isAsciiPrintable(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static String BytetohexString(byte[] b, int len) {
        StringBuilder sb = new StringBuilder(b.length * (2 + 1));
        Formatter formatter = new Formatter(sb);

        for (int i = 0; i < len; i++) {
            if (i < len - 1)
                formatter.format("%02X:", b[i]);
            else
                formatter.format("%02X", b[i]);
        }
        formatter.close();
        return sb.toString();
    }

    private static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PublicFunctions.ACTION_GATT_CONNECTED);
        intentFilter.addAction(PublicFunctions.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(PublicFunctions.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(PublicFunctions.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static BluetoothGattCharacteristic getSwitchChara(List<BluetoothGattService> gattServices) {
        if (gattServices==null) return null;
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                if(gattCharacteristic.getUuid().toString().equals("00000001-1212-efde-1523-785feabcd123")){
                    return gattCharacteristic;
                }
            }
        }
        return null;
    }
}
