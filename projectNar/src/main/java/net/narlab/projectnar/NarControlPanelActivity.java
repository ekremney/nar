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

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

import net.narlab.projectnar.general.Nar;
import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

public class NarControlPanelActivity extends Activity {

	public static final String EXT_NAR_DELETED = "NarCtrlPanelAct_nar_deleted";
	public static final String EXT_NAR_NAME_CHG = "NarCtrlPanelAct_nar_name_changed";
	public static final String EXT_NAR_ID = "NarCtrlPanelAct_nar_id";
	public static final String EXT_NAR_POSITION = "NarCtrlPanelAct_nar_position";
	private Nar mNar;

	private BootstrapButton onOffBtn;
	private BootstrapButton chgNameBtn;
	private BootstrapEditText narNameEditText;
	private Intent mResultIntent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nar_control_panel);

		Intent intent = getIntent();
		int position = intent.getIntExtra(EXT_NAR_POSITION, 0);
		mNar = DataHolder.getNarListAdapter().get(position);


		Helper.toastIt(""+mNar.getLastalive());
//		((TextView)findViewById(R.id.nar_ctrl_lastalive)).setText(mNar.getLastaliveS());

		((TextView)findViewById(R.id.nar_ctrl_nar_name)).setText(mNar.getName());

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
			String message = "1";

			new AsyncSendMessageToNar(mNar.getId(), message).execute();

		} else if (vId == R.id.nar_ctrl_btn_unregister) {
			// disable until a response comes
			v.setEnabled(false);
			Intent resultIntent = getResultIntent();
			resultIntent.putExtra(EXT_NAR_ID, mNar.getId());
			resultIntent.putExtra(EXT_NAR_DELETED, true);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
			overridePendingTransition(R.anim.open_main, R.anim.close_next);

		} else if (vId == R.id.nar_ctrl_btn_chg_name) {
			chgNameBtn.setEnabled(false);
			String name = narNameEditText.getText().toString();
			if (name.length() > 3 && name.length() < 32) {
				mNar.setName(name);
			}
			chgNameBtn.setEnabled(true);
		}
	}

	public Intent getResultIntent() {
		if (mResultIntent == null) {
			mResultIntent = new Intent();
		}
		return mResultIntent;
	}

	public class AsyncSendMessageToNar extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();
		private String narId;

		/**
		 * constructor
		 */
		public AsyncSendMessageToNar(String narId, String message) {
			this.narId = narId;
			mData.add(new BasicNameValuePair("nar_id", narId.replace(' ', '_')));
			mData.add(new BasicNameValuePair("message", message));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			String result = narId+"Sent message: "+mData.get(0).getValue();
			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://128.199.52.88:5000/");

			try {
				// Add your data
				httppost.setEntity(new UrlEncodedFormEntity(mData));

				// Execute HTTP Post Request
				//HttpResponse response =
				httpclient.execute(httppost);

			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
			return result;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			Log.i(getClass().getSimpleName()+"_res", result);
			onOffBtn.setEnabled(true);
		}
	}

}
