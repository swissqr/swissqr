package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.barcode.QRSwissBarcode;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.AlternativeSchema;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.IAlternativeSchema;
import ch.swissqr.content.ch.MapData;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.content.ch.formats.QRStringFormatSwiss;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;
import ch.swissqr.barcode.ErrorCorrectionLevel;

/**
 * Payload Information which is needed to generate a Swiss QR barcode. We
 * provide the the functionality to construct a payload and validate the
 * provided input. See https://www.paymentstandards.ch/dam/downloads/ig-qr-bill-en.pdf
 *
 * Please note that the content must be rendered in UTF-8
 *
 * @author pschatzmann
 */
public class ContentBarcodeCH implements IContent {
	private static final Logger LOG = Logger.getLogger(ContentBarcodeCH.class);
	/** Constant <code>QR_TYPE="SPC"</code> */
	public final static String QR_TYPE = "SPC";
	/** Constant <code>VERSION="0200"</code> */
	public final static String VERSION = "0200";
	/** Constant <code>CODING_TYPE="1"</code> */
	public final static String CODING_TYPE = "1";
	private CreditorInformation creditorInformation = new CreditorInformation();
	private Address ultimateCreditor = new Address();
	private PaymentAmount paymentAmount = new PaymentAmount();
	private Address debitor = new Address();
	private PaymentReference paymentReference = new PaymentReference();
	private List<IAlternativeSchema> alternativeSchema = new ArrayList();
	private QRStringFormatSwiss stringFormat = new QRStringFormatSwiss();
	private Properties properties = new Properties();
	private boolean test = false;
	/**
	 * Empty constructor
	 */
	public ContentBarcodeCH() {
	}

	/**
	 * Create barcode based on scanned text input
	 *
	 * @param content a {@link java.lang.String} object
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public ContentBarcodeCH(String content) throws BarcodeException {
		parse(content);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Fills the barcode based on a scanned barcode content
	 */
	@Override
	public IContent parse(String content) throws BarcodeException {		
		IContent result = new QRStringFormatSwiss().parse(content);
		this.copyFrom((ContentBarcodeCH) result);
		return result;
	}

