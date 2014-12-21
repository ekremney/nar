package net.narlab.projectnar.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import net.narlab.projectnar.general.Nar;
import net.narlab.projectnar.general.NarList;

/**
 * @author fma
 * @date 13.08.2014.
 * these are simply short hand methods
 */
public class Helper {
	private static Context C;

	public static void setContext(Context C) {
		if (Helper.C == null) { // don't set unless it is null
			Helper.C = C;
		}
		DataHolder.setContext(C);
	}
	public static void toastIt(String msg) {
		toastIt(msg, Toast.LENGTH_SHORT);
	}
	public static void toastIt(String msg, int len) {
		Toast.makeText(C, msg, len).show();
	}
	public static void toastIt(int strId, int len) {
		toastIt(C.getString(strId), len);
	}
	public static String getTag(Object o) {return o.getClass().getSimpleName();}

	public static String getExceptionString(Exception e) {
		return  ".\nType: "+e.getClass().getSimpleName()
				+"\nCause: "+e.getCause()
				+"\nMessage: "+e.getMessage();
	}

	public static SharedPreferences getSharedPreferences() {
		return C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE);
	}

	public static void editSharedPreferences(String name, String val) {
		SharedPreferences.Editor prefEditor = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE).edit();
		prefEditor.putString(name, val);
		prefEditor.apply();
	}

	public static String getNarListFromPrefs() {
		SharedPreferences prefs = getSharedPreferences();
		return prefs.getString(DataHolder.PREF_NAR_LIST, "[]");
	}

	public static void putNarListToPrefs(NarList narList) {
		SharedPreferences.Editor prefEditor = C.getSharedPreferences(DataHolder.PREF_FILE, Context.MODE_PRIVATE).edit();
		String jstr = null;
		for (Nar n: narList) {
//			narList
		}
//		prefEditor.putString(DataHolder.PREF_NAR_LIST, );
		prefEditor.apply();
	}
}
