package com.touchKin.touchkinapp;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.skyfishjy.library.RippleBackground;
import com.touchKin.touchkinapp.adapter.BluetoothDeviceAdapter;
import com.touchKin.touckinapp.R;

public class BluetoothScan extends ActionBarActivity {

	Button scan;
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int SCAN_PERIOD = 5000;
	BluetoothDeviceAdapter adapter;
	ArrayList<BluetoothDevice> mDevices;
	ArrayList<Integer> rssiList;
	Handler mHandler;
	private boolean mScanning;
	RippleBackground rippleBackground;
	private Toolbar toolbar;
	TextView mTitle;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_scan);
		rippleBackground = (RippleBackground) findViewById(R.id.content);
		mHandler = new Handler();
		scan = (Button) findViewById(R.id.bluetooth_scan);
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		mTitle.setText("Add a device");
		mDevices = new ArrayList<BluetoothDevice>();
		rssiList = new ArrayList<Integer>();
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(BluetoothScan.this, R.string.ble_not_supported,
					Toast.LENGTH_SHORT).show();
			Intent i = new Intent(BluetoothScan.this, DashBoardActivity.class);
			Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
					getApplicationContext(), R.anim.animation,
					R.anim.animation2).toBundle();
			startActivity(i, bndlanimation);
			finish();

		}
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null) {
			Toast.makeText(BluetoothScan.this,
					R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
			return;
		}

		scan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				rippleBackground.startRippleAnimation();

				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						scanLeDevice(true);
					}
				}, SCAN_PERIOD);

				// // adapter = new BluetoothDeviceAdapter(null, null);
				// // setListAdapter(adapter);
				// scanLeDevice(true);
				//
				// // handler.postDelayed(new Runnable() {
				// // @Override
				// // public void run() {
				// // // foundDevice();
				// // rippleBackground.stopRippleAnimation();
				// //
				// //
				// // Intent i = new Intent(BluetoothScan.this,
				// // BluetoothScanList.class);
				// // Bundle bndlanimation =
				// ActivityOptions.makeCustomAnimation(
				// // getApplicationContext(), R.anim.animation,
				// // R.anim.animation2).toBundle();
				// // startActivity(i, bndlanimation);
				// // }
				// // }, 6000);
				// >>>>>>> 6872c2e574a20061e1648b66fe16a19b93a5d92b
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled()) {
			if (!mBluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void scanLeDevice(final boolean enable) {
		if (enable) {
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable() {
				@SuppressLint("NewApi")
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					Intent intent = new Intent(BluetoothScan.this,
							BluetoothScanList.class);
					intent.putParcelableArrayListExtra("Devices", mDevices);
					intent.putIntegerArrayListExtra("rssi", rssiList);
					startActivity(intent);
					finish();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		} else {
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mDevices.add(device);
					rssiList.add(rssi);
				}
			});
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED) {
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		scanLeDevice(false);
		rssiList.clear();
		mDevices.clear();

	}
}