	/**
	 * Returns the barcode header information section
	 *
	 * @return a {@link java.lang.String} object
	 */
	@JsonIgnore
	public String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(QR_TYPE);
		sb.append(StringUtils.CRLF);
		sb.append(VERSION);
		sb.append(StringUtils.CRLF);
		sb.append(CODING_TYPE);
		sb.append(StringUtils.CRLF);
		return sb.toString();
	}

	/**
	 * Returns the creditor information section
	 *
	 * @return a {@link ch.swissqr.content.ch.CreditorInformation} object
	 */
	@XmlElement
	public CreditorInformation getCreditorInformation() {
		return creditorInformation;
	}

	/**
	 * Returns the payment amount section
	 *
	 * @return a {@link ch.swissqr.content.ch.PaymentAmount} object
	 */
	@XmlElement
	public PaymentAmount getPaymentAmount() {
		return this.paymentAmount;
	}

	/**
	 * Returns the debitor section
	 *
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	@XmlElement
	public Address getDebitor() {
		return this.debitor;
	}

	/**
	 * Returns the payment reference section
	 *
	 * @return a {@link ch.swissqr.content.ch.PaymentReference} object
	 */
	@XmlElement
	public PaymentReference getPaymentReference() {
		return this.paymentReference;
	}

	/**
	 * Returns the alternative schemas
	 *
	 * @return a {@link java.util.List} object
	 */
	@XmlElement
	public List<IAlternativeSchema> getAlternativeSchema() {
		return this.alternativeSchema;
	}

	/**
	 * Returns the creditor information address section
	 *
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	@XmlElement
	public Address getUltimateCreditor() {
		return this.ultimateCreditor;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Determines if the information is complete and correct
	 */
	@Override
	@JsonIgnore
	public List<Error> check() {
		List<Error> missingFields = new ArrayList();
		missingFields.addAll(creditorInformation.check());
		if (getUltimateCreditor().isDefined()) {
			missingFields.addAll(ultimateCreditor.check("UltimateCreditor"));
		}
		
		if (!StringUtils.isEmpty(this.debitor.getName())) {
			missingFields.addAll(this.debitor.check("Debitor"));
		}

		missingFields.addAll(paymentReference.check());
		
		for (IAlternativeSchema as : this.getAlternativeSchema()) {
			missingFields.addAll(as.check());
		}
		
		if (!missingFields.isEmpty()) {
			LOG.warn(missingFields.toString());
		}
		return missingFields;
	}

	/**
	 * Defines the creditor information
	 *
	 * @param ci a {@link ch.swissqr.content.ch.CreditorInformation} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH creditor(CreditorInformation ci) {
		this.creditorInformation = ci;
		return this;
	}
	
	/**
	 * Defines the ultimate creditor
	 *
	 * @param adr a {@link ch.swissqr.content.ch.Address} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH ultimateCreditor(Address adr) {
		this.ultimateCreditor = adr;
		return this;
	}

	/**
	 * Defines the payment amount
	 *
	 * @param pa a {@link ch.swissqr.content.ch.PaymentAmount} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH paymentAmount(PaymentAmount pa) {
		this.paymentAmount = pa;
		return this;
	}

	/**
	 * Defines the debitor
	 *
	 * @param adr a {@link ch.swissqr.content.ch.Address} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH debitor(Address adr) {
		this.debitor = adr;
		return this;
	}

	/**
	 * Defines the payment reference
	 *
	 * @param pr a {@link ch.swissqr.content.ch.PaymentReference} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH paymentReference(PaymentReference pr) {
		this.paymentReference = pr;
		return this;
	}

	/**
	 * adds multiple alternative schema s
	 *
	 * @param as a {@link java.util.List} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH alternativeSchema(List<IAlternativeSchema> as) {
		this.alternativeSchema = as;
		return this;
	}

	/**
	 * adds a new alternative schema (as string). This method is not recommended
	 * because it tries to automatically split up the string into a title and the content
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH alternativeSchema(String str) {
		this.alternativeSchema.add(new AlternativeSchema(str));
		return this;
	}

	/**
	 * adds a new alternative schema (as string)
	 *
	 * @param title a {@link java.lang.String} object
	 * @param parameters a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH alternativeSchema(String title, String parameters) {
		this.alternativeSchema.add(new AlternativeSchema(title, parameters));
		return this;
	}
	
	/**
	 * Adds one alternative schema
	 *
	 * @param as a {@link ch.swissqr.content.ch.IAlternativeSchema} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public ContentBarcodeCH alternativeSchema(IAlternativeSchema as) {
		this.alternativeSchema.add(as);
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns the barcode content
	 */
	@Override
	public String toString() {
		return stringFormat.write(Arrays.asList(this));
	}

	/**
	 * {@inheritDoc}
	 *
	 * Provides the content in UTF-8
	 */
	@Override
	@JsonIgnore
	public String getContent() throws UnsupportedEncodingException {
		String targetEncoding = "UTF-8"; // used to be "ISO-8859-1";
		byte[] bytes = stringFormat.write(Arrays.asList(this)).getBytes(Charset.forName(targetEncoding));
		return new String(bytes, targetEncoding);
	}

	/**
	 * Setter to define the CreditorInformation
	 *
	 * @param creditorInformation a {@link ch.swissqr.content.ch.CreditorInformation} object
	 */
	public void setCreditorInformation(CreditorInformation creditorInformation) {
		this.creditorInformation = creditorInformation;
	}

	/**
	 * Setter to define the Ultimate Creditor Address
	 *
	 * @param ultimatCreditorInformation a {@link ch.swissqr.content.ch.Address} object
	 */
	public void setUltimateCreditor(Address ultimatCreditorInformation) {
		this.ultimateCreditor = ultimatCreditorInformation;
	}

	/**
	 * Setter to define the PaymentAmount
	 *
	 * @param paymentAmount a {@link ch.swissqr.content.ch.PaymentAmount} object
	 */
	public void setPaymentAmount(PaymentAmount paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	/**
	 * Setter to define the Debitor (Address)
	 *
	 * @param ultimatDebitor a {@link ch.swissqr.content.ch.Address} object
	 */
	public void setDebitor(Address ultimatDebitor) {
		this.debitor = ultimatDebitor;
	}

	/**
	 * Setter to define the PaymentReference
	 *
	 * @param paymentReference a {@link ch.swissqr.content.ch.PaymentReference} object
	 */
	public void setPaymentReference(PaymentReference paymentReference) {
		this.paymentReference = paymentReference;
	}

	/**
	 * Setter to define the list of alternative schemas
	 *
	 * @param alternativeSchema a {@link java.util.List} object
	 */
	public void setAlternativeSchema(List<IAlternativeSchema> alternativeSchema) {
		this.alternativeSchema = alternativeSchema;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Validates the object and returns true if there are no issues
	 */
	@Override
	@JsonIgnore
	public boolean isOK() {
		return this.check().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Clears the content of the corrent object
	 */
	@Override
	public void clean() {
		creditorInformation = new CreditorInformation();
		ultimateCreditor = new Address();
		paymentAmount = new PaymentAmount();
		debitor = new Address();
		paymentReference = new PaymentReference();
		alternativeSchema = null;
		stringFormat = new QRStringFormatSwiss();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Used to define additional output information: e.g page format, picture type..
	 */
	@JsonIgnore
	@Override
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Used to identify the message type
	 */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix(){
		return Arrays.asList("SPC");
	}


	/**
	 * Copies the content of the source ContentBarcodeCH
	 *
	 * @param source a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public void copyFrom(ContentBarcodeCH source) {
		this.creditor(source.getCreditorInformation());
		this.debitor(source.getDebitor());
		this.paymentAmount(source.getPaymentAmount());
		this.paymentReference(source.getPaymentReference());
		this.ultimateCreditor(source.getUltimateCreditor());
		this.alternativeSchema(source.getAlternativeSchema());
		
	}

	/** {@inheritDoc} */
	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		Double mmEff = mm==null? 46.0 : mm;
		ErrorCorrectionLevel levelEff = errorCorrectionLevel == null ? errorCorrectionLevel.M:  errorCorrectionLevel;
		IBarcode barcode = this.isOK() ? new QRSwissBarcode(this.isTest(),mmEff,levelEff) : new QRBarcode(mmEff, levelEff);
		return barcode.createImage(this.getContent(), format);
	}

	/**
	 * <p>toBarcode.</p>
	 *
	 * @param format a {@link java.lang.String} object
	 * @return a {@link java.awt.image.BufferedImage} object
	 * @throws java.io.UnsupportedEncodingException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	public BufferedImage toBarcode(String format) throws UnsupportedEncodingException, BarcodeException, IOException {
		IBarcode barcode = this.isOK() ? new QRSwissBarcode(this.isTest()): new QRBarcode(46.0, ErrorCorrectionLevel.M);
		return barcode.createImage(this.getContent(), format);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Provides all fields in a Map
	 */
	@Override
	@JsonIgnore
	public Map<String,Object> getDataMap() {
		return new MapData().contentToMap(this);
	}

	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String, Object> record) throws ParseException {
		new MapData().mapToContent(record, this);		
	}
	
	/** {@inheritDoc} */
	@Override
	public String getContentType() {
		return ContentBarcodeCH.class.getSimpleName();
	}


	/** {@inheritDoc} */
	@Override
	public void setTest(boolean isTest) {
		this.test = isTest;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTest() {
		return this.test;
	}

	/// Defines the file format for the mass processing
	/**
	 * <p>setFilename.</p>
	 *
	 * @param filename a {@link java.lang.String} object
	 */
	public void setFilename(String filename){
		this.properties.put("filename",filename);
	}
	
	/**
	 * <p>getFilename.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	@XmlElement
	public String getFilename() {
		return StringUtils.getProperty("filename","");
	}
	
	// Defines the picture format for the mass processing
	/**
	 * <p>setPictureFormat.</p>
	 *
	 * @param fmt a {@link java.lang.String} object
	 */
	public void setPictureFormat(String fmt) {
		this.properties.put("pictureFormat", fmt);
	}
	
	/**
	 * <p>getPictureFormat.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	@XmlElement
	public String getPictureFormat() {
		return StringUtils.getProperty("pictureFormat","");
	}

	// Defines the dimension of the image
	/**
	 * <p>setDimension.</p>
	 *
	 * @param dim a {@link java.lang.String} object
	 */
	public void setDimension(String dim) {
		this.properties.put("dimension", dim);
	}

	@XmlElement
	String getDimension() {
		return StringUtils.getProperty("dimension","");		
	}

}
