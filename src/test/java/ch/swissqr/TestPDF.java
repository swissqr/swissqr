package ch.swissqr;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Test;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.content.ch.PaymentReference.ReferenceType;
import ch.swissqr.paymentslip.PaymentSlipPDF;
import ch.swissqr.pdf.Document;
import ch.swissqr.pdf.parsing.PDFContent;
import ch.swissqr.pdf.parsing.Text;

/**
 * Tests for creating and scanning PDF documents
 * 
 * @author pschatzmann
 *
 */
public class TestPDF {
	private static Logger LOG = Logger.getLogger(TestPDF.class);

	@Test
	public void testCreatePaymentSlip() throws Exception {
		ContentBarcodeCH bc = new TestBarcode().testContent(); //new TestBarcode().getContent());
		bc.getPaymentAmount().dueDate(new Date());
		for (String lang : Arrays.asList("de", "en", "it", "fr", "xx")) {
			PaymentSlipPDF ps = new PaymentSlipPDF(bc, lang, PaymentSlipPDF.Format.A4, true, true);
			File file = new File("src/test/resources/generated/test_" + lang + ".pdf");
			LOG.info(file);
			ps.save(file);
			ps.close();
		}
	}

	@Test
	public void testCreatePaymentSlipBox() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getDebitor().name((String)null);
		bc.getPaymentAmount().amount((BigDecimal)null);
		bc.getPaymentAmount().dueDate(new Date());

		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.A4, true, true);
		ps.save(new File("src/test/resources/generated/test_box.pdf"));
		ps.close();
	}
	
	@Test
	public void testCreatePaymentSlipMaxInfo() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH().alternativeSchema("test","/AlternativeSchema")
				.paymentAmount(new PaymentAmount(new BigDecimal(10000000.00), "CHF", new Date()))
				.paymentReference(new PaymentReference()
						.referenceType(ReferenceType.QRR)
						.reference(xx(27))
						.unstructuredMessage(x(140))
						.billInformation(x(140)))
				.alternativeSchema(x(10),x(100))
				.creditor(new CreditorInformation().iban(x(21))
						.creditorAddress(new Address()
						.unstructured(x(70), x(70),x(70), "CH")))
				.debitor(new Address(x(70)+","+x(16)+" 9999999999999999"+","+"9999999999999999 "+ x(35)))
				.ultimateCreditor(new Address()
						.structured(x(70), x(70), x(16), x(16),x(35), "CH"));

		bc.getPaymentAmount().dueDate(new Date());
		for (String lang : Arrays.asList("de", "en", "it", "fr", "xx")) {
			PaymentSlipPDF ps = new PaymentSlipPDF(bc, lang, PaymentSlipPDF.Format.A4, true, true);
			ps.save(new File("src/test/resources/generated/testMax_" + lang + ".pdf"));
			ps.close();
		}
	}
	
	
	private String x(int len){
		StringBuffer sb = new StringBuffer();
		for (int j=0;j<len-1;j++) {
			if (j%10==0) {
				sb.append(" ");				
			} else {
				sb.append("X");
			}
		}
		sb.append("*");
		return sb.toString();
	}

	private String xx(int len){
		StringBuffer sb = new StringBuffer();
		for (int j=0;j<len-1;j++) {
			sb.append("X");
		}
		sb.append("*");
		return sb.toString();
	}


	@Test
	public void testReadBarcodeString() throws Exception {
		Document doc = new Document(new File("src/test/resources/test_en.pdf"));
		List<String> strings = doc.getBarcodeStrings();
		Assert.assertEquals(1, strings.size());
		String str = strings.get(0);
		Assert.assertTrue(str.startsWith("SPC"));
	}

	@Test
	public void testReadBarcode() throws Exception {
		Document doc = new Document(new File("src/test/resources/test_en.pdf"));

		List<ContentBarcodeCH> list = doc.getSwissBarcodeContent();
		doc.getBarcodeContent();
		Assert.assertEquals(1, list.size());
		ContentBarcodeCH bc = list.get(0);
		Assert.assertEquals(1499.95, bc.getPaymentAmount().getAmount().doubleValue(), 0.001);
		Assert.assertEquals("CHF", bc.getPaymentAmount().getCurrency());
	}

	@Test
	public void testCombinePDF() throws Exception {
		Document doc = new Document(new File("src/test/resources/invoice-sample.pdf"));
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		doc.addPaymentSlip(bc, "en");
		doc.save(new File("src/test/resources/generated/invoice-sample-combined.pdf"));
	}

	@Test
	public void testWrite() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.Others, true, true);
		FileOutputStream os = new FileOutputStream("src/test/resources/generated/write.pdf");
		ps.write(os);
		os.close();
		ps.close();
	}

	@Test
	public void testWriteA5() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.A5, true, true);
		FileOutputStream os = new FileOutputStream("src/test/resources/generated/writeA5.pdf");
		ps.write(os);
		os.close();
		ps.close();
	}
	
	@Test
	public void testWriteA6() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.A6, true, true);
		FileOutputStream os = new FileOutputStream("src/test/resources/generated/writeA6.pdf");
		ps.write(os);
		os.close();
		ps.close();
	}

	@Test
	public void testWriteImages() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.Others, false, true);
		for (String img : Arrays.asList("svg", "gif", "jpeg", "png")) {
			FileOutputStream os = new FileOutputStream("src/test/resources/generated/write." + img);
			ps.write(os, img);
			os.close();
		}
		ps.close();
	}

	@Test
	public void testWriteImagesNoAmount() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getPaymentAmount().amount(null);
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.Others, false, true);
		for (String img : Arrays.asList("svg", "gif", "jpeg", "png")) {
			FileOutputStream os = new FileOutputStream("src/test/resources/generated/writeNoAmount." + img);
			ps.write(os, img);
			os.close();
		}
		ps.close();
	}

	@Test
	public void testWriteImagesNoDebitor() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getDebitor().name(null);
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.Others, false,true);
		for (String img : Arrays.asList("svg", "gif", "jpeg", "png")) {
			FileOutputStream os = new FileOutputStream("src/test/resources/generated/writeNoDebitor." + img);
			ps.write(os, img);
			os.close();
		}
		ps.close();
	}

	@Test
	public void testParsePDF() throws Exception {
		ContentBarcodeCH bc = new ContentBarcodeCH(new TestBarcode().getContent());
		bc.getPaymentAmount().dueDate(new Date());
		PaymentSlipPDF ps = new PaymentSlipPDF(bc, "en", PaymentSlipPDF.Format.Others, false,true);
		PDDocument doc = ps.getDocument();
		PDFContent content = new PDFContent();
		LOG.info(content.getText(doc));
		for (Text txt : content.getContent(doc)) {
			LOG.info(txt);
		}
	}

}
