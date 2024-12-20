package ch.swissqr.service.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;


import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ch.PaymentReference.ReferenceType;
import ch.swissqr.content.ch.formats.NameValueStringFormat;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.errors.LicenceError;
import ch.swissqr.paymentslip.PaymentSlipPDF;
import ch.swissqr.paymentslip.PaymentSlipPDF.Format;
import ch.swissqr.service.web.usage.UsageValidator;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Simple REST webserivces for the processing of one single Swiss QR barcode
 *
 * @author pschatzmann
 */

@Path("/service/simpleswissqr")
public class QRSimpleSwiss {
	private static final Logger LOG = Logger.getLogger(QRSimpleSwiss.class);
	private @Context HttpHeaders headers;

	public enum Langauge {
		en, de, it, fr
	};

	/**
	 * <p>Constructor for QRSimpleSwiss.</p>
	 */
	public QRSimpleSwiss() {
		LOG.info("SwissQRSimple");
	}

	/**
	 * Check the completeness and correctness of the swiss barcode content
	 *
	 * @param iban a {@link java.lang.String} object
	 * @param creditor a {@link java.lang.String} object
	 * @param crName a {@link java.lang.String} object
	 * @param crStreet a {@link java.lang.String} object
	 * @param creditorAdressLine1 a {@link java.lang.String} object
	 * @param creditorAdressLine2 a {@link java.lang.String} object
	 * @param crHouseNumber a {@link java.lang.String} object
	 * @param crPostalCode a {@link java.lang.String} object
	 * @param crCity a {@link java.lang.String} object
	 * @param crCountry a {@link java.lang.String} object
	 * @param ultimateCreditor a {@link java.lang.String} object
	 * @param ucName a {@link java.lang.String} object
	 * @param ucStreet a {@link java.lang.String} object
	 * @param ultimateCreditorAdressLine1 a {@link java.lang.String} object
	 * @param utlimateCreditorAdressLine2 a {@link java.lang.String} object
	 * @param ucHouseNumber a {@link java.lang.String} object
	 * @param ucPostalCode a {@link java.lang.String} object
	 * @param ucCity a {@link java.lang.String} object
	 * @param ucCountry a {@link java.lang.String} object
	 * @param amount a {@link java.lang.Double} object
	 * @param currency a {@link java.lang.String} object
	 * @param dueDateString a {@link java.lang.String} object
	 * @param debitor a {@link java.lang.String} object
	 * @param debName a {@link java.lang.String} object
	 * @param debStreet a {@link java.lang.String} object
	 * @param debitorAdressLine1 a {@link java.lang.String} object
	 * @param debitorAdressLine2 a {@link java.lang.String} object
	 * @param debHouseNumber a {@link java.lang.String} object
	 * @param debPostalCode a {@link java.lang.String} object
	 * @param debCity a {@link java.lang.String} object
	 * @param debCountry a {@link java.lang.String} object
	 * @param referenceType a {@link ch.swissqr.content.ch.PaymentReference.ReferenceType} object
	 * @param reference a {@link java.lang.String} object
	 * @param message a {@link java.lang.String} object
	 * @param billInformation a {@link java.lang.String} object
	 * @param unstructuredMessage a {@link java.lang.String} object
	 * @param alternativeSchema a {@link java.lang.String} object
	 * @param alternativeSchemaParameters a {@link java.lang.String} object
	 * @param alternativeScheme1 a {@link java.lang.String} object
	 * @param alternativeSchemaParameters1 a {@link java.lang.String} object
	 * @param uriDetails a {@link javax.ws.rs.core.UriInfo} object
	 * @param licenseKey a {@link java.lang.String} object
	 * @return a {@link java.util.List} object
	 * @throws java.text.ParseException if any.
	 * @throws ch.swissqr.errors.LicenceError if any.
	 */
	@GET
	@Path("/check")
	@Produces("application/json")
	public List<Error> simpleCheck(@QueryParam("IBAN") String iban, @QueryParam("Creditor") String creditor,
			@QueryParam("CreditorName") String crName, @QueryParam("CreditorStreet") String crStreet,
			@QueryParam("CreditorAddressLine1") String creditorAdressLine1,
			@QueryParam("CreditorAddressLine2") String creditorAdressLine2,
			@QueryParam("CreditorHouseNumber") String crHouseNumber,
			@QueryParam("CreditorPostalCode") String crPostalCode, @QueryParam("CreditorCity") String crCity,
			@QueryParam("CreditorCountry") String crCountry, @QueryParam("UltimateCreditor") String ultimateCreditor,
			@QueryParam("UltimateCreditorName") String ucName, @QueryParam("UltimateCreditorStreet") String ucStreet,
			@QueryParam("UltimateCreditorAddressLine1") String ultimateCreditorAdressLine1,
			@QueryParam("UltimateCreditorAddressLine2") String utlimateCreditorAdressLine2,
			@QueryParam("UltimateCreditorHouseNumber") String ucHouseNumber,
			@QueryParam("UltimateCreditorPostalCode") String ucPostalCode,
			@QueryParam("UltimateCreditorCity") String ucCity, @QueryParam("UltimateCreditorCountry") String ucCountry,
			@QueryParam("Amount") Double amount, 
			@QueryParam("Currency") String currency,
			@QueryParam("DueDate") String dueDateString, 
			@QueryParam("Debitor") String debitor,
			@QueryParam("DebitorName") String debName, 
			@QueryParam("DebitorStreet") String debStreet,
			@QueryParam("DebitorAddressLine1") String debitorAdressLine1,
			@QueryParam("DebitorAddressLine2") String debitorAdressLine2,
			@QueryParam("DebitorHouseNumber") String debHouseNumber,
			@QueryParam("DebitorPostalCode") String debPostalCode,
			@QueryParam("DebitorCity") String debCity,
			@QueryParam("DebitorCountry") String debCountry,
			@QueryParam("ReferenceType") @DefaultValue("NON") ReferenceType referenceType,
			@QueryParam("Reference") String reference, 
			@QueryParam("Message") String message,
			@QueryParam("BillInformation") String billInformation,
			@QueryParam("UnstructuredMessage") String unstructuredMessage,
			@QueryParam("AlternativeSchema") String alternativeSchema,
			@QueryParam("AlternativeSchemaParameters") String alternativeSchemaParameters,
			@QueryParam("AlternativeSchema1") String alternativeScheme1,
			@QueryParam("AlternativeSchemaParameters1") String alternativeSchemaParameters1,
			@Context UriInfo uriDetails,
			@QueryParam("licenseKey") String licenseKey)
			throws ParseException, LicenceError {

		LOG.info("simpleCheck");
		boolean ok = true;
		if (!StringUtils.equals(iban,CommonServicesAPI.TEST_IBAN)) {
			UsageValidator.checkEx(headers, licenseKey, 1);
		}

		List<Error> result = new ArrayList();
		ContentBarcodeCH content = new ContentBarcodeCH();
		content.setTest(!ok);
		content.setDataMap(toMap(uriDetails.getQueryParameters()));
		result = content.check();
		return result;
	}

