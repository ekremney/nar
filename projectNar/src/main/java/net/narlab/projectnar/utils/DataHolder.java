package net.narlab.projectnar.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author fma
 * @since 21.07.2014.
 */
public class DataHolder {
	public static final boolean LOGIN_TEST = true;
	static private NarConnManager connMng;
	static private HttpClient httpClient;
	static final private String serverUrl = "http://88.231.19.113";
	public final static int REGISTER_NAR_REQ_CODE = 0x0f3b, SCANNER_REQ_CODE = 0x0f3a; // request codes

	public static NarConnManager getConnMng() {
		if (connMng == null) {
			connMng = new NarConnManager();
		}
		return connMng;
	}

	// common HttpClient for cookies
	public static HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public static String getServerUrl() {
		return serverUrl;
	}

}
