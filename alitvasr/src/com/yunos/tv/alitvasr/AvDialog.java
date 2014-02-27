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
		final String messNotice = "ħ��1.6ϵͳ����wifi�ȵ㡢ϵͳ�����Ż�����ȫ���ȹ��ܣ���ѡ��������"
				+ "����������������ϸ��չ涨�������������\n"
				+ "1.������������ɺ�ѡ��\"��������\"�������̰γ�AV��\n"
				+ "2.ϵͳ������ָʾ�ƻ������˸5-10���ӣ���ʱ�����жϵ�Դ\n" + "3.ָʾ�Ƴ���������AV�ߣ������ɹ���\n"
				+ "\nע�������������⣬�뷴��������������\n" + "����������ʧ�ܣ�����ͨ������ϵͳ�������";

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		TextView title = new TextView(this);
		title.setText("�𾴵�AV���û�");
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
		alertBuilder.setNegativeButton("��֪����",
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
		alertBuilder.setPositiveButton("ȡ��",
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