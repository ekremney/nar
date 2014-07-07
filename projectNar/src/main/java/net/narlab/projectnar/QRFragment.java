package net.narlab.projectnar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class QRFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static QRFragment newInstance() {
        return new QRFragment();
	}

	public QRFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return  inflater.inflate(R.layout.fragment_qr_test, container, false);
	}
}
