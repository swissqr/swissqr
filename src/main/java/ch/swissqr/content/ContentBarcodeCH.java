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
 *
 */
public class ContentBarcodeCH implements IContent {
	private static final Logger LOG = Logger.getLogger(ContentBarcodeCH.class);
	public final static String QR_TYPE = "SPC";
	public final static String VERSION = "0200";
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
	 * @param content
	 * @throws FormatException
	 */
	public ContentBarcodeCH(String content) throws BarcodeException {
		parse(content);
	}

	/**
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
	 * @return
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
	 * @return
	 */
	@XmlElement
	public CreditorInformation getCreditorInformation() {
		return creditorInformation;
	}

	/**
	 * Returns the payment amount section
	 * @return
	 */
	@XmlElement
	public PaymentAmount getPaymentAmount() {
		return this.paymentAmount;
	}

	/**
	 * Returns the debitor section
	 * @return
	 */
	@XmlElement
	public Address getDebitor() {
		return this.debitor;
	}

	/**
	 * Returns the payment reference section
	 * @return
	 */
	@XmlElement
	public PaymentReference getPaymentReference() {
		return this.paymentReference;
	}

	/**
	 * Returns the alternative schemas
	 * @return
	 */
	@XmlElement
	public List<IAlternativeSchema> getAlternativeSchema() {
		return this.alternativeSchema;
	}

	/**
	 * Returns the creditor information address section
	 * @return
	 */
	@XmlElement
	public Address getUltimateCreditor() {
		return this.ultimateCreditor;
	}

	/**
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
	 * @param ci
	 * @return
	 */
	public ContentBarcodeCH creditor(CreditorInformation ci) {
		this.creditorInformation = ci;
		return this;
	}
	
	/**
	 * Defines the ultimate creditor
	 * @param adr
	 * @return
	 */
	public ContentBarcodeCH ultimateCreditor(Address adr) {
		this.ultimateCreditor = adr;
		return this;
	}

	/**
	 * Defines the payment amount
	 * @param pa
	 * @return
	 */
	public ContentBarcodeCH paymentAmount(PaymentAmount pa) {
		this.paymentAmount = pa;
		return this;
	}

	/**
	 * Defines the debitor 
	 * @param adr
	 * @return
	 */
	public ContentBarcodeCH debitor(Address adr) {
		this.debitor = adr;
		return this;
	}

	/**
	 * Defines the payment reference
	 * @param pr
	 * @return
	 */
	public ContentBarcodeCH paymentReference(PaymentReference pr) {
		this.paymentReference = pr;
		return this;
	}

	/**
	 * adds multiple alternative schema s
	 * @param as
	 * @return
	 */
	public ContentBarcodeCH alternativeSchema(List<IAlternativeSchema> as) {
		this.alternativeSchema = as;
		return this;
	}

	/**
	 * adds a new alternative schema (as string). This method is not recommended
	 * because it tries to automatically split up the string into a title and the content
	 * @param as
	 * @return
	 */
	public ContentBarcodeCH alternativeSchema(String str) {
		this.alternativeSchema.add(new AlternativeSchema(str));
		return this;
	}

	/**
	 * adds a new alternative schema (as string)
	 * @param as
	 * @return
	 */
	public ContentBarcodeCH alternativeSchema(String title, String parameters) {
		this.alternativeSchema.add(new AlternativeSchema(title, parameters));
		return this;
	}
	
	/**
	 * Adds one alternative schema
	 * @param as
	 * @return
	 */
	public ContentBarcodeCH alternativeSchema(IAlternativeSchema as) {
		this.alternativeSchema.add(as);
		return this;
	}

	/**
	 * Returns the barcode content 
	 */
	@Override
	public String toString() {
		return stringFormat.write(Arrays.asList(this));
	}

	/**
	 * Provides the content in UTF-8
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
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
	 * @param creditorInformation
	 */
	public void setCreditorInformation(CreditorInformation creditorInformation) {
		this.creditorInformation = creditorInformation;
	}

