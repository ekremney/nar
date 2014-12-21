package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import net.narlab.projectnar.utils.OnFragmentInteractionListener;

import java.util.Date;

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
		String narId = ((TextView)findViewById(R.id.nar_id  )).getText().toString();

		if (narId.length() > 0) {
			view.setEnabled(false);
			ToastIt("Nar with id registered: "+narId);

			Intent resultIntent = new Intent();
			resultIntent.putExtra(EXT_NAR_ID, narId);
			resultIntent.putExtra(EXT_LASTALIVE, new Date().toString());
			setResult(Activity.RESULT_OK, resultIntent);
			view.setEnabled(true);
			finish();
			overridePendingTransition(R.anim.open_main, R.anim.close_next);
		} else {
			ToastIt("You should enter nar info or scan device qr code");
		}
	}


	// get result from qr scan and generate a new Nar object
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == DataHolder.REG_NAR_QR_REQ_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				String narId = intent.getStringExtra(QRScannerActivity.EXT_QR_RESULT_NAR_ID);

				Log.d("QrScanned", "id=" + narId);

				((TextView) findViewById(R.id.nar_id)).setText(narId);

			} else {
				ToastIt("QR result was not ok: " + resultCode, Toast.LENGTH_LONG);
			}
		}
	}

}
