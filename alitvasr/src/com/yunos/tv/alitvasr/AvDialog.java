package com.yunos.tv.alitvasr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.R;

public class AvDialog extends Activity {
	private static final String TAG = "alitvasr";
	protected static final String OSUPDATE_ACTION = "com.yunos.osupdate.front.UpdateActivity";

	private static final String AV_TEST_GROUP = "801";

	@Override
	protected void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		final String messNotice = "魔盒1.6系统新增wifi热点、系统进程优化、安全锁等功能，可选择升级。"
				+ "如需升级，您务必严格按照规定步骤进行升级：\n"
				+ "1.升级包下载完成后，选择\"重启升级\"，并立刻拔除AV线\n"
				+ "2.系统重启后，指示灯会持续闪烁5-10分钟，此时请勿切断电源\n" + "3.指示灯常亮，接入AV线，升级成功！\n"
				+ "\n注：若升级有问题，请反馈到旺旺：冰妍\n" + "若升级不幸失败，您可通过重置系统完成升级";

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		TextView title = new TextView(this);
		title.setText("尊敬的AV线用户");
		title.setPadding(10, 10, 10, 10);
		title.setGravity(Gravity.CENTER);
		title.setTextSize(30);
		alertBuilder.setCustomTitle(title);
		TextView msg = new TextView(this);
		msg.setText(messNotice);
		msg.setPadding(10, 10, 10, 10);
		msg.setGravity(Gravity.CLIP_HORIZONTAL);
		msg.setTextSize(18);

		alertBuilder.setView(msg);
		alertBuilder.setCancelable(true);
		alertBuilder.setIcon(R.drawable.ic_dialog_alert);

		// Setting Negative "YES" Button
		alertBuilder.setNegativeButton("我知道了",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/* send UUID to AV GROUP */
						SendMessage tmpSend = new SendMessage();
						String uuidResult = tmpSend
								.SendUuidToService(AV_TEST_GROUP);
						/* call osupdate to download the packages */
						if (uuidResult.equals("success")) {
							try {
								Intent LaunchIntent = new Intent(
										OSUPDATE_ACTION);
								LaunchIntent.setComponent(ComponentName
										.unflattenFromString("com.yunos.osupdate/.front.UpdateActivity"));
								startActivity(LaunchIntent);
							} catch (ActivityNotFoundException e) {
								Log.d(TAG, "Osupdate class not found");
							}
						}
					}
				});
		// Setting Negative "NO" Button
		alertBuilder.setPositiveButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/* send AV- to staticAV service */
						SendMessage tmpSend = new SendMessage();
						tmpSend.SendToService("AV-");
					}
				});

		// Showing Alert Message
		alertBuilder.show();
	}
}