package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.swissqr.barcode.ErrorCorrectionLevel;
import ch.swissqr.barcode.IBarcode;
import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.barcode.QRCombinedBarcode;
import ch.swissqr.content.ch.formats.FormatException;
import ch.swissqr.content.ch.formats.QRStringFormatEU;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Quick Response Code: Data relevant for the initiation of a SEPA cretid
 * transfer as described by the European Payments Council (see
 * https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation
 *
 * @author pschatzmann
 */
public class ContentBarcodeEU implements IContent {
	private static final Logger LOG = Logger.getLogger(ContentBarcodeEU.class);
	private NumberFormat format = DecimalFormat.getInstance(new Locale("de_CH"));
	private Properties properties = new Properties();
	private boolean test;

	public enum Version {
		V001, V002
	};

	private String serviceTag = "BCD";
	private Version version = Version.V002;
	private String characterSet = "1"; // UTF8
	private String identification = "SCT";
	@XmlElement()
	private String bic = "";
	@XmlElement()
	private String name = "";
	@XmlElement()
	private String iban = "";
	@XmlElement()
	private BigDecimal amount = null;
	@XmlElement()
	private String currency = "EUR";
	@XmlElement()
	private String purpose = "";
	@XmlElement()
	private String remittanceReference = "";
	@XmlElement()
	private String remittanceText = "";
	@XmlElement()
	private String information = "";

	/**
	 * <p>Constructor for ContentBarcodeEU.</p>
	 */
	public ContentBarcodeEU() {
		format.setGroupingUsed(false);
	}

	/**
	 * <p>Constructor for ContentBarcodeEU.</p>
	 *
	 * @param version a {@link ch.swissqr.content.ContentBarcodeEU.Version} object
	 */
	public ContentBarcodeEU(Version version) {
		this();
	}

	/**
	 * <p>create.</p>
	 *
	 * @param c a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public static ContentBarcodeEU create(ContentBarcodeCH c) {
		ContentBarcodeEU result = new ContentBarcodeEU(Version.V002);
		result.amount = c.getPaymentAmount().getAmount();
		result.currency = c.getPaymentAmount().getCurrency();
		result.iban = c.getCreditorInformation().getIban();
		result.name = c.getCreditorInformation().getCreditorAddress().getName();
		result.remittanceText("Client:" + c.getDebitor().getName());
		result.remittanceReference(c.getPaymentReference().getUnstructuredMessage());
		return result;
	}

	/**
	 * <p>Getter for the field <code>version</code>.</p>
	 *
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU.Version} object
	 */
	public Version getVersion() {
		return version;
	}

	/**
	 * <p>version.</p>
	 *
	 * @param version a {@link ch.swissqr.content.ContentBarcodeEU.Version} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU version(Version version) {
		this.version = version;
		return this;
	}

	/**
	 * <p>Getter for the field <code>bic</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getBic() {
		return bic;
	}

	/**
	 * <p>bic.</p>
	 *
	 * @param bic a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU bic(String bic) {
		this.bic = bic;
		return this;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>name.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * <p>Getter for the field <code>iban</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * <p>iban.</p>
	 *
	 * @param iban a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU iban(String iban) {
		this.iban = iban;
		return this;
	}

	/**
	 * <p>Getter for the field <code>amount</code>.</p>
	 *
	 * @return a {@link java.math.BigDecimal} object
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * <p>getAmountString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getAmountString() {
		StringBuffer sb = new StringBuffer();
		if (this.amount != null) {
			sb.append(StringUtils.str(this.getCurrency()));
			sb.append(format.format(this.getAmount()));
		}
		return sb.toString();
	}

	/**
	 * <p>amount.</p>
	 *
	 * @param amount a {@link java.math.BigDecimal} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU amount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	/**
	 * <p>amount.</p>
	 *
	 * @param amountStr a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU amount(String amountStr) {
		if (!StringUtils.isEmpty(amountStr)) {
			amount = new BigDecimal(amountStr);
		} else {
			amount = null;
		}
		return this;
	}

	/**
	 * <p>Getter for the field <code>currency</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getCurrency() {
		return StringUtils.isEmpty(currency) ? "EUR" : currency;
	}

	/**
	 * <p>characterSet.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU characterSet(String string) {
		this.characterSet = string;
		return this;		
	}

	/**
	 * <p>identification.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU identification(String string) {
		this.identification = string;
		return this;
	}
	
	/**
	 * <p>currency.</p>
	 *
	 * @param currency a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU currency(String currency) {
		this.currency = currency;
		return this;
	}

	/**
	 * <p>Getter for the field <code>purpose</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getPurpose() {
		return purpose;
	}

	/**
	 * <p>purpose.</p>
	 *
	 * @param purpose a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU purpose(String purpose) {
		this.purpose = purpose;
		return this;
	}

	/**
	 * <p>Getter for the field <code>remittanceReference</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getRemittanceReference() {
		return remittanceReference;
	}

	/**
	 * <p>remittanceReference.</p>
	 *
	 * @param remittanceReference a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU remittanceReference(String remittanceReference) {
		this.remittanceReference = remittanceReference;
		return this;
	}

	/**
	 * <p>Getter for the field <code>remittanceText</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getRemittanceText() {
		return remittanceText;
	}

	/**
	 * <p>remittanceText.</p>
	 *
	 * @param remittanceText a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU remittanceText(String remittanceText) {
		this.remittanceText = remittanceText;
		return this;
	}

	/**
	 * <p>Getter for the field <code>information</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * <p>information.</p>
	 *
	 * @param information a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ContentBarcodeEU} object
	 */
	public ContentBarcodeEU information(String information) {
		this.information = information;
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getContent() throws UnsupportedEncodingException {
		return new QRStringFormatEU().write(Arrays.asList(this));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		try {
			return this.getContent();
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
			return e.getMessage();
		}
	}

	/**
	 * <p>getHeader.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	@JsonIgnore
	public String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.serviceTag);
		sb.append(System.lineSeparator());
		sb.append(this.version);
		sb.append(System.lineSeparator());
		sb.append(this.characterSet);
		sb.append(System.lineSeparator());
		sb.append(this.identification);
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public List<Error> check() {
		List<Error> result = new ArrayList();
		switch (this.getVersion()) {
		case V001:
			StringUtils.check("bic", true, 11, this.getBic(), result);
			break;
		case V002:
			StringUtils.check("bic", false, 11, this.getBic(), result);
			break;
		}
		StringUtils.check("Name of the Beneficiary", true, 70, this.getName(), result);
		StringUtils.check("Account number of the Beneficiary", true, 34, this.getIban(), result);
		StringUtils.check("Amount of the Credit Transfer in Euro", false, 12, this.getAmountString(), result);
		StringUtils.check("Purpose of the Credit Transfer", false, 4, this.getPurpose(), result);
		StringUtils.check("Remittance Reference Information", false, 35, this.getRemittanceReference(), result);
		StringUtils.check("Remittance Information (Unstructured)", false, 140, this.getRemittanceText(), result);
		StringUtils.check("Beneficiary to originator information", false, 70, this.getInformation(), result);
		return result;
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public boolean isOK() {
		return check().isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public void clean() {
		bic = "";
		name = "";
		iban = "";
		amount = null;
		currency = "EUR";
		purpose = "";
		remittanceReference = "";
		remittanceText = "";
		information = "";
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

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix() {
		return Arrays.asList("BCD");
	}

	/** {@inheritDoc} */
	@Override
	public IContent parse(String str) throws FormatException {
		return new QRStringFormatEU().parse(str, this);
	}

	/**
	 * Provide the content as line separated list of parameter name : value
	 *
	 * @return a {@link java.lang.String} object
	 */
	@JsonIgnore
	public String getNameValues() {
		StringBuffer sb = new StringBuffer();
		for (Entry<String,Object> e : this.getDataMap().entrySet()) {
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
			sb.append(System.lineSeparator());			
		}
		return sb.toString();
	}

	
	/** {@inheritDoc} */
	@Override
	public String getContentType() {
		return this.getClass().getSimpleName();
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		IBarcode barcode = new QRBarcode( mm, errorCorrectionLevel);
		if (this.isTest()) {
			barcode = new QRCombinedBarcode("/icons/test.png", mm, errorCorrectionLevel);
		}
		return barcode.createImage(this.getContent(), format);
	}

	
	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Map<String, Object> getDataMap() {
		Map<String,Object> map = new TreeMap();
		map.put("Name", name);
		map.put("BIC", bic);
		map.put("IBAN", iban);
		map.put("Amount", format.format(this.getAmount()));
		map.put("Currency", currency);
		map.put("Purpose", purpose);
		map.put("RemittanceReference", remittanceReference);
		map.put("RemittanceText", remittanceText);
		map.put("Information", information);
		map.put("contentType", getContentType());
		
		return map;
	}

	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String, Object> record) throws FormatException, ParseException {		
		this.name = StringUtils.str(record.get("Name"));
		this.bic = StringUtils.str(record.get("BIC"));
		this.iban = StringUtils.str(record.get("IBAN"));
		this.amount(StringUtils.str(record.get("Amount")));
		this.currency(StringUtils.str(record.get("Currency")));
		this.purpose = (StringUtils.str(record.get("Purpose")));
		this.remittanceReference = (StringUtils.str(record.get("RemittanceReference")));
		this.remittanceText = (StringUtils.str(record.get("RemittanceText")));
		this.information = (StringUtils.str(record.get("Information")));
		
		setupProperties(record);

		
	}

	private void setupProperties(Map<String, Object> record) {
		String fileName = (String) record.get("filename");
		if (fileName!=null) {
			this.getProperties().put("filename", fileName);
		}
		String pictureFormat = (String) record.get("pictureFormat");
		if (pictureFormat!=null) {
			this.getProperties().put("pictureFormat", pictureFormat);
		}
		String dimension = (String) record.get("dimension");
		if (dimension!=null) {
			this.getProperties().put("dimension", dimension);
		}
		String errorCorrection = (String) record.get("errorCorrection");
		if (dimension!=null) {
			this.getProperties().put("errorCorrection", dimension);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setTest(boolean test) {
		this.test = test;
		
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTest() {
		return this.test;
	}

}
