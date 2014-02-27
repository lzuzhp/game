package com.yunos.tv.alitvasr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class NetworkChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "alitvasr";
	private final String version = "1.5.0-R-20131221.1224";

	@Override
	public void onReceive(Context context, Intent intent) {
		final Context mContext = context;
		if (version.equals(Build.VERSION.RELEASE)) {
			Intent bootIntent = new Intent(mContext, MainServiceManager.class);
			mContext.startService(bootIntent);
		}
		Log.d(TAG,"version " + Build.VERSION.RELEASE);
	}
}
