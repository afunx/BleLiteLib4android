package com.afunx.ble.blelitelib.bean;

import android.bluetooth.BluetoothDevice;

public class BleDevice {

	private BluetoothDevice mBluetoothDevice;
	private int mRssi;
	private byte[] mScanRecord;

	public BluetoothDevice getBluetoothDevice() {
		return mBluetoothDevice;
	}

	public void setBluetoothDevice(BluetoothDevice mBluetoothDevice) {
		this.mBluetoothDevice = mBluetoothDevice;
	}

	public int getRssi() {
		return mRssi;
	}

	public void setRssi(int mRssi) {
		this.mRssi = mRssi;
	}

	public byte[] getScanRecord() {
		return mScanRecord;
	}

	public void setScanRecord(byte[] mScanRecord) {
		this.mScanRecord = mScanRecord;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof BleDevice)) {
			BleDevice other = (BleDevice) obj;
			return mBluetoothDevice.getAddress().equals(other.mBluetoothDevice.getAddress());
		}
		return false;
	}
}
