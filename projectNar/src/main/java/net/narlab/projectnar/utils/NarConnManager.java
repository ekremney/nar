package net.narlab.projectnar.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import net.narlab.projectnar.MainActivity;
import net.narlab.projectnar.Nar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by fma on 11/07/14.
 */
public class NarConnManager {
	private Nar nar;
	private final String BASE_URL = "http://88.231.14.118";
	private final String TAG = "NarConnMng";
	private HttpClient httpClient;

	public NarConnManager(Nar nar) {
		this.nar = nar;
	}

	private HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

	public void login() {
		Log.d("Login:", nar.getId() + "|" + nar.getPass());
		Log.e(TAG, "Try to send command");
		// set url
		String url = BASE_URL+"/android/login";

		// Add post data
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("username", nar.getId());
		data.put("password", nar.getPass());
		data.put("mesg", "Log me in pls");

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);

		asyncHttpPost.execute(url);
	}

	public void sendMessage(String topic, String message) {
		Log.e(TAG, "Try to send command");
		// set url
		String url = BASE_URL+"/android/message";

		// Add post data
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("id", nar.getId());
		data.put("topic", topic);
		data.put("message", message);

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);

		asyncHttpPost.execute(url);

	}

	public void checkState() {
		Log.e(TAG, "Check login state");
		// set url
		String url = BASE_URL+"/android/state";

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(null);

		asyncHttpPost.execute(url);

	}

	public void logout() {
		Log.d("Logout:", nar.getId() + "|" + nar.getPass());
		Log.e(TAG, "Try to logout");
		// set url
		String url = BASE_URL+"/android/logout";

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(null);

		asyncHttpPost.execute(url);
	}

	public class AsyncHttpPost extends AsyncTask<String, String, String> {
		private HashMap<String, String> mData = null;// post data

		/**
		 * constructor
		 */
		public AsyncHttpPost(HashMap<String, String> data) {
			mData = data;
		}

		/**
		 * background
		 */
		@Override
		protected String doInBackground(String... params) {
			byte[] result;
			String str = "";
			HttpClient client = getHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0] is URL

			Log.w("Url", params[0]);
			try {
				// set up post data
				if (mData != null) {
					ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					for (String key : mData.keySet()) {
						nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
					}

					post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				}
				HttpResponse response = client.execute(post);
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			Log.w("Complete", result);
		}
	}
}
