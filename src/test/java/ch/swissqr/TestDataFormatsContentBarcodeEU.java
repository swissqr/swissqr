package ch.swissqr;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.swissqr.content.ContentBarcodeEU;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.formats.CSVFormat;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.content.ch.formats.JsonFormat;
import ch.swissqr.content.ch.formats.QRStringFormatSwiss;
import ch.swissqr.errors.BarcodeException;

/**
 * Tests for processing the EU Quick Response barcode type
 * 
 * @author pschatzmann
 *
 */


public class TestDataFormatsContentBarcodeEU {
	private static final Logger LOG = Logger.getLogger(TestDataFormatsContentBarcodeEU.class);

	@Test
	public void testWriteJson() throws Exception {
		ContentBarcodeEU c = content();
		String jsonInString = new JsonFormat().write(Arrays.asList(c));
		LOG.info(jsonInString);
	}
	
	@Test
	public void testReadJson() throws JsonParseException, JsonMappingException, IOException, FormatException, BarcodeException {
		InputStream is = this.getClass().getResourceAsStream("/formats/eu.json");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String jsonInString = s.hasNext() ? s.next() : "";
		List<ContentBarcodeEU> content =(List) new JsonFormat().read(jsonInString);
		Assert.assertEquals("IBAN12345677", content.get(0).getIban());
	}
		
	
	@Test
	public void testWriteCSV() throws FormatException {
		ContentBarcodeEU c = content();
		String csvString = new CSVFormat().write(Arrays.asList(c));
		System.out.println(csvString);
		Assert.assertTrue(csvString.contains(",IBAN,"));
	}
	
	@Test
	public void testReadCSV() throws FormatException, IOException, ParseException {
		InputStream is = this.getClass().getResourceAsStream("/formats/eu.csv");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String csvInString = s.hasNext() ? s.next() : "";
		
		List<ContentBarcodeEU> content = (List) new CSVFormat().read(csvInString);
		Assert.assertEquals(1, content.size());
		Assert.assertEquals("IBAN12345677", content.get(0).getIban());
	}
	
	@Test
	public void testReadWrite() throws FormatException, IOException, ParseException {
		ContentBarcodeEU c = content();
		ContentBarcodeEU c1 = (ContentBarcodeEU) new ContentBarcodeEU().parse(c.getContent());
		Assert.assertEquals(c.getContent(), c1.getContent());
	}
	
	@Test
	public void testReadWriteCSV() throws FormatException, IOException, ParseException {
		ContentBarcodeEU c = content();		
		String csvString = new CSVFormat().write(Arrays.asList(c));
		List<ContentBarcodeEU> content = (List) new CSVFormat().read(csvString);
		Assert.assertEquals(c.getContent(), content.get(0).getContent());
	}

	@Test
	public void testReadWriteJson() throws FormatException, IOException, ParseException {
		ContentBarcodeEU c = content();		
		String csvString = new JsonFormat().write(Arrays.asList(c));
		List<ContentBarcodeEU> content = (List) new JsonFormat().read(csvString);
		Assert.assertEquals(c.getContent(), content.get(0).getContent());
	}

	
	private ContentBarcodeEU content() {
		ContentBarcodeEU c = new ContentBarcodeEU()
		.amount(new BigDecimal(100.10))
		.iban("IBAN12345677")
		.bic("bic")
		.currency("EUR")
		.information("info")
		.name("name")
		.purpose("purpose")
		.remittanceReference("ref")
		.remittanceText("txt");
		return c;
	}
	
}
