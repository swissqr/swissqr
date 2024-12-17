package ch.swissqr.errors;

/**
 * Exception which is documenting that the user was calling the service too many times
 * in the allocated period of time
 *
 * @author pschatzmann
 */
public class UsageViolationException extends Exception {
	private static final long serialVersionUID = 1L;

	UsageViolationException(String error) {
		super(error);
	}
}
