package ch.swissqr.errors;

/**
 * <p>LicenceError class.</p>
 *
 * @author pschatzmann
 */
public class LicenceError extends Exception {
	private static final long serialVersionUID = 1L;
	/**
	 * <p>Constructor for LicenceError.</p>
	 *
	 * @param e a {@link java.lang.Exception} object
	 */
	public LicenceError(Exception e) {
		super(e);
	}
	/**
	 * <p>Constructor for LicenceError.</p>
	 *
	 * @param msg a {@link java.lang.String} object
	 */
	public LicenceError(String msg) {
		super(msg);
	}

}
