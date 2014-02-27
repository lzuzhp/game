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
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

import com.yunos.baseservice.clouduuid.CloudUUID;

import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.util.Log;

public class SendMessage {

	private static final String TAG = "alitvasr";

	private Map<String, String> recordMap = new HashMap<String, String>();
	private Map<String, String> sendMap = new HashMap<String, String>();
	private static final String FROM = "from";
	private static final String MESSAGE = "message";
	private static final String UUID_KEY = "uuid";
	private static final String TIMESTAMP_KEY = "timestamp";
	private static final String TYPE = "type";

	private static final String PHONE_TYPE = "phoneType";
	private static final String IMEI = "imei";
	private static final String GROUP_ID = "groupId";

	private String result = "";

	// private static final String DATA_BUS_SERVER_URL =
	// "https://activity.tv.yunos.com/device/log/commit.htm";
	private static final String DATA_BUS_SERVER_URL = "http://10.125.2.82:8080/device/log/commit.htm";
	private static final String UUID_SERVER_URL = "https://osupdate.aliyun.com/update/setGroup";

	/*
	 * Connection timeout
	 */
	private static final int CONNECTION_TIMEOUT = 25000;
	/*
	 * Socket timeout
	 */
	private static final int SOCKET_TIMEOUT = 25000;

	private Object lock = new Object();
	private boolean isFinished = false;

	/*
	 * send <FROM,MESSAGE> to service
	 */
	public String SendToService(String type) {
		// send message
		recordMap.put(UUID_KEY, getClientUUID());
		recordMap.put(TIMESTAMP_KEY, formatTime());
		recordMap.put(TYPE, type);
		String message = buildMessage();

		sendMap.put(FROM, "staticav");
		sendMap.put(MESSAGE, message);
		// new thread to handle
		new Thread(new Runnable() {
			public void run() {
				result = doPost(DATA_BUS_SERVER_URL, sendMap);
				// try another time if fail
				if (result == null)
					doPost(DATA_BUS_SERVER_URL, sendMap);
				else
					Log.d(TAG, "result is:" + result);
				synchronized (lock) {
					isFinished = true;
					lock.notify();
				}
			}
		}).start();
		/* wait the result */
		synchronized (lock) {
			while (!isFinished) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (result.contains("\"message\":\"SUCCESS\"")) {
				return "success";
			} else {
				return "fail";
			}
		}
	}

	/*
	 * send <GROUPID,UUID> to service
	 */
	public String SendUuidToService(String group) {
		// send message
		sendMap.put(IMEI, getClientUUID());
		sendMap.put(GROUP_ID, group);
		sendMap.put(PHONE_TYPE, getPhoneType());

		// new thread to handle
		new Thread(new Runnable() {
			public void run() {
				result = doPost(UUID_SERVER_URL, sendMap);
				// try another time if fail
				if (result == null) {
					doPost(UUID_SERVER_URL, sendMap);
				} else {
					Log.d(TAG, "SendUuidToService result is: " + result);
				}
				synchronized (lock) {
					isFinished = true;
					lock.notify();
				}
			}
		}).start();
		/* wait the result */
		synchronized (lock) {
			while (!isFinished) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (result.contains("\"success\":\"true\"")) {
				return "success";
			} else {
				return "fail";
			}
		}
	}
	/*
	 * get phone type
	 */
	private String getPhoneType() {
		String type = Build.MODEL;
		if (Integer.parseInt(Build.VERSION.SDK) >= 10) {
			type += Build.VERSION.SDK;
		}
		String release = Build.VERSION.RELEASE;
		String[] strTemps = release.split("-");
		if (null != strTemps && strTemps.length > 1)
			type += strTemps[1];
		return type;
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