	/**
	 * Generates a swiss QR Barcode: If the provided information has issues (see
	 * check) we generate a simple QR code (w/o a swiss cross in the center)
	 *
	 * @param uriDetails a {@link javax.ws.rs.core.UriInfo} object
	 * @param iban a {@link java.lang.String} object
	 * @param creditor a {@link java.lang.String} object
	 * @param creditorAdressLine1 a {@link java.lang.String} object
	 * @param creditorAdressLine2 a {@link java.lang.String} object
	 * @param crName a {@link java.lang.String} object
	 * @param crStreet a {@link java.lang.String} object
	 * @param crHouseNumber a {@link java.lang.String} object
	 * @param crPostalCode a {@link java.lang.String} object
	 * @param crCity a {@link java.lang.String} object
	 * @param crCountry a {@link java.lang.String} object
	 * @param ultimateCreditor a {@link java.lang.String} object
	 * @param ucName a {@link java.lang.String} object
	 * @param ultimateCreditorAdressLine1 a {@link java.lang.String} object
	 * @param utlimateCreditorAdressLine2 a {@link java.lang.String} object
	 * @param ucStreet a {@link java.lang.String} object
	 * @param ucHouseNumber a {@link java.lang.String} object
	 * @param ucPostalCode a {@link java.lang.String} object
	 * @param ucCity a {@link java.lang.String} object
	 * @param ucCountry a {@link java.lang.String} object
	 * @param amount a {@link java.lang.Double} object
	 * @param currency a {@link java.lang.String} object
	 * @param dueDateString a {@link java.lang.String} object
	 * @param debitor a {@link java.lang.String} object
	 * @param debName a {@link java.lang.String} object
	 * @param debitorAdressLine1 a {@link java.lang.String} object
	 * @param debitorAdressLine2 a {@link java.lang.String} object
	 * @param debStreet a {@link java.lang.String} object
	 * @param debHouseNumber a {@link java.lang.String} object
	 * @param debPostalCode a {@link java.lang.String} object
	 * @param debCity a {@link java.lang.String} object
	 * @param debCountry a {@link java.lang.String} object
	 * @param referenceType a {@link ch.swissqr.content.ch.PaymentReference.ReferenceType} object
	 * @param reference a {@link java.lang.String} object
	 * @param message a {@link java.lang.String} object
	 * @param billInformation a {@link java.lang.String} object
	 * @param unstructuredMessage a {@link java.lang.String} object
	 * @param alternativeSchema a {@link java.lang.String} object
	 * @param alternativeSchemaParameters a {@link java.lang.String} object
	 * @param alternativeScheme1 a {@link java.lang.String} object
	 * @param alternativeSchemaParameters1 a {@link java.lang.String} object
	 * @param licenseKey a {@link java.lang.String} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 * @throws java.text.ParseException if any.
	 * @throws java.io.UnsupportedEncodingException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 * @throws ch.swissqr.errors.LicenceError if any.
	 */
	@GET
	@Path("/barcode")
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response simpleBarcode(@Context UriInfo uriDetails,
			@QueryParam("IBAN") String iban, 
			@QueryParam("Creditor") String creditor,
			@QueryParam("CreditorAddressLine1") String creditorAdressLine1,
			@QueryParam("CreditorAddressLine2") String creditorAdressLine2,
			@QueryParam("CreditorName") String crName, 
			@QueryParam("CreditorStreet") String crStreet,
			@QueryParam("CreditorHouseNumber") String crHouseNumber,
			@QueryParam("CreditorPostalCode") String crPostalCode, 
			@QueryParam("CreditorCity") String crCity,
			@QueryParam("CreditorCountry") String crCountry, 
			@QueryParam("UltimateCreditor") String ultimateCreditor,
			@QueryParam("UltimateCreditorName") String ucName,
			@QueryParam("UltimateCreditorAddressLine1") String ultimateCreditorAdressLine1,
			@QueryParam("UltimateCreditorAddressLine2") String utlimateCreditorAdressLine2,
			@QueryParam("UltimateCreditorStreet") String ucStreet,
			@QueryParam("UltimateCreditorHouseNumber") String ucHouseNumber,
			@QueryParam("UltimateCreditorPostalCode") String ucPostalCode,
			@QueryParam("UltimateCreditorCity") String ucCity, 
			@QueryParam("UltimateCreditorCountry") String ucCountry,
			@QueryParam("Amount") Double amount, 
			@QueryParam("Currency") String currency,
			@QueryParam("DueDate") String dueDateString, 
			@QueryParam("Debitor") String debitor,
			@QueryParam("DebitorName") String debName, 
			@QueryParam("DebitorAddressLine1") String debitorAdressLine1,
			@QueryParam("DebitorAddressLine2") String debitorAdressLine2,
			@QueryParam("DebitorStreet") String debStreet,
			@QueryParam("DebitorHouseNumber") String debHouseNumber,
			@QueryParam("DebitorPostalCode") String debPostalCode, 
			@QueryParam("DebitorCity") String debCity,
			@QueryParam("DebitorCountry") String debCountry,
			@QueryParam("ReferenceType") @DefaultValue("NON") ReferenceType referenceType,
			@QueryParam("Reference") String reference, 
			@QueryParam("Message") String message,
			@QueryParam("BillInformation") String billInformation,
			@QueryParam("UnstructuredMessage") String unstructuredMessage,
			@QueryParam("AlternativeSchema") String alternativeSchema,
			@QueryParam("AlternativeSchemaParameters") String alternativeSchemaParameters,
			@QueryParam("AlternativeSchema1") String alternativeScheme1,
			@QueryParam("AlternativeSchemaParameters1") String alternativeSchemaParameters1,
			@QueryParam("licenseKey") String licenseKey)
			throws ParseException, UnsupportedEncodingException, BarcodeException, IOException, LicenceError {

		LOG.info("simpleBarcode");
		// default processing
		boolean ok = true;
		if (!StringUtils.equals(CommonServicesAPI.TEST_IBAN,iban)) {
			ok = UsageValidator.check(headers, licenseKey, 1);
		}

		ContentBarcodeCH content = new ContentBarcodeCH();
		content.setTest(!ok);
		content.setDataMap(toMap(uriDetails.getQueryParameters()));
		String format = getFormatFromMime();

		return Response.ok(content.toBarcode(format, null, null)).build();
	}
	
