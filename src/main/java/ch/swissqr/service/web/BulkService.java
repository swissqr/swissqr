package ch.swissqr.service.web;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.formats.AnyFormat;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.service.web.usage.UsageValidator;
import ch.swissqr.utils.Error;

/**
 * REST web-services for the bulk processing of multiple barcodes. The result is
 * returned as zip file. The standard json conversion is not flexible enough for
 * our purpose because the data type is not exactly known. Therfore we perform
 * the conversion ourselfs
 *
 * @author pschatzmann
 */

@Path("/service/objects")
public class BulkService implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Logger LOG = Logger.getLogger(BulkService.class);
	private @Context HttpHeaders headers;

	/**
	 * <p>Constructor for BulkService.</p>
	 */
	public BulkService() {
		LOG.info("SwissQR");
	}

	/**
	 * Checks the completeness and correctness of the provided data
	 *
	 * @throws java.lang.Exception
	 * @param barcodes a {@link java.lang.String} object
	 * @return a {@link java.util.List} object
	 */

	@POST
	@Path("/check")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	@Produces(MediaType.APPLICATION_JSON)
	public List<Error> check(String barcodes) throws Exception {
		List<Error> result = new ArrayList();
		LOG.info(headers.getRequestHeaders());
		List<IContent> barcodeList = getContent(barcodes);
		
		UsageValidator.checkEx(headers, null, barcodeList.size());
		
		for (IContent content : barcodeList) {
			result.addAll(content.check());
		}
		return result;
	}

	/**
	 * Get one or multiple barcode images
	 *
	 * @throws java.lang.Exception
	 * @param barcodes a {@link java.lang.String} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 */
	@POST
	@Path("/barcodes")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	@Produces({"application/zip", MediaType.APPLICATION_JSON})
	public Response getBarcodes(String barcodes) throws Exception {
		LOG.info("barcodes");
		List<IContent> barcodeList = this.getContent(barcodes);
		boolean ok = UsageValidator.check(headers, null, barcodeList.size());
		for(IContent c : barcodeList) {
			c.setTest(!ok);
		}

		if (barcodes.isEmpty()) {
			LOG.info("the content is empty");
		}
		StreamingOutput stream = CommonServicesAPI.getBarcodeStreamingOutput(barcodeList);
		return Response.ok(stream, "application/zip").build();
	}

	/**
	 * Get one or multiple paymentslip images
	 *
	 * @throws java.lang.Exception
	 * @param barcodes a {@link java.lang.String} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 */
	@POST
	@Path("/paymentslips")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
	@Produces({"application/zip", MediaType.APPLICATION_JSON})
	public Response getPaymentSlips(String barcodes)throws Exception {
		List<IContent> barcodeList = this.getContent(barcodes);
		List<ContentBarcodeCH> chBarcodes = toContentBarcodeCH(barcodeList);
		boolean ok = UsageValidator.check(headers, null, chBarcodes.size());
		for(IContent c : barcodeList) {
			c.setTest(!ok);
		}

		StreamingOutput stream = CommonServicesAPI.getPaymentSlipStreamingOutput(chBarcodes);
		return Response.ok(stream, "application/zip").build();
	}

	private List<ContentBarcodeCH> toContentBarcodeCH(List<IContent> list) {
		List<ContentBarcodeCH> result = new ArrayList();
		for (IContent c : list) {
			if (c instanceof ContentBarcodeCH) {
				result.add((ContentBarcodeCH) c);
			}
		}
		return result;
	}

	private List<IContent> getContent(String contentString) throws FormatException, IOException, ParseException, BarcodeException {
		return AnyFormat.read(contentString);
	}

}
