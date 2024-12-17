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
 * QR barcode which calls a tel number
 *
 * @author pschatzmann
 */
public class ContentTel extends ContentBase implements IContent {
	private String telephoneNumber;
	// tel:<number>	
	/**
	 * <p>Constructor for ContentTel.</p>
	 *
	 * @param telNumber a {@link java.lang.String} object
	 */
	public ContentTel(String telNumber) {
		super("tel:"+telNumber);
		this.telephoneNumber = telNumber;
	}
	
	/**
	 * <p>Constructor for ContentTel.</p>
	 */
	public ContentTel() {
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix(){
		return Arrays.asList("tel:");
	}

	/**
	 * <p>Getter for the field <code>telephoneNumber</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * <p>Setter for the field <code>telephoneNumber</code>.</p>
	 *
	 * @param telephoneNumber a {@link java.lang.String} object
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
		this.setContent("tel:"+telephoneNumber);
	}
	
	/** {@inheritDoc} */
	@Override
	public IContent parse(String str) {
		String number = str.replaceFirst("tel:", "");
		this.setTelephoneNumber(number);
		return this;
	}
	
	/** {@inheritDoc} */
	@Override
	@JsonIgnore
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		QRCombinedBarcode barcode = new QRCombinedBarcode(this.isTest()? "/icons/test.png":"/icons/phone.png", mm, errorCorrectionLevel);
		return barcode.createImage(this.getContent(), format);
	}
	
	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Map<String, Object> getDataMap() {
		Map result  = new HashMap();
		result.put("telephoneNumber", this.getTelephoneNumber());
		result.put("contentType", this.getContentType());
		return result;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String,Object> record) {
		super.setDataMap(record);
		this.setTelephoneNumber(StringUtils.str(record.get("telephoneNumber")));
	}


}
