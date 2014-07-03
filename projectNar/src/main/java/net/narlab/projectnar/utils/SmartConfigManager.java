package net.narlab.projectnar.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.integrity_project.smartconfiglib.FirstTimeConfig;
import com.integrity_project.smartconfiglib.FirstTimeConfigListener;

//import com.integrity_project.smartconfiglib.FirstTimeConfig;


public class SmartConfigManager implements FirstTimeConfigListener {

    private FirstTimeConfig firstTimeConfig = null;
    private Context context = null;
    private boolean started = false;

    public SmartConfigManager(Context c) {
        context = c;
    }

    public boolean startSmartConfig(String ssid, String pass, String gateway, String deviceInput) {
        try {
            if (firstTimeConfig == null) {
                firstTimeConfig = buildFirstTimeConfig(SmartConfigManager.this, ssid, pass, gateway, deviceInput);
            }
            if (!started && firstTimeConfig != null) {
                firstTimeConfig.transmitSettings();
                started = true;
            }
        } catch (Exception e) {
            Log.e("smartConfigM", "first time config failed :/");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private FirstTimeConfig buildFirstTimeConfig(FirstTimeConfigListener listener, String ssid, String pass, String gateway, String deviceInput)
            throws Exception {
/*		String aesKey = extras.getString(EXTRA_AES_KEY);
        byte[] transmissionKey = aesKey.getBytes();*/

        if (deviceInput == null || deviceInput.length() == 0) {
            deviceInput = "CC3000";
        }
        // AES key isn't being redacted below because it's public knowledge.
        Log.d("FirstTimeConfig", "SSID=" + ssid + ", pass=" + pass + ", gatewayIP=" + gateway);
        Toast.makeText(context, "First Time build", Toast.LENGTH_SHORT).show();
        return new FirstTimeConfig(listener, pass, null, gateway,
                ssid, deviceInput);
        //		return new FirstTimeConfig(listener, pass, transmissionKey, gatewayIP, ssid);
    }

    @Override
    public void onFirstTimeConfigEvent(FtcEvent arg0, Exception arg1) {
        try {
            SmartConfigManager.this.firstTimeConfig.stopTransmitting();
            /**
             * Adding the Try catch just to ensure the event doesnt retrun null.Some times observed null from Lib file.Just a safety measure
             */
            arg1.printStackTrace();

        } catch (Exception e) {

        }

        Log.e("FTC_ARG", "" + arg0);
        switch (arg0) {
            case FTC_ERROR:
                /**
                 * Stop transmission
                 */
                Toast.makeText(context, "FTC_ERROR", Toast.LENGTH_SHORT).show();
                break;
            case FTC_SUCCESS:

                /**
                 * Show user alert on success
                 */
                Toast.makeText(context, "FTC_SUCCESS", Toast.LENGTH_SHORT).show();
                break;
            case FTC_TIMEOUT:
                /**
                 * Show user alert when timed out
                 */
                Toast.makeText(context, "FTC_TIMEOUT", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

}
