package com.afunx.ble.blelitelib.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.afunx.ble.blelitelib.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by afunx on 03/07/2017.
 */

public class BlePropertyAdapter extends BaseAdapter {

    private Context context;
    private List<BluetoothGattCharacteristic> characteristicList;

    BlePropertyAdapter(Context context) {
        this.context = context;
        characteristicList = new ArrayList<>();
    }

    public void addResult(BluetoothGattCharacteristic characteristic) {
        characteristicList.add(characteristic);
    }

    public void clear() {
        characteristicList.clear();
    }

    @Override
    public int getCount() {
        return characteristicList.size();
    }

    @Override
    public BluetoothGattCharacteristic getItem(int position) {
        if (position > characteristicList.size())
            return null;
        return characteristicList.get(position);
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
            holder.img_next = (ImageView) convertView.findViewById(R.id.img_next);
        }

        BluetoothGattCharacteristic characteristic = characteristicList.get(position);
        String uuid = characteristic.getUuid().toString();

        holder.txt_title.setText(String.valueOf(context.getString(R.string.property) + "（" + position + ")"));
        holder.txt_uuid.setText(uuid);

        StringBuilder property = new StringBuilder();
        int charaProp = characteristic.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            property.append("Read");
            property.append(" , ");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            property.append("Write");
            property.append(" , ");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0) {
            property.append("Write No Response");
            property.append(" , ");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            property.append("Notify");
            property.append(" , ");
        }
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            property.append("Indicate");
            property.append(" , ");
        }
        if (property.length() > 1) {
            property.delete(property.length() - 2, property.length() - 1);
        }
        if (property.length() > 0) {
            holder.txt_type.setText(String.valueOf(context.getString(R.string.property) + "( " + property.toString() + ")"));
            holder.img_next.setVisibility(View.VISIBLE);
        } else {
            holder.img_next.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txt_title;
        TextView txt_uuid;
        TextView txt_type;
        ImageView img_next;
    }
}
