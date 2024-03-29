package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;
import net.narlab.projectnar.utils.OnFragmentInteractionListener;

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

public class RegisterNarActivity extends FragmentActivity implements OnFragmentInteractionListener {

	public static final String EXT_NAR_ID = "RegNar_ext_nar_id";
	public static final String EXT_LASTALIVE = "RegNar_ext_lastalive";
	private int rootViewHeight;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_nar);

		Fragment fragment = new NewNarInfoFragment();

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.replace(R.id.new_nar_progress, fragment);
		transaction.commit();

		final View rootView = findViewById(R.id.new_nar_progress);
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int heightDiff;
				heightDiff = rootViewHeight - rootView.getHeight();
				// if this is first run don't change anything since onCreate fires this as well
				if (rootViewHeight == 0) {
					rootViewHeight = rootView.getHeight();
					return;
				}
				rootViewHeight = rootView.getHeight();

//				Helper.toastIt("diff="+heightDiff+"=>" + "w:" + rootView.getWidth() + " h:" + rootView.getHeight()+);
				if (heightDiff >  100) { // if more than 100 pixels, its probably a keyboard...
					findViewById(R.id.new_nar_title).setVisibility(View.GONE);
				}
				if (heightDiff < -100) {
					findViewById(R.id.new_nar_title).setVisibility(View.VISIBLE);
				}
			}
		});
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	private void ToastIt(String s, int len) {
		Toast.makeText(getApplicationContext(), s, len).show();
	}
	private void ToastIt(String s) {
		ToastIt(s, Toast.LENGTH_SHORT);
	}

	public void onScanQRCodeBtnClick(View view) {
		Intent intent = new Intent(this, QRScannerActivity.class);
		startActivityForResult(intent, DataHolder.REG_NAR_QR_REQ_CODE);
	}

	public void onAddNewNar(View view) {
		String narId   = ((TextView)findViewById(R.id.nar_id  )).getText().toString();
		String narPass = ((TextView)findViewById(R.id.nar_pass)).getText().toString();

		if (narId.length() > 0 && narPass.length() > 0) {
			view.setEnabled(false);
			new AsyncAddActivateNar(narId, narPass).execute();
		} else {
			ToastIt("You should enter nar info or scan device qr code");
		}
	}


	// get result from qr scan and generate a new Nar object
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == DataHolder.REG_NAR_QR_REQ_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String narId = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT_NAR_ID);
				String narPass = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT_NAR_PASS);

				Log.d("Test", "id=" + narId + "__pass=" + narPass);

				((TextView) findViewById(R.id.nar_id)).setText(narId);
				((TextView) findViewById(R.id.nar_pass)).setText(narPass);

			} else {
				ToastIt("QR result was not ok: " + resultCode, Toast.LENGTH_LONG);
			}
		}
	}

	public class AsyncAddActivateNar extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();

		/**
		 * constructor
		 */
		public AsyncAddActivateNar(String narId, String narPass) {
			// Parse Data from qr string and add to post data

			mData.add(new BasicNameValuePair("nar_id", narId));
			mData.add(new BasicNameValuePair("nar_pass", narPass));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			byte[] result;
			String str = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/android/register_nar");

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

			findViewById(R.id.new_nar_btn_add).setEnabled(true);

			JSONObject json;
//			Log.i(TAG+"_res", result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);
//				Log.e(TAG+"_err", ""+err);
				if (err == null) {
					String narId = json.optString("nar_id", null);
					String lastalive = json.optString("lastalive", null);
					if (narId == null || lastalive == null) {
						return;
					}
//					Log.i(Helper.getTag(this), "Registered nar: " + narId + "|" + lastalive);

					ToastIt("Nar with id registered: "+narId);

					Intent resultIntent = new Intent();
					resultIntent.putExtra(EXT_NAR_ID, narId);
					resultIntent.putExtra(EXT_LASTALIVE, lastalive);
					setResult(Activity.RESULT_OK, resultIntent);
					RegisterNarActivity.this.finish();
					overridePendingTransition(R.anim.open_main, R.anim.close_next);

				} else {
					ToastIt(err);
				}
			} catch (Exception e) {
				Helper.getExceptionString(e);
			}

		}
	}

}