	/**
	 * Encoded content for barcode. We deconde and forward the request to the barcode service
	 *
	 * @throws java.io.UnsupportedEncodingException
	 * @throws java.net.URISyntaxException
	 * @param encoded a {@link java.lang.String} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 */
	@GET
	@Path("/barcode/{encoded}")
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response simpleBarcodeEncoded(@PathParam("encoded") String encoded) throws UnsupportedEncodingException, URISyntaxException {
		String decoded = URLDecoder.decode(encoded, "UTF8");
		LOG.info("simpleBarcodeEncoded: "+decoded);
		decoded = decoded.replaceAll(" ", "%20");
	    URI uri = new URI("https://swissqr.ch/service/simpleswissqr/barcode?"+decoded);
	    LOG.info(uri);
	    return Response.seeOther( uri ).build();
	}
	
	/**
	 * <p>simpleBarcode.</p>
	 *
	 * @param map a {@link javax.ws.rs.core.MultivaluedMap} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 * @throws java.text.ParseException if any.
	 * @throws java.io.UnsupportedEncodingException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 * @throws ch.swissqr.errors.LicenceError if any.
	 */
	@POST
	@Path("/barcode")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response simpleBarcode(MultivaluedMap map) throws ParseException, UnsupportedEncodingException, BarcodeException, IOException, LicenceError {
		Map simpleMap = toMap(map);
		boolean ok = true;
		LOG.info("simpleBarcode-post");
		if (!StringUtils.equals((String)simpleMap.get("IBAN"),(CommonServicesAPI.TEST_IBAN))) {
			ok = UsageValidator.check(headers, (String)simpleMap.get("licenseKey"), 1);
		}
		
		ContentBarcodeCH content = new ContentBarcodeCH();
		content.setTest(!ok);
		content.setDataMap(simpleMap);
		String format = getFormatFromMime();
		return Response.ok(content.toBarcode(format, null, null)).build();
	
	}

