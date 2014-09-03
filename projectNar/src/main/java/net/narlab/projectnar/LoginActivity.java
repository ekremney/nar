package net.narlab.projectnar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.FontAwesomeText;

import net.narlab.projectnar.general.AccountGeneral;
import net.narlab.projectnar.services.NarMQTTService;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * A register screen that offers register via email/password.

 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity extends Activity {

	public static final String ARG_PREVENT_AUTO_LOGIN = "LoginAct_ARG_PREVENT_AUTO_LOGIN";
	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	private static final String TAG = "LoginActivity";

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mServerHostnameView;

	private FontAwesomeText mProgressView;

	private View mLoginFormView;
	private View rootView;

	private int rootViewHeight;
	private AccountManager accMng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();

		// set this for toasts etc
		Helper.setContext(getApplicationContext());

		Log.v(TAG, "Account List\n=====");
		accMng = AccountManager.get(getApplicationContext());
		for(Account acc : accMng.getAccountsByType(AccountGeneral.ACCOUNT_TYPE)) {
			Log.v(TAG, acc.toString());
		}
		Log.v(TAG, ".\n=====");

		String mDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

		Log.i(TAG, "Device ID: "+mDeviceID);
		Intent mServiceIntent = new Intent(this, NarMQTTService.class);
		mServiceIntent.setData(Uri.parse(mDeviceID));

		// Starts the IntentService
		startService(mServiceIntent);

/*
		SharedPreferences.Editor editor = getSharedPreferences(NarMQTTService.TAG, MODE_PRIVATE).edit();
		editor.putString(NarMQTTService.PREF_DEVICE_ID, mDeviceID);
		editor.commit();

		NarMQTTService.actionStart(getApplicationContext());
*/
		// Set up the register form.
		mEmailView = (EditText) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mServerHostnameView = (EditText) findViewById(R.id.server_hostname);
		BootstrapButton loginBtn = (BootstrapButton) findViewById(R.id.nar_user_btn_login);
		BootstrapButton registerBtn = (BootstrapButton) findViewById(R.id.nar_user_btn_register);

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = (FontAwesomeText)findViewById(R.id.login_progress);

		rootView = findViewById(R.id.login_root_view);

		SharedPreferences mPreferences = Helper.getSharedPreferences();
		mPreferences.registerOnSharedPreferenceChangeListener(
				new SharedPreferences.OnSharedPreferenceChangeListener() {
					@Override
					public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
						if (s.equals(DataHolder.PREF_NAME_SERVER_URL)) {
							DataHolder.setServerHostname(sharedPreferences.getString(s, ""));
						}
					}
				}
		);

		String serverHostname = Helper.getSharedPreferences().getString(DataHolder.PREF_NAME_SERVER_URL, "");
		if (serverHostname.length() == 0) {
			serverHostname = DataHolder.getServerHostname();
			Helper.editSharedPreferences(DataHolder.PREF_NAME_SERVER_URL, serverHostname);
		}
		DataHolder.setServerHostname(serverHostname);
		mServerHostnameView.setText(serverHostname);

		// try it last so everything will be set
		if (getIntent() != null && !getIntent().getBooleanExtra(ARG_PREVENT_AUTO_LOGIN, false)) {
			fillUserCredentials();
		}

		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		registerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(LoginActivity.this, AuthenticatorActivity.class);
				intent.putExtra(AuthenticatorActivity.ARG_IS_FROM_LOGIN_ACT, true);

				String accountName = mEmailView.getText().toString();
				if (!isEmailValid(accountName)) {
					accountName = null;
				}

				intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, accountName);
				startActivityForResult(intent, DataHolder.AUTH_USER_REQ_CODE);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}
		});


		// remove title if soft keyboard open
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

				if (heightDiff >  100) { // if more than 100 pixels, its probably a keyboard...
					findViewById(R.id.login_title).setVisibility(View.GONE);
				}
				if (heightDiff < -100) {
					findViewById(R.id.login_title).setVisibility(View.VISIBLE);
				}
			}
		});

	}

	public void onChangeUrlClicked(View v) {
		String serverHostname = mServerHostnameView.getText().toString();
		Helper.editSharedPreferences(DataHolder.PREF_NAME_SERVER_URL, serverHostname);
		DataHolder.setServerHostname(serverHostname);
		Helper.toastIt("New server Url:\n"+serverHostname);
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			try {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == DataHolder.AUTH_USER_REQ_CODE) {
			if (resultCode == RESULT_OK) {
				fillUserCredentials();
			} else {
				Helper.toastIt("Couldn't register user");
			}
		}
	}

	private void fillUserCredentials() {
		Account accList[] = accMng.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
		final Account acc;
		if (accList.length == 1) {
			acc = accList[0];
			// TODO: change auto login to not send password
			mEmailView.setText(acc.name);
			mPasswordView.setText(accMng.getPassword(acc));
/*			NotificationCompat.Builder mBuilder =
					new NotificationCompat.Builder(this)
							.setSmallIcon(R.drawable.nar_notif_icon)
							.setContentTitle("Narlab")
							.setContentText("User found: "+acc.name);
			// Sets an ID for the notification
			int mNotificationId = 333;
			// Gets an instance of the NotificationManager service
			NotificationManager mNotifyMgr =
					(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			// Builds the notification and issues it.
			mNotifyMgr.notify(mNotificationId, mBuilder.build());
			//			accMng.getAuthToken(acc, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, this, null, null);*/
			attemptLogin();
		}
	}

		/**
		 * Attempts to sign in or register the account specified by the register form.
		 * If there are form errors (invalid email, missing fields, etc.), the
		 * errors are presented and no actual register attempt is made.
		 */
	public void attemptLogin() {
		NarWifiManager wifiManager;
		wifiManager = DataHolder.getWifiManager();

		if (!wifiManager.isOnline()) {
			Helper.toastIt(R.string.internet_disconnected, Toast.LENGTH_LONG);
		}

		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the register attempt.
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;


		// Check for a valid password, if the user entered one.
		int passVal = isPasswordValid(password);
		if (passVal > 0) {
			focusView = mPasswordView;
			cancel = true;
			switch (isPasswordValid(password)) {
				case 1: mPasswordView.setError(getString(R.string.error_password_short)); break;
				case 2: mPasswordView.setError(getString(R.string.error_password_invalid)); break;
			}
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user register attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask(email, password);
			mAuthTask.execute((Void) null);
		}
	}
	private boolean isEmailValid(String email) {
		return email.toLowerCase().matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}(\\..+)*$");
	}

	private int isPasswordValid(String password) {
		if (TextUtils.isEmpty(password) || password.length() <6) {
			return 1; // short password error
		}
		// for now just check for short pass
/*		else if (!password.matches("[a-zA-Z]+]") || !password.matches("[0-9]") ) {
			return 2;
		}*/
		return 0;
	}

	/**
	 * Shows the progress UI and hides the register form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			if (show) {
				InputMethodManager imm = (InputMethodManager) getSystemService(
						INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);
			}

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			((LinearLayout)rootView).setGravity(show ? Gravity.CENTER : Gravity.TOP);

			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.startRotate(true, FontAwesomeText.AnimationSpeed.SLOW);
			mProgressView.animate().setDuration(shortAnimTime).alpha(
					show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class UserLoginTask extends AsyncTask<Void, Void, String> {

		private final String email;
		private final String password;
		private final String LOGIN_URL = DataHolder.getServerUrl()+"/android/login";

		private String toastMsg; // message for toast in postExecute
		private Exception e;

		UserLoginTask(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		protected String doInBackground(Void... params) {
			// attempt authentication against a network service.

			byte[] resEnt;
			String resStr;
			HttpClient client = DataHolder.getHttpClient();
			HttpPost post = new HttpPost(LOGIN_URL);

			try {
				// set up post data
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("username", email));
				nameValuePair.add(new BasicNameValuePair("password", password));

				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));

				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();

				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					resEnt = EntityUtils.toByteArray(response.getEntity());
					resStr = new String(resEnt, "UTF-8");
//					Log.w("Login", resStr);
					return resStr;
				}
			} // separate so we can do custom stuff
			catch (IOException e) {
				this.e = e;
				Helper.getExceptionString(e);
				return null;
			}

			return null;
		}

		@Override
		protected void onPostExecute(final String resStr) {
			boolean success = false;

			try {
				Object json = new JSONTokener(resStr).nextValue();

				if (json instanceof JSONArray) {
					DataHolder.getNarList().clear();
					DataHolder.addNars((JSONArray)json);
					success = true;
				} else if (json instanceof JSONObject) {
					toastMsg = ((JSONObject)json).optString("error"
							,"no error message sent");
//					Log.e(TAG, toastMsg);
					Helper.toastIt(toastMsg);
				} else {
					// there was an error with server
//					Log.e(TAG, "Unknown error: "+json);
					Helper.toastIt("There was a server error try again later");
				}
			} catch (Exception e) {
				Log.e(Helper.getTag(this), Helper.getExceptionString(e));
//				e.printStackTrace();
			}

			mAuthTask = null;

			if (success) {
				// start application and kill login activity
				startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				finish();
			} else {
				showProgress(false);
				if (toastMsg != null) {
					Helper.toastIt(toastMsg);
				} else if (this.e == null) {
					mEmailView.setError(getString(R.string.error_incorrect_info));
					mPasswordView.setError(getString(R.string.error_incorrect_info));
					mEmailView.requestFocus();
				} else if (e instanceof IOException) {
					Helper.toastIt(R.string.server_unreachable);
				}
			}
			this.e = null;
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
