package net.narlab.projectnar.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import net.narlab.projectnar.general.NarAuthenticator;

/**
 * @author fma
 * @date 13.08.2014.
 */
public class NarAuthenticatorService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		NarAuthenticator authenticator = new NarAuthenticator(this);
		return authenticator.getIBinder();
	}
}