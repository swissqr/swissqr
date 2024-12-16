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

public interface IContent {
	boolean isOK();
	public String getContent() throws UnsupportedEncodingException;
	public void clean();
	public List<Error> check();
	public Properties getProperties();
	public Collection<String> getPrefix();
	public IContent parse(String content) throws FormatException, BarcodeException;
	public String getContentType();
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException;
	public void setDataMap(Map<String,Object> record) throws FormatException, ParseException;
	public Map<String, Object> getDataMap();
	public void setTest(boolean test);
	public boolean isTest();

}