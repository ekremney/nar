package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.narlab.projectnar.adapters.NarListAdapter;
import net.narlab.projectnar.services.NarMQTTService;
import net.narlab.projectnar.utils.DataHolder;
import net.narlab.projectnar.utils.Helper;

public class HomeActivity extends ActionBarActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
//	SectionsPagerAdapter mSectionsPagerAdapter;

	//DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
	NarListAdapter narListAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
//	ViewPager mViewPager;

	View lastView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_device_list);
		Helper.setContext(getApplicationContext());

		final ListView listview = (ListView) findViewById(R.id.listView);


		String mDeviceID = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
		Log.i(getClass().getSimpleName(), "Device ID: "+mDeviceID);
		Intent mServiceIntent = new Intent(this, NarMQTTService.class);
		mServiceIntent.setData(Uri.parse(mDeviceID));
		// Starts the IntentService
		startService(mServiceIntent);

		// 1. pass context and data to the custom adapter
		// get data from global nar list
		narListAdapter = DataHolder.getNarListAdapter();
		listview.setAdapter(narListAdapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
									int position, long id) {
				lastView = view;

				Intent intent = new Intent(HomeActivity.this, NarControlPanelActivity.class);
				intent.putExtra(NarControlPanelActivity.EXT_NAR_POSITION, position);
				startActivityForResult(intent, DataHolder.NAR_CTRL_PANEL_REQ);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
			}

		});

		String narListStr = Helper.getNarListFromPrefs();
		Log.i("NarList", narListStr);
		DataHolder.addNars(narListStr);
/*
		setContentView(R.layout.activity_home);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Log.d("SettingsMenu", "" + item.getItemId());
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_empty) {
			Log.d("SettingsMenu", "Empty Action yey");
			return true;
		} else if (id == R.id.action_exit) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

/*	disable for now
*/
	private static long back_pressed = 0;

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
			finish();
		} else {
			Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
		}
		back_pressed = System.currentTimeMillis();
	}


	/**
	 * Called when the user clicks a button-used for generic button click events-
	 */
	public void onButtonClicked(View view) {
		switch (view.getId()) {
			case R.id.home_button_add_new:
				Intent intent = new Intent(this, RegisterNarActivity.class);
				startActivityForResult(intent, DataHolder.REG_NAR_REQ_CODE);
				overridePendingTransition (R.anim.open_next, R.anim.close_main);
				break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// get result of nar registration
		if (requestCode == DataHolder.REG_NAR_REQ_CODE) {

			if (resultCode == Activity.RESULT_OK) {

				String narId = intent.getStringExtra(RegisterNarActivity.EXT_NAR_ID);
				String lastalive = intent.getStringExtra(RegisterNarActivity.EXT_LASTALIVE);
				// for new nars narName is always narId
				narListAdapter.add(narId, narId, lastalive);
			} else if (resultCode == Activity.RESULT_CANCELED) {

				Helper.toastIt("Canceled");
			} else {

				Helper.toastIt("Add nar failed with result code: " + resultCode);
			}

		// get result of nar panel (for delete etc)
		} else if (requestCode == DataHolder.NAR_CTRL_PANEL_REQ) {

			if (resultCode == Activity.RESULT_OK) {

				boolean isDeleted, isNameChg;
				isDeleted = intent.getBooleanExtra(NarControlPanelActivity.EXT_NAR_DELETED, false);
				isNameChg = intent.getBooleanExtra(NarControlPanelActivity.EXT_NAR_NAME_CHG, false);

				if (isDeleted) {

					final String narId = intent.getStringExtra(NarControlPanelActivity.EXT_NAR_ID);
					Helper.toastIt("Nar with id deleted: "+narId, Toast.LENGTH_LONG);
					final View view = lastView;
//					lastView.setEnabled(false);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						view.animate().setDuration(1000).alpha(0)
								.withEndAction(new Runnable() {
									@Override
									public void run() {
										narListAdapter.remove(narId);
										for(int i=0;i<narListAdapter.getCount();i++)
										{
											System.out.println("Adapter Count:"+narListAdapter.getCount()+" "+narListAdapter.get(i));

										}
										view.setAlpha(1);
									}
								});
					} else {
						narListAdapter.remove(narId);
					}
				}
				if (isNameChg) {
					narListAdapter.notifyDataSetChanged();
				}
			} else {
				Log.w(Helper.getTag(this), "Returned with result code: "+resultCode);
			}
		}
	}

}
