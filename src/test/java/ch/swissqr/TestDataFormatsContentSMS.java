package ch.swissqr;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ContentSMS;
import ch.swissqr.content.ContentTel;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.content.ch.formats.CSVFormat;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.content.ch.formats.JsonFormat;
import ch.swissqr.content.ch.formats.QRStringFormatSwiss;
import ch.swissqr.content.ch.formats.XmlFormat;
//import ch.swissqr.content.ch.formats.XmlFormat;
import ch.swissqr.errors.BarcodeException;
/**
 * Tests for the SMS barcode type
 * 
 * @author pschatzmann
 *
 */

public class TestDataFormatsContentSMS {
	private static final Logger LOG = Logger.getLogger(TestDataFormatsContentSMS.class);

	@Test
	public void testWriteJson() throws Exception {
		IContent c = content();
		String jsonInString = new JsonFormat().write(Arrays.asList(c));
		LOG.info(jsonInString);
	}
	
	@Test
	public void testReadJson() throws JsonParseException, JsonMappingException, IOException, FormatException, BarcodeException {
		InputStream is = this.getClass().getResourceAsStream("/formats/sms.json");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String jsonInString = s.hasNext() ? s.next() : "";
		List<ContentSMS> bc = (List)new JsonFormat().read(jsonInString);
		System.out.println(bc);
		Assert.assertEquals(ContentSMS.class, bc.get(0).getClass());
		Assert.assertEquals("99 99 99 99 99",bc.get(0).getTelephoneNumber());
	}	

	@Test
	public void testWriteCSV() throws FormatException {
		IContent c = content();
		String csvString = new CSVFormat().write(Arrays.asList(c));
		System.out.println(csvString);
	}
	
	@Test
	public void testReadCSV() throws FormatException, IOException, ParseException {
		InputStream is = this.getClass().getResourceAsStream("/formats/sms.csv");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String csvInString = s.hasNext() ? s.next() : "";
		
		List<ContentSMS> content =(List) new CSVFormat().read(csvInString);
		System.out.println(content);
		Assert.assertEquals(1, content.size());
		Assert.assertEquals(ContentSMS.class, content.get(0).getClass());
		Assert.assertEquals("99 99 99 99 99",content.get(0).getTelephoneNumber());
	}
	
	private ContentSMS content() {
		ContentSMS c = new ContentSMS();
		c.setTelephoneNumber("99 99 99 99 99");
		c.setMessage("message");
		return c;
	}
	
}
