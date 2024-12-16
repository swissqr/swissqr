package ch.swissqr.service.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import ch.swissqr.barcode.ErrorCorrectionLevel;
import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.barcode.QRCombinedBarcode;
import ch.swissqr.barcode.QRSwissBarcode;
import ch.swissqr.content.ContentMail;
import ch.swissqr.content.ContentSMS;
import ch.swissqr.content.ContentTel;
import ch.swissqr.content.ContentVCard;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.formats.QRStringFormatSwiss;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.errors.LicenceError;
import ch.swissqr.errors.UsageViolationException;
import ch.swissqr.service.web.usage.UsageValidator;
import ch.swissqr.utils.StringUtils;

/**
 * Basic REST webservices to generate QR barcodes
 * 
 * @author pschatzmann
 *
 */

@Path("/service/basic")
public class BasicService {
	private final Logger LOG = Logger.getLogger(BasicService.class);
	private @Context HttpHeaders headers;

	public BasicService() {
		LOG.info("BarcodeService");
	}

	/**
	 * Test if the service is up and running...
	 * 
	 * @return
	 */
	@GET
	@Path("/ping")
	@Produces("text/plain")
	public String ping() {
		return "pong";
	}

	@GET
	@Path("/swagger")
	@Produces("application/yaml")
	public String swagger() {
		String fileName = "swagger.yaml";
		Locale loc = headers.getLanguage();
		if (loc != null) {
			String language = loc.getLanguage();
			if (language.equalsIgnoreCase("de")) {
				LOG.info("german swagger requested");
				fileName = "swagger.yaml";
			}
		}
		LOG.info("swagger " + fileName);
		String result = StringUtils.loadResource(this.getClass(), fileName);
		return result;
	}

	/**
	 * Returns a barcode image for the indicated content string. We only render the
	 * swiss cross when there are no errors
	 * 
	 * @param contentString
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
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response getBarcode(@QueryParam("content") String contentString,
			@QueryParam("dimension") @DefaultValue("46") double mm,
			@QueryParam("errorCorrectionLevel") @DefaultValue("M") ErrorCorrectionLevel errorCorrectionLevel,
			@QueryParam("licenseKey") String licenseKey) throws BarcodeException, IOException, LicenceError {

		LOG.info("getBarcode");
		boolean ok = UsageValidator.check(headers, licenseKey, 1);

		IBarcode barcodeSwiss = new QRSwissBarcode(!ok);
		IBarcode barcode = new QRBarcode(mm, errorCorrectionLevel);
		if (!ok) {
			barcode = new QRCombinedBarcode("/icons/test.png", mm, errorCorrectionLevel);
		}
		IBarcode bc = null;
		try {
			QRStringFormatSwiss sf = new QRStringFormatSwiss();
			List<IContent> contents = sf.read(contentString);
			bc = contents.get(0).isOK() ? barcodeSwiss : barcode;
		} catch (Exception ex) {
			LOG.warn(ex);
			bc = barcode;
		}
		return Response.ok(bc.createImage(StringUtils.str(contentString), getFormatFromMime())).build();
	}

	/**
	 * Barcode service for mail
	 * 
	 * @param to
	 * @param subject
	 * @param message
	 * @param mm
	 * @param errorCorrectionLevel
	 * @param licenseKey
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws BarcodeException
	 * @throws IOException
	 * @throws LicenceError
	 */
	 
	@GET
	@Path("/mail")
	@Produces({ "image/png", "image/gif", "image/jpeg" })

	public Response getBarcodeMail(@QueryParam("mailAddress") String to, @QueryParam("subject") String subject,
			@QueryParam("message") String message, @QueryParam("dimension") @DefaultValue("46") double mm,
			@QueryParam("errorCorrectionLevel") @DefaultValue("H") ErrorCorrectionLevel errorCorrectionLevel,
			@QueryParam("licenseKey") String licenseKey)
			throws UnsupportedEncodingException, BarcodeException, IOException, LicenceError {

		boolean valid = UsageValidator.check(headers, licenseKey, 1);
		String format = getFormatFromMime();
		ContentMail c = new ContentMail(to, subject, message);
		c.setTest(!valid);
		return Response.ok(c.toBarcode(format, mm, errorCorrectionLevel)).build();
	}

