package net.narlab.projectnar.utils;

import android.content.Context;

import net.narlab.projectnar.general.Nar;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author fma
 * @since 21.07.2014.
 * these are getter/setter methods for common objects
 */
public class DataHolder {

	public static final int REG_NAR_QR_REQ_CODE = 0x0f3a; // QRScannerAct request code
	public static final int REG_NAR_REQ_CODE = 0x0f3b; // RegisterNarAct request code
	public static final int NAR_CTRL_PANEL_REQ = 0x0f3c; // NarControlPanelAct request code
	public static final int AUTH_USER_REQ_CODE = 0xf3d; // AuthenticatorAct request code

	public static final String PREF_FILE = "nar_prefs";
	public static final String PREF_NAME_SERVER_URL = "SPREFS_SERVER_URL";

	private static NarConnManager connMng;
	private static NarWifiManager wifiManager;
	private static HttpClient httpClient;
	private static String serverHostname = "192.168.2.4";

	private static final ArrayList<Nar> narList = new ArrayList<Nar>();
	private static Context C;

	public static void setContext(Context C) {
		if (DataHolder.C == null ) {
			DataHolder.C = C;
		}
	}

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
		return "http://"+ serverHostname;
	}

	public static void setServerHostname(String serverHostname) {
		DataHolder.serverHostname = serverHostname.trim();
	}
	public static String getServerHostname() {
		return serverHostname;
	}

	public static void addNars(JSONArray json) {
		try {
//			Log.w("DH", json.toString());
			for (int i=0; i<json.length(); ++i) {
				JSONObject jsonOb = json.getJSONObject(i);
				narList.add(new Nar(jsonOb.getString("nar_id"),
						jsonOb.getString("nar_name"),
						jsonOb.getString("lastalive")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

/*	public static void addNars(String narListStr) {
		try {
			JSONArray json = new JSONArray(narListStr);
			addNars(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
*/
	public static ArrayList<Nar> getNarList() {
		return narList;
	}

	public static NarWifiManager getWifiManager() {
		if (wifiManager == null) {
			wifiManager = new NarWifiManager(C);
		}
		return wifiManager;
	}

}
