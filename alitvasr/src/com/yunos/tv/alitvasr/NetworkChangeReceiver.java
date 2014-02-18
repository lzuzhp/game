package com.yunos.tv.alitvasr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent bootIntent = new Intent(context, MainServiceManager.class);
		context.startService(bootIntent);
	}
}
