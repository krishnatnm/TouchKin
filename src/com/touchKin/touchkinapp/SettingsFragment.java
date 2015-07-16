package com.touchKin.touchkinapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.touchKin.touckinapp.R;

public class SettingsFragment extends Fragment {
	public SettingsFragment() {
		// TODO Auto-generated constructor stub

	}

	Button addDevice;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View v = inflater.inflate(R.layout.settings, null);
		init(v);
		addDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), BluetoothScan.class);
				startActivity(intent);
			}
		});

		return v;
	}

	public void init(View v) {
		// adapter = new DashBoardAdapter(getChildFragmentManager(), parent);
		addDevice = (Button) v.findViewById(R.id.addDevice);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

}
