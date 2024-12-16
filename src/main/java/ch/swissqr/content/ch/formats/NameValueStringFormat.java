package ch.swissqr.content.ch.formats;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;

/**
 * Wriging of string of the format fieldName:fieldContent
 * 
 * @author pschatzmann
 *
 */
public class NameValueStringFormat implements IFormat {
	final static public String CRLF = System.lineSeparator(); // "\\r\\n";

	@Override
	public String write(List<IContent> list) throws FormatException {
		StringBuffer sb = new StringBuffer();
		for (IContent cObj : list) {
			for (Entry<String,Object> e : cObj.getDataMap().entrySet()) {
				sb.append(e.getKey());
				sb.append(": ");
				sb.append(e.getValue());
				sb.append(CRLF);
				
			}
		}
		return sb.toString();
	}

	@Override
	public List<IContent> read(String string) throws FormatException, ParseException {
		List<IContent> result = new ArrayList();
		Map map = new TreeMap();
		String lines[] = string.split(System.lineSeparator());
		for (String line : lines) {
			String fields[] = line.split(":");
			if (fields.length == 2) {
				String key = fields[0].trim();
				if (!map.containsKey(key)) {
					map.put(key, fields[1]);
				} else {
					ContentBarcodeCH c = new ContentBarcodeCH();
					c.setDataMap(map);
					result.add(c);
					map.clear();
				}
			}
		}
		if (!map.isEmpty()) {
			ContentBarcodeCH c = new ContentBarcodeCH();
			c.setDataMap(map);
			result.add(c);
		}
		return result;

	}

}
