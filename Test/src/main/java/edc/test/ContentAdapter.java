package edc.test;

/**
 * Created by zkshen on 2016/4/2.
 */

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContentAdapter extends PagerAdapter {

    private List<View> views;
    private Button scanbutton;

    public ContentAdapter(List<View> views) {
        this.views = views;
    }
    @Override
    public int getCount() {
        return views.size();
    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        switch (position){
            case 1:
                this.scanbutton = (Button) view.findViewById(R.id.scanbutton);
                scanbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //System.out.println("没有蓝牙设备");
                    }
                });
                break;
            default:
                break;
        }
        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }
}
