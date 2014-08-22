package net.narlab.projectnar.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.integrity_project.smartconfiglib.FirstTimeConfig;
import com.integrity_project.smartconfiglib.FirstTimeConfigListener;

import net.narlab.projectnar.R;

//import com.integrity_project.smartconfiglib.FirstTimeConfig;


public class SmartConfigManager implements FirstTimeConfigListener {

	private FirstTimeConfig firstTimeConfig = null;
	private SmartConfigFinishedListener callerActivity;
	private boolean started = false;

	public boolean startSmartConfig(SmartConfigFinishedListener caller, String ssid, String pass, String gateway, String deviceName) {
		this.callerActivity = caller;

		try {
			if (firstTimeConfig == null) {
				deviceName = "CC3000";
				firstTimeConfig = buildFirstTimeConfig(SmartConfigManager.this, ssid, pass, gateway, deviceName);
			}
			if (!started && firstTimeConfig != null) {
				firstTimeConfig.transmitSettings();
				started = true;
			}
		} catch (Exception e) {
			Log.e("smartConfigM", "first time config failed :(");
			Helper.toastIt( "first time config failed :(");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean stopSmartConfig() {
		try {
			if (firstTimeConfig!=null && started) {
				firstTimeConfig.stopTransmitting();
			}
			return true;
		} catch (Exception e) {
			Helper.toastIt("Couldn't stop Smart Config");
			return false;
		}
	}

	private FirstTimeConfig buildFirstTimeConfig(FirstTimeConfigListener listener, String ssid, String pass, String gateway, String deviceName)
			throws Exception {
/*		String aesKey = extras.getString(EXTRA_AES_KEY);
		byte[] transmissionKey = aesKey.getBytes();*/

		if (deviceName == null || deviceName.length() == 0) {
			deviceName = "CC3000";
		}
		// AES key isn't being redacted below because it's public knowledge.
		Log.d("FirstTimeConfig", "SSID=" + ssid + ", pass=" + pass + ", gatewayIP=" + gateway);
		Helper.toastIt("First Time build", Toast.LENGTH_SHORT);
		return new FirstTimeConfig(listener, pass, null, gateway,
				ssid, deviceName);
		//		return new FirstTimeConfig(listener, pass, transmissionKey, gatewayIP, ssid);
	}

	@Override
	public void onFirstTimeConfigEvent(FtcEvent ftcEvent, Exception ftcException) {
		final String TAG = this.getClass().getSimpleName();
		Log.e("FTC_ARG", "" + ftcEvent);

		// call the callerActivity's event method
		callerActivity.onSmartConfigFinished(ftcEvent);

		try {
			// this idiotic cc3000 library throws NullPointerException whenever the hell it wants (yes I'm pissed off)
			SmartConfigManager.this.firstTimeConfig.stopTransmitting();
			/**
			 * Adding the Try catch just to ensure the event doesn't return null.Some times observed null from Lib file.Just a safety measure
			 */
			Log.e(TAG+"_ftc", Helper.getExceptionString(ftcException));

		} catch (Exception e) {
			Log.e(TAG+"_stop", Helper.getExceptionString(e));
		}

	}

	public interface SmartConfigFinishedListener {
		public void onSmartConfigFinished(FtcEvent ftcEvent);
	}

}
