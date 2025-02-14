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
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.AlternativeSchema;
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
import ch.swissqr.utils.StringUtils;

/**
 * Tests for processing the SwissQR Barcodes
 * 
 * @author pschatzmann
 *
 */

public class TestDataFormatsContentBarcodeCH {
	private static final Logger LOG = Logger.getLogger(TestDataFormatsContentBarcodeCH.class);

	@Test
	public void testWriteJson() throws Exception {
		ContentBarcodeCH c = content();
		String jsonInString = new JsonFormat().write(Arrays.asList(c));
		LOG.info(jsonInString);
	}
	
	@Test
	public void testReadJson() throws JsonParseException, JsonMappingException, IOException, FormatException, BarcodeException {
		InputStream is = this.getClass().getResourceAsStream("/formats/ch.json");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String jsonInString = s.hasNext() ? s.next() : "";
		List<IContent> bc = new JsonFormat().read(jsonInString);
		
		Assert.assertTrue(((ContentBarcodeCH)bc.get(0)).getCreditorInformation().getCreditorAddress().isDefined());
		Assert.assertTrue(((ContentBarcodeCH)bc.get(0)).getDebitor().isDefined());		
	}
		
//	@Test
//	public void testWriteXML() throws FormatException {
//		ContentBarcodeCH c = content();
//		String jsonInString = new XmlFormat().write(Arrays.asList(c));
//		System.out.println(jsonInString);
//
//	}
//	
//	@Test
//	public void testReadXML() throws FormatException, BarcodeException {
//		InputStream is = this.getClass().getResourceAsStream("/formats/ch.xml");
//		Scanner s = new Scanner(is).useDelimiter("\\A");
//		String xmlString = s.hasNext() ? s.next() : "";
//		List<IContent> bc = new XmlFormat().read(xmlString);
//
//		Assert.assertTrue(((ContentBarcodeCH)bc.get(0)).getCreditorInformation().getCreditorAddress().isDefined());
//		Assert.assertTrue(((ContentBarcodeCH)bc.get(0)).getDebitor().isDefined());				
//	}
	
	@Test
	public void testWriteText() throws FormatException {
		ContentBarcodeCH c = content();
		String jsonInString = new QRStringFormatSwiss().write(Arrays.asList(c));
		System.out.println(jsonInString);
	}
	
	@Test
	public void testReadText() throws FormatException, BarcodeException {
		ContentBarcodeCH c = content();
		QRStringFormatSwiss sf = new QRStringFormatSwiss();
		String str = sf.write(Arrays.asList(c));
		List<IContent> c1 = sf.read(str);

		String write = sf.write(c1);
		System.out.println(str);
		System.out.println(write);
		Assert.assertEquals(str, write);
	}
	
	
	@Test
	public void testWriteCSV() throws FormatException {
		ContentBarcodeCH c = content();
		String csvString = new CSVFormat().write(Arrays.asList(c));
		System.out.println(csvString);
		Assert.assertTrue(csvString.contains(",IBAN,"));
	}
	
	@Test
	public void testReadCSV() throws FormatException, IOException, ParseException {
		InputStream is = this.getClass().getResourceAsStream("/formats/ch.csv");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String csvInString = s.hasNext() ? s.next() : "";
		
		List<IContent> content = new CSVFormat().read(csvInString);
		Assert.assertEquals(1, content.size());
	}

	@Test
	public void testCRLF1() throws FormatException, IOException, ParseException {
		ContentBarcodeCH c = new ContentBarcodeCH()
		.paymentAmount(new PaymentAmount(100.10))
		.paymentReference(new PaymentReference("Invoice no 123").billInformation("bill-info"))
		.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"))
		.alternativeSchema(new AlternativeSchema("schema1","/par1")).alternativeSchema(new AlternativeSchema("schema2","/par2"));
		System.out.println("alt schema & bill info");
		System.out.println("****************");
		System.out.println(c.toString());		
		System.out.println("****************");
		Assert.assertFalse(c.toString().endsWith(StringUtils.CRLF));	
	}
	@Test
	public void testCRLF2() throws FormatException, IOException, ParseException {
		ContentBarcodeCH c = new ContentBarcodeCH()
		.paymentAmount(new PaymentAmount(100.10))
		.paymentReference(new PaymentReference("Invoice no 123"))
		.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"))
		.alternativeSchema(new AlternativeSchema("schema1","/par1")).alternativeSchema(new AlternativeSchema("schema2","/par2"));
		System.out.println("alt schema no bill info");
		System.out.println("****************");
		System.out.println(c.toString());		
		System.out.println("****************");
		Assert.assertFalse(c.toString().endsWith(StringUtils.CRLF));	
	}

	@Test
	public void testCRLF3() throws FormatException, IOException, ParseException {
		ContentBarcodeCH c = new ContentBarcodeCH()
		.paymentAmount(new PaymentAmount(100.10))
		.paymentReference(new PaymentReference("Invoice no 123"))
		.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"));
		System.out.println("no alt schema no bill info");
		System.out.println("****************");
		System.out.println(c.toString());		
		System.out.println("****************");
		Assert.assertFalse(c.toString().endsWith(StringUtils.CRLF));	
	}
	
	
	private ContentBarcodeCH content() {
		ContentBarcodeCH c = new ContentBarcodeCH()
		.paymentAmount(new PaymentAmount(100.10))
		.paymentReference(new PaymentReference("Invoice no 123").billInformation("bill-info"))
		.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"))
		.alternativeSchema(new AlternativeSchema("schema1","/par1")).alternativeSchema(new AlternativeSchema("schema2","/par2"));
		return c;
	}
	
}
