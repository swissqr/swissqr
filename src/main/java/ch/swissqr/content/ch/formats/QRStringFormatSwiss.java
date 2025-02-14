package ch.swissqr.content.ch.formats;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.AddressType;
import ch.swissqr.content.ch.AlternativeSchema;
import ch.swissqr.content.ch.CreditorInformation;
import ch.swissqr.content.ch.IAlternativeSchema;
import ch.swissqr.content.ch.PaymentAmount;
import ch.swissqr.content.ch.PaymentReference;
import ch.swissqr.content.ch.PaymentReference.ReferenceType;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.StringUtils;

/**
 * Serialization and Deserialization of the standard text format as defined in
 * the specification. However the standard system string encoding is used which
 * is usually (UTF8)
 *
 * Windows is using CRLF to delimit lines. Linux and OSx are using just LF
 * We remove all CRs so that we can use the same logic in all environments
 *
 * @author pschatzmann
 */
public class QRStringFormatSwiss implements IFormat {
	private final static Logger LOG = Logger.getLogger(QRStringFormatSwiss.class);

	/** {@inheritDoc} */
	@Override
	public List<IContent> read(String contentString) throws FormatException, BarcodeException {
		return parseList(contentString);
	}

	/**
	 * <p>parseList.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link java.util.List} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public List<IContent> parseList(String str) throws FormatException, BarcodeException {
		// remove all cr
		str = str.replaceAll("\\r", "");
		List<IContent> result = new ArrayList();
		String barcodeStringArray[] = str.split("SPC" + "\\n");
		for (String barcodeString : barcodeStringArray) {
			IContent content = parse(barcodeString);
			if (content != null) {
				result.add(content);
			}
		}
		return result;
	}

	/**
	 * <p>parse.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.IContent} object
	 * @throws ch.swissqr.content.ch.formats.FormatException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public IContent parse(String str) throws FormatException, BarcodeException {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		str = str.replaceAll("\\r", "");
		if (!str.startsWith("SPC")) {
			str = "SPC\n" + str;
		}
		String sa[] = str.split("\\n");
		if (sa.length<10) {
			throw new BarcodeException("Barcode is not a Swiss QR Code");
		}

		String version = sa[1];
		int line = 2;		
		String iban = sa[++line];
		
		Address creditor,ultimateCreditor;
		if (!version.equals("0100")) {
			creditor = getAddressV2(sa, line);
			line = line + 7;
			ultimateCreditor = getAddressV2(sa, line);
			line = line + 7;
		} else {
			creditor = getAddress(sa, line);
			line = line + 6;
			ultimateCreditor = getAddress(sa, line);
			line = line + 6;
		}

		// String ucName = sa[++line]; // 10

		String value = sa[++line];
		BigDecimal amount = StringUtils.isEmpty(value) ? null : new BigDecimal(value.replaceAll(" ", ""));
		String currency = sa[++line];
		
		if (version.equals("0100")) {
			String date = sa[++line];
			Date dueDate = getDate(date);
		}

		Address debitor;
		if (!version.equals("0100")) {
			debitor = getAddressV2(sa, line);
			line = line + 7;
		} else {
			debitor = getAddress(sa, line);
			line = line + 6;			
		}

		ReferenceType rt = ReferenceType.NON;
		try {
			if (!StringUtils.isEmpty(sa[++line])) {
				rt = ReferenceType.valueOf(sa[line].trim());
			}
		} catch (Exception ex) {
			LOG.warn(ex);
		}
		
	    String reference = nextLine(sa,++line);
	    String message = nextLine(sa,++line);	 
	    
	    String billInformation = null;
	    // support for new information starting v2
		if (!version.equals("0100")) {
			String trailer = nextLine(sa,++line);
		    if (!trailer.equalsIgnoreCase("EPD")) {
		    	throw new BarcodeException("Message does not contain mandatory EPD");
		    }
		    
			billInformation = nextLine(sa,++line);	
		}
		String alternativeSchemeParameters1 = nextLine(sa,++line);
		String alternativeSchemeParameters2 = nextLine(sa,++line);

		ContentBarcodeCH result = new ContentBarcodeCH();
		result.creditor(new CreditorInformation().iban(iban).creditorAddress(creditor))
				.ultimateCreditor(ultimateCreditor).debitor(debitor)
				.paymentAmount(new PaymentAmount().amount(amount).currency(currency)).paymentReference(
						new PaymentReference()
							.unstructuredMessage(message)
							.reference(reference)
							.referenceType(rt)
							.billInformation(billInformation));

		List<IAlternativeSchema> alternativeSchemas = new ArrayList();
		if (!StringUtils.isEmpty(alternativeSchemeParameters1)) {
			alternativeSchemas.add(new AlternativeSchema(alternativeSchemeParameters1));
		}
		if (!StringUtils.isEmpty(alternativeSchemeParameters2)) {
			alternativeSchemas.add(new AlternativeSchema(alternativeSchemeParameters2));
		}
		result.alternativeSchema(alternativeSchemas);
		return result;
	}

	private String nextLine(String[] sa, int i) {
		return i>=sa.length ? "": sa[i];
	}

	private Date getDate(String date) throws FormatException {
		Date dueDate = null;
		if (!StringUtils.isEmpty(date)) {
			try {
				dueDate = StringUtils.parseDate(date);
			} catch (ParseException e) {
				throw new FormatException(e);
			}
		}
		return dueDate;
	}

	private Address getAddress(String[] sa, int line) {
		return new Address().name(sa[++line]).street(sa[++line]).houseNumber(sa[++line]).postalCode(sa[++line])
				.city(sa[++line]).country(sa[++line]);
	}

	private Address getAddressV2(String[] sa, int line) {
		// the address type is mandatory - if it is not available3 the address
		// must be empty
		String addressType = sa[++line];
		Address result = new Address();
		if (!StringUtils.isEmpty(addressType)) {
			result.addressType(AddressType.valueOf(addressType)).name(sa[++line]);
			if (result.getAddressType()==AddressType.S) {
				result.street(sa[++line]).houseNumber(sa[++line]).postalCode(sa[++line])
					.city(sa[++line]).country(sa[++line]);
			} else {
				result.addressLine1(sa[++line]).addressLine2(sa[++line]).country(sa[line+=3]);
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String write(List<IContent> list) {
		StringBuffer sb = new StringBuffer();
		for (IContent cObj : list) {
			if (cObj instanceof ContentBarcodeCH) {
				ContentBarcodeCH c = (ContentBarcodeCH) cObj;
				sb.append(c.getHeader());
				sb.append(c.getCreditorInformation());
				sb.append(c.getUltimateCreditor());
				sb.append(c.getPaymentAmount());
				sb.append(c.getDebitor());
				// payment reference, unstructured msg, billing information
				sb.append(c.getPaymentReference());
				// if there is no bill information and we have some alternative schema we add a empty line
				if (!c.getPaymentReference().hasBillInformation() && !c.getAlternativeSchema().isEmpty()) {
					sb.append(StringUtils.CRLF);
				}
				for (IAlternativeSchema as : c.getAlternativeSchema()) {
					sb.append(StringUtils.CRLF);
					sb.append(as.toString());
				}
			}
		}
		return sb.toString();
	}

	private String toString(Object str) {
		return str==null? "" : str.toString();
	}

}
