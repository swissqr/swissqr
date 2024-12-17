package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.swissqr.barcode.ErrorCorrectionLevel;

import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;
import ch.swissqr.barcode.ErrorCorrectionLevel;

/**
 * A simple QR barcode which can represent any string
 *
 * @author pschatzmann
 */
public abstract class ContentBase implements IContent {
	private String content="";
	private Properties properties = new Properties();
	private boolean test = false;

	/**
	 * <p>Constructor for ContentBase.</p>
	 */
	public ContentBase() {}
	
	/**
	 * <p>Constructor for ContentBase.</p>
	 *
	 * @param content a {@link java.lang.String} object
	 */
	public ContentBase(String content) {
		this.content = content;
	}
	
	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public boolean isOK() {
		return !StringUtils.isEmpty(content);
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public String getContent() throws UnsupportedEncodingException {
		return content;
	}

	/** {@inheritDoc} */
	@Override
	public void clean() {
		content="";		
	}
	
	/**
	 * <p>Setter for the field <code>content</code>.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 */
	public void setContent(String str) {
		this.content = str;
	}

	/** {@inheritDoc} */
	@Override
	public List<Error> check() {
		return Arrays.asList();
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.content;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Used to define additional output information: e.g page format, picture type..
	 */
	@JsonIgnore
	@Override
	public Properties getProperties() {
		return this.properties;
	}
	
	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix(){
		// empty list
		return Arrays.asList();
	}

	/** {@inheritDoc} */
	@Override
	public IContent parse(String content)  {
		 this.content = content;
		 return this;
	}


	/** {@inheritDoc} */
	@Override
	public String getContentType() {
		return this.getClass().getSimpleName();
	}
	

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel)
			throws UnsupportedEncodingException, BarcodeException, IOException {
		IBarcode barcode = new QRBarcode( mm, errorCorrectionLevel);
		return barcode.createImage(this.getContent(), format);
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String, Object> values) {
		String fileName = (String) values.get("filename");
		if (fileName!=null) {
			this.getProperties().put("filename", fileName);
		}
		String pictureFormat = (String) values.get("pictureFormat");
		if (pictureFormat!=null) {
			this.getProperties().put("pictureFormat", pictureFormat);
		}
		String dimension = (String) values.get("dimension");
		if (dimension!=null) {
			this.getProperties().put("dimension", dimension);
		}
		String errorCorrection = (String) values.get("errorCorrection");
		if (errorCorrection!=null) {
			this.getProperties().put("errorCorrection", errorCorrection);
		}
	}

	
	/** {@inheritDoc} */
	@Override
	public void setTest(boolean test) {
		this.test = test;
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isTest() {
		return this.test;
	}


}
