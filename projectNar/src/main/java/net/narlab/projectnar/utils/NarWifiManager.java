package net.narlab.projectnar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.Locale;

public class NarWifiManager {

	public static class IPParser {
		public static String IntToString(int ip) {
			// for logical shift use >>> but since we take last 8 bits it doesn't matter
			return String.format(Locale.US, "%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
		}

		public static byte[] IntToByteArray(int ip) {
			byte[] r;
			r = new byte[]{(byte) ip, (byte) (ip >> 8), (byte) (ip >> 16), (byte) (ip >> 24)};
			return r;
		}

		public static int StringToInt(String dns2) {
			String s[] = dns2.split("");
			if (s.length > 3) {
				return (Integer.parseInt(s[0]) << 24) + (Integer.parseInt(s[1]) << 16) + (Integer.parseInt(s[2]) << 8) + Integer.parseInt(s[3]);
			}
			return 0;
		}
	}

//	private Context C;
	private WifiManager wManager;
	private ConnectivityManager wConnManager;
	private WifiInfo wInfo;

	public NarWifiManager(Context C) {
//		this.C = C;
		this.wManager = (WifiManager) C.getSystemService(Context.WIFI_SERVICE);
		this.wConnManager = (ConnectivityManager) C.getSystemService(Context.CONNECTIVITY_SERVICE);
		this.wInfo = wManager.getConnectionInfo();

		Log.v("NarWM", "Init test================");
		Log.v("NarWM", "" + wInfo.toString());
		Log.v("NarWM", "" + wInfo.getBSSID());
		Log.v("NarWM", "" + wInfo.getSSID());

		Log.v("NarWM", "Connected: " + isWifiConnected());

		Log.v("NarWM_dns1", IPParser.IntToString(wManager.getDhcpInfo().dns1));
		Log.v("NarWM_dns2", IPParser.IntToString(wManager.getDhcpInfo().dns2));

		Log.v("NarWM", "Init ok================");
	}

	/**
	 * Check Wifi Connection
	 *
	 * @return boolean state of wifi connection
	 */
	public boolean isWifiConnected() {
		NetworkInfo wifiConnInfo = wConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifiConnInfo.isConnected();
	}

	public boolean isInternetConnected() {
		try {
			return wConnManager.getActiveNetworkInfo().isConnected();
		} catch (NullPointerException e) {
			return false;
		}
	}

	// Maybe check connection before all?
	// get SSID of network (it might be NULL and might need to trim "" if there is at start and end)
	public String getSSID() {
		return wInfo.getSSID();
	}

	// get IP as 32 bit int
	public int getIP() {
		return wInfo.getIpAddress();
	}

	// get IP as in xxx.xxx.xxx.xxx format string
	public String getIPString() {
		return IPParser.IntToString(getIP());
	}

	// get gateway as 32 bit int
	public int getGateway() {
		return wManager.getDhcpInfo().gateway;
	}

	// get gateway as in xxx.xxx.xxx.xxx format string
	public String getGatewayString() {
		return IPParser.IntToString(getGateway());
	}

	// get netmask as 32 bit int
	public int getNetmask() {
		return wManager.getDhcpInfo().netmask;
	}

	// get netmask as in xxx.xxx.xxx.xxx format string
	public String getNetmaskString() {
		return IPParser.IntToString(getNetmask());
	}

	public int getDHCPServ() {
		return wManager.getDhcpInfo().serverAddress;
	}

	public int getDNS1() {
		return wManager.getDhcpInfo().dns1;
	}

	public String getDNS1String() {
		return IPParser.IntToString(getDNS1());
	}

	public int getDNS2() {
		return wManager.getDhcpInfo().dns2;
	}

	public String getDNS2String() {
		return IPParser.IntToString(getDNS2());
	}

}