	/**
	 * Generates a payment slip which contains a QR barcode
	 *
	 * @param uriDetails a {@link javax.ws.rs.core.UriInfo} object
	 * @param pageFormat a {@link ch.swissqr.paymentslip.PaymentSlipPDF.Format} object
	 * @param printLintes a boolean
	 * @param printReceipt a boolean
	 * @param language a {@link ch.swissqr.service.web.QRSimpleSwiss.Langauge} object
	 * @param iban a {@link java.lang.String} object
	 * @param creditor a {@link java.lang.String} object
	 * @param crName a {@link java.lang.String} object
	 * @param creditorAdressLine1 a {@link java.lang.String} object
	 * @param creditorAdressLine2 a {@link java.lang.String} object
	 * @param crStreet a {@link java.lang.String} object
	 * @param crHouseNumber a {@link java.lang.String} object
	 * @param crPostalCode a {@link java.lang.String} object
	 * @param crCity a {@link java.lang.String} object
	 * @param crCountry a {@link java.lang.String} object
	 * @param ultimateCreditor a {@link java.lang.String} object
	 * @param ucName a {@link java.lang.String} object
	 * @param ucStreet a {@link java.lang.String} object
	 * @param ultimateCreditorAdressLine1 a {@link java.lang.String} object
	 * @param utlimateCreditorAdressLine2 a {@link java.lang.String} object
	 * @param ucHouseNumber a {@link java.lang.String} object
	 * @param ucPostalCode a {@link java.lang.String} object
	 * @param ucCity a {@link java.lang.String} object
	 * @param ucCountry a {@link java.lang.String} object
	 * @param amount a {@link java.lang.Double} object
	 * @param currency a {@link java.lang.String} object
	 * @param dueDateString a {@link java.lang.String} object
	 * @param debitor a {@link java.lang.String} object
	 * @param debName a {@link java.lang.String} object
	 * @param debitorAdressLine1 a {@link java.lang.String} object
	 * @param debitorAdressLine2 a {@link java.lang.String} object
	 * @param debStreet a {@link java.lang.String} object
	 * @param debHouseNumber a {@link java.lang.String} object
	 * @param debPostalCode a {@link java.lang.String} object
	 * @param debCity a {@link java.lang.String} object
	 * @param debCountry a {@link java.lang.String} object
	 * @param referenceType a {@link ch.swissqr.content.ch.PaymentReference.ReferenceType} object
	 * @param reference a {@link java.lang.String} object
	 * @param message a {@link java.lang.String} object
	 * @param billInformation a {@link java.lang.String} object
	 * @param unstructuredMessage a {@link java.lang.String} object
	 * @param alternativeSchema a {@link java.lang.String} object
	 * @param alternativeSchemaParameters a {@link java.lang.String} object
	 * @param alternativeScheme1 a {@link java.lang.String} object
	 * @param alternativeSchemaParameters1 a {@link java.lang.String} object
	 * @param licenseKey a {@link java.lang.String} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 * @throws java.text.ParseException if any.
	 * @throws java.io.IOException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws ch.swissqr.errors.LicenceError if any.
	 */
	@GET
	@Path("/paymentslip")
	@Produces({  "image/png", "image/gif", "image/jpeg", "application/pdf" })
	public Response simplePaymentslip( 
			@Context UriInfo uriDetails,
			@QueryParam("pageFormat") 
			@DefaultValue("A4") Format pageFormat,
			@QueryParam("printLines") @DefaultValue("true") boolean printLintes,
			@QueryParam("printReceipt") @DefaultValue("true") boolean printReceipt,
			@QueryParam("language") @DefaultValue("de") Langauge language, @QueryParam("IBAN") String iban,
			@QueryParam("Creditor") String creditor,
			@QueryParam("CreditorName") String crName,
			@QueryParam("CreditorAddressLine1") String creditorAdressLine1,
			@QueryParam("CreditorAddressLine2") String creditorAdressLine2,
			@QueryParam("CreditorStreet") String crStreet, 
			@QueryParam("CreditorHouseNumber") String crHouseNumber,
			@QueryParam("CreditorPostalCode") String crPostalCode, 
			@QueryParam("CreditorCity") String crCity,
			@QueryParam("CreditorCountry") String crCountry, 
			@QueryParam("UltimateCreditor") String ultimateCreditor,
			@QueryParam("UltimateCreditorName") String ucName, 
			@QueryParam("UltimateCreditorStreet") String ucStreet,
			@QueryParam("UltimateCreditorAddressLine1") String ultimateCreditorAdressLine1,
			@QueryParam("UltimateCreditorAddressLine2") String utlimateCreditorAdressLine2,
			@QueryParam("UltimateCreditorHouseNumber") String ucHouseNumber,
			@QueryParam("UltimateCreditorPostalCode") String ucPostalCode,
			@QueryParam("UltimateCreditorCity") String ucCity, 
			@QueryParam("UltimateCreditorCountry") String ucCountry,
			@QueryParam("Amount") Double amount, 
			@QueryParam("Currency") String currency,
			@QueryParam("DueDate") String dueDateString,
			@QueryParam("Debitor") String debitor,
			@QueryParam("DebitorName") String debName, 
			@QueryParam("DebitorAddressLine1") String debitorAdressLine1,
			@QueryParam("DebitorAddressLine2") String debitorAdressLine2,
			@QueryParam("DebitorStreet") String debStreet,
			@QueryParam("DebitorHouseNumber") String debHouseNumber,
			@QueryParam("DebitorPostalCode") String debPostalCode, 
			@QueryParam("DebitorCity") String debCity,
			@QueryParam("DebitorCountry") String debCountry,
			@QueryParam("ReferenceType") @DefaultValue("NON") ReferenceType referenceType,
			@QueryParam("Reference") String reference, 
			@QueryParam("Message") String message,
			@QueryParam("BillInformation") String billInformation,
			@QueryParam("UnstructuredMessage") String unstructuredMessage,
			@QueryParam("AlternativeSchema") String alternativeSchema,
			@QueryParam("AlternativeSchemaParameters") String alternativeSchemaParameters,
			@QueryParam("AlternativeSchema1") String alternativeScheme1,
			@QueryParam("AlternativeSchemaParameters1") String alternativeSchemaParameters1,
			@QueryParam("licenseKey") String licenseKey)
			throws ParseException, IOException, BarcodeException, LicenceError {

		LOG.info("simplePaymentslip");
		boolean ok = true;
		if (!StringUtils.equals(iban,CommonServicesAPI.TEST_IBAN)) {
			ok = UsageValidator.check(headers, licenseKey, 1);
		}
		
		String format = getFormatFromMime();

		ContentBarcodeCH content = new ContentBarcodeCH();
		content.setDataMap(toMap(uriDetails.getQueryParameters()));
		content.setTest(!ok);

		PaymentSlipPDF ps = new PaymentSlipPDF(content, language.name(), pageFormat, printLintes, printReceipt);
		byte[] imageData = ps.getBytes(format);
		return Response.ok(new ByteArrayInputStream(imageData)).build();
	}
	
	
	/**
	 * <p>simplePaymentslip.</p>
	 *
	 * @param map a {@link javax.ws.rs.core.MultivaluedMap} object
	 * @return a {@link javax.ws.rs.core.Response} object
	 * @throws java.text.ParseException if any.
	 * @throws java.io.UnsupportedEncodingException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 * @throws ch.swissqr.errors.LicenceError if any.
	 */
	@POST
	@Path("/paymentslip")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces({ "image/png", "image/gif", "image/jpeg" })
	public Response simplePaymentslip(MultivaluedMap map) throws ParseException, UnsupportedEncodingException, BarcodeException, IOException, LicenceError {
		Map<String,Object> simpleMap = toMap(map);
		LOG.info("simplePaymentslip-post "+simpleMap);
		boolean ok = true;
		if (!StringUtils.equals((String)simpleMap.get("IBAN"),CommonServicesAPI.TEST_IBAN)) {
			ok = UsageValidator.check(headers, (String)simpleMap.get("licenseKey"), 1);
			LOG.info("License check "+ok);
		}
		
		ContentBarcodeCH content = new ContentBarcodeCH();
 		content.setDataMap(simpleMap);
		content.setTest(!ok);
		String format = getFormatFromMime();
		
		PaymentSlipPDF ps = new PaymentSlipPDF(content, content.getProperties().getProperty("langauge", "de"), getPageFormat(content), getPrintLines(content), getPrintReceipt(content));
		byte[] imageData = ps.getBytes(format);
		return Response.ok(new ByteArrayInputStream(imageData)).build();
	
	}

