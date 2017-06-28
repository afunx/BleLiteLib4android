package com.afunx.ble.blelitelib.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.afunx.ble.blelitelib.app.R;
import com.afunx.ble.blelitelib.bean.BleDevice;

import java.util.ArrayList;
import java.util.List;

public class BleDeviceAdapter extends BaseAdapter {

	private final List<BleDevice> mBleDeviceList;
	private final Context mContext;
	private final LayoutInflater mInflater;

	public BleDeviceAdapter(Context context) {
		mBleDeviceList = new ArrayList<BleDevice>();
		mContext = context;
		mInflater = LayoutInflater.from(context);
	}
	
	private BleDevice getBleDevice(BleDevice device) {
		for (BleDevice deviceInList : mBleDeviceList) {
			if (deviceInList.equals(device)) {
				return deviceInList;
			}
		}
		return null;
	}

	/**
	 * add or update ble device
	 *
	 * @param device the device to be added or updated
	 * @return whether the device is added or updated
	 */
	private boolean addOrUpdateDeviceInternal(BleDevice device) {
		BleDevice deviceInList = getBleDevice(device);
		if (deviceInList == null) {
			// add
			mBleDeviceList.add(device);
			return true;
		} else {
			// update
			deviceInList.setRssi(device.getRssi());
			deviceInList.setBluetoothDevice(device.getBluetoothDevice());
			deviceInList.setScanRecord(device.getScanRecord());
			return false;
		}
	}

	/**
	 * add or update device and notify data set changed
	 *
	 * @param device the device to be added or updated
	 * @return whether the device is added
	 */
	public boolean addOrUpdateDevice(BleDevice device) {
		return addOrUpdateDeviceInternal(device);
	}
	
	/**
	 * clear deviceList and notify data set invalidated
	 */
	public void clear() {
		mBleDeviceList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mBleDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBleDeviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static class ViewHolder {
		TextView tvDevName;
		TextView tvDevAddr;
		TextView tvDevSig;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// get view holder
		ViewHolder viewHolder = null;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.ble_device_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.tvDevName = (TextView) convertView.findViewById(R.id.tv_dev_name);
			viewHolder.tvDevAddr = (TextView) convertView.findViewById(R.id.tv_dev_addr);
			viewHolder.tvDevSig = (TextView) convertView.findViewById(R.id.tv_dev_sig);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// set view content
		final BleDevice device = mBleDeviceList.get(position);
		final Resources resources = mContext.getResources();
		// device name
		String devName = device.getBluetoothDevice().getName();
		if (TextUtils.isEmpty(devName)) {
			devName = resources.getString(R.string.device_name_unknown_device);
		}
		viewHolder.tvDevName.setText(devName);
		// device address
		String titleAddr = resources.getString(R.string.device_address);
		String devAddr = device.getBluetoothDevice().getAddress();
		viewHolder.tvDevAddr.setText(titleAddr + ": " + devAddr);
		// device signal
		String titleSig = resources.getString(R.string.device_signal);
		int devSig = device.getRssi();
		viewHolder.tvDevSig.setText(titleSig + ": " + devSig);
		return convertView;
	}

}
