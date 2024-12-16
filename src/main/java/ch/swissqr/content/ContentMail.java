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
 *
 */
public class ContentMail extends ContentBase implements IContent {
	// mailto:test@gmail.com?subject=Test%20Subject&amp;body=This%20is%20a%20test.
	private String mailAddress;
	private String subject;
	private String message;

	public ContentMail(String mailAddress, String subject, String msg) throws UnsupportedEncodingException {
		this.mailAddress = mailAddress;
		this.subject = subject;
		this.message = msg;
	}

	public ContentMail() {
	}

	@Override
	public String getContent() throws UnsupportedEncodingException {
		return "mailto:" + encode(mailAddress) + "?subject=" + encode(subject) + "&body=" + encode(message);
	}
	
	@JsonIgnore
	@Override
	public Collection<String> getPrefix() {
		return Arrays.asList("mailto:");
	}

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

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@JsonIgnore
	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		QRCombinedBarcode barcode = new QRCombinedBarcode(this.isTest()? "/icons/test.png":"/icons/mail.png", mm, errorCorrectionLevel);
		return barcode.createImage(this.getContent(), format);
	}
	
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
