package ch.swissqr;

import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.swissqr.content.ch.formats.NameValueStringFormat;
import ch.swissqr.errors.ErrorInformation;
import ch.swissqr.service.web.Main;

/**
 * Tests related to the /service/basic
 * 
 * @author pschatzmann
 *
 */

public class TestClientBarcodeCHService {
	private static final Logger LOG = Logger.getLogger(TestClientBarcodeCHService.class);
	private static final String HTTP_URL = URL.URL+"/service/simpleswissqr";
	private static final String LICENSE = "NzcbBE5fwwUhABmlTLWKFPPGeMaMnreTObGBzAR4vjQ=";

	@BeforeClass
	public static void setup() throws InterruptedException {
	     Runnable r = new Runnable() {
	         public void run() {
	        	 	try {
        	 			if (Main.isAvailable()) {
        	 				Main.main(new String[0]);
        	 			}
				} catch (UnknownHostException ex) {
					LOG.error(ex,ex);
				}
	         }
	     };

	     Thread thread = new Thread(r);
	     thread.start();
	     
		 Thread.sleep(2000);
	}
	
	@AfterClass
	public static void shutdown() throws InterruptedException {
		Main.stop();
		Thread.sleep(5000);

	}
	
	@Test
	public void testBarcodeCH() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/barcode")
		.queryParam("Creditor", "Robert Schneider AG,Rue du Lac 1268/2/22,2501 Biel")
		.queryParam("Debitor", "Pia-Maria Rutschmann-Schnyder,Grosse Marktgasse 28,9400 Rorschach")
		.queryParam("ReferenceType", "QRR")		
		.queryParam("Reference", "210000000003139471430009017")
		.queryParam("AdditionalInformation", "Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010")
		.queryParam("BillInformation","bill-info")
		.queryParam("AlternativeSchema", "alt1")
		.queryParam("AlternativeSchemaParameters", "UV1;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15: 12:39:22 test")
		.queryParam("AlternativeSchema1", "alt2")
		.queryParam("AlternativeSchemaParameters1", "/test")
		.queryParam("DueDate", "2019-10-31")
		.queryParam("IBAN", "CH4431999123000889012")
		.queryParam("Amount", "123949.75")
		.queryParam("Currency", "CHF")
		.queryParam("licenseKey", LICENSE)
		.request()
		.accept("image/png")
		.get(InputStream.class);
		
		Assert.assertTrue(is.available()>0);

		// we store the result in a file
		File file = new File("./src/test/resources/generated/testBarcodeCH.png");
		file.delete();
		Files.copy(is, file.toPath());
	}
	
	@Test
	public void testBarcodeCHHeader() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/barcode")
		.queryParam("Creditor", "Robert Schneider AG,Rue du Lac 1268/2/22,2501 Biel")
		.queryParam("Debitor", "Pia-Maria Rutschmann-Schnyder,Grosse Marktgasse 28,9400 Rorschach")
		.queryParam("ReferenceType", "QRR")		
		.queryParam("Reference", "210000000003139471430009017")
		.queryParam("AdditionalInformation", "Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010")
		.queryParam("AlternativeSchema", "alt1")
		.queryParam("AlternativeSchemaParameters", "UV1;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15: 12:39:22 test")
		.queryParam("AlternativeSchema1", "alt2")
		.queryParam("AlternativeSchemaParameters1", "/test")
		.queryParam("DueDate", "2019-10-31")
		.queryParam("IBAN", "CH4431999123000889012")
		.queryParam("Amount", "123949.75")
		.queryParam("Currency", "CHF")
		.queryParam("licenseKey", LICENSE)
		.request()
		.header("licenseKey", LICENSE)
		.accept("image/png")
		.get(InputStream.class);

		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);
	}
	
	@Test
	public void testPaymentSlipCH() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/paymentslip")
		.queryParam("Creditor", "Robert Schneider AG,Rue du Lac 1268/2/22,2501 Biel")
		.queryParam("Debitor", "Pia-Maria Rutschmann-Schnyder,Grosse Marktgasse 28,9400 Rorschach")
		.queryParam("ReferenceType", "QRR")		
		.queryParam("Reference", "210000000003139471430009017")
		.queryParam("AdditionalInformation", "Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010")
		.queryParam("BillInformation","bill-info")
		.queryParam("AlternativeSchema", "alt1")
		.queryParam("AlternativeSchemaParameters", "UV1;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15: 12:39:22 test1234567890")
		.queryParam("AlternativeSchema1", "alt2")
		.queryParam("AlternativeSchemaParameters1", "/test")
		.queryParam("DueDate", "2019-10-31")
		.queryParam("IBAN", "CH4431999123000889012")
		.queryParam("Amount", "123949.75")
		.queryParam("Currency", "CHF")
		.queryParam("language", "en")
		.queryParam("pageFormat", "A4")
		.queryParam("printLines", "true")
		//.queryParam("licenseKey", LICENSE)
		.request()
		.accept("image/png")
		.get(InputStream.class);
		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);

		// we store the result in a file
		File file = new File("./src/test/resources/generated/testPaymentslipCH.png");
		file.delete();
		Files.copy(is, file.toPath());
	}
	
	


	@Test
	public void testBarcodeCHCheck() throws Exception {
		Client client = ClientBuilder.newClient();
		List<Map> list = client.target(HTTP_URL)
		.path("/check")
		.queryParam("Creditor", "Robert Schneider AG,Rue du Lac 1268/2/22,2501 Biel")
		.queryParam("Debitor", "Pia-Maria Rutschmann-Schnyder,Grosse Marktgasse 28,9400 Rorschach")
		.queryParam("ReferenceType", "QRR")		
		.queryParam("Reference", "210000000003139471430009017")
		.queryParam("AdditionalInformation", "Instruction of 15.09.2019##S1/01/20170309/11/10201409/20/14000000/22/36958/30/CH106017086/40/1020/41/3010")
		.queryParam("AlternativeSchema", "alt1")
		.queryParam("AlternativeSchemaParameters", "UV1;1.1;1278564;1A-2F-43-AC-9B-33-21-B0-CC-D4-28-56;TCXVMKC22;2019-02-10T15: 12:39:22 test")
		.queryParam("AlternativeSchema1", "alt2")
		.queryParam("AlternativeSchemaParameters1", "/test")
		.queryParam("DueDate", "2019-10-31")
		.queryParam("IBAN", "CH4431999123000889012")
		.queryParam("Amount", "123949.75")
		.queryParam("Currency", "CHF")
		.queryParam("licenseKey", LICENSE)
		.request()
		.accept("application/json")
		.get(new GenericType<ArrayList<Map>>() {});

		LOG.info(list);

		Assert.assertTrue(list.isEmpty());
	}
	

	@Test
	public void testParseSimple() throws Exception {
		String HTTP_URL = URL.URL+"/service/simpleswissqr";
		
		Client client = ClientBuilder.newClient().register(MultiPartFeature.class);
		WebTarget webTarget = client.target(HTTP_URL)
				.path("/imageToText");

		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", new File("./src/test/resources/generated/testBarcodeCH.png"),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		multiPart.bodyPart(fileDataBodyPart);
	
		String response = webTarget.request().header("licenseKey",LICENSE).accept(MediaType.TEXT_PLAIN)
				.post(Entity.entity(multiPart, multiPart.getMediaType()),String.class);
		
		LOG.info(response);
		
		Assert.assertFalse(response.isEmpty());
		Assert.assertTrue(new NameValueStringFormat().read(response).get(0).getDataMap().size()>0);

	}


}
