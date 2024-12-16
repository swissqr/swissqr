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
 *
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

	public ContentBarcodeEU() {
		format.setGroupingUsed(false);
	}

	public ContentBarcodeEU(Version version) {
		this();
	}

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

	public Version getVersion() {
		return version;
	}

	public ContentBarcodeEU version(Version version) {
		this.version = version;
		return this;
	}

	public String getBic() {
		return bic;
	}

	public ContentBarcodeEU bic(String bic) {
		this.bic = bic;
		return this;
	}

	public String getName() {
		return name;
	}

	public ContentBarcodeEU name(String name) {
		this.name = name;
		return this;
	}

	public String getIban() {
		return iban;
	}

	public ContentBarcodeEU iban(String iban) {
		this.iban = iban;
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getAmountString() {
		StringBuffer sb = new StringBuffer();
		if (this.amount != null) {
			sb.append(StringUtils.str(this.getCurrency()));
			sb.append(format.format(this.getAmount()));
		}
		return sb.toString();
	}

	public ContentBarcodeEU amount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public ContentBarcodeEU amount(String amountStr) {
		if (!StringUtils.isEmpty(amountStr)) {
			amount = new BigDecimal(amountStr);
		} else {
			amount = null;
		}
		return this;
	}

	public String getCurrency() {
		return StringUtils.isEmpty(currency) ? "EUR" : currency;
	}

	public ContentBarcodeEU characterSet(String string) {
		this.characterSet = string;
		return this;		
	}

	public ContentBarcodeEU identification(String string) {
		this.identification = string;
		return this;
	}
	
	public ContentBarcodeEU currency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getPurpose() {
		return purpose;
	}

	public ContentBarcodeEU purpose(String purpose) {
		this.purpose = purpose;
		return this;
	}

	public String getRemittanceReference() {
		return remittanceReference;
	}

	public ContentBarcodeEU remittanceReference(String remittanceReference) {
		this.remittanceReference = remittanceReference;
		return this;
	}

	public String getRemittanceText() {
		return remittanceText;
	}

	public ContentBarcodeEU remittanceText(String remittanceText) {
		this.remittanceText = remittanceText;
		return this;
	}

	public String getInformation() {
		return information;
	}

	public ContentBarcodeEU information(String information) {
		this.information = information;
		return this;
	}

	@Override
	public String getContent() throws UnsupportedEncodingException {
		return new QRStringFormatEU().write(Arrays.asList(this));
	}

	@Override
	public String toString() {
		try {
			return this.getContent();
		} catch (UnsupportedEncodingException e) {
			LOG.error(e);
			return e.getMessage();
		}
	}

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

	@JsonIgnore
	@Override
	public boolean isOK() {
		return check().isEmpty();
	}

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
	 * Used to define additional output information: e.g page format, picture type..
	 */
	@JsonIgnore
	@Override
	public Properties getProperties() {
		return this.properties;
	}

	@JsonIgnore
	@Override
	public Collection<String> getPrefix() {
		return Arrays.asList("BCD");
	}

	@Override
	public IContent parse(String str) throws FormatException {
		return new QRStringFormatEU().parse(str, this);
	}

	/**
	 * Provide the content as line separated list of parameter name : value
	 * 
	 * @return
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

	
	@Override
	public String getContentType() {
		return this.getClass().getSimpleName();
	}

	@JsonIgnore
	@Override
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel) throws UnsupportedEncodingException, BarcodeException, IOException {
		IBarcode barcode = new QRBarcode( mm, errorCorrectionLevel);
		if (this.isTest()) {
			barcode = new QRCombinedBarcode("/icons/test.png", mm, errorCorrectionLevel);
		}
		return barcode.createImage(this.getContent(), format);
	}

	
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

	@Override
	public void setTest(boolean test) {
		this.test = test;
		
	}

	@Override
	public boolean isTest() {
		return this.test;
	}

}
