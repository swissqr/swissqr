package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.swissqr.barcode.ErrorCorrectionLevel;

import ch.swissqr.barcode.QRCombinedBarcode;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.StringUtils;

/**
 * Creates a QR barcode which contains a e-mail address
 *
 * @author pschatzmann
 */
public class ContentMail extends ContentBase implements IContent {
	// mailto:test@gmail.com?subject=Test%20Subject&amp;body=This%20is%20a%20test.
	private String mailAddress;
	private String subject;
	private String message;

	/**
	 * <p>Constructor for ContentMail.</p>
	 *
	 * @param mailAddress a {@link java.lang.String} object
	 * @param subject a {@link java.lang.String} object
	 * @param msg a {@link java.lang.String} object
	 * @throws java.io.UnsupportedEncodingException if any.
	 */
	public ContentMail(String mailAddress, String subject, String msg) throws UnsupportedEncodingException {
		this.mailAddress = mailAddress;
		this.subject = subject;
		this.message = msg;
	}

	/**
	 * <p>Constructor for ContentMail.</p>
	 */
	public ContentMail() {
	}

	/** {@inheritDoc} */
	@Override
	public String getContent() throws UnsupportedEncodingException {
		return "mailto:" + encode(mailAddress) + "?subject=" + encode(subject) + "&body=" + encode(message);
	}
	
	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix() {
		return Arrays.asList("mailto:");
	}

	/** {@inheritDoc} */
	@Override
	public IContent parse(String str)  {
		try {
			str = URLDecoder.decode(str, System.getProperty("file.encoding"));
			str = str.replace("mailto:", "");
			int start = str.indexOf("?subject=");
			if (start > 0) {
				int end = str.indexOf("&body=");
				if (end > 0) {
					this.setMailAddress(str.substring(0, start));
					this.setSubject(str.substring(start+9, end));
					this.setMessage(str.substring(end+6));
				} else {
					this.setMailAddress(str.substring(0, start));
					this.setSubject(str.substring(start+6));
				}
			} else {
				this.setMailAddress(str);
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return this;
	}

	/**
	 * <p>Getter for the field <code>mailAddress</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getMailAddress() {
		return mailAddress;
	}

	/**
	 * <p>Setter for the field <code>mailAddress</code>.</p>
	 *
	 * @param mailAddress a {@link java.lang.String} object
	 */
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	/**
	 * <p>Getter for the field <code>subject</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * <p>Setter for the field <code>subject</code>.</p>
	 *
	 * @param subject a {@link java.lang.String} object
	 */
	public void setSubject(String subject) {
		this.subject = subject;
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

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		QRCombinedBarcode barcode = new QRCombinedBarcode(this.isTest()? "/icons/test.png":"/icons/mail.png", mm, errorCorrectionLevel);
		return barcode.createImage(this.getContent(), format);
	}
	
	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Map<String, Object> getDataMap() {
		Map result  = new HashMap();
		result.put("mailAddress", this.getMailAddress());
		result.put("subject", this.getSubject());
		result.put("message", this.getMessage());
		result.put("contentType", this.getContentType());
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String,Object> record) {
		super.setDataMap(record);
		this.setMailAddress(StringUtils.str(record.get("mailAddress")));
		this.setSubject(StringUtils.str(record.get("subject")));
		this.setMessage(StringUtils.str(record.get("message")));
	}

	private static String encode(String msg) throws UnsupportedEncodingException {
		return URLEncoder.encode(StringUtils.str(msg), System.getProperty("file.encoding"));
	}

}
