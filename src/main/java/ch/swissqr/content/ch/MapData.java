package ch.swissqr.content.ch;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ch.PaymentReference.ReferenceType;
import ch.swissqr.service.web.SwaggerService;
import ch.swissqr.utils.StringUtils;

/**
 * Populates the BarcodeContentCH from a Map and converts a ContentBarcodeCH to a map
 *
 * @author pschatzmann
 */
public class MapData {
	private final static Logger LOG = Logger.getLogger(MapData.class);
	private Map<String, Object> values;
	private DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

	/**
	 * Converts a map to a ContentBarcodeCH object
	 *
	 * @throws java.text.ParseException
	 * @param values a {@link java.util.Map} object
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 */
	public void mapToContent(Map<String, Object> values, ContentBarcodeCH content) throws ParseException {
		this.values = values;

		setAddess("Creditor", content.getCreditorInformation().getCreditorAddress());
		setAddess("UltimateCreditor", content.getUltimateCreditor());
		setAddess("Debitor", content.getDebitor());

		Date dueDate = StringUtils.parseDate(get("DueDate"));
		ReferenceType rt = getReferenceType();

		content.getCreditorInformation().iban(get("IBAN"));
		content.paymentAmount(
				new PaymentAmount().amount(getNumber("Amount")).currency(get("Currency")).dueDate(dueDate));
		content.paymentReference(new PaymentReference().unstructuredMessage(get("Message")).reference(get("Reference"))
				.referenceType(rt).billInformation(get("BillInformation")));

		String AlternativeSchemaParameters = get("AlternativeSchema");
		if (!StringUtils.isEmpty(AlternativeSchemaParameters)) {
			String AlternativeSchemaParametersContent = get("AlternativeSchemaParameters");
			content.alternativeSchema(AlternativeSchemaParameters, AlternativeSchemaParametersContent);
		}
		AlternativeSchemaParameters = get("AlternativeSchema1");
		if (!StringUtils.isEmpty(AlternativeSchemaParameters)) {
			String AlternativeSchemaParameters1Content = get("AlternativeSchemaParameters1");
			content.alternativeSchema(AlternativeSchemaParameters, AlternativeSchemaParameters1Content);
		}
		
		updateProperties(values, content);
		
	}

	private void updateProperties(Map<String, Object> values, ContentBarcodeCH content) {
		// generell
		String fileName = (String) values.get("filename");
		if (fileName!=null) {
			content.getProperties().put("filename", fileName);
		}
		String pictureFormat = (String) values.get("pictureFormat");
		if (pictureFormat!=null) {
			content.getProperties().put("pictureFormat", pictureFormat);
		}
		// für payment slip
		String language = (String) values.get("language");
		if (language!=null) {
			content.getProperties().put("language", language);
		}
		String paperFormat = (String) values.get("pageFormat");
		if (paperFormat!=null) {
			content.getProperties().put("pageFormat", paperFormat);
		}
		String printLines = (String) values.get("printLines");
		if (printLines!=null) {
			content.getProperties().put("printLines", printLines);
		}
	}

	private ReferenceType getReferenceType() {
		ReferenceType rt = ReferenceType.NON;
		String referenceType = StringUtils.str((get("ReferenceType"))).trim();
		if (!StringUtils.isEmpty(referenceType)) {
			rt = ReferenceType.valueOf(referenceType);
		}
		return rt;
	}

	private void setAddess(String addressType, Address address) {
		String formattedAddress = get(addressType);
		if (!StringUtils.isEmpty(formattedAddress)) {
			address.setAddressPrinted(formattedAddress);
		} else {
			address.name(get(addressType + "Name"))
				.country(get(addressType + "Country"));
			// we do not expect the address type to be  
			AddressType strucutredOrUnstructured = getAddressType(addressType);
			if (strucutredOrUnstructured!=null) {
				address.addressType(strucutredOrUnstructured);
				switch (strucutredOrUnstructured) {
					case U:
						address.addressLine1(get(addressType + "AddressLine1"))
							.addressLine2(get(addressType + "AddressLine2"));
						break;
						
					case S:
					address.street(get(addressType + "Street"))
						.houseNumber(get(addressType + "HouseNumber")).postalCode(get(addressType + "PostalCode"))
						.city(get(addressType + "City"));
					break;
				}
			}
		}
	}
	// if the AddressType is not provided we determine it dynamically
	private AddressType getAddressType(String addressType) {
		String strucutredOrUnstructured = get(addressType + "AddressType");
		AddressType result = null;
		if (StringUtils.isEmpty(strucutredOrUnstructured)){
			if (StringUtils.isEmpty(get(addressType + "City"))) {
				if (!StringUtils.isEmpty(get(addressType + "Name"))) {
					result = AddressType.U;
				}
			} else {
				result = AddressType.S;	
			}
		} else {
			result = AddressType.valueOf(strucutredOrUnstructured.trim());
		}
		return result;
		
	}
	
