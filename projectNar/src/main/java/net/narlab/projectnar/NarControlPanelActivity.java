package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import net.narlab.projectnar.general.Nar;
import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;
import net.narlab.projectnar.utils.NarWifiManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class NarControlPanelActivity extends Activity {

	public static final String EXT_NAR_DELETED = "NarCtrlPanelAct_nar_deleted";
	public static final String EXT_NAR_NAME_CHG = "NarCtrlPanelAct_nar_name_changed";
	public static final String EXT_NAR_ID = "NarCtrlPanelAct_nar_id";
	public static final String EXT_NAR_POSITION = "NarCtrlPanelAct_nar_position";
	private Nar mNar;

	private BootstrapButton unregisterBtn, onOffBtn, chgNameBtn;
	private BootstrapEditText narNameEditText;
	private Intent mResultIntent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nar_control_panel);

		Intent intent = getIntent();
		int position = intent.getIntExtra(EXT_NAR_POSITION, 0);
		mNar = DataHolder.getNarList().get(position);


		Helper.toastIt(""+mNar.getLastalive());
		((TextView)findViewById(R.id.nar_ctrl_lastalive)).setText(mNar.getLastaliveS());

		((TextView)findViewById(R.id.nar_ctrl_nar_name)).setText(mNar.getName());

		unregisterBtn = (BootstrapButton) findViewById(R.id.nar_ctrl_btn_unregister);
		onOffBtn = (BootstrapButton) findViewById(R.id.nar_ctrl_btn_on_off);
		chgNameBtn = (BootstrapButton) findViewById(R.id.nar_ctrl_btn_chg_name);
		narNameEditText = (BootstrapEditText) findViewById(R.id.nar_ctrl_nar_name);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.nar_control_panel, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id==0) {
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		Intent resultIntent = getResultIntent();
		setResult(RESULT_OK, resultIntent);
		finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void onButtonClicked(View v) {
		int vId = v.getId();
		if (vId == R.id.nar_ctrl_btn_on_off) {
			onOffBtn.setEnabled(false);
			String state = onOffBtn.getText().toString();
			String message;

			// send reverse of current
			if ( state.equals(getString(R.string.nar_ctrl_btn_on)) ) {
				message = "off";
			} else {
				message = "on";
			}
			new AsyncSendMessageToNar(mNar.getId(), message).execute();

		} else if (vId == R.id.nar_ctrl_btn_unregister) {
			// disable until a response comes
			v.setEnabled(false);
			new AsyncUnregisterNarTask(mNar.getId()).execute();

		} else if (vId == R.id.nar_ctrl_btn_smartcfg) {
			NarWifiManager nWM = DataHolder.getWifiManager();
			if (!nWM.isWifiConnected()) {
				Helper.toastIt("Please connect to the same wifi you are setting up", Toast.LENGTH_LONG);
				return;
			}

			Intent intent = new Intent(this, SmartConfigActivity.class);
			intent.putExtra(SmartConfigActivity.EXT_NAR_ID, mNar.getId());
			intent.putExtra(SmartConfigActivity.EXT_WIFI_SSID, nWM.getSSID());
			intent.putExtra(SmartConfigActivity.EXT_GATEWAY, nWM.getGatewayString());
			startActivity(intent);
			overridePendingTransition (R.anim.open_next, R.anim.close_main);
		} else if (vId == R.id.nar_ctrl_btn_chg_name) {
			chgNameBtn.setEnabled(false);
			String name = narNameEditText.getText().toString();
			if (name.length() > 0) {
				new AsyncChangeNameTask(mNar.getId(), name).execute();
			}
		}
	}

	public Intent getResultIntent() {
		if (mResultIntent == null) {
			mResultIntent = new Intent();
		}
		return mResultIntent;
	}

	// used for unregistering nar from this account
	public class AsyncUnregisterNarTask extends AsyncTask<Void, Void, String> {
		private static final String TAG = "AsyncUnregisterNarTask";
		private String narId;

		/**
		 * constructor
		 */
		public AsyncUnregisterNarTask(String narId) {
			this.narId = narId;
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			byte[] result;
			String str = "";
			HttpClient client = DataHolder.getHttpClient();
			String url = DataHolder.getServerUrl()+"/android/unregister_nar/"+narId;
			HttpPost post = new HttpPost(url);

			try {

				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			}
			catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("logged_out")) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}

			unregisterBtn.setEnabled(true);

			JSONObject json;
			Log.i(TAG + "_res", result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);

				if (err == null) {
					String message = json.optString("message", null);
					if (message == null) {
						return;
					}
					Log.i(TAG+"_message", message);

					Intent resultIntent = getResultIntent();
					resultIntent.putExtra(EXT_NAR_ID, narId);
					resultIntent.putExtra(EXT_NAR_DELETED, true);
					setResult(Activity.RESULT_OK, resultIntent);
					NarControlPanelActivity.this.finish();
					overridePendingTransition(R.anim.open_main, R.anim.close_next);

				} else {
					Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
					if (err.equals("Nar is not registered")) {
						Intent resultIntent = getResultIntent();
						resultIntent.putExtra(EXT_NAR_ID, narId);
						resultIntent.putExtra(EXT_NAR_DELETED, true);
						setResult(Activity.RESULT_OK, resultIntent);
						NarControlPanelActivity.this.finish();
						overridePendingTransition(R.anim.open_main, R.anim.close_next);
					}
				}
			} catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}

		}
	}

	public class AsyncSendMessageToNar extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();
		private String narId;

		/**
		 * constructor
		 */
		public AsyncSendMessageToNar(String narId, String message) {
			this.narId = narId;
			mData.add(new BasicNameValuePair("message", message));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			byte[] result;
			String str = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/android/message/"+narId);

			try {
				// set up post data
				post.setEntity(new UrlEncodedFormEntity(mData, "UTF-8"));

				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			}
			catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("logged_out")) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}

			onOffBtn.setEnabled(true);

			JSONObject json;
