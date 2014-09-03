package net.narlab.projectnar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.FontAwesomeText;
import com.integrity_project.smartconfiglib.FirstTimeConfigListener;

import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;
import net.narlab.projectnar.utils.NarWifiManager;
import net.narlab.projectnar.utils.SmartConfigManager;

public class SmartConfigActivity extends ActionBarActivity implements SmartConfigManager.SmartConfigFinishedListener {
	public static final String EXT_WIFI_SSID = "SmartConfigAct_wifi_ssid";
	public static final String EXT_NAR_ID = "SmartConfigAct_nar_id";
	public static final String EXT_GATEWAY = "SmartConfigAct_gateway";

	private String ssid, narId, gateway;
	private SmartConfigManager nSCM;
	private NarWifiManager nWM;

	private BootstrapButton setupBtn;
	private BootstrapEditText passEditText, aesKeyEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the message from the intent
		Intent intent = getIntent();
		ssid = intent.getStringExtra(SmartConfigActivity.EXT_WIFI_SSID);
		narId = intent.getStringExtra(SmartConfigActivity.EXT_NAR_ID);
		gateway = intent.getStringExtra(SmartConfigActivity.EXT_GATEWAY);

		setContentView(R.layout.activity_smart_config);

		((BootstrapEditText)findViewById(R.id.wifi_nar_id)).setText(narId);
		((BootstrapEditText)findViewById(R.id.wifi_ssid)).setText(ssid);

		aesKeyEditText = (BootstrapEditText)findViewById(R.id.wifi_sc_key);
//		aesKeyEditText.setText(gateway);

		setupBtn = (BootstrapButton) findViewById(R.id.btn_setup_device);
		passEditText = (BootstrapEditText) findViewById(R.id.wifi_pass);

		nSCM = new SmartConfigManager();
		nWM = DataHolder.getWifiManager();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return id == R.id.action_settings || super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		nSCM.stopSmartConfig();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void onSetupDeviceBtnClick(View v) {
		if (!nWM.isWifiConnected()) {
			Helper.toastIt("Wifi is not connected");
			return;
		} else if (!nWM.isOnline()) {
			Helper.toastIt("Not connected to internet");
			return;
		}
		setupBtn.setEnabled(false);
		setupBtn.startRotateRight(true, FontAwesomeText.AnimationSpeed.SLOW);

		String pass = passEditText.getText().toString();
		String aesKey = aesKeyEditText.getText().toString();

		nSCM.startSmartConfig(SmartConfigActivity.this, ssid, pass, gateway, narId, aesKey);
	}

	@Override
	public void onSmartConfigFinished(FirstTimeConfigListener.FtcEvent ftcEvent) {
		setupBtn.setEnabled(true);
		setupBtn.stopAnimationRight();

		// show some message
		String message;
		switch (ftcEvent) {
			case FTC_ERROR:
				message = getString(R.string.ftc_error);
				break;
			case FTC_SUCCESS:
				message = getString(R.string.ftc_success);
				break;
			case FTC_TIMEOUT:
				message = getString(R.string.ftc_timeout);
				break;
			default:
				message = "Unknown ftcEvent";
				break;
		}
		Log.d(this.getClass().getSimpleName(), message);
		Helper.toastIt(message);
	}
}
