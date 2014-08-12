package net.narlab.projectnar.utils;

import android.content.Context;

import net.narlab.projectnar.Nar;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author fma
 * @since 21.07.2014.
 */
public class DataHolder {
	public static final boolean LOGIN_TEST = false;

	public static final int REG_NAR_QR_REQ_CODE = 0x0f3a; // QRScannerAct request code
	public static final int REG_NAR_REQ_CODE = 0x0f3b; // RegisterNarAct request code
	public static final int NAR_CTRL_PANEL_REQ = 0x0f3c; // QRScannerAct request code

	private static NarConnManager connMng;
	private static NarWifiManager wifiManager;
	private static HttpClient httpClient;
	private static final String serverUrl = "http://88.230.148.11";
	private static final ArrayList<Nar> narList = new ArrayList<Nar>();

	public static NarConnManager getConnMng() {
		if (connMng == null) {
			connMng = new NarConnManager();
		}
		return connMng;
	}

	// common HttpClient for cookies
	public static HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public static String getServerUrl() {
		return serverUrl;
	}

	public static void addNars(JSONArray json) {
		try {
			for (int i=0; i<json.length(); ++i) {
				JSONObject jsonOb = json.getJSONObject(i);
				narList.add(new Nar(jsonOb.getString("nar_id"), 0));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void addNars(String narListStr) {
		try {
			JSONArray json = new JSONArray(narListStr);
			addNars(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Nar> getNarList() {
		return narList;
	}

	public static NarWifiManager getNewWifiManager(Context c) {
		if (wifiManager == null) {
			wifiManager = new NarWifiManager(c);
		}
		return wifiManager;
	}

	public static NarWifiManager getWifiManager() {
		return wifiManager;
	}
}
