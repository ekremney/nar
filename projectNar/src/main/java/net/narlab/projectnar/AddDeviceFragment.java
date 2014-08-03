package net.narlab.projectnar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AddDeviceFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static AddDeviceFragment newInstance() {
        return new AddDeviceFragment();
	}

	public AddDeviceFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return  inflater.inflate(R.layout.fragment_add_device, container, false);
	}
}
