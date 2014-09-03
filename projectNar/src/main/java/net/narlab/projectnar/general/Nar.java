package net.narlab.projectnar.general;

import net.narlab.projectnar.utils.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Fma
 * @date 07.08.2014
 */
public class Nar {
	private String id, name;
	private Date lastalive;
	// holds the current state of nar device (could be changed in future)
	private boolean state;

	/**
	 * 	take parameters already parsed
	 * @param id of nar
	 * @param lastalive time of nar
	 */
	public Nar(String id, String name, String lastalive) {
		this.id = id;
		this.name = name;
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
	 * @return id of nar
	 */
	public final String getId() {
		return this.id;
	}

	/**
	 * @return name of nar
	 */
	public String getName() {
		return name;
	}

	/**
	 * sets name of nar
	 * @param name new name for nar
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return nar as our qr format string
	 */
	@Override
	public String toString() {
		return "id="+this.id+"|lastalive="+this.lastalive;
	}

	/**
	 * @param state new state of nar
	 */
	public void setState(boolean state) {
		this.state = state;
	}

	/**
	 * @return state of nar
	 */
	public boolean getState() {
		return state;
	}

}
