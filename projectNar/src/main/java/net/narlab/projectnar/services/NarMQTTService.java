package net.narlab.projectnar.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import net.narlab.projectnar.R;

/**
 * Created by fma on 17.07.2014.
 */
public class NarMQTTService extends Service{
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, final int startId)
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
//				handleStart(intent, startId);
			}
		}, "MQTTservice").start();

		// return START_NOT_STICKY - we want this Service to be left running
		//  unless explicitly stopped, and it's process is killed, we want it to
		//  be restarted
		return START_STICKY;
	}



}