//			Log.i(TAG+"_res", result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);
//				Log.e(TAG+"_err", ""+err);
				if (err == null) {
					String reply = json.optString("reply", null);
					Boolean newState = json.optBoolean("state", !mNar.getState());
					if (reply == null) {
						reply = "No reply";
					}
					Helper.toastIt(reply);

					mNar.setState(newState);
					if (newState) {
						onOffBtn.setBootstrapType("success");
						onOffBtn.setRightIcon(getString(R.string.nar_ctrl_btn_on_icon));
						onOffBtn.setText(getString(R.string.nar_ctrl_btn_on));
					} else {
						onOffBtn.setBootstrapType("danger");
						onOffBtn.setRightIcon(getString(R.string.nar_ctrl_btn_off_icon));
						onOffBtn.setText(getString(R.string.nar_ctrl_btn_off));
					}

				} else {
					Helper.toastIt(err, Toast.LENGTH_LONG);
				}
			} catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}

		}
	}

	public class AsyncChangeNameTask extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();
		private String narId, newNarName;

		/**
		 * constructor
		 */
		public AsyncChangeNameTask(String narId, String newNarName) {
			this.narId = narId;
			this.newNarName = newNarName;
			mData.add(new BasicNameValuePair("name", newNarName));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			byte[] result;
			String str = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/android/change_name/"+narId);

			try {
				// set up post data
				post.setEntity(new UrlEncodedFormEntity(mData, "UTF-8"));

				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			}
			catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			if (result.equals("logged_out")) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}

			chgNameBtn.setEnabled(true);

//			Log.w(TAG, result);
			JSONObject json;
//			Log.i(TAG+"_res", result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);
//				Log.e(TAG+"_err", ""+err);
				if (err == null) {
					String message = json.optString("message", null);
					if (json.optBoolean("status", false)) {
						getResultIntent().putExtra(EXT_NAR_NAME_CHG, true);
					}
					if (message== null) {
						message = "No Message";
					}
					Helper.toastIt(message);

					narNameEditText.setText(newNarName);
					mNar.setName(newNarName);
				} else {
					Helper.toastIt("Error: "+err, Toast.LENGTH_LONG);
				}
			} catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
			}

		}
	}
}
