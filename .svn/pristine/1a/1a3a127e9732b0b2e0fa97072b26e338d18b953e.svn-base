package ch.swissqr.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.swissqr.errors.BarcodeException;

/**
 * Translates barcode content strings into the corresponding java objects
 * 
 * @author pschatzmann
 *
 */

public class AllBarcodeTypes {
	static Collection<IContent> emptyContentObjects = Arrays.asList(new ContentBarcodeCH(), new ContentBarcodeEU(),
			new ContentMail(), new ContentSMS(), new ContentTel(), new ContentString());

	static Collection<IContent> getAllBarcodes() {
		return emptyContentObjects;
	}

	public static IContent getIContent(String str)
			throws InstantiationException, IllegalAccessException, BarcodeException {
		for (IContent empty : getAllBarcodes()) {

			if (empty.getPrefix().isEmpty()) {
				return empty.getClass().newInstance().parse(str);
			}
			for (String prefix : empty.getPrefix()) {
				if (str.toLowerCase().startsWith(prefix.toLowerCase())) {
					return empty.getClass().newInstance().parse(str);
				}
			}
		}
		return null;
	}

	public static List<IContent> getIContentList(Collection<String> strList)
			throws InstantiationException, IllegalAccessException, BarcodeException {
		List<IContent> result = new ArrayList();
		for (String str : strList) {
			result.add(getIContent(str));
		}
		return result;
	}

}
