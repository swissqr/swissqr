package ch.swissqr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.glassfish.jersey.internal.Errors;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.barcode.QRSwissBarcode;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.AlternativeSchema;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.content.ch.PaymentReference.ReferenceType;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Tests for the creation of barcodes 
 * 
 * @author pschatzmann
 *
 */
public class TestBarcode {
	public String getContent() {
		return StringUtils.loadResource(this.getClass(), "content.txt");
	}
	
	@Test
	public void testBarCode() throws Exception {
		IBarcode bc = new QRBarcode();
		byte ba[] =  bc.create(getContent(), "PNG");
		FileOutputStream fos = new FileOutputStream("src/test/resources/test.png");
		fos.write(ba);
		fos.close();
	}

	@Test
	public void testBarCodeBufferedImage() throws Exception {
		IBarcode bc = new QRBarcode();
		BufferedImage image =  bc.createImage(getContent(), "PNG");
		FileOutputStream fos = new FileOutputStream("src/test/resources/testBuffered.png");
		ImageIO.write(image, "PNG", fos);
		fos.close();
	}

	
	@Test
	public void testSwissBarCode() throws Exception {
		IBarcode bc = new QRSwissBarcode();
		byte ba[] =  bc.create(getContent(), "PNG");
		FileOutputStream fos = new FileOutputStream("src/test/resources/testSwiss.png");
		fos.write(ba);
		fos.close();
	}
	
	public ContentBarcodeCH testContent() throws Exception {
		//PaymentAmount pa = new PaymentAmount(100.10);
		//PaymentReference pr = new PaymentReference(ReferenceType.QRR,"210000000003139471430009017","");
		
		//ContentBarcodeCH c = new ContentBarcodeCH().paymentAmount(pa).paymentReference(pr)
		//.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		//.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"));
		
		ContentBarcodeCH bc = new ContentBarcodeCH().alternativeSchema(new AlternativeSchema("testAlternativeSchema","/par"))
				.paymentAmount(new PaymentAmount(new BigDecimal(10000000.00), "CHF", new Date()))
				.paymentReference(new PaymentReference()
						.referenceType(ReferenceType.NON)
						.reference("Reference Number")
						.unstructuredMessage("This is a unstructured message")
						.billInformation("Bill information"))
				.alternativeSchema("altSchema","/par1")
				.alternativeSchema("altSchema1","/par2")
				.creditor(new CreditorInformation().iban("IBAN12345677")
						.creditorAddress(new Address()
						.unstructured("Phil Schatzmann", "Stutzhaldenstrasse 3","8834 Schindellegi", "CH")))
				.debitor(new Address("Mr Debitor, Debitor 3, 8834 DebitorCity"))
				.ultimateCreditor(new Address()
						.structured("UTD", "UDT Street", "UTD no", "UTD ZIP","Schindellegi", "CH"));
		System.out.println(bc);
		return bc;
	}
	
	@Test
	public void testBarcodeJson() throws Exception {
		ContentBarcodeCH bc = testContent();
		
		File file = new File("./src/test/resources/generated/swissqr.json");
		file.delete();
		System.out.println(Arrays.asList(bc));
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(new File("src/test/resources/generated/swissqr.json"), bc);
		System.out.println(objectMapper.writeValueAsString(Arrays.asList(bc)));
	}
	
	public void testContentBarcode() throws Exception {
		PaymentAmount pa = new PaymentAmount(100.10);
		PaymentReference pr = new PaymentReference("Invoice no 123");
		
		ContentBarcodeCH c = new ContentBarcodeCH().paymentAmount(pa).paymentReference(pr)
		.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"));
		
		if (c.isOK()) {
		
			List<Error> errors = c.check();
		}
		
		BufferedImage image = c.toBarcode("PNG");
		FileOutputStream fos = new FileOutputStream("src/test/resources/test1.png");
		ImageIO.write(image, "PNG", fos);
		fos.close();
	}
	
	@Test
	public void testMap() throws Exception {
		PaymentAmount pa = new PaymentAmount(100.10);
		PaymentReference pr = new PaymentReference("Invoice no 123").billInformation("bill-info");
		ContentBarcodeCH c = new ContentBarcodeCH().paymentAmount(pa).paymentReference(pr)
		.creditor(new CreditorInformation().iban("IBAN12345677").creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
		.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"))
		.alternativeSchema("title","schema");
		
		Map<String,Object> map = c.getDataMap();
		ContentBarcodeCH target = new ContentBarcodeCH();
		target.setDataMap(map);
				
		Assert.assertEquals(c.getContent(), target.getContent());
	}
	
	@Test
	public void testLoadFromContentString() throws ParseException, FormatException, BarcodeException {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());	
		Assert.assertEquals(1499.95, bc.getPaymentAmount().getAmount().doubleValue(),0.001);
		Assert.assertEquals("CHF", bc.getPaymentAmount().getCurrency());
	}
		

}
