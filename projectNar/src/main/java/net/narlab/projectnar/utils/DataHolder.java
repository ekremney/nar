package net.narlab.projectnar.utils;

import android.content.Context;

import net.narlab.projectnar.adapters.NarListAdapter;
import net.narlab.projectnar.general.Nar;
import net.narlab.projectnar.general.NarList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author fma
 * @since 21.07.2014.
 * these are getter/setter methods for common objects
 */
public class DataHolder {

	public static final int REG_NAR_QR_REQ_CODE = 0x0f3a; // QRScannerAct request code
	public static final int REG_NAR_REQ_CODE = 0x0f3b; // RegisterNarAct request code
	public static final int NAR_CTRL_PANEL_REQ = 0x0f3c; // NarControlPanelAct request code

	public static final String PREF_FILE = "nar_prefs";
	public static final String PREF_NAR_LIST = "prefs_nar_list_str";

	private static final String serverHostname = "iot.eclipse.org";

	private static NarList narList;
	private static Context C;
	private static NarListAdapter narListAdapter;

	public static void setContext(Context C) {
		if (DataHolder.C == null ) {
			DataHolder.C = C;
		}
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
						jsonOb.getString("nar_id"), "19/12/2014"));
//						jsonOb.getString("lastalive")));
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

	public static NarListAdapter getNarListAdapter() {
		if (narListAdapter == null) {
			if (narList == null) {
				narList = new NarList();
			}
			narListAdapter = new NarListAdapter(DataHolder.C, narList);
		}
		return narListAdapter;
	}

}
