package ch.swissqr.content.ch.formats;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.MapData;
import ch.swissqr.utils.StringUtils;

/**
 * Convert CSV content to List of BarcodeContentCH
 * 
 * @author pschatzmann
 *
 */

public class CSVFormat implements IFormat {
	private static final Logger LOG = Logger.getLogger(CSVFormat.class);

	@Override
	public String write(List<IContent> list) throws FormatException {
		try {
			org.apache.commons.csv.CSVFormat csvFileFormat = org.apache.commons.csv.CSVFormat.DEFAULT
					.withFirstRecordAsHeader();
			Writer stringWriter = new StringWriter();
			if (!list.isEmpty()) {
				CSVPrinter csvPrinter = new CSVPrinter(stringWriter, csvFileFormat);

				Collection<String> keys = getKeys(list);
				csvPrinter.printRecord(keys);

				for (IContent code : list) {
					Map<String, Object> record = code.getDataMap();
					for (String field : keys) {
						csvPrinter.print(StringUtils.str(record.get(field)));
					}
					csvPrinter.println();
					csvPrinter.flush();
				}
				csvPrinter.close();
			}
			return stringWriter.toString();
		} catch (Exception ex) {
			throw new FormatException(ex);
		}
	}

	private Collection<String> getKeys(List<IContent> list) {
		Set<String> keys = new TreeSet();
		for (IContent code : list) {
			Map<String, Object> record = code.getDataMap();
			keys.addAll(record.keySet());
		}
		return keys;
	}

	@Override
	public List<IContent> read(String csv) throws FormatException, IOException, ParseException {
		List<IContent> result = new ArrayList();

		Iterable<CSVRecord> records = org.apache.commons.csv.CSVFormat.DEFAULT.withFirstRecordAsHeader()
				.parse(new StringReader(csv));
		for (CSVRecord record : records) {
			try {
			String type = record.get("contentType");
			IContent c = (IContent)Class.forName("ch.swissqr.content."+type).newInstance();
			c.setDataMap((Map)record.toMap());
			result.add(c);
			} catch(Exception ex) {
				LOG.error(ex,ex);
			}
		}
		return result;
	}

}