	private boolean getPrintLines(ContentBarcodeCH content) {
		return  !"false".equals(content.getProperties().getProperty("printLines"));
	}

	private boolean getPrintReceipt(ContentBarcodeCH content) {
		return  !"false".equals(content.getProperties().getProperty("printReceipt"));
	}

	private Format getPageFormat(ContentBarcodeCH content) {
		Format  pageFormat = null;
		String pageFormatStr = content.getProperties().getProperty("pageFormat");
		if (!StringUtils.isEmpty(pageFormatStr)) {
			pageFormat = Format.valueOf(pageFormatStr);
		} else {
			pageFormat = Format.A4;
		}
		return pageFormat;
	}


	/**
	 * Returns the content of the barcode image file. If it is a Swiss Barcode we
	 * convert the content to a readable "paramtername : content" text format
	 *
	 * @throws java.io.IOException
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws ch.swissqr.errors.LicenceError
	 * @param body a {@link org.glassfish.jersey.media.multipart.FormDataMultiPart} object
	 * @return a {@link java.lang.String} object
	 */

	@POST
	@Path("/imageToText")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_PLAIN)
	public String imageToText(FormDataMultiPart body)
			throws  IOException, BarcodeException, LicenceError {

		LOG.info("imageToText");
		UsageValidator.checkEx(headers, null, body.getBodyParts().size());

		QRBarcode bc = new QRBarcode();
		StringBuffer result = new StringBuffer(); 
		
		for (StreamInfo si : StreamInfo.getStreams(body)) {
			String contentString = bc.readImage(si.inputStream);
			try {
				if (contentString.startsWith("SPC")) {
					if (result.length()>0) {
						result.append(System.lineSeparator());
					}
					ContentBarcodeCH content = new ContentBarcodeCH(contentString);
					contentString = new NameValueStringFormat().write(Arrays.asList(content));
					result.append(contentString);
				}
			} catch (Exception ex) {
				LOG.warn("Could not convert to swiss barcode - we return the original content - " + ex, ex);
			}
		}
		
		return result.toString();
	}

	/**
	 * <p>checkFileType.</p>
	 *
	 * @param fileDetail a {@link org.glassfish.jersey.media.multipart.FormDataContentDisposition} object
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public static void checkFileType(FormDataContentDisposition fileDetail) throws BarcodeException {
		String fileName = fileDetail.getFileName().toLowerCase();
		if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
				|| fileName.endsWith(".gif")) {
			LOG.info("Valid file " + fileName);
		} else {
			throw new BarcodeException("Invalid file type: only png, jpg and gif are supported");
		}
	}

	/**
	 * <p>toMap.</p>
	 *
	 * @param queryParameters a {@link javax.ws.rs.core.MultivaluedMap} object
	 * @return a {@link java.util.Map} object
	 */
	public static Map toMap(MultivaluedMap<String, String> queryParameters) {
		Map result = new TreeMap();
		for (Entry<String, List<String>> e : queryParameters.entrySet()) {
			result.put(e.getKey(), e.getValue().get(0));
		}
		return result;
	}
	
	private String getFormatFromMime() {
	    String accept = headers.getHeaderString(HttpHeaders.ACCEPT);
		String result = StringUtils.getFormatFromMime(accept, "png");
		LOG.info(accept+" -> "+result);
		return result;
	}
}
