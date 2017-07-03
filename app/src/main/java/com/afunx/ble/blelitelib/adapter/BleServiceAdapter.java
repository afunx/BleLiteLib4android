package com.afunx.ble.blelitelib.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afunx.ble.blelitelib.app.R;

import java.util.ArrayList;
import java.util.List;

public class BleServiceAdapter extends BaseAdapter {

    private Context context;
    private List<BluetoothGattService> bluetoothGattServices;

    public BleServiceAdapter(Context context) {
        this.context = context;
        bluetoothGattServices = new ArrayList<>();
    }

    public void addResults(List<BluetoothGattService> services) {
        for (BluetoothGattService service : services) {
            bluetoothGattServices.add(service);
        }
    }

    public void addResult(BluetoothGattService service) {
        bluetoothGattServices.add(service);
    }

    public void clear() {
        bluetoothGattServices.clear();
    }

    @Override
    public int getCount() {
        return bluetoothGattServices.size();
    }

    @Override
    public BluetoothGattService getItem(int position) {
        if (position > bluetoothGattServices.size())
            return null;
        return bluetoothGattServices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.adapter_service, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txt_uuid = (TextView) convertView.findViewById(R.id.txt_uuid);
            holder.txt_type = (TextView) convertView.findViewById(R.id.txt_type);
        }

        BluetoothGattService service = bluetoothGattServices.get(position);
        String uuid = service.getUuid().toString();

        holder.txt_title.setText(String.valueOf(context.getString(R.string.service) + "（" + position + ")"));
        holder.txt_uuid.setText(uuid);
        holder.txt_type.setText(context.getString(R.string.main_service));
        return convertView;
    }

    private static class ViewHolder {
        TextView txt_title;
        TextView txt_uuid;
        TextView txt_type;
    }
}