package com.yunos.tv.alitvasr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		final Context mContext = context;
		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			Intent bootIntent = new Intent(mContext, MainServiceManager.class);
			mContext.startService(bootIntent);
		}
	}
}
