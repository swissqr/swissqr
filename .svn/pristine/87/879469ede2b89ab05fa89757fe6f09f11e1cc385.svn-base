package ch.swissqr;

import java.io.File;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.swissqr.service.web.Main;

/**
 * Tests related to the /service/basic
 * 
 * @author pschatzmann
 *
 */

public class TestClientBarcode {
	private static final Logger LOG = Logger.getLogger(TestClientBarcode.class);
	private static final String HTTP_URL = URL.URL+"/service/basic";
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
	public void testBarcode() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/barcode")
		.queryParam("content", "test")
		.queryParam("licenseKey", LICENSE)
		.request()
		.accept("image/png")
		.get(InputStream.class);

		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);

		File file = new File("./src/test/resources/generated/testBarcode.png");
		file.delete();
		Files.copy(is, file.toPath());
		client.close();
	}
	

	@Test
	public void testSMSBarcode() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/sms")
		.queryParam("telNo", "123456")
		.queryParam("licenseKey", LICENSE)
		.queryParam("message", "msg1")		
		.request().accept("image/png")
		.get(InputStream.class);

		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);
		File file = new File("./src/test/resources/generated/testSMS.png");
		file.delete();
		Files.copy(is, file.toPath());
		client.close();

	}
	
	@Test
	public void testPhoneBarcode() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/phone")
		.queryParam("telNo", "000 00 00 00 00")
		.queryParam("licenseKey", LICENSE)
		.request().accept("image/png")
		.get(InputStream.class);
		
		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);
		File file = new File("./src/test/resources/generated/testPhone.png");
		file.delete();
		Files.copy(is, file.toPath());
		client.close();
	}
	
	@Test
	public void testMailBarcode() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/mail")
		.queryParam("mailAddress", "phil.schatzmann@gmail.com")
		.queryParam("subject", "subject1")
		.queryParam("message", "msg1")
		.queryParam("licenseKey", LICENSE)
		.request()
		.accept("image/png")
		.get(InputStream.class);
		
		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);

		File file = new File("./src/test/resources/generated/testMail.png");
		file.delete();
		Files.copy(is, file.toPath());
		client.close();
	}
	
	@Test
	public void testVCardBarcode() throws Exception {
		Client client = ClientBuilder.newClient();
		InputStream is = client.target(HTTP_URL)
		.path("/vcard")
		.queryParam("name", "test name")
		.queryParam("phoneNumber", "number")
		.queryParam("licenseKey", LICENSE)
		.request().accept("image/png")
		.get(InputStream.class);

		Thread.sleep(1000);
		Assert.assertTrue(is.available()>0);
		File file = new File("./src/test/resources/generated/testVCard.png");
		file.delete();
		Files.copy(is, file.toPath());
		client.close();
	}


}
