package net.narlab.projectnar.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.integrity_project.smartconfiglib.FirstTimeConfig;
import com.integrity_project.smartconfiglib.FirstTimeConfigListener;

//import com.integrity_project.smartconfiglib.FirstTimeConfig;


public class SmartConfigManager implements FirstTimeConfigListener {

	private FirstTimeConfig firstTimeConfig = null;
	private boolean started = false;

	public boolean startSmartConfig(String ssid, String pass, String gateway, String deviceName) {
		try {
			if (firstTimeConfig == null) {
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
	public void onFirstTimeConfigEvent(FtcEvent arg0, Exception arg1) {
		try {
			SmartConfigManager.this.firstTimeConfig.stopTransmitting();
			/**
			 * Adding the Try catch just to ensure the event doesn't return null.Some times observed null from Lib file.Just a safety measure
			 */
			arg1.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.e("FTC_ARG", "" + arg0);
		switch (arg0) {
			case FTC_ERROR:
				/**
				 * Stop transmission
				 */
				Helper.toastIt("FTC_ERROR", Toast.LENGTH_SHORT);
				break;
			case FTC_SUCCESS:

				/**
				 * Show user alert on success
				 */
				Helper.toastIt("FTC_SUCCESS", Toast.LENGTH_SHORT);
				break;
			case FTC_TIMEOUT:
				/**
				 * Show user alert when timed out
				 */
				Helper.toastIt("FTC_TIMEOUT", Toast.LENGTH_SHORT);
				break;
			default:
				break;
		}

	}

}
