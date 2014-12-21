package net.narlab.projectnar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.Arrays;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class QRScannerActivity extends ActionBarActivity implements ZBarScannerView.ResultHandler {
    public final static String EXT_QR_RESULT_NAR_ID = "QRScanner_qr_result_nar_id";
	private final static String TAG = "QRScannerActivity";
	private ZBarScannerView mScannerView;

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		mScannerView = new ZBarScannerView(this);

		// focus just on QR codes
		mScannerView.setFormats( Arrays.asList(BarcodeFormat.QRCODE) );

		setContentView(mScannerView);
	}

	@Override
	public void onResume() {
		super.onResume();
		mScannerView.setResultHandler(this);
		mScannerView.startCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		mScannerView.stopCamera();
	}

	@Override
	public void handleResult(Result rawResult) {
		// ok we got the qr result now make a toast for it!
//        Toast.makeText(this, "Contents = " + rawResult.getContents(), Toast.LENGTH_SHORT).show();
		// parse result

		String narId = "";
		String[] pr_l = rawResult.getContents().split("\\|");
		int result = Activity.RESULT_OK;
		for (String pr_s: pr_l) {
			String[] pr = pr_s.split("=");
			if (pr.length != 2) {
				Log.e(TAG, "Malformed parameter segment => " + pr_s);
				result = Activity.RESULT_CANCELED;
			}
			if (pr[0].equals("nar_id")) {
				narId = pr[1];
			} else {
				Log.e(TAG, "Unknown [parameter:value] => ["+pr[0]+":"+pr[1]+"]");
				result = Activity.RESULT_CANCELED;
			}
		}


        // return result by intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXT_QR_RESULT_NAR_ID, narId);
        setResult(result, resultIntent);
        finish();
	}
}
