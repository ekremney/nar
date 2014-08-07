package net.narlab.projectnar;

import java.util.Date;

/**
 * @author Fma
 */
public class Nar {
	private String id;
	private Date lastalive;
	static private final String TAG = "NarObj";

	/**
	 * 	take parameters already parsed
	 * @param id of nar
	 * @param lastalive time of nar
	 */
	public Nar(String id, long lastalive) {
		this.id = id;
		this.lastalive = new Date(lastalive);
	}

	/**
	 * takes directly from qr reading
	 * @param params parameters in the following format: prm1=PRM1_VAL|prm2=PRM2_VAL ... (id=IDV|pass=PASSV)
	 */

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
	public Date getLastalive() {
		return this.lastalive;
	}
	public String getLastaliveS() {
		return this.lastalive.toString();
	}

	/**
	 *
	 * @return in our qr format
	 */
	@Override
	public String toString() {
		return "id="+this.id+"|lastalive="+this.lastalive;
	}

}
