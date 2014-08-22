package net.narlab.projectnar.general;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import net.narlab.projectnar.AuthenticatorActivity;
import net.narlab.projectnar.utils.DataHolder;

import static net.narlab.projectnar.general.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static net.narlab.projectnar.general.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
import static net.narlab.projectnar.general.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY;
import static net.narlab.projectnar.general.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;

/**
 * @author fma
 * @since  13.08.2014.
 */
public class NarAuthenticator extends AbstractAccountAuthenticator {

	private final Context mContext;

	public NarAuthenticator(Context mContext) {
		super(mContext);

		this.mContext = mContext;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
		return null;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {


		// If the caller requested an authToken type we don't support, then
		// return an error
		if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
			return result;
		}
		// Extract the username and password from the Account Manager, and ask
		// the server for an appropriate AuthToken.
		final AccountManager am = AccountManager.get(mContext);
		String authToken = am.peekAuthToken(account, authTokenType);

		// Lets give another try to authenticate the user
		if (TextUtils.isEmpty(authToken)) {
			final String password = am.getPassword(account);
			if (password != null) {
				try {
					authToken = DataHolder.getConnMng().login(account.name, password, authTokenType);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// If we get an authToken - we return it
		if (!TextUtils.isEmpty(authToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
			return result;
		}
		// If we get here, then we couldn't access the user's password - so we
		// need to re-prompt them for their credentials. We do that by creating
		// an intent to display our AuthenticatorActivity.
		final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
			return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
		else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
			return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
		else
			return authTokenType + " (Label)";
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}
}
