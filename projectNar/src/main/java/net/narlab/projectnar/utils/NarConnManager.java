package net.narlab.projectnar.utils;

import android.os.AsyncTask;
import android.util.Log;

import net.narlab.projectnar.Nar;
import net.narlab.projectnar.NarList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author fma
 * @date 11/07/14.
 */
public class NarConnManager {
	private NarList narList;
	private final String BASE_URL = DataHolder.getServerUrl();
	private final String TAG = "NarConnMng";

	public NarConnManager() {
		narList = new NarList();
	}
	public NarConnManager(Nar nar) {
		this();
		narList.add(nar);
	}

	public void addNar(Nar nar) {
		narList.add(nar);
	}
	
	public Nar getNar(String narId) {
		return narList.get(narId);
	}

	public void register(String narId) {
		Nar nar = narList.get(narId);
		register(nar);
	}
	public void register(Nar nar) {
		Log.d("Login:", nar.getId() + "|" + nar.getPass());
		Log.e(TAG, "Try to send command");

		// set url
		String url = BASE_URL+"/android/add_nar/"+nar.getId();

		// Add post data
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("password", nar.getPass());
		data.put("mesg", "Log me in pls");

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(data);

		asyncHttpPost.execute(url);
	}

	public void sendMessage(String narId, String topic, String message) {
		Nar nar = narList.get(narId);

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

	public void checkState(String narId) {
		Nar nar = narList.get(narId);
		Log.e(TAG, "Check login state");
		// set url
		String url = BASE_URL+"/android/nar_state/"+nar.getId();

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(null);

		asyncHttpPost.execute(url);

	}

	public void logout() {
		Log.e(TAG, "Try to logout");
		// set url
		String url = BASE_URL+"/android/logout";

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(null);

		asyncHttpPost.execute(url);
	}

	public void test() {
		Log.e(TAG, "Test server!");
		// set url
		String url = BASE_URL+"/android/test";

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
			HttpClient client = DataHolder.getHttpClient();
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
