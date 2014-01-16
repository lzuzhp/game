package com.example.sudoku;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class Sudoku extends Activity implements OnClickListener{
	private static final String TAG = "Sudoku" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Set up click listeners for all the buttons
		View continueButton = findViewById(R.id.button_continue);
		continueButton.setOnClickListener(this);
		View newButton = findViewById(R.id.button_new);
		newButton.setOnClickListener(this);
		View aboutButton = findViewById(R.id.button_about);
		aboutButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.button_exit);
		exitButton.setOnClickListener(this);
	}

	//myHandler class
	private final class MyHandler extends Handler{
		public MyHandler (Looper looper){
			super(looper);
		}
		
		public void handleMessage (Message msg){
			Log.d(TAG,String.format("handler meg %s ProceeID %s ThreadID %s",msg.what,android.os.Process.myPid(),android.os.Process.myTid()));
		}
	}
	
	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.button_continue:
			startGame(Game.DIFFICULTY_CONTINUE);
			break;
			// ...
 		case R.id.button_about:
			//haipeng BEGIN
			//Function:handle thread test
			Runnable runnable = new Runnable (){
				public void run(){
					Log.d(TAG,String.format("delay 10s before ProceeID %s ThreadID %s",android.os.Process.myPid(),android.os.Process.myTid()));
					
					long endTime = System.currentTimeMillis() + 5*1000;
					
					while (System.currentTimeMillis() < endTime) {
						synchronized (this) {
						try {
							   wait(endTime - System.currentTimeMillis());
						     } catch (Exception e) {
							}
						}
					}
					Log.d(TAG,String.format("delay 10s after ProceeID %s ThreadID %s",android.os.Process.myPid(),android.os.Process.myTid()));
					
				}
			};
			HandlerThread thread = new HandlerThread("Sodoku about thread");
			thread.start();
			MyHandler handler = new MyHandler(thread.getLooper());
			
			Message msg = handler.obtainMessage();
			msg.what=1;
			handler.sendMessage(msg);
			//handler.post(runnable);
			
			//haipeng ENG
			
			Intent i = new Intent(this, About.class);
			startActivity(i);
			break;
	      // More buttons go here (if any) ...
 		case R.id.button_exit:
			 finish();
			 break;
 		case R.id.button_new:
			/*haipeng BEGIN
			*Function:get all the install apps
			final PackageManager pm = getPackageManager();
			//get a list of installed apps.
			List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
			
			for (ApplicationInfo packageInfo : packages) {
				//packageInfo.packageName = "hello";
			    Log.d(TAG, "Installed package :" + packageInfo.packageName);
			    Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName)); 
			}
			haipeng ENG*/
			//haipeng BEGIN
			//Function file read
			/*
			String txtName = "packages_boot.list";
			File data = Environment.getDataDirectory();
			File path = new File(data, "system/" + txtName);
			List<String> terms = new ArrayList<String>();
			try {
			    BufferedReader br = new BufferedReader (
			                        new InputStreamReader(
			                        new FileInputStream(path)));
			    String line;
			    String[] saLineElements;
			    while ((line = br.readLine()) != null)
			    {
			    	terms.add(line);
			    	Log.d(TAG, "line :" + line);
			    }
			     br.close();
			} 
			catch (FileNotFoundException e) {
			    System.err.println("FileNotFoundException: " + e.getMessage());
			} catch (IOException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			
			int tmp = 0;
			while (tmp < terms.size()){
				Log.d(TAG, "allowed package :" + terms.get(tmp));
				tmp++;
			}
			//haipeng END
			 * 
			 */
				openNewGameDialog();
				break;
			  }
	}
	@Override
	protected void onResume() {
		super.onResume();
		Music.play(this, R.raw.main);
	}
	@Override
	protected void onPause() {
		super.onPause();
		Music.stop(this);
	}
	
	private void openNewGameDialog() {
		new AlertDialog.Builder(this)
		.setTitle(R.string.new_game_title)
		.setItems(R.array.difficulty,
		new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialoginterface,
		int i) {
		startGame(i);
		}
		})
		.show();
	}
	
	private void startGame(int i) {
		Log.d(TAG, "clicked on " + i);
		Intent intent = new Intent(Sudoku.this, Game.class);
		intent.putExtra(Game.KEY_DIFFICULTY, i);
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.settings:
	startActivity(new Intent(this, Prefs.class));
	return true;
	// More items go here (if any) ...
	}
	return false;
	}

}
