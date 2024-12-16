package ch.swissqr.content.ch.formats;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;

/**
 * reading and writing of string to SwissQRContent
 * @author pschatzmann
 */
public interface IFormat {

	String write(List<IContent> list) throws FormatException;

	List<IContent> read(String jsonInString) throws FormatException, IOException, ParseException, BarcodeException;


}