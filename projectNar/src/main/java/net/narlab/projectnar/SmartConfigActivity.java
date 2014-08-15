package net.narlab.projectnar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.FontAwesomeText;

import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;
import net.narlab.projectnar.utils.NarWifiManager;
import net.narlab.projectnar.utils.SmartConfigManager;

public class SmartConfigActivity extends ActionBarActivity {
	public static final String EXT_WIFI_SSID = "SmartConfigAct_wifi_ssid";
	public static final String EXT_NAR_ID = "SmartConfigAct_nar_id";
	public static final String EXT_GATEWAY = "SmartConfigAct_gateway";

	private String ssid, narId, gateway;
	private SmartConfigManager nSCM;
	private NarWifiManager nWM;

	private BootstrapButton setupBtn;
	private BootstrapEditText passEditText, gatewayEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get the message from the intent
		Intent intent = getIntent();
		ssid = intent.getStringExtra(this.EXT_WIFI_SSID);
		narId = intent.getStringExtra(this.EXT_NAR_ID);
		gateway = intent.getStringExtra(this.EXT_GATEWAY);

		setContentView(R.layout.activity_smart_config);

		((BootstrapEditText)findViewById(R.id.wifi_nar_id)).setText(narId);
		((BootstrapEditText)findViewById(R.id.wifi_ssid)).setText(ssid);

		gatewayEditText = (BootstrapEditText)findViewById(R.id.wifi_gateway);
		gatewayEditText.setText(gateway);

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

	// TODO: add stopping transmission to here and on destroy
	@Override
	public void onBackPressed() {

	}

	public void onSetupDeviceBtnClick(View v) {
		if (!nWM.isWifiConnected()) {
			Helper.toastIt("Wifi is not connected");
			return;
		} else if (!nWM.isInternetConnected()) {
			Helper.toastIt("Not connected to internet");
			return;
		}
		setupBtn.setEnabled(false);
		setupBtn.startRotateRight(true, FontAwesomeText.AnimationSpeed.SLOW);

		String pass = passEditText.getText().toString();
		gateway = gatewayEditText.getText().toString();

		nSCM.startSmartConfig(ssid, pass, gateway, narId);
	}
}
