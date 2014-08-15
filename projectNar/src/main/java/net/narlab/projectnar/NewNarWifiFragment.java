package net.narlab.projectnar;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import net.narlab.projectnar.utils.NarWifiManager;
import net.narlab.projectnar.utils.OnFragmentInteractionListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewNarWifiFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NewNarWifiFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	// tag for logs
	private static final String TAG = "NewNarWifiFragment";
	private static final String ARG_W_SSID = "wifi_ssid";
	//		private static final String ARG_W_IP = "wifi_ip";
	private static final String ARG_W_NETMASK = "wifi_netmask";
	//		private static final String ARG_W_PASS = "wifi_pass";
	private static final String ARG_W_GATEWAY = "wifi_gateway";
	private static final String ARG_W_DNS1 = "wifi_dns1";
	private static final String ARG_W_DNS2 = "wifi_dns2";


    private OnFragmentInteractionListener mListener;

	private String ssid, netmask, gateway, dns1, dns2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nWM nar network manager.
     * @return A new instance of fragment NewNarWifiFragment.
     */

	/**
	 * Returns a new instance of this fragment for the given section
	 * number.
	 */
	public static NewNarWifiFragment newInstance(NarWifiManager nWM) {
		NewNarWifiFragment fragment = new NewNarWifiFragment();
		Bundle args = new Bundle();

		args.putString(ARG_W_SSID, nWM.getSSID());
		args.putString(ARG_W_DNS1, nWM.getDNS1String());
		args.putString(ARG_W_DNS2, nWM.getDNS2String());
		args.putString(ARG_W_NETMASK, nWM.getNetmaskString());
		args.putString(ARG_W_GATEWAY, nWM.getGatewayString());
		fragment.setArguments(args);

		Log.i(TAG, args.toString());
		return fragment;
	}
	public NewNarWifiFragment() {
		// Required empty public constructor
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			ssid = getArguments().getString(ARG_W_SSID);
			netmask = getArguments().getString(ARG_W_NETMASK);
			gateway = getArguments().getString(ARG_W_GATEWAY);
			dns1 = getArguments().getString(ARG_W_DNS1);
			dns2 = getArguments().getString(ARG_W_DNS2);
		}
	}

	/*
	IP Addr: 192.168.2.180
	Netmask: 255.255.255.0
	Gateway: 192.168.2.1
	DHCPsrv: 192.168.2.1
	DNSserv: 192.168.1.1
	*/

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_wifi_info, container, false);

		TextView textView;
		EditText editText;

		editText = (EditText) rootView.findViewById(R.id.wifi_ssid);
		editText.setText(ssid);

		// maybe add a password storage
		editText = (EditText) rootView.findViewById(R.id.wifi_pass);
		editText.requestFocus();
/*			passET.setText(getArguments().getString(ARG_W_PASS));*/
/*
		textView = (TextView) rootView.findViewById(R.id.wifi_netmask);
		textView.setText(netmask);

		textView = (TextView) rootView.findViewById(R.id.wifi_gateway);
		textView.setText(gateway);

		textView = (TextView) rootView.findViewById(R.id.wifi_dns1);
		textView.setText(dns1);

		textView = (TextView) rootView.findViewById(R.id.wifi_dns2);

		if (NarWifiManager.IPParser.StringToInt(dns2) != 0) {
			textView.setText(getArguments().getString(ARG_W_DNS2));
		} else {
			textView.setVisibility(TextView.INVISIBLE); // or GONE
		}
*/
		return rootView;
	}
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
