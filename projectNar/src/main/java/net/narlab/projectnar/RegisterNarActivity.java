package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.FontAwesomeText;

import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.NarConnManager;
import net.narlab.projectnar.utils.NarWifiManager;
import net.narlab.projectnar.utils.OnFragmentInteractionListener;
import net.narlab.projectnar.utils.SmartConfigManager;

public class RegisterNarActivity extends FragmentActivity implements OnFragmentInteractionListener {

	private Nar newNar;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_nar);

		Fragment fragment = new NewNarInfoFragment();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.new_nar_progress, fragment);
		transaction.commit();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_nar, menu);
        return true;
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
	public void onFragmentInteraction(Uri uri) {
		// Dont know what to do
		Log.d("onFragmentInteraction", uri.toString());
	}

	private void ToastIt(String s, int len) {
		Toast.makeText(getApplicationContext(), s, len).show();
	}
	private void ToastIt(String s) {
		ToastIt(s, Toast.LENGTH_SHORT);
	}

	public void onScanQRCodeBtnClick(View view) {
		Intent intent = new Intent(this, QRScannerActivity.class);
		startActivityForResult(intent, DataHolder.REGISTER_NAR_REQ_CODE);
	}

	private NarWifiManager narWifiManager = null;
	private NarWifiManager getNarWifiManager() {
		if (narWifiManager == null) {
			narWifiManager = new NarWifiManager(this);
		}
		return narWifiManager;
	}

	public void onStartSmartConfig(View view) {
		if (newNar == null) {
			ToastIt("You should enter nar info or scan device qr code");
			return;
		}

		Fragment fragment = NewNarWifiFragment.newInstance(getNarWifiManager());

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.new_nar_progress, fragment);
		transaction.commit();

		if (!getNarWifiManager().isWifiConnected()) {
			Toast.makeText(getApplicationContext(), R.string.wifi_offline, Toast.LENGTH_LONG).show();
			return;
		}


		// get setup button and animate icon
		BootstrapButton b = (BootstrapButton) findViewById(R.id.setup_device);
		b.toggleRotation(BootstrapButton.BB_ICON_RIGHT,
				true, FontAwesomeText.AnimationSpeed.SLOW);

		// get wifi and smart config managers
		NarWifiManager nWM = getNarWifiManager();
		SmartConfigManager sCM = new SmartConfigManager(getApplicationContext());

//		Intent intent = new Intent(this, SmartConfigActivity.class);
		/* TODO: replace below 2 lines with
		 * String pass = ((EditText) findViewById(R.id.wifi_pass)).getText().toString();
		 */
		String pass = "narlab.net1";
		EditText et = ((EditText) findViewById(R.id.wifi_pass));
		et.setText(pass);
		sCM.startSmartConfig(nWM.getSSID(), pass, nWM.getGatewayString(), null);
		Toast.makeText(getApplicationContext(), "Trying to connect", Toast.LENGTH_LONG).show();
//		String ssid = editText.getText().toString();
//		intent.putExtra(EXTRA_W_SSID, ssid);
//		startActivity(intent);
	}


	// get result from qr scan and generate a new Nar object
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == DataHolder.REGISTER_NAR_REQ_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String res = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT);

				try {
					newNar = new Nar(res);
					((TextView) findViewById(R.id.nar_id)).setText(newNar.getId());
					((TextView) findViewById(R.id.nar_pass)).setText(newNar.getPass());
					Log.d("Test", res + "=>id=" + newNar.getId() + "__pass=" + newNar.getPass());
					final NarConnManager narConnMng = DataHolder.getConnMng();
					narConnMng.register(newNar);
					narConnMng.test();
					final Nar f_nar = newNar;
					new Thread(new Runnable() {

						@Override
						public void run() {
							try {
								Thread.sleep(10000);
								narConnMng.sendMessage(f_nar.getId(), "TestMessage", "TestContent");
								Thread.sleep(10000);
								narConnMng.logout();
								Thread.sleep(10000);
								narConnMng.checkState(f_nar.getId());
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
//					connManager.logout();
//					connManager.sendMessage("id", nar.getId());
				} catch (Nar.NarMalformedParameterException e) {
					ToastIt("Couldn't start nar: " + e.getMessage(), Toast.LENGTH_LONG);
				}

				return;
			} else {
				ToastIt("QR result was not ok: " + resultCode);
			}
		}
		ToastIt("QR Scan failed", Toast.LENGTH_LONG);
	}

}