	private String get(String key) {
		return StringUtils.str(values.get(key));
	}

	private BigDecimal getNumber(String key) {
		Object obj = values.get(key);
		if (obj != null) {
			if (obj instanceof Double) {
				return BigDecimal.valueOf((Double) obj);
			} else if (obj instanceof BigDecimal) {
				return (BigDecimal) obj;
			} else {
				String str = obj.toString().trim();
				if (!str.isEmpty()) {
					return new BigDecimal(str);
				}
			}
		}
		return null;
	}

	/**
	 * Converts a ContentBarcodeCH to a map
	 *
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @return a {@link java.util.Map} object
	 */
	public Map<String, Object> contentToMap(ContentBarcodeCH content) {
		return contentToMap(content, false);
	}

	/**
	 * <p>contentToMap.</p>
	 *
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @param withEmtpyEntries a boolean
	 * @return a {@link java.util.Map} object
	 */
	public Map<String, Object> contentToMap(ContentBarcodeCH content, boolean withEmtpyEntries) {
		Map<String, Object> result = new TreeMap();
		putAddress(content.getDebitor(), "Debitor", withEmtpyEntries, result);
		putAddress(content.getCreditorInformation().getCreditorAddress(), "Creditor", withEmtpyEntries, result);
		putAddress(content.getUltimateCreditor(), "UltimateCreditor", withEmtpyEntries, result);
		putResult(result, "IBAN", content.getCreditorInformation().getIban(), withEmtpyEntries);
		putResult(result, "Amount", content.getPaymentAmount().getAmountStr(), withEmtpyEntries);
		putResult(result, "Currency", content.getPaymentAmount().getCurrency(), withEmtpyEntries);
		putResult(result, "Message", content.getPaymentReference().getUnstructuredMessage(), withEmtpyEntries);
		putResult(result, "Reference", content.getPaymentReference().getReference(), withEmtpyEntries);

		ReferenceType refType = content.getPaymentReference().getReferenceType();
		if (refType != null)
			putResult(result, "ReferenceType", content.getPaymentReference().getReferenceType().name(),
					withEmtpyEntries);

		Date date = content.getPaymentAmount().getDueDate();
		if (date != null) {
			putResult(result, "DueDate", df.format(date), withEmtpyEntries);
		}
		
		if (content.getPaymentReference().getUnstructuredMessage()!=null) {
			putResult(result, "UnstructuredMessage", content.getPaymentReference().getUnstructuredMessage().toString(),withEmtpyEntries);		
		}
		
		if (content.getPaymentReference().getBillInformation()!=null) {
			putResult(result, "BillInformation", content.getPaymentReference().getBillInformation().toString(),withEmtpyEntries);		
		}

		for (int j=0;j<content.getAlternativeSchema().size();j++) {
			String prefix = "";
			if (j>0) {
				prefix = String.valueOf(j);
			}
			putResult(result, "AlternativeSchema"+prefix, content.getAlternativeSchema().get(j).getTitle(), withEmtpyEntries);
			putResult(result, "AlternativeSchemaParameters"+prefix, content.getAlternativeSchema().get(j).getContent(), withEmtpyEntries);
		}
		
		result.put("contentType",ContentBarcodeCH.class.getSimpleName());
		
		return result;
	}

	private void putAddress(Address adr, String prefix, boolean withEmtpyEntries, Map<String, Object> result) {
		AddressType adressType = adr.getAddressType();
		if (adressType != null) {
			putResult(result, prefix + "AddressType", adressType.name(), withEmtpyEntries);
		} else {
			LOG.warn("The address type is not defined");
		}
		putResult(result, prefix + "Name", adr.getName(), withEmtpyEntries);
		putResult(result, prefix + "AddressLine1", adr.getAddressLine1(), withEmtpyEntries);
		putResult(result, prefix + "AddressLine2", adr.getAddressLine2(), withEmtpyEntries);
		putResult(result, prefix + "Country", adr.getCountryISO(), withEmtpyEntries);
		if (adr.getAddressType()==AddressType.S) {
			putResult(result, prefix + "Street", adr.getStreet(), withEmtpyEntries);
			putResult(result, prefix + "HouseNumber", adr.getHouseNumber(), withEmtpyEntries);
			putResult(result, prefix + "PostalCode", adr.getPostalCode(), withEmtpyEntries);
			putResult(result, prefix + "City", adr.getCity(), withEmtpyEntries);
		} 
	}

	private void putResult(Map<String, Object> result, String key, String value, boolean withEmtpyEntries) {
		if (withEmtpyEntries || !StringUtils.isEmpty(value)) {
			result.put(key, value);
		}
	}

}
