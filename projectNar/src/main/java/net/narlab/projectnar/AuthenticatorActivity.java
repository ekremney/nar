package net.narlab.projectnar;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import net.narlab.projectnar.general.AccountGeneral;


public class AuthenticatorActivity  extends AccountAuthenticatorActivity {

	public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
	public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
	public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
	public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
	private static final int REQ_SIGNUP = 0xf123;


	private AccountManager mAccountManager;
	private  String mAuthTokenType;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

		mAccountManager = AccountManager.get(getBaseContext());
		String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
		mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
		if (mAuthTokenType == null)
			mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
		if (accountName != null) {
			((TextView)findViewById(R.id.auth_username)).setText(accountName);
		}
/*		findViewById(R.id.auth_btn_reg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});*/
/*		findViewById(R.id.auth_btn_reg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
				// and return them in setAccountAuthenticatorResult(). See finishLogin().
				Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
				signup.putExtras(getIntent().getExtras());
				startActivityForResult(signup, REQ_SIGNUP);
			}
		});
*/
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
