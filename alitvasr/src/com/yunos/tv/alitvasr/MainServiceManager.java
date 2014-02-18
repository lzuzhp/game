package com.yunos.tv.alitvasr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import com.yunos.baseservice.clouduuid.CloudUUID;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.SSLCertificateSocketFactory;
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

	private Map<String, String> recordMap = new HashMap<String, String>();
	private Map<String, String> sendMap = new HashMap<String, String>();
	private static final String FROM = "from";
	private static final String MESSAGE = "message";
	private static final String UUID_KEY = "uuid";
	private static final String TIMESTAMP_KEY = "timestamp";
	private static final String type = "type";

	private String lastValue = "";
	private String curValue = "";

	private static final String DATA_BUS_SERVER_URL = "https://activity.tv.yunos.com/device/log/commit.htm";
	// private static final String DATA_BUS_SERVER_URL =
	// "http://10.125.2.82:8080/device/log/commit.htm";

	/*
	 * Connection timeout
	 */
	private static final int CONNECTION_TIMEOUT = 25000;
	/*
	 * Socket timeout
	 */
	private static final int SOCKET_TIMEOUT = 25000;

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
		int connect = 0;
		if (isConnected()) {
			// get the last value
			SharedPreferences userDetails = this.getSharedPreferences(
					"userinfo", Context.MODE_PRIVATE);
			if (userDetails != null) {
				lastValue = userDetails.getString("staticAv", "");
			} else {
				lastValue = "";
			}
			Log.d(TAG, "lastValue " + lastValue);

			// get the current value

			DisplayManagerAw displayManager = new DisplayManagerAw();
			if (displayManager.getHdmiHotPlugStatus() != 0) {
				curValue = "HDMI";
			} else if (displayManager.getTvHotPlugStatus() != 0) {
				curValue = "AV";
			} else {
				curValue = "N/A";
			}

			Log.d(TAG, "curValue " + curValue);

			if (!curValue.equals(lastValue)) {

				Log.d(TAG, "save value ");

				SharedPreferences.Editor edit = userDetails.edit();
				edit.clear();
				edit.putString("staticAv", curValue);
				edit.commit();

				// send message
				recordMap.put(UUID_KEY, getClientUUID());
				recordMap.put(TIMESTAMP_KEY, formatTime());
				recordMap.put(type, curValue);
				String message = buildMessage();

				sendMap.put(FROM, "staticav");
				sendMap.put(MESSAGE, message);
				// new thread to handle

				new Thread(new Runnable() {
					public void run() {
						String result = doPost(DATA_BUS_SERVER_URL, sendMap);
						// try another time if fail
						if (result == null)
							doPost(DATA_BUS_SERVER_URL, sendMap);
						else
							Log.d(TAG, "result is:" + result);
					}
				}).start();
				connect = 1;

			} else {
				Log.i(TAG, "Already Recoard: " + lastValue);
			}
		} else {
			Log.d(TAG, "network not available");
			connect = 0;
		}

		return connect;
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

	/*
	 * get the UUID
	 */
	private String getClientUUID() {
		return CloudUUID.getCloudUUID();
	}

	/**
	 * format the current time
	 * 
	 * @return
	 */
	private String formatTime() {
		Calendar time = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SSS");
		String ctime = formatter.format(time.getTime());
		return ctime;
	}

	private String buildMessage() {
		StringBuilder strBuilder = new StringBuilder();
		Set<Entry<String, String>> entrySet = recordMap.entrySet();
		int index = 0;
		strBuilder.append("{");
		for (Entry<String, String> entry : entrySet) {
			strBuilder.append("\"");
			strBuilder.append(entry.getKey());
			strBuilder.append("\":\"");
			strBuilder.append(entry.getValue());
			strBuilder.append("\"");
			if (index++ < entrySet.size() - 1) {
				strBuilder.append(",");
			}
		}
		strBuilder.append("}");
		return strBuilder.toString();
	}

	private String doPost(String urlStr, Map<String, String> params) {
		HttpURLConnection connection = null;
		try {
			Log.d(TAG, "doPost:" + urlStr);
			String queryString = getQueryString(params);
			Log.d(TAG, "queryString:" + queryString);
			URL httpUrl = new URL(urlStr);
			connection = (HttpURLConnection) getConnection(httpUrl);
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();

			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());

			if (queryString != null) {
				out.writeBytes(queryString);
			}
			out.flush();
			out.close();
			Log.d(TAG, "getResponseCode:" + connection.getResponseCode());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return getResponseBody(connection.getInputStream());
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception:", e);
		} finally {
			// release connection
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	/**
	 * getConnection
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private URLConnection getConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		if (connection instanceof HttpsURLConnection) {
			SSLSocketFactory sf = SSLCertificateSocketFactory.getInsecure(0,
					null);
			((HttpsURLConnection) connection).setSSLSocketFactory(sf);
			((HttpsURLConnection) connection)
					.setHostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname,
								SSLSession session) {
							return true;
						}
					});
		}
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		connection.setReadTimeout(SOCKET_TIMEOUT);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		return connection;
	}

	/**
	 * getQueryString
	 * 
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getQueryString(Map<String, String> params)
			throws UnsupportedEncodingException {
		if (params != null && !params.isEmpty()) {
			// build queryString
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, String>> iter = params.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> param = iter.next();
				sb.append(param.getKey()).append("=")
						.append(URLEncoder.encode(param.getValue(), "UTF-8"));
				if (iter.hasNext()) {
					sb.append("&");
				}
			}
			return sb.toString();
		}
		return null;
	}

	private String getResponseBody(InputStream is) throws IOException {
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		BufferedReader reader = new BufferedReader(isr);
		String line = null;
		StringBuilder sb = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

}
