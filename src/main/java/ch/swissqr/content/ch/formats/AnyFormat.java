package ch.swissqr.content.ch.formats;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.StringUtils;

/**
 * Converts different String representations into a list of ContentBarcodeCH objects
 *
 * @author pschatzmann
 */
public class AnyFormat {

	/**
	 * <p>read.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link java.util.List} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 * @throws java.io.IOException if any.
	 * @throws java.text.ParseException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public static List<IContent> read(String str) throws FormatException, IOException, ParseException, BarcodeException {
		CharSequence s = "\\n";
		str = str.trim().replace(s, System.lineSeparator());
		
		if (str.startsWith("[")) {
			// json
			return new JsonFormat().read(str);
		}	else if (str.contains("<root>")) {
			// xml
			return new XmlFormat().read(str);
		} else if (str.startsWith("SPC")) {
			// string format
			return new QRStringFormatSwiss().read(str);
		} else if (str.startsWith("BCD")) {
			// string format
			return new QRStringFormatEU().read(str);
		} else if (count(str, ":") > 5) {
			// Name Value String
			return new NameValueStringFormat().read(str);
		} else if (count(StringUtils.firstLine(str), ",") >= 1) {
			// csv
			return new CSVFormat().read(str);
		}
			throw new ParseException("The data format is not supported for :"+str, 0);
	}

	private static int count(String str, String contains) {
		return str.length() - str.replace(contains, "").length();
	}

}
