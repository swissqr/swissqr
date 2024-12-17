package ch.swissqr.content.ch.formats;

import ch.swissqr.errors.BarcodeException;

/**
 * <p>FormatException class.</p>
 *
 * @author pschatzmann
 */
public class FormatException extends BarcodeException {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for FormatException.</p>
	 *
	 * @param ex a {@link java.lang.Exception} object
	 */
	public FormatException(Exception ex){
		super(ex);
	}
}
