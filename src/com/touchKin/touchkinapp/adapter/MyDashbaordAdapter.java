package com.touchKin.touchkinapp.adapter;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.GpsStatus.Listener;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.touchKin.touchkinapp.DashBoardActivity;
import com.touchKin.touchkinapp.Fragment2;
import com.touchKin.touchkinapp.Interface.ViewPagerListener;
import com.touchKin.touchkinapp.custom.ImageLoader;
import com.touchKin.touchkinapp.custom.RoundedImageView;
import com.touchKin.touchkinapp.model.ParentListModel;
import com.touchKin.touckinapp.R;

public class MyDashbaordAdapter extends PagerAdapter implements
		ViewPagerListener {
	Context context;
	List<ParentListModel> parentList;
	LayoutInflater inflater;
	String serverPath = "https://s3-ap-southeast-1.amazonaws.com/touchkin-dev/avatars/";
	Vibrator vib;
	Boolean isFirst = false;

	public MyDashbaordAdapter(Context context, List<ParentListModel> parentList) {
		this.parentList = parentList;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		Fragment2.listener = MyDashbaordAdapter.this;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		// TODO Auto-generated method stub

		RoundedImageView imageView;
		final ParentListModel parent = parentList.get(position);
		View view = inflater.inflate(R.layout.dashboard_touch_screen,
				container, false);
		TextView parenTop = (TextView) view.findViewById(R.id.parentNameTV);
		TextView parentBottom = (TextView) view
				.findViewById(R.id.parentBottonTouch);
		if (parentList.size() > 1 && parentList.get(1).getIsPendingTouch()) {
			parenTop.setText(parentList.get(1).getParentName()
					+ " has sent you a touch");
			if (position == 0) {
				parentBottom.setText("Swipe to get the touch");
			} else {
				parentBottom.setText("Tap and hold his/her photo to recieve");
			}
		} else {
			parenTop.setText("it's some time in india");
			if (!isFirst)
				parentBottom.setText("Send him/her a touch");
			else {
				parentBottom.setText("Add a video to touch ?");
			}

		}
		imageView = (RoundedImageView) view.findViewById(R.id.profile_pic);
		ImageLoader imageLoader = new ImageLoader(context);
		String name = parent.getParentName();
		int resID = 0;
		if (!name.equalsIgnoreCase("")) {
			String cut = name.substring(0, 1).toLowerCase();
			resID = context.getResources().getIdentifier(cut, "drawable",
					context.getPackageName());
			Log.d("cut", cut + " " + resID);
		}
		imageLoader.DisplayImage(serverPath + parent.getParentId() + ".jpeg",
				resID, imageView);
		((ViewPager) container).addView(view);
		imageView.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (parent.getIsPendingTouch()) {

					if (position > 0) {
						vib.vibrate(500);
						if (parent.getIsTouchMedia()) {
							((DashBoardActivity) context).goToKinbook();
						}
						SharedPreferences pendingTouch = context
								.getSharedPreferences("pendingTouch", 0);
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

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return super.saveState();
	}

	public int getCount() {
		return parentList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == ((LinearLayout) arg1);

	}

	@Override
	public void sendTouchCLicked(Boolean isFirstTime) {
		// TODO Auto-generated method stub
		if (isFirstTime) {
			isFirstTime = true;
			notifyDataSetChanged();
		} else {
			isFirstTime = false;
			notifyDataSetChanged();
		}
	}

}
