package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.touchKin.touchkinapp.adapter.BluetoothDeviceAdapter;
import com.touchKin.touchkinapp.model.BluetoothDeviceModel;
import com.touchKin.touckinapp.R;

public class BluetoothScanList extends ActionBarActivity implements
		OnItemClickListener, OnClickListener {
	private Toolbar toolbar;
	TextView mTitle, skip, back;
	ListView bledevicelist;
	BluetoothDeviceAdapter adapter;
	List<BluetoothDeviceModel> deviceList;
	List<BluetoothDevice> mDevices;
	List<Integer> rssiList;
	ImageButton addContactButton;
	Button next, previous;

	static final int PICK_CONTACT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.available_bluetooth_device_list);
		init();
		mTitle.setText("Select a device");
		next.setOnClickListener(this);
		bledevicelist.setOnItemClickListener(this);
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mDevices = bundle.getParcelableArrayList("Devices");
			rssiList = bundle.getIntegerArrayList("rssi");

		}
		if (mDevices != null && !mDevices.isEmpty()) {
			for (BluetoothDevice device : mDevices) {
				BluetoothDeviceModel item = new BluetoothDeviceModel();
				item.setDeviceName(device.getName());
				item.setCheck(false);
				item.setDeviceId(device.getAddress());
				deviceList.add(item);
			}
		}
		if (deviceList.isEmpty()) {
			DialogFragment newFragment = new NoDeviceDialogFragment();
			newFragment.show(getSupportFragmentManager(), "TAG");
		}

		adapter.notifyDataSetChanged();

	}

	private void init() {
		// TODO Auto-generated method stub
		bledevicelist = (ListView) findViewById(R.id.ble_device_list);
		deviceList = new ArrayList<BluetoothDeviceModel>();
		adapter = new BluetoothDeviceAdapter(deviceList, this);
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
		next = (Button) findViewById(R.id.next_button);
		previous = (Button) findViewById(R.id.previous_button);
		bledevicelist.setAdapter(adapter);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		BluetoothDeviceModel item = deviceList.get(position);
		Toast.makeText(BluetoothScanList.this, "" + item, Toast.LENGTH_SHORT)
				.show();
		for (BluetoothDeviceModel data : deviceList) {

			if (data.equals(item)) {
				data.setCheck(true);
			} else {
				data.setCheck(false);
			}

			bledevicelist.setAdapter(adapter);

		}

		Intent i = new Intent(BluetoothScanList.this,
				DeviceControlActivity.class);
		i.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS,
				item.getDeviceId());
		i.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME,
				item.getDeviceName());
		i.putExtra(DeviceControlActivity.EXTRAS_DEVICE_RSSI,
				item.getDeviceRssi());
		Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
				getApplicationContext(), R.anim.animation, R.anim.animation2)
				.toBundle();
		startActivity(i, bndlanimation);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next_button:
			finish();
			break;

		default:
			break;
		}

	}

	public class NoDeviceDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Get the layout inflater
			// LayoutInflater inflater = getActivity().getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog
			// layout
			// Bundle mArgs = getArguments();

			// View view = inflater.inflate(R.layout.contact_info, null);
			// final EditText nameBox = (EditText) view.findViewById(R.id.name);
			// final EditText phoneBox = (EditText)
			// view.findViewById(R.id.number);
			// final EditText nickname = (EditText) view
			// .findViewById(R.id.nickname);
			// nameBox.setText(mArgs.getString("name"));
			// phoneBox.setText(mArgs.getString("number"));
			// View headerview = inflater.inflate(R.layout.header_view, null);
			// final TextView title = (TextView) headerview
			// .findViewById(R.id.parentNameTV);
			// title.setText(mArgs.getString("title"));
			builder
			// Add action buttons
			.setTitle("No Device found")
					.setMessage(
							"Check the device is in range and then try again")
					.setPositiveButton("Try again",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									// sign in the user ...
									Intent i = new Intent(
											BluetoothScanList.this,
											BluetoothScan.class);
									startActivity(i);
									finish();
								}

							});

			return builder.create();
		}

	}
}
