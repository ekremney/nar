package net.narlab.projectnar.general;

import net.narlab.projectnar.utils.Helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Fma
 * @date 07.08.2014
 */
public class Nar {
	private String id;
	private Date lastalive;
	// holds the current state of nar device (could be changed in future)
	private boolean state;

	/**
	 * 	take parameters already parsed
	 * @param id of nar
	 * @param lastalive time of nar
	 */
	public Nar(String id, String lastalive) {
		this.id = id;
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		isoFormat.setTimeZone(TimeZone.getDefault());
		try {
			this.lastalive = isoFormat.parse(lastalive);
		} catch (Exception e) {
			// set to epoch
			this.lastalive = new Date(0);
			Helper.getExceptionString(e);
		}
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
		SimpleDateFormat frm = new SimpleDateFormat("HH:mm dd/MMM/yyyy");
		return frm.format(this.getLastalive());
	}

	/**
	 *
	 * @return in our qr format
	 */
	@Override
	public String toString() {
		return "id="+this.id+"|lastalive="+this.lastalive;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return state;
	}
}
