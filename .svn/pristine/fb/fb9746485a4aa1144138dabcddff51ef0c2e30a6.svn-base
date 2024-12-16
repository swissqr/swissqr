package ch.swissqr.utils;

/**
 * Result of data validation
 * @author pschatzmann
 *
 */

public class Error {
	private String fileName="";
	private String fieldName="";
	private String message;
	
	public Error() {}
	
	public Error(Exception ex){
		this.message = ex.getMessage();
	}

	public Error(String msg){
		this.message = msg;
	}

	public Error(String fieldName, String msg){
		this.message = msg;
		this.fieldName = fieldName;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String text) {
		this.message = text;
	}
	public String toString() {
		return "Error: "+message;
	}
}
