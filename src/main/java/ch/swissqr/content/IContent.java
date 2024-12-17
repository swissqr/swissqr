package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ch.swissqr.barcode.ErrorCorrectionLevel;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Error;

/**
 * <p>IContent interface.</p>
 *
 * @author pschatzmann
 */
public interface IContent {
	/**
	 * <p>isOK.</p>
	 *
	 * @return a boolean
	 */
	boolean isOK();
	/**
	 * <p>getContent.</p>
	 *
	 * @return a {@link java.lang.String} object
	 * @throws java.io.UnsupportedEncodingException if any.
	 */
	public String getContent() throws UnsupportedEncodingException;
	/**
	 * <p>clean.</p>
	 */
	public void clean();
	/**
	 * <p>check.</p>
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<Error> check();
	/**
	 * <p>getProperties.</p>
	 *
	 * @return a {@link java.util.Properties} object
	 */
	public Properties getProperties();
	/**
	 * <p>getPrefix.</p>
	 *
	 * @return a {@link java.util.Collection} object
	 */
	public Collection<String> getPrefix();
	/**
	 * <p>parse.</p>
	 *
	 * @param content a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.IContent} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public IContent parse(String content) throws FormatException, BarcodeException;
	/**
	 * <p>getContentType.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getContentType();
	/**
	 * <p>toBarcode.</p>
	 *
	 * @param format a {@link java.lang.String} object
	 * @param mm a {@link java.lang.Double} object
	 * @param errorCorrectionLevel a {@link ch.swissqr.barcode.ErrorCorrectionLevel} object
	 * @return a {@link java.awt.image.BufferedImage} object
	 * @throws java.io.UnsupportedEncodingException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException;
	/**
	 * <p>setDataMap.</p>
	 *
	 * @param record a {@link java.util.Map} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 * @throws java.text.ParseException if any.
	 */
	public void setDataMap(Map<String,Object> record) throws FormatException, ParseException;
	/**
	 * <p>getDataMap.</p>
	 *
	 * @return a {@link java.util.Map} object
	 */
	public Map<String, Object> getDataMap();
	/**
	 * <p>setTest.</p>
	 *
	 * @param test a boolean
	 */
	public void setTest(boolean test);
	/**
	 * <p>isTest.</p>
	 *
	 * @return a boolean
	 */
	public boolean isTest();

}
