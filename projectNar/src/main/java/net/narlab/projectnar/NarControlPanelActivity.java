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

import net.narlab.projectnar.utils.DataHolder;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class NarControlPanelActivity extends Activity {

	public static final String EXT_NAR_DELETED = "NarCtrlPanelAct_nar_deleted";
	public static final String EXT_NAR_ID = "NarCtrlPanelAct_nar_id";
	public static final String EXT_NAR_POSITION = "NarCtrlPanelAct_nar_position";
	String narId;
	private Nar nar;

	// keep them in case we need to cancel
	private AsyncUnregisterNar unregisterTask;
	private AsyncSendMessageToNar sendMessageTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nar_control_panel);

		Intent intent = getIntent();
		narId = intent.getStringExtra(EXT_NAR_ID);
		String narId = intent.getStringExtra(EXT_NAR_ID);
		int position = intent.getIntExtra(EXT_NAR_POSITION, 0);

		nar = DataHolder.getNarList().get(position);
		((TextView)findViewById(R.id.nar_ctrl_lastalive)).setText(nar.getLastaliveS());

		((TextView)findViewById(R.id.nar_ctrl_nar_title)).setText(narId);
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
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void onButtonClicked(View v) {
		int vId = v.getId();
		if (vId == R.id.nar_ctrl_on_off) {
			v.setEnabled(false);
			String state = ((BootstrapButton)v).getText();

			String topic;
			if ( state.equals(getString(R.string.nar_ctrl_btn_on)) ) {
				topic = "Yey On";
				nar.setState(true);
			} else {
				topic = "Pff Off";
				nar.setState(false);
			}
			sendMessageTask = new AsyncSendMessageToNar(narId, topic, state);
			sendMessageTask.execute();

		} else if (vId == R.id.nar_ctrl_unregister) {
			// disable until a response comes
			v.setEnabled(false);
			unregisterTask = new AsyncUnregisterNar(narId);
			unregisterTask.execute();

/*			narConnMng.unregister(nar.getId());
			Intent resultIntent = new Intent();
			resultIntent.putExtra(EXT_NAR_ID, narId);
			resultIntent.putExtra(EXT_NAR_DELETED, true);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();*/
		}
	}

	// used for unregistering nar from this account
	public class AsyncUnregisterNar extends AsyncTask<Void, Void, String> {
		private static final String TAG = "AsyncUnregisterNar";
		private String narId;

		/**
		 * constructor
		 */
		public AsyncUnregisterNar(String narId) {
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
				e.printStackTrace();
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

			findViewById(R.id.nar_ctrl_unregister).setEnabled(true);

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

					Intent resultIntent = new Intent();
					resultIntent.putExtra(EXT_NAR_ID, narId);
					resultIntent.putExtra(EXT_NAR_DELETED, true);
					setResult(Activity.RESULT_OK, resultIntent);
					NarControlPanelActivity.this.finish();
					overridePendingTransition(R.anim.open_main, R.anim.close_next);

				} else {
					Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
					if (err.equals("Nar is not registered")) {
						Intent resultIntent = new Intent();
						resultIntent.putExtra(EXT_NAR_ID, narId);
						resultIntent.putExtra(EXT_NAR_DELETED, true);
						setResult(Activity.RESULT_OK, resultIntent);
						NarControlPanelActivity.this.finish();
						overridePendingTransition(R.anim.open_main, R.anim.close_next);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public class AsyncSendMessageToNar extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();
		private static final String TAG = "AsyncAddActiveNar";
		private String narId;

		/**
		 * constructor
		 */
		public AsyncSendMessageToNar(String narId, String topic, String message) {
			// Parse Data from qr string and add to post data

			this.narId = narId;
			mData.add(new BasicNameValuePair("topic", topic));
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
				e.printStackTrace();
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

			findViewById(R.id.nar_ctrl_on_off).setEnabled(true);

			JSONObject json;
//			Log.i(TAG+"_res", result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);
//				Log.e(TAG+"_err", ""+err);
				if (err == null) {
					String reply = json.optString("reply", null);
					Boolean newState = json.optBoolean("state", !nar.getState());
					if (reply == null) {
						reply = "No reply";
					}
					// TODO: update view and values according to reply
					Toast.makeText(getApplicationContext(), reply, Toast.LENGTH_SHORT).show();

					BootstrapButton onOffBtn = (BootstrapButton) findViewById(R.id.nar_ctrl_on_off);
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
					Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
}