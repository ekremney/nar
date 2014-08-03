package net.narlab.projectnar;

import android.util.Log;

/**
 * @author Fma
 */
public class Nar {
	private String id, pass;
	static private final String TAG = "NarObj";

	/**
	 * 	take parameters already parsed
	 * @param id of nar
	 * @param pass of nar
	 */
	public Nar(String id, String pass) {
		this.id = id;
		this.pass = pass;
	}

	/**
	 * takes directly from qr reading
	 * @param params parameters in the following format: prm1=PRM1_VAL|prm2=PRM2_VAL ... (id=IDV|pass=PASSV)
	 */
	public Nar(String params) throws NarMalformedParameterException {
		String[] pr_l = params.split("\\|");

		for (String pr_s: pr_l) {
			String[] pr = pr_s.split("=");
			Log.i(TAG, pr[0] + "=>" + pr[1]);
			if (pr[0].equals("id")) {
				this.id = pr[1];
			} else if (pr[0].equals("pass")) {
				this.pass = pr[1];
			} else {
				Log.e(TAG, "Unknown parameter name: " + pr[0]);
			}
		}

		if (id == null || pass == null) {
			throw new NarMalformedParameterException("id or pass cannot be parsed from params string");
		}
	}

	/**
	 *
	 * @return id of nar
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * @return pass of nar (may need to encrypt in future)
	 */
	public String getPass() {
		return this.pass;
	}

	/**
	 *
	 * @return in our qr format
	 */
	@Override
	public String toString() {
		return "id="+this.id+"|pass="+this.pass;
	}

	public class NarMalformedParameterException extends Exception {
		public NarMalformedParameterException(String message) {
			super(message);
		}
	}
}
