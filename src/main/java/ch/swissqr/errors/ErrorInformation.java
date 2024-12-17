package ch.swissqr.errors;

/**
 * Error information which will be returned by the webservices
 *
 * @author pschatzmann
 */
public class ErrorInformation  {
	private int status;
	private String message;

	/**
	 * <p>Constructor for ErrorInformation.</p>
	 */
	public ErrorInformation() {}
	
	/**
	 * <p>Constructor for ErrorInformation.</p>
	 *
	 * @param status a int
	 * @param message a {@link java.lang.String} object
	 */
	public ErrorInformation(int status, String message) {
		this .status = status;
		this.message = message;
	}
	
	/**
	 * <p>Getter for the field <code>status</code>.</p>
	 *
	 * @return a int
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * <p>Setter for the field <code>status</code>.</p>
	 *
	 * @param status a int
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * <p>Getter for the field <code>message</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * <p>Setter for the field <code>message</code>.</p>
	 *
	 * @param message a {@link java.lang.String} object
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
