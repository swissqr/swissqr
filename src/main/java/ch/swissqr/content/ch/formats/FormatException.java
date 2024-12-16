package ch.swissqr.content.ch.formats;

import ch.swissqr.errors.BarcodeException;

public class FormatException extends BarcodeException {
	private static final long serialVersionUID = 1L;

	public FormatException(Exception ex){
		super(ex);
	}
}