	/**
	 * Setter to define the Ultimate Creditor Address
	 * @param ultimatCreditorInformation
	 */
	public void setUltimateCreditor(Address ultimatCreditorInformation) {
		this.ultimateCreditor = ultimatCreditorInformation;
	}

	/**
	 * Setter to define the PaymentAmount
	 * @param paymentAmount
	 */
	public void setPaymentAmount(PaymentAmount paymentAmount) {
		this.paymentAmount = paymentAmount;
	}

	/**
	 * Setter to define the Debitor (Address)
	 * @param ultimatDebitor
	 */
	public void setDebitor(Address ultimatDebitor) {
		this.debitor = ultimatDebitor;
	}

	/**
	 * Setter to define the PaymentReference 
	 * @param paymentReference
	 */
	public void setPaymentReference(PaymentReference paymentReference) {
		this.paymentReference = paymentReference;
	}

	/**
	 * Setter to define the list of alternative schemas
	 * @param alternativeSchema
	 */
	public void setAlternativeSchema(List<IAlternativeSchema> alternativeSchema) {
		this.alternativeSchema = alternativeSchema;
	}

	/**
	 * Validates the object and returns true if there are no issues
	 */
	@Override
	@JsonIgnore
	public boolean isOK() {
		return this.check().isEmpty();
	}

	/**
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
	 * Used to define additional output information: e.g page format, picture type..
	 */
	@JsonIgnore
	@Override
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * Used to identify the message type
	 */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix(){
		return Arrays.asList("SPC");
	}


	/**
	 * Copies the content of the source ContentBarcodeCH
	 * @param source
	 */
	public void copyFrom(ContentBarcodeCH source) {
		this.creditor(source.getCreditorInformation());
		this.debitor(source.getDebitor());
		this.paymentAmount(source.getPaymentAmount());
		this.paymentReference(source.getPaymentReference());
		this.ultimateCreditor(source.getUltimateCreditor());
		this.alternativeSchema(source.getAlternativeSchema());
		
	}

	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		Double mmEff = mm==null? 46.0 : mm;
		ErrorCorrectionLevel levelEff = errorCorrectionLevel == null ? errorCorrectionLevel.M:  errorCorrectionLevel;
		IBarcode barcode = this.isOK() ? new QRSwissBarcode(this.isTest(),mmEff,levelEff) : new QRBarcode(mmEff, levelEff);
		return barcode.createImage(this.getContent(), format);
	}

	public BufferedImage toBarcode(String format) throws UnsupportedEncodingException, BarcodeException, IOException {
		IBarcode barcode = this.isOK() ? new QRSwissBarcode(this.isTest()): new QRBarcode(46.0, ErrorCorrectionLevel.M);
		return barcode.createImage(this.getContent(), format);
	}

	/**
	 * Provides all fields in a Map
	 * @return
	 */
	@Override
	@JsonIgnore
	public Map<String,Object> getDataMap() {
		return new MapData().contentToMap(this);
	}

	@Override
	public void setDataMap(Map<String, Object> record) throws ParseException {
		new MapData().mapToContent(record, this);		
	}
	
	@Override
	public String getContentType() {
		return ContentBarcodeCH.class.getSimpleName();
	}


	@Override
	public void setTest(boolean isTest) {
		this.test = isTest;
	}

	@Override
	public boolean isTest() {
		return this.test;
	}

	/// Defines the file format for the mass processing
	public void setFilename(String filename){
		this.properties.put("filename",filename);
	}
	
	@XmlElement
	public String getFilename() {
		return StringUtils.getProperty("filename","");
	}
	
	// Defines the picture format for the mass processing
	public void setPictureFormat(String fmt) {
		this.properties.put("pictureFormat", fmt);
	}
	
	@XmlElement
	public String getPictureFormat() {
		return StringUtils.getProperty("pictureFormat","");
	}

	// Defines the dimension of the image
	public void setDimension(String dim) {
		this.properties.put("dimension", dim);
	}

	@XmlElement
	String getDimension() {
		return StringUtils.getProperty("dimension","");		
	}

}
