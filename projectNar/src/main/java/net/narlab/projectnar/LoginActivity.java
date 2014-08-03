package net.narlab.projectnar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

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

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * A register screen that offers register via email/password.

 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor>{

	/**
	 * Keep track of the register task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setupActionBar();

		// Set up the register form.
		mEmailView = (EditText) findViewById(R.id.email);
		populateAutoComplete();

		mPasswordView = (EditText) findViewById(R.id.password);
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

		BootstrapButton mEmailSignInButton = (BootstrapButton) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	private void populateAutoComplete() {
		if (VERSION.SDK_INT >= 14) {
			// Use ContactsContract.Profile (API 14+)
			getLoaderManager().initLoader(0, null, this);
		} else if (VERSION.SDK_INT >= 8) {
			// Use AccountManager (API 8+)
			new SetupEmailAutoCompleteTask().execute(null, null);
		}
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

	/**
	 * Attempts to sign in or register the account specified by the register form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual register attempt is made.
	 */
	public void attemptLogin() {
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
		Log.w("LoginVals", email+":"+password);
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
		return email.toLowerCase().matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");
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

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(
					show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE +
						" = ?", new String[]{ContactsContract.CommonDataKinds.Email
																	 .CONTENT_ITEM_TYPE},

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}

		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = {
				ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
		};

		int ADDRESS = 0;
//		int IS_PRIMARY = 1;
	}

	/**
	 * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
	 * the email text field with results on the main UI thread.
	 */
	class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... voids) {
			ArrayList<String> emailAddressCollection = new ArrayList<String>();

			// Get all emails from the user's contacts and copy them to a list.
			ContentResolver cr = getContentResolver();
			Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					null, null, null);
			while (emailCur.moveToNext()) {
				String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
						.CommonDataKinds.Email.DATA));
				emailAddressCollection.add(email);
			}
			emailCur.close();

			return emailAddressCollection;
		}

		@Override
		protected void onPostExecute(List<String> emailAddressCollection) {
		   addEmailsToAutoComplete(emailAddressCollection);
		}
	}

	/** TODO: add bootstrap AutoComplete */
	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		//Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
/*		ArrayAdapter<String> adapter =
				new ArrayAdapter<String>(LoginActivity.this,
						android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
*/
//		mEmailView.setAdapter(adapter);
	}

	/**
	 * Represents an asynchronous register/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String email;
		private final String password;
		private final String LOGIN_URL = DataHolder.getServerUrl()+"/android/login";

		UserLoginTask(String email, String password) {
			this.email = email;
			this.password = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (DataHolder.LOGIN_TEST) {
				return true;
			}
			// attempt authentication against a network service.

			byte[] result;
			boolean loggedIn = false;
			String str;
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
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
					Log.w("Login", str);

					if (str.equals("logged_in")) {
						loggedIn = true;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

/*			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(email)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(password);
				}
			}*/

			return loggedIn;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				// start application and kill login activity
//				Intent regactivity = new Intent(LoginActivity.this, HomeActivity.class);
				startActivity(new Intent(LoginActivity.this, HomeActivity.class));
				finish();
			} else {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
