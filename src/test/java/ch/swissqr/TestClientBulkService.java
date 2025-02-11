package ch.swissqr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.service.web.Main;
import ch.swissqr.utils.Error;

/**
 * Tests related to /service/objects
 * 
 * @author pschatzmann
 *
 */
public class TestClientBulkService {
	private static final Logger LOG = Logger.getLogger(TestClientBulkService.class);
	private static final String HTTP_URL = URL.URL+"/service/objects";
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
	public void testCheckBarCodeJson() throws Exception {
		ContentBarcodeCH c = object();
		List<ContentBarcodeCH> l = Arrays.asList(c);
		Client client = getClient();
		
		WebTarget webTarget = client.target(HTTP_URL).path("/check");
		List<Error> r = webTarget.request()
				.header("licenseKey",LICENSE)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.json(l),ArrayList.class);
	
		LOG.info(r);
		Assert.assertEquals(false, r.isEmpty());
	}

	
	@Test
	public void testCreateBarCodeJson() throws Exception {
		ContentBarcodeCH c = object();
		List<IContent> l = Arrays.asList(c);
		Client client = getClient();
		LOG.info(HTTP_URL);
		
		WebTarget webTarget = client.target(HTTP_URL).path("/barcodes");
		InputStream inputStream = webTarget.request()
				.header("licenseKey",LICENSE)
				.accept("application/zip")
				.post(Entity.json(l), InputStream.class);
		
		File file = new File("./src/test/resources/generated/testCreateBarCodeJson.zip");
		file.delete();
		Files.copy(inputStream, file.toPath());
		testZip(file);
		Assert.assertTrue(inputStream!=null);
	}
	
	@Test
	public void testCreateBarCodeCSV() throws Exception {
		InputStream is = this.getClass().getResourceAsStream("/barcodesCH.csv");
		Scanner s = new Scanner(is).useDelimiter("\\A");
		String csv = s.hasNext() ? s.next() : "";
		
		Client client = getClient();
		WebTarget webTarget = client.target(HTTP_URL).path("/barcodes");
		InputStream inputStream = webTarget.request()
				.header("licenseKey",LICENSE)
				.accept("application/zip")
				.post(Entity.text(csv), InputStream.class);
		
		File file = new File("./src/test/resources/generated/testCreateBarCodeCSV.zip");
		file.delete();
		Files.copy(inputStream, file.toPath());
		testZip(file);
		Assert.assertTrue(inputStream!=null);
	}
	
	@Test
	public void testCreatePaymentSlipJson() throws Exception {
		ContentBarcodeCH c = object();
		List<IContent> l = Arrays.asList(c);
		Client client = getClient();
		WebTarget webTarget = client.target(HTTP_URL)
				.path("/paymentslips");
		InputStream inputStream = webTarget.request()
				.header("licenseKey",LICENSE)
				.accept("application/zip")
				.post(Entity.json(l),InputStream.class);
		File file = new File("./src/test/resources/generated/testCreatPaymentslipJson.zip");
		file.delete();
		Files.copy(inputStream, file.toPath());
		testZip(file);
	
		Assert.assertTrue(inputStream!=null);
	}

	private void testZip(File file) throws ZipException, IOException {
	    Assert.assertTrue(new ZipFile(file).entries().hasMoreElements());
	}

	private Client getClient() {
		Client client = ClientBuilder.newClient().register(JacksonFeature.class);//.register(JsonProcessingFeature.class);
		return client;
	}

	private ContentBarcodeCH object() {
		ContentBarcodeCH c = new ContentBarcodeCH().paymentAmount(new PaymentAmount(100.10))
				.paymentReference(new PaymentReference("Invoice no 123"))
				.creditor(new CreditorInformation().iban("IBAN12345677")
						.creditorAddress(new Address("Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi")))
				.debitor(new Address("Herr Phil Schatzmann, Stutzhaldenstrasse 3, 8834 Schindellegi"));
		return c;
	}

}
