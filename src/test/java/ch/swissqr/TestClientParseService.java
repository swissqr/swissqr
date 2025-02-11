package ch.swissqr;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.swissqr.content.ch.formats.NameValueStringFormat;
import ch.swissqr.service.web.Main;

/**
 * Webserice tests: service/parser
 * 
 * @author pschatzmann
 *
 */
public class TestClientParseService {
	private static final Logger LOG = Logger.getLogger(TestClientParseService.class);
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
	public void testParse() throws Exception {
		String HTTP_URL = URL.URL+"/service/parser";

		Client client = ClientBuilder.newClient().register(JacksonFeature.class).register(MultiPartFeature.class);

		WebTarget webTarget = client.target(HTTP_URL).path("/objects");
		MultiPart multiPart = new MultiPart();
		multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", new File("./src/test/resources/write.png"),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		multiPart.bodyPart(fileDataBodyPart);
		multiPart.bodyPart(fileDataBodyPart);

		
		List response = webTarget.request().accept(MediaType.APPLICATION_JSON)
				.header("licenseKey", LICENSE)
				.post(Entity.entity(multiPart, multiPart.getMediaType()),ArrayList.class);
		
		LOG.info(response);
		Assert.assertEquals(2, response.size());

	}
	
}
