package com.yunos.tv.alitvasr;

import java.util.Timer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.DisplayManagerAw;

public class MainServiceManager extends Service {
	private Timer timer = new Timer();
	private static final String TAG = "alitvasr";

	private String lastValue = "";
	private String curValue = "";
	private int noticeValue = 0;

	private static final String HDMI_TEST_GROUP = "370";

	public MainServiceManager() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// code to execute when the service is first created
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		final Context mContext = this;
		if (isConnected()) {
			// get the last value
			SharedPreferences userDetails = mContext.getSharedPreferences(
					"userinfo", Context.MODE_PRIVATE);
			if (userDetails != null) {
				lastValue = userDetails.getString("staticAv", "");
				noticeValue = userDetails.getInt("userNotice", 0);
			} else {
				lastValue = "";
				noticeValue = 0;
			}
			Log.d(TAG, "lastValue " + lastValue + " noticeValue " + noticeValue);
			// get the current value
			DisplayManagerAw displayManager = new DisplayManagerAw();
			if (displayManager.getTvHotPlugStatus() != 0) {
				curValue = "AV";
				// /* dialog to notice user */
				if (noticeValue == 0) {
					Intent intentAV = new Intent(mContext, AvDialog.class);
					intentAV.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intentAV.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intentAV);
					/* start the dialog only once */
					SharedPreferences.Editor edit = userDetails.edit();
					edit.putInt("userNotice", 1);
					edit.commit();
				}
			} else {
				curValue = "HDMI";
				/* dialog to notice user */
				// if (noticeValue == 0) {
				// Intent intentAV = new Intent(mContext, AvDialog.class);
				// intentAV.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				// intentAV.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intentAV);
				// /* start the dialog only once */
				// SharedPreferences.Editor edit = userDetails.edit();
				// edit.putInt("userNotice", 1);
				// edit.commit();
				// }
			}
			/* save new current value */
			Log.d(TAG, "curValue " + curValue);
			if (!curValue.equals(lastValue)) {
				Log.d(TAG, "save value ");
				SharedPreferences.Editor edit = userDetails.edit();
				edit.putString("staticAv", curValue);
				edit.commit();
				if (curValue.equals("HDMI")) {
					/* send UUID to HDMI group */
					SendMessage tmpSend = new SendMessage();
					tmpSend.SendUuidToService(HDMI_TEST_GROUP);
				}
			} else {
				Log.d(TAG, "Alredy saved");
			}
		} else {
			Log.d(TAG, "network not available");
		}

		return 1;
	}

	/*
	 * if the net work available
	 */
	public boolean isConnected() {
		boolean isEthernet = false, isWifi = false;

		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] activeNetInfo = connectivityManager.getAllNetworkInfo();
		if (activeNetInfo != null) {
			for (NetworkInfo network : activeNetInfo) {

				if (network.getType() == ConnectivityManager.TYPE_WIFI) {
					if (network.isConnected() && network.isAvailable())
						isWifi = true;
				}
				if (network.getType() == ConnectivityManager.TYPE_ETHERNET) {
					if (network.isConnected() && network.isAvailable())
						isEthernet = true;
				}
			}
		}
		return isWifi || isEthernet;
	}

}
