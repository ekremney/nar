package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.FontAwesomeText;

import net.narlab.projectnar.utils.NarWifiManager;
import net.narlab.projectnar.utils.NarWifiManager.IPParser;
import net.narlab.projectnar.utils.SmartConfigManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	NarWifiManager narWifiManager = null;
	public final static String EXTRA_W_SSID = "com.example.myfirstapp.MESSAGE";
	private final static int REQUEST_CODE = 0x0f3a; // request code for QRScanner

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

/*		Log.d("NarWInit", "Try init======================");
		NarWifiManager nWifiManager = new NarWifiManager(this);
		Log.d("NarWInit", "End init======================");
		Log.d("NarWM", "Wifi connected: "+nWifiManager.isWifiConnected());*/
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Log.d("SettingsMenu", ""+item.getItemId());
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_empty) {
			Log.d("SettingsMenu", "Empty Action yey");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static long back_pressed = 0;

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
			finish();
		} else {
			Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
		}
		back_pressed = System.currentTimeMillis();
	}

	private NarWifiManager getNarWifiManager() {
		if (narWifiManager == null) {
			narWifiManager = new NarWifiManager(this);
		}
		return narWifiManager;
	}

	/**
	 * Called when the user clicks the "Setup Device" button
	 * to start activity for SmartConfig
	 */
	public void onSetupDeviceBtnClick(View view) {

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
		String pass = "3b7GmFY4Mt";
		EditText et = ((EditText) findViewById(R.id.wifi_pass));
		et.setText(pass);
		sCM.startSmartConfig(nWM.getSSID(), pass, nWM.getGatewayString(), null);
		Toast.makeText(getApplicationContext(), "Trying to connect", Toast.LENGTH_LONG).show();
//		String ssid = editText.getText().toString();
//		intent.putExtra(EXTRA_W_SSID, ssid);
//		startActivity(intent);

	}

	public void onScanQRCodeBtnClick(View view) {
		Intent intent = new Intent(this, QRScannerActivity.class);
		ToastIt("Scan QR Code!");
		intent.putExtra("ScanStart", "Scan started");
		startActivityForResult(intent, REQUEST_CODE);
	}

	public void ToastIt(String s, int len) {
		Toast.makeText(getApplicationContext(), s, len).show();
	}
	private void ToastIt(String s) {
		ToastIt(s, Toast.LENGTH_SHORT);
	}

	private Map parseNarQRString(String qr_s) {
		String []qr_sl = qr_s.split("\\|");
		Map params = new Hashtable();
		if (qr_sl.length < 2) {
			ToastIt("QR Code is not from a valid nar!");
			return params;
		}
		for (String s: qr_sl) {
			String[] pr = s.split("=");
			if (pr.length == 2) {
				params.put(pr[0], pr[1]);
			} else {
				ToastIt("Malformed paramater: "+s);
			}
		}
		return params;
	}
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String res = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT);
				Map nar_params = parseNarQRString(res);

				if (nar_params.size() >=2) {
					String id = (String)nar_params.get("ID");
					String pass = (String)nar_params.get("PASS");
					((TextView) findViewById(R.id.nar_id  )).setText( id );
					((TextView) findViewById(R.id.nar_pass)).setText( pass );
					Log.d("Test", res + "=>id=" + id + "__pass=" + pass);

				}
				return;
			}
		}
		ToastIt("QR Scan failed", Toast.LENGTH_LONG);
	}
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			/*if (position > 2) {
				position = 2;
			} else if (position < 0) {
				position = 0;
			}*/
			String s[] = {"First", "Second", "Third"};
			switch (position) {
				case 0:
					return QRFragment.newInstance();
				case 1:
					return WifiInfoFragment.newInstance(getNarWifiManager());
				default:
					return PlaceholderFragment.newInstance(position + 1, s[position]);
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return getString(R.string.title_qr_code).toUpperCase(l);
				case 1:
					return getString(R.string.title_smart_config).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private static final String ARG_SECTION_STRING = "section_string";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber, String sectionString) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			args.putString(ARG_SECTION_STRING, sectionString);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			TextView textView;
			textView = (TextView) rootView.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

			textView = (TextView) rootView.findViewById(R.id.section_string);
			textView.setText(getArguments().getString(ARG_SECTION_STRING));

			return rootView;
		}
	}

	/**
	 * @author Fma
	 */
	public static class WifiInfoFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_W_SSID = "wifi_ssid";
		//		private static final String ARG_W_IP = "wifi_ip";
		private static final String ARG_W_NETMASK = "wifi_netmask";
		//		private static final String ARG_W_PASS = "wifi_pass";
		private static final String ARG_W_GATEWAY = "wifi_gateway";
		private static final String ARG_W_DNS1 = "wifi_dns1";
		private static final String ARG_W_DNS2 = "wifi_dns2";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static WifiInfoFragment newInstance(NarWifiManager nWM) {
			WifiInfoFragment fragment = new WifiInfoFragment();
			Bundle args = new Bundle();

			args.putString(ARG_W_SSID, nWM.getSSID());
			args.putString(ARG_W_DNS1, nWM.getDNS1String());
			args.putString(ARG_W_DNS2, nWM.getDNS2String());
			args.putString(ARG_W_NETMASK, nWM.getNetmaskString());
			args.putString(ARG_W_GATEWAY, nWM.getGatewayString());
			fragment.setArguments(args);

			Log.i("WifiInfoFrag", args.toString());
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_wifi_info, container, false);

			TextView textView;
			EditText ssidET, passET;

			ssidET = (EditText) rootView.findViewById(R.id.wifi_ssid);
			ssidET.setText(getArguments().getString(ARG_W_SSID));

			// maybe add a password storage
			passET = (EditText) rootView.findViewById(R.id.wifi_pass);
			passET.requestFocus();
/*			passET.setText(getArguments().getString(ARG_W_PASS));*/

			textView = (TextView) rootView.findViewById(R.id.wifi_netmask);
			textView.setText(getArguments().getString(ARG_W_NETMASK));

			textView = (TextView) rootView.findViewById(R.id.wifi_gateway);
			textView.setText(getArguments().getString(ARG_W_GATEWAY));

			textView = (TextView) rootView.findViewById(R.id.wifi_dns1);
			textView.setText(getArguments().getString(ARG_W_DNS1));

			String dns2 = getArguments().getString(ARG_W_DNS2);
			textView = (TextView) rootView.findViewById(R.id.wifi_dns2);
			if (IPParser.StringToInt(dns2) != 0) {
				textView.setText(getArguments().getString(ARG_W_DNS2));
			} else {
				textView.setVisibility(TextView.INVISIBLE); // or GONE
			}

			return rootView;
		}
		/*
		IP Addr: 192.168.2.180
		Netmask: 255.255.255.0
		Gateway: 192.168.2.1
		DHCPsrv: 192.168.2.1
		DNSserv: 192.168.1.1
		*/
	}
}
