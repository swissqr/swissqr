package ch.swissqr.errors;

/**
 * Generic application related exception
 *
 * @author pschatzmann
 */
public class BarcodeException extends Exception {
	/**
	 * <p>Constructor for BarcodeException.</p>
	 */
	public BarcodeException() {}
	/**
	 * <p>Constructor for BarcodeException.</p>
	 *
	 * @param ex a {@link java.lang.Exception} object
	 */
	public BarcodeException(Exception ex){
		super(ex);
	}

	/**
	 * <p>Constructor for BarcodeException.</p>
	 *
	 * @param msg a {@link java.lang.String} object
	 */
	public BarcodeException(String msg) {
		super(msg);
	}
}
