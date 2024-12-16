package ch.swissqr.errors;

public class LicenceError extends Exception {
	private static final long serialVersionUID = 1L;
	public LicenceError(Exception e) {
		super(e);
	}
	public LicenceError(String msg) {
		super(msg);
	}

}
