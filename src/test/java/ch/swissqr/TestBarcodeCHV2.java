package ch.swissqr;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.MapData;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.content.ch.PaymentReference.ReferenceType;
import ch.swissqr.content.ch.formats.CSVFormat;
import ch.swissqr.content.ch.formats.JsonFormat;
import ch.swissqr.content.ch.formats.QRStringFormatSwiss;
import ch.swissqr.utils.StringUtils;

/**
 * Test for the changes to Version 2.0 of the SwissQR Specification.
 * 
 * @author pschatzmann
 *
 */
public class TestBarcodeCHV2 {


	@Test
	public void testAddressUnstructured() throws Exception {
		Address adr = new Address().unstructured("name", "line1", "line2", "CH");
		Assert.assertEquals(adr.getAddressType().name(), "U");
	}

	@Test
	public void testAddressUnstructured1() throws Exception {
		Address adr = new Address().name("name").addressLine1("line1").addressLine2("line2").country("CH");
		Assert.assertEquals(adr.getAddressType().name(), "U");
	}

	@Test
	public void testAddressStructured() throws Exception {
		Address adr = new Address().structured("name", "street", "nr", "plz", "city", "CH");
		Assert.assertEquals(adr.getAddressType().name(), "S");
	}

	@Test
	public void testAddressStructured1() throws Exception {
		Address adr = new Address().name("name").street("street").houseNumber("nr").postalCode("plz").city("city").country("CH");
		Assert.assertEquals(adr.getAddressType().name(), "S");
	}

	@Test
	public void testJson() throws Exception {
		ContentBarcodeCH c = content();
		String original = new JsonFormat().write(Arrays.asList(c));

		List<IContent> bc = new JsonFormat().read(original);
		String copy = new JsonFormat().write(bc);

		Assert.assertTrue(checkCompleteness((ContentBarcodeCH) bc.get(0)).isEmpty());
		Assert.assertEquals(original, copy);

	}

	@Test
	public void testPNG() throws Exception {
		IBarcode bc = new QRBarcode();
		byte ba[] =  bc.create(content().getContent(), "PNG");
		FileOutputStream fos = new FileOutputStream("src/test/resources/generated/testV2.png");
		fos.write(ba);
		fos.close();
	}

	
	
	@Test
	public void testString() throws Exception {
		ContentBarcodeCH c = content();
		String original = new QRStringFormatSwiss().write(Arrays.asList(c));

		List<IContent> bc = new QRStringFormatSwiss().read(original);
		String copy = new QRStringFormatSwiss().write(Arrays.asList(c));
		
		Assert.assertEquals(original, copy);
		Assert.assertTrue(checkCompleteness((ContentBarcodeCH) bc.get(0)).isEmpty());

	}

	@Test
	public void testCSV() throws Exception {
		ContentBarcodeCH c = content();
		String original = new CSVFormat().write(Arrays.asList(c));

		List<IContent> bc = new CSVFormat().read(original);
		String copy = new CSVFormat().write(Arrays.asList(c));

		Assert.assertTrue(checkCompleteness((ContentBarcodeCH) bc.get(0)).isEmpty());
		Assert.assertEquals(original, copy);

	}

	@Test
	public void testMap() throws Exception {
		ContentBarcodeCH c = content();

		String originalStr = new QRStringFormatSwiss().write(Arrays.asList(c));
		Map originalMap = new MapData().contentToMap(c);
		System.out.println("originalMap:"+originalMap);

		ContentBarcodeCH copy = new ContentBarcodeCH();
		new MapData().mapToContent(originalMap, copy);
		Map copyMap = new MapData().contentToMap(copy);
		System.out.println("copyMap:    "+copyMap);
		
		String copyStr = new QRStringFormatSwiss().write(Arrays.asList(copy));
		System.out.println("originalStr:"+originalStr);
		System.out.println("copyStr:"+copyStr);

		Assert.assertTrue(checkCompleteness(copy).isEmpty());
		Assert.assertEquals(originalStr, copyStr);

	}

