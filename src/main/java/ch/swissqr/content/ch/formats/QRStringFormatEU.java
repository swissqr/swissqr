package ch.swissqr.content.ch.formats;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ch.swissqr.content.ContentBarcodeEU;
import ch.swissqr.content.ContentBarcodeEU.Version;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.StringUtils;

/**
 * Serialization and Deserialization of the standard text format as defined in
 * the specificaiton. However the standard system string encoding is used which
 * is usually (UTF8)
 * 
 * 
 * @author pschatzmann
 *
 */
public class QRStringFormatEU implements IFormat {
	private final static Logger LOG = Logger.getLogger(QRStringFormatEU.class);
	//final static public String CRLF = System.lineSeparator(); // "\\r\\n";

	@Override
	public List<IContent> read(String contentString) throws FormatException, BarcodeException {
		return parseList(contentString);
	}

	public List<IContent> parseList(String str) throws FormatException, BarcodeException {
		// remove all cr
		str = str.replaceAll("\\r", "");
		List<IContent> result = new ArrayList();
		String barcodeStringArray[] = str.split("BCD" + "\\n");
		for (String barcodeString : barcodeStringArray) {
			IContent content = parse(barcodeString, new ContentBarcodeEU());
			if (content != null) {
				result.add(content);
			}
		}
		return result;
	}

	public IContent parse(String str, ContentBarcodeEU eu) throws FormatException {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		if (!str.startsWith("BCD")) {
			str = "BCD\n" + str;
		}
		str = str.replaceAll("\\r", "");
		String stringArray[] = str.split("\\n");
		int len = stringArray.length;
		eu.version(Version.valueOf(stringArray[1]));
		eu.characterSet (stringArray[2]);
		eu.identification (stringArray[3]);

		if (len >= 5)
			eu.bic (stringArray[4]);
		if (len >= 6)
			eu.name (stringArray[5]);
		if (len >= 7)
			eu.iban(stringArray[6]);
		if (len >= 8) {
			String amountStr = stringArray[7];
			if (!StringUtils.isEmpty(amountStr)) {
				String number = amountStr.replaceAll("[^0-9?!\\.]","");
				eu.amount( new BigDecimal(number));
			} else {
				eu.amount((BigDecimal)null);
			}
		}
		if (len >= 9)
			eu.purpose(stringArray[8]);
		if (len >= 10)
			eu.remittanceReference (stringArray[9]);
		if (len >= 11)
			eu.remittanceText(stringArray[10]);
		if (len >= 12)
			eu.information(stringArray[11]);
		return eu;
	}



	@Override
	public String write(List<IContent> list) {
		StringBuffer sb = new StringBuffer();
		for (IContent cObj : list) {
			if (cObj instanceof ContentBarcodeEU) {
				ContentBarcodeEU eu = (ContentBarcodeEU) cObj;
				sb.append(eu.getHeader());
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getBic()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getName()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getIban()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getAmountString()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getPurpose()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getRemittanceReference()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getRemittanceText()));
				sb.append(System.lineSeparator());
				sb.append(StringUtils.str(eu.getInformation()));
			}
		}
		return sb.toString();
	}

}
