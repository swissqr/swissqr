package ch.swissqr.service.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import ch.swissqr.barcode.ErrorCorrectionLevel;

import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ContentBarcodeEU;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.errors.LicenceError;
import ch.swissqr.errors.UsageViolationException;
import ch.swissqr.service.web.usage.UsageValidator;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Simple REST webserivces for the processing of one single EU Quick Response
 * Code QR Barcode
 * 
 * @author pschatzmann
 *
 */
@Path("/service/simpleeuqr")
public class QRSimpleEU {
	private final Logger LOG = Logger.getLogger(QRSimpleEU.class);
	private @Context HttpHeaders headers;

	/**
	 * Validates the correctness and completeness of the Quick Response Code
	 * @param name
	 * @param iban
	 * @param bic
	 * @param amount
	 * @param currency
	 * @param information
	 * @param purpose
	 * @param remittanceReference
	 * @param remittanceText
	 * @param format
	 * @param licenseKey
	 * @return
	 * @throws BarcodeException
	 * @throws IOException
	 * @throws LicenceError
	 */
	
	@GET
	@Path("/check")
	@Produces("application/json")

	public List<Error> simpleCheck(@QueryParam("Name") String name, @QueryParam("IBAN") String iban,
			@QueryParam("BIC") String bic, @QueryParam("Amount") String amount, @QueryParam("Currency") String currency,
			@QueryParam("Information") String information, @QueryParam("Purpose") String purpose,
			@QueryParam("RemittanceReference") String remittanceReference,
			@QueryParam("RemittanceText") String remittanceText,
			@QueryParam("pictureFormat") @DefaultValue("PNG") String format,
			@QueryParam("licenseKey") String licenseKey) throws BarcodeException, IOException, LicenceError {

		LOG.info("check");
		UsageValidator.checkEx(headers, licenseKey, 1);
		ContentBarcodeEU euCode = new ContentBarcodeEU();
		euCode.name(name).iban(iban).bic(bic).currency(currency).information(information).purpose(purpose);
		euCode.remittanceReference(remittanceReference).remittanceText(remittanceText);
		euCode.amount(amount);

		return euCode.check();
	}

	/**
	 * Create a single EU Quick Response Code
     *
	 * @param name
	 * @param iban
	 * @param bic
	 * @param amount
	 * @param currency
	 * @param information
	 * @param purpose
	 * @param remittanceReference
	 * @param remittanceText
	 * @param mm
	 * @param errorCorrectionLevel
	 * @param licenseKey
	 * @return
	 * @throws BarcodeException
	 * @throws IOException
	 * @throws LicenceError
	 */
	@GET
	@Path("/barcode")
	@Consumes({ "text/plain" })
	@Produces({ "image/gif", "image/png", "image/jpeg", "application/json" })

	public Response getBarcode(@QueryParam("Name") String name, @QueryParam("IBAN") String iban,
			@QueryParam("BIC") String bic, @QueryParam("Amount") String amount, @QueryParam("Currency") String currency,
			@QueryParam("Information") String information, @QueryParam("Purpose") String purpose,
			@QueryParam("RemittanceReference") String remittanceReference,
			@QueryParam("RemittanceText") String remittanceText, @QueryParam("Dimension") @DefaultValue("46") double mm,
			@QueryParam("ErrorCorrectionLevel") @DefaultValue("M") ErrorCorrectionLevel errorCorrectionLevel,
			@QueryParam("licenseKey") String licenseKey) throws BarcodeException, IOException, LicenceError {

		LOG.info("getBarcode");
		boolean ok = UsageValidator.check(headers, licenseKey, 1);
		ContentBarcodeEU euCode = new ContentBarcodeEU();
		euCode.setTest(!ok);
		euCode.name(name).iban(iban).bic(bic).currency(currency).information(information).purpose(purpose);
		euCode.remittanceReference(remittanceReference).remittanceText(remittanceText);
		euCode.amount(amount);

		if (!euCode.isOK()) {
			throw new BarcodeException(euCode.check().toString());
		}

		String format = getFormatFromMime();
		return Response.ok(euCode.toBarcode(format, mm, errorCorrectionLevel)).build();

	}

	@POST
	@Path("/barcode")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response getBarcode(MultivaluedMap map)
			throws ParseException, UnsupportedEncodingException, BarcodeException, IOException, LicenceError {
		LOG.info("getBarcode");
		Map simpleMap = QRSimpleSwiss.toMap(map);
		boolean ok = UsageValidator.check(headers, (String) simpleMap.get("licenseKey"), 1);

		ContentBarcodeEU content = new ContentBarcodeEU();
		content.setTest(!ok);
		content.setDataMap(simpleMap);
		String format = getFormatFromMime();
		return Response.ok(content.toBarcode(format, null, null)).build();
	}

	/**
	 * Returns the content of the barcode image file. If it is a Swiss Barcode we
	 * convert the content to a readable paramtername : content text format
	 * 
	 * @param body
	 * @return
	 * @throws IOException
	 * @throws BarcodeException
	 * @throws LicenceError
	 */

	@POST
	@Path("/imageToText")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)

	public String imageToText(FormDataMultiPart body) throws IOException, BarcodeException, LicenceError {

		LOG.info("imageToText");
		UsageValidator.checkEx(headers, null, body.getBodyParts().size());

		QRBarcode bc = new QRBarcode();
		StringBuffer result = new StringBuffer();
		for (StreamInfo si : StreamInfo.getStreams(body)) {

			String contentString = bc.readImage(si.inputStream);
			try {
				if (contentString.startsWith("BCD")) {
					if (result.length() > 0) {
						result.append(System.lineSeparator());
					}
					ContentBarcodeEU content = new ContentBarcodeEU();
					content.parse(contentString);
					contentString = content.getNameValues();
					result.append(contentString);
				}
			} catch (Exception ex) {
				LOG.warn("Could not convert to swiss barcode - we return the original content - " + ex, ex);
			}
		}
		return result.toString();
	}

	private String getFormatFromMime() {
		String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
		String result = StringUtils.getFormatFromMime(accept, "png");
		LOG.info(accept + " -> " + result);
		return result;
	}

}
