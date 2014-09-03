package net.narlab.projectnar;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import net.narlab.projectnar.general.AccountGeneral;
import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;

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


public class AuthenticatorActivity  extends AccountAuthenticatorActivity {

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
	public final static String ARG_IS_FROM_LOGIN_ACT = "IS_FROM_LOGIN_ACT";

	private AccountManager mAccountManager;
	private  String mAuthTokenType;
	private BootstrapButton registerBtn;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

		mAccountManager = AccountManager.get(getBaseContext());

		// set helper classes' context
		Helper.setContext(getApplicationContext());

		// register only one account
		Account[] accList = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
		if (accList.length == 1) {
			Helper.toastIt("A User is already registered: "+accList[0].name);
			finishOK();
		}

		// if there is no account register it
		mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
		if (mAuthTokenType == null) {
			mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
		}

		String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
		if (accountName != null) {
			((TextView)findViewById(R.id.auth_username)).setText(accountName);
		}

		registerBtn = (BootstrapButton) findViewById(R.id.auth_btn_reg);
		registerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// disable to prevent multi request
				registerBtn.setEnabled(false);

				new AsyncRegisterUser().execute();
			}
		});

	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.authenticator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
			Log.v(Helper.getTag(this), "Clicked: "+id);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED);
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public void finishOK() {
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.open_main, R.anim.close_next);
	}

	public class AsyncRegisterUser extends AsyncTask<Void, Void, String> {
		private ArrayList<NameValuePair> mData = new ArrayList<NameValuePair>();
		private String email, pass;

		/**
		 * constructor
		 */
		public AsyncRegisterUser() {
			email = ((EditText) findViewById(R.id.auth_username)).getText().toString();
			pass = ((EditText) findViewById(R.id.auth_password)).getText().toString();

			mData.add(new BasicNameValuePair("username", email));
			mData.add(new BasicNameValuePair("password", pass));
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(Void... voids) {
			byte[] result;
			String str = "";
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(DataHolder.getServerUrl()+"/android/register_user");

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
				Log.e(this.getClass().getSimpleName(), Helper.getExceptionString(e));
				return null;
//				e.printStackTrace();
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			registerBtn.setEnabled(true);

			if (result == null) {
				Helper.toastIt("Couldn't authenticate user try again later");
				return;
			}

			JSONObject json;
//			Log.i(Helper.getTag(this) + "_res", result);
//			Helper.toastIt(result);
			try {
				json = new JSONObject(result);
				String err = json.optString("error", null);
//				Log.e(Helper.getTag(this)+"_err", ""+err);
				if (err == null) { // it was a success
					Helper.toastIt(json.optString("message")+": "+json.optString("email"));

					// add account
					final Account account = new Account(email, AccountGeneral.ACCOUNT_TYPE);

					Helper.toastIt("Auth: "+json.getString("auth_token"));

					mAccountManager.addAccountExplicitly(account, pass, null);
					mAccountManager.setAuthToken(account,
							mAuthTokenType,
							json.getString("auth_token"));

					finishOK();

				} else {
					Helper.toastIt(err, Toast.LENGTH_LONG);
				}
			} catch (Exception e) {
				Log.e(this.getClass().getSimpleName(), Helper.getExceptionString(e));
//				e.printStackTrace();
			}

		}
	}
}
