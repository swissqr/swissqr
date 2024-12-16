package ch.swissqr.service.web;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;


/**
 * Startup of the webservice on localhost. The port can be passed as parameter. If no port is
 * indicated we use port 9990.
 * 
 * @author pschatzmann
 *
 */
public class Main {
	private final static Logger LOG = Logger.getLogger(Main.class);
	private volatile static boolean active = true;
	private static String port = "9990";
	private static String host = "localhost";

	/**
	 * Constructor
	 */
	public Main() {		
	}
	/**
	 * Starts the service server
	 * @param args
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException {
		if (args.length > 0) {
			host = args[0];
		}
		if (args.length > 1) {
			port = args[1];
		}

		String url = "http://" + host + "/";

		URI baseUri = UriBuilder.fromUri(url).port(Integer.parseInt(port)).build();
		ResourceConfig config = new ResourceConfig();
		config.register(BasicService.class);
		config.register(BulkService.class);
		config.register(QRSimpleSwiss.class);
		config.register(QRSimpleEU.class);
		config.register(ParserService.class);
		config.register(SwaggerService.class);
		config.register(JacksonFeature.class);
		config.register(MultiPartFeature.class);
		config.register(GenericExceptionMapper.class);
		config.register(CORSResponseFilter.class);		

		JdkHttpServerFactory.createHttpServer(baseUri, config);
		LOG.info("HTTP Server started on " + baseUri );
		
		// For docker we need to run forever
		waitForever();
		
	}
	
	private static void waitForever() {
		while(active) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		LOG.info("**END**");
	}
	
	public static void stop() {
		active = false;
	}
	
	public static boolean isAvailable() {
	    try (Socket ignored = new Socket(host, Integer.valueOf(port))) {
	        return false;
	    } catch (IOException ignored) {
	        return true;
	    }
	}

	
}
