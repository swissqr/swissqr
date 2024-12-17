package ch.swissqr.utils;

/**
 * Result of data validation
 *
 * @author pschatzmann
 */
public class Error {
	private String fileName="";
	private String fieldName="";
	private String message;
	
	/**
	 * <p>Constructor for Error.</p>
	 */
	public Error() {}
	
	/**
	 * <p>Constructor for Error.</p>
	 *
	 * @param ex a {@link java.lang.Exception} object
	 */
	public Error(Exception ex){
		this.message = ex.getMessage();
	}

	/**
	 * <p>Constructor for Error.</p>
	 *
	 * @param msg a {@link java.lang.String} object
	 */
	public Error(String msg){
		this.message = msg;
	}

	/**
	 * <p>Constructor for Error.</p>
	 *
	 * @param fieldName a {@link java.lang.String} object
	 * @param msg a {@link java.lang.String} object
	 */
	public Error(String fieldName, String msg){
		this.message = msg;
		this.fieldName = fieldName;
	}

	/**
	 * <p>Getter for the field <code>fileName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * <p>Setter for the field <code>fileName</code>.</p>
	 *
	 * @param fileName a {@link java.lang.String} object
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * <p>Getter for the field <code>fieldName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * <p>Setter for the field <code>fieldName</code>.</p>
	 *
	 * @param fieldName a {@link java.lang.String} object
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
	 * @param text a {@link java.lang.String} object
	 */
	public void setMessage(String text) {
		this.message = text;
	}
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {
		return "Error: "+message;
	}
}