	protected List<String> checkCompleteness(ContentBarcodeCH bc) {
		List<String> missingFields = new ArrayList();
		Map barcodeMap = new MapData().contentToMap(bc);
		checkField("CreditorName", barcodeMap, missingFields);
		checkField("CreditorAddressLine1", barcodeMap, missingFields);
		checkField("CreditorAddressLine2", barcodeMap, missingFields);
		// the following is empty for unstructured fields
		// checkField("CreditorStreet", barcodeMap, missingFields);
		// checkField("CreditorHouseNumber", barcodeMap, missingFields);
		// checkField("CreditorPostalCode", barcodeMap, missingFields);
		// checkField("CreditorCity", barcodeMap, missingFields);
		checkField("CreditorCountry", barcodeMap, missingFields);
		checkField("UltimateCreditorName", barcodeMap, missingFields);
		checkField("UltimateCreditorAddressLine1", barcodeMap, missingFields);
		checkField("UltimateCreditorAddressLine2", barcodeMap, missingFields);
		checkField("UltimateCreditorStreet", barcodeMap, missingFields);
		checkField("UltimateCreditorHouseNumber", barcodeMap, missingFields);
		checkField("UltimateCreditorPostalCode", barcodeMap, missingFields);
		checkField("UltimateCreditorCity", barcodeMap, missingFields);
		checkField("UltimateCreditorCountry", barcodeMap, missingFields);
		checkField("Amount", barcodeMap, missingFields);
		checkField("Currency", barcodeMap, missingFields);
		//checkField("DueDate", barcodeMap, missingFields);
		checkField("DebitorName", barcodeMap, missingFields);
		checkField("DebitorAddressLine1", barcodeMap, missingFields);
		checkField("DebitorAddressLine2", barcodeMap, missingFields);
		checkField("DebitorStreet", barcodeMap, missingFields);
		checkField("DebitorHouseNumber", barcodeMap, missingFields);
		checkField("DebitorPostalCode", barcodeMap, missingFields);
		checkField("DebitorCity", barcodeMap, missingFields);
		checkField("DebitorCountry", barcodeMap, missingFields);
		checkField("ReferenceType", barcodeMap, missingFields);
		checkField("Reference", barcodeMap, missingFields);
		checkField("Message", barcodeMap, missingFields);
		checkField("BillInformation", barcodeMap, missingFields);
		checkField("UnstructuredMessage", barcodeMap, missingFields);
		//checkField("AlternativeSchemaParameters", barcodeMap, missingFields);

		System.out.println("Missing fields: " + missingFields);
		return missingFields;

	}

	private void checkField(String fld, Map<String, String> barcode, List<String> list) {
		String value = barcode.get(fld);
		if (StringUtils.isEmpty(value)) {
			list.add(fld);
		}
	}

	/*
	 * Example with all fields filled out
	 */

	protected ContentBarcodeCH content() {
		ContentBarcodeCH bc = new ContentBarcodeCH()
				.paymentAmount(new PaymentAmount(new BigDecimal(100.00), "CHF", new Date()))
				.paymentReference(new PaymentReference()
						.referenceType(ReferenceType.QRR)
						.reference("21 00000 00003 13947 14300 09017")
						.unstructuredMessage("msg")
						.billInformation("BillInfo"))
				.alternativeSchema("altSchema","/par")
				.alternativeSchema("altSchema1","/par1")
				.creditor(new CreditorInformation().iban("IBAN12345677")
						.creditorAddress(new Address()
						.unstructured("Phil Schatzmann", "Stutzhaldenstrasse 3","8834 Schindellegi", "CH")))
				.debitor(new Address("Mr Debitor, Debitor 3, 8834 DebitorCity"))
				.ultimateCreditor(new Address()
						.structured("UTD", "UDT Street", "UTD no", "UTD ZIP","Schindellegi", "CH"));
		return bc;
	}

}
