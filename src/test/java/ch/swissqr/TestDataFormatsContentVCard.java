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
import ch.swissqr.content.ContentMail;
import ch.swissqr.content.ContentSMS;
import ch.swissqr.content.ContentTel;
import ch.swissqr.content.ContentVCard;
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
 * Tests for the VCard barcode type
 * 
 * @author pschatzmann
 *
 */

public class TestDataFormatsContentVCard {
	private static final Logger LOG = Logger.getLogger(TestDataFormatsContentVCard.class);

	@Test
	public void testWriteJson() throws Exception {
		IContent c = content();
		String jsonInString = new JsonFormat().write(Arrays.asList(c));
		LOG.info(jsonInString);
	}
	
	@Test
	public void testReadJson() throws JsonParseException, JsonMappingException, IOException, FormatException, BarcodeException {
		InputStream is = this.getClass().getResourceAsStream("/formats/vcard.json");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String jsonInString = s.hasNext() ? s.next() : "";
		List<ContentVCard> bc = (List)new JsonFormat().read(jsonInString);
		System.out.println(bc);
		Assert.assertEquals(ContentVCard.class, bc.get(0).getClass());
		Assert.assertEquals("Phil Schatzmann",bc.get(0).getFormattedName());
	}	

	@Test
	public void testWriteCSV() throws FormatException {
		IContent c = content();
		String csvString = new CSVFormat().write(Arrays.asList(c));
		System.out.println(csvString);
	}
	
	@Test
	public void testReadCSV() throws FormatException, IOException, ParseException {
		InputStream is = this.getClass().getResourceAsStream("/formats/vcard.csv");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String csvInString = s.hasNext() ? s.next() : "";
		
		List<ContentVCard> content =(List) new CSVFormat().read(csvInString);
		System.out.println(content);
		Assert.assertEquals(1, content.size());
		Assert.assertEquals(ContentVCard.class, content.get(0).getClass());
		Assert.assertEquals("Phil Schatzmann",content.get(0).getFormattedName());
	}
	
	private ContentVCard content() {
		ContentVCard c = new ContentVCard();
		c.setFormattedName("Phil Schatzmann");
		c.setAddress("adr");
		c.setEmail("email");
		c.setOrganization("company");
		c.setTelephoneNumber("phone");
		c.setTitle("title");
		c.setUrl("url");
		return c;
	}
	
}