	/**
	 * Barcode service for sms /**
	 * 
	 * @param to
	 * @param message
	 * @param mm
	 * @param errorCorrectionLevel
	 * @param licenseKey
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws BarcodeException
	 * @throws IOException
	 * @throws LicenceError
	 */
	@GET
	@Path("/sms")
	@Produces({ "image/png", "image/gif", "image/jpeg" })

	public Response getBarcodeSms(@QueryParam("telNo") String to, @QueryParam("message") String message,
			@QueryParam("dimension") @DefaultValue("46") double mm,
			@QueryParam("errorCorrectionLevel") @DefaultValue("H") ErrorCorrectionLevel errorCorrectionLevel,
			@QueryParam("licenseKey") String licenseKey)
			throws UnsupportedEncodingException, BarcodeException, IOException, LicenceError {

		boolean valid = UsageValidator.check(headers, licenseKey, 1);
		String format = getFormatFromMime();
		ContentSMS c = new ContentSMS(to, message);
		c.setTest(!valid);
		return Response.ok(c.toBarcode(format, mm, errorCorrectionLevel)).build();

	}

	/**
	 * Barcode service for tel no
	 * 
	 * @param to
	 * @param mm
	 * @param errorCorrectionLevel
	 * @param licenseKey
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws BarcodeException
	 * @throws IOException
	 * @throws LicenceError
	 */
	@GET
	@Path("/phone")
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response getBarcodeTel(@QueryParam("telNo") String to,
			@QueryParam("dimension") @DefaultValue("46") double mm,
			@QueryParam("errorCorrectionLevel") @DefaultValue("H") ErrorCorrectionLevel errorCorrectionLevel,
			@QueryParam("licenseKey") String licenseKey)
			throws UnsupportedEncodingException, BarcodeException, IOException, LicenceError {

		boolean valid = UsageValidator.check(headers, licenseKey, 1);
		String format = getFormatFromMime();
		ContentTel c = new ContentTel(to);
		c.setTest(!valid);
		return Response.ok(c.toBarcode(format, mm, errorCorrectionLevel)).build();
	}

	/**
	 * Barcode service for vcard address
	 * 
	 * @param name
	 * @param address
	 * @param company
	 * @param email
	 * @param title
	 * @param website
	 * @param phoneNumber
	 * @param mm
	 * @param errorCorrectionLevel
	 * @param licenseKey
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws BarcodeException
	 * @throws IOException
	 * @throws LicenceError
	 */
	 
	@GET
	@Path("/vcard")
	@Produces({ "image/png", "image/gif", "image/jpeg" })

	public Response getBarcodeVCard(@QueryParam("name") String name, @QueryParam("address") String address,
			@QueryParam("company") String company, @QueryParam("email") String email, @QueryParam("title") String title,
			@QueryParam("website") String website, @QueryParam("phoneNumber") String phoneNumber,
			@QueryParam("dimension") @DefaultValue("46") int mm,
			@QueryParam("errorCorrectionLevel") @DefaultValue("H") ErrorCorrectionLevel errorCorrectionLevel,
			@QueryParam("licenseKey") String licenseKey)
			throws UnsupportedEncodingException, BarcodeException, IOException, LicenceError {

		boolean valid = UsageValidator.check(headers, licenseKey, 1);

		ContentVCard vcard = new ContentVCard();
		// VCard vcard = vc.getVcard();
		if (!StringUtils.isEmpty(name))
			vcard.setFormattedName(name);
		if (!StringUtils.isEmpty(address)) {
			vcard.setAddress(address);
		}
		if (!StringUtils.isEmpty(company))
			vcard.setOrganization(company);
		if (!StringUtils.isEmpty(email))
			vcard.setEmail(email);
		if (!StringUtils.isEmpty(phoneNumber))
			vcard.setTelephoneNumber(phoneNumber);
		if (!StringUtils.isEmpty(title))
			vcard.setTitle(title);
		if (!StringUtils.isEmpty(website))
			vcard.setUrl(website);

		QRCombinedBarcode barcode = new QRCombinedBarcode("/icons/address.png", mm, errorCorrectionLevel);
		String format = getFormatFromMime();
		vcard.setTest(!valid);
		return Response.ok(barcode.createImage(vcard.getContent(), format)).build();
	}

	private String getFormatFromMime() {
		String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
		String result = StringUtils.getFormatFromMime(accept, "png");
		LOG.info(accept + " -> " + result);
		return result;
	}

}
