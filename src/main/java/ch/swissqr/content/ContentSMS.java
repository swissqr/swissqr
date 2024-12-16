package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
 * Creates a QR barcode which contains a SMS 
 * 
 * @author pschatzmann
 *
 */
public class ContentSMS extends ContentBase implements IContent {
	// SMSTO:[RUFNUMMER]:[NACHRICHT]
	private String telephoneNumber = "";
	private String message = "";

	public ContentSMS(String telNumber, String message) {
		super();
		this.telephoneNumber = telNumber;
		this.message = message;
	}

	public ContentSMS() {
	}

	@JsonIgnore
	@Override
	public Collection<String> getPrefix() {
		return Arrays.asList("smsto:");
	}

	@Override
	public IContent parse(String str) {
		String sa[] = str.split(":");
		if (sa.length >= 2)
			this.setTelephoneNumber(sa[1]);
		if (sa.length >= 3) {
			// put message toghether again
			StringBuffer sb = new StringBuffer();
			for (int j = 1; j < sa.length; j++) {
				sb.append(sa[j]);
				if (j < sa.length - 1) {
					sb.append(":");
				}
			}
			this.setMessage(sb.toString());
		}

		return this;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	@JsonIgnore
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		QRCombinedBarcode barcode = new QRCombinedBarcode(this.isTest()? "/icons/test.png":"/icons/sms.png", mm, errorCorrectionLevel);
		return barcode.createImage(this.getContent(), format);
	}
	
	@Override
	public String getContent() throws UnsupportedEncodingException {
		this.setContent("smsto:" + this.telephoneNumber + ":" + this.message);
		return super.getContent();
	}
	
	@JsonIgnore
	@Override
	public Map<String, Object> getDataMap() {
		Map result  = new HashMap();
		result.put("telephoneNumber", this.getTelephoneNumber());
		result.put("message", this.getMessage());
		result.put("contentType", this.getContentType());
		return result;
	}
	
	@Override
	public void setDataMap(Map<String,Object> record) {
		super.setDataMap(record);
		this.setTelephoneNumber(StringUtils.str(record.get("telephoneNumber")));
		this.setMessage(StringUtils.str(record.get("message")));

	}


}
