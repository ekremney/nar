package net.narlab.projectnar.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * @author fma
 * @date 13.08.2014.
 */
public class Helper {
	private static Context C;

	public static void setContext(Context C) {
		Helper.C = C;
	}
	public static void toastIt(String msg) {
		toastIt(msg, Toast.LENGTH_SHORT);
	}
	public static void toastIt(String msg, int len) {
		Toast.makeText(C, msg, len).show();
	}
	public static void toastIt(int strId) {
		toastIt(strId, Toast.LENGTH_SHORT);
	}
	public static void toastIt(int strId, int len) {
		toastIt(C.getString(strId), len);
	}
	public static String getTag(Object a) {return a.getClass().getSimpleName();}

	public static String getExceptionString(Exception e) {
		return  ".\nType: "+e.getClass().getSimpleName()
				+"\nCause: "+e.getCause()
				+"\nMessage: "+e.getMessage();
	}
}
