package ch.swissqr.content.ch.formats;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;

/**
 * reading and writing of string to SwissQRContent
 *
 * @author pschatzmann
 */
public interface IFormat {

	/**
	 * <p>write.</p>
	 *
	 * @param list a {@link java.util.List} object
	 * @return a {@link java.lang.String} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 */
	String write(List<IContent> list) throws FormatException;

	/**
	 * <p>read.</p>
	 *
	 * @param jsonInString a {@link java.lang.String} object
	 * @return a {@link java.util.List} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 * @throws java.io.IOException if any.
	 * @throws java.text.ParseException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	List<IContent> read(String jsonInString) throws FormatException, IOException, ParseException, BarcodeException;


}
