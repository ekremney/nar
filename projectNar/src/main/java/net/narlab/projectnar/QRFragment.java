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
/*	private static final String ARG_SECTION_NUMBER = "section_number";
	private static final String ARG_SECTION_STRING = "section_string";
*/

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static QRFragment newInstance(int sectionNumber, String sectionString) {
		QRFragment fragment = new QRFragment();
/*		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		args.putString(ARG_SECTION_STRING, sectionString);
		fragment.setArguments(args);*/
		return fragment;
	}

	public QRFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_qr_test, container, false);
/*
		TextView textView;
		textView= (TextView) rootView.findViewById(R.id.section_label);
		textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));

		textView = (TextView) rootView.findViewById(R.id.section_string);
		textView.setText(getArguments().getString(ARG_SECTION_STRING));
		*/
		return rootView;
	}
}