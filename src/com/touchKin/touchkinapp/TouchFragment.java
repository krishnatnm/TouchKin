package com.touchKin.touchkinapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.touchKin.touchkinapp.Interface.FragmentInterface;
import com.touchKin.touchkinapp.Interface.ViewPagerListener;
import com.touchKin.touchkinapp.custom.CustomRequest;
import com.touchKin.touchkinapp.custom.HoloCircularProgressBar;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.PieSlice;
import com.touchKin.touchkinapp.model.AppController;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class TouchFragment extends Fragment implements FragmentInterface,
		ViewPagerListener {
	private HoloCircularProgressBar mHoloCircularProgressBar;
	private ObjectAnimator mProgressBarAnimator;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";
	ImageView parentImage;
	TextView parentName, parentBotton;
	ParentListModel parent;
	int resID;
	Vibrator vib;
	String backData;

	// newInstance constructor for creating fragment with arguments
	public static TouchFragment newInstance(int page, String title) {
		TouchFragment touchFragment = new TouchFragment();
		Bundle args = new Bundle();
		args.putInt("someInt", page);
		args.putString("someTitle", title);
		touchFragment.setArguments(args);
		return touchFragment;

	}

	// Store instance variables based on arguments passed
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vib = (Vibrator) this.getActivity().getSystemService(
				Context.VIBRATOR_SERVICE);
		Fragment1.listener = TouchFragment.this;
	}

	// Inflate the view for the fragment based on layout XML
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dashboard_touch_screen,
				container, false);
		// final PieGraph pg = (PieGraph) view.findViewById(R.id.piegraph);
		mHoloCircularProgressBar = (HoloCircularProgressBar) view
				.findViewById(R.id.holoCircularProgressBar);

		// ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
		// PieSlice slice = new PieSlice();
		parentName = (TextView) view.findViewById(R.id.parentNameTV);
		parentImage = (ImageView) view.findViewById(R.id.profile_pic);
		parentBotton = (TextView) view.findViewById(R.id.parentBottonTouch);
		// ((DashBoardActivity) getActivity()).setCustomButtonListner(this);
		parentImage.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (parent != null) {
					if (parent.getIsPendingTouch()) {
						vib.vibrate(500);
						SharedPreferences pendingTouch = getActivity()
								.getSharedPreferences("pendingTouch", 0);
						if (parent.getIsTouchMedia()) {
							((DashBoardActivity) getActivity()).goToKinbook();
						}
						String array = pendingTouch.getString("touch", null);
						try {
							JSONArray arrayObj = new JSONArray(array);
							if (arrayObj != null && arrayObj.length() > 0) {
								for (int i = 0; i < arrayObj.length(); i++) {
									JSONObject obj = arrayObj.getJSONObject(i);
									if (obj.getString("id").equalsIgnoreCase(
											parent.getParentId())) {
										arrayObj.remove(i);
									}
								}

							}
							Editor tokenedit = pendingTouch.edit();
							tokenedit.putString("touch", arrayObj + "");
							tokenedit.commit();
							parent.setIsPendingTouch(false);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		return view;
	}

	/**
	 * Animate.
	 *
	 * @param progressBar
	 *            the progress bar
	 * @param listener
	 *            the listener
	 */
	// private void animate(final HoloCircularProgressBar progressBar,
	// final AnimatorListener listener) {
	// final float progress = (float) (Math.random() * 2);
	// int duration = 3000;
	// animate(progressBar, listener, progress, duration);
	// }

	private void animate(final HoloCircularProgressBar progressBar,
			final AnimatorListener listener, final float progress,
			final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress",
				progress);
		mProgressBarAnimator.setDuration(duration);

		mProgressBarAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBar.setMarkerProgress(progress);
		mProgressBarAnimator.start();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mHoloCircularProgressBar.setProgress(0.0f);
		// animate(mHoloCircularProgressBar, null, 0.05f, 3000);
		// Toast.makeText(getActivity(), "Resume", Toast.LENGTH_SHORT).show();
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
		SetImage();
		//
		// ImageLoader imageLoader = new ImageLoader(getActivity());
		// parent = ((DashBoardActivity) getActivity()).getSelectedParent();
		// Log.d("Parent", "" + parent);
		// if (parent != null) {
		// imageLoader.DisplayImage(serverPath + parent.getParentId()
		// + ".jpeg", R.drawable.ic_user_image, parentImage);
		// }
	}

	@Override
	public void fragmentBecameVisible() {
		// TODO Auto-generated method stub
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);

	}

	private void SetImage() {
		// TODO Auto-generated method stub
		ImageLoader imageLoader = new ImageLoader(getActivity());
		parent = ((DashBoardActivity) getActivity()).getSelectedParent();

		Log.d("Parent", "" + parent);
		if (parent != null) {

			getCurrent(parent.getParentId());

			String cut = parent.getParentName().substring(0, 1).toLowerCase();
			resID = getActivity().getResources().getIdentifier(cut, "drawable",
					getActivity().getPackageName());
			Log.d("cut", cut + " " + resID);
			imageLoader.DisplayImage(serverPath + parent.getParentId()
					+ ".jpeg", resID, parentImage);
			if (parent.getIsPendingTouch()) {
				parentName.setText(parent.getParentName()
						+ " has sent you a touch ");
				parentBotton.setText("Tap and hold his/her photo to receive");
			} else {
				parentName.setText(parent.getParentName() + " feeling good ");
				parentBotton.setText("There last touch was ");
			}

		}

	}

	// @Override
	// public void onButtonClickListner(int position, String value,
	// Boolean isAccept) {
	// // TODO Auto-generated method stub
	// SetImage();
	// }

	public void getCurrent(String id) {
		Log.d("id ", id);
		CustomRequest req = new CustomRequest(
				"http://54.69.183.186:1340/activity/current/" + id,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseArray) {
						// TODO Auto-generated method stub
						Log.d("Response Array Current",
								responseArray.toString());

						setSlices(responseArray);

					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {

						Log.d("Error", "" + error.networkResponse);
						VolleyLog.e("Error: ", error.getMessage());
						String json = null;

						NetworkResponse response = error.networkResponse;
						if (!InternetAvailable()) {
							Toast.makeText(getActivity(),
									"Please Check your intenet connection",
									Toast.LENGTH_SHORT).show();

						}

						// Log.d("Response", response.data.toString());
						if (response != null && response.data != null) {
							switch (response.statusCode) {
							case 400:
								json = new String(response.data);
								json = trimMessage(json, "message");
								if (json != null)
									displayMessage(json, 400);

								Log.d("Response", response.data.toString());
							}
						}

					}

				}) {
			public java.util.Map<String, String> getHeaders()
					throws com.android.volley.AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Authorization", "Bearer "
						+ ((DashBoardActivity) getActivity()).getToken());
				return headers;

			};
		};

		AppController.getInstance().addToRequestQueue(req);

	}

	private boolean InternetAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public void displayMessage(String toastString, int code) {
		Toast.makeText(getActivity(), toastString + " code error: " + code,
				Toast.LENGTH_LONG).show();
	}

	public String trimMessage(String json, String key) {
		String trimmedString = null;

		try {
			JSONObject obj = new JSONObject(json);
			Log.d("JSOn", " " + obj);
			trimmedString = obj.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return trimmedString;
	}

	public void setSlices(JSONObject slicesObject) {
		ArrayList<PieSlice> slices = new ArrayList<PieSlice>();
		final Resources resources = getResources();

		Iterator<String> iter = slicesObject.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			try {
				PieSlice slice = new PieSlice();
				int value = slicesObject.getInt(key);

				if (value == 0) {

					slice.setColor(resources.getColor(R.color.daily_prog_left));
				} else {
					slice.setColor(resources.getColor(R.color.daily_prog_done));
				}
				slices.add(slice);
			} catch (JSONException e) {
				// Something went wrong!
			}

		}
		mHoloCircularProgressBar.setSlices(slices);
		mHoloCircularProgressBar.setProgress(0.0f);
		animate(mHoloCircularProgressBar, null, (float) (1.0f / 30), 1000);
	}

	@Override
	public void sendTouchCLicked(Boolean isFirstTime) {
		// TODO Auto-generated method stub
		if (isFirstTime) {
			backData = parentBotton.getText().toString();
			parentBotton.setText("Add a video to touch");
		} else {
			parentBotton.setText(backData);
		}
	}
}
