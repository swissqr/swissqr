package ch.swissqr.content.ch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Creditor information section of swiss barcode payload
 *
 * @author pschatzmann
 */
public class CreditorInformation {
 	private String iban = "";
 	private Address creditorAddress = new Address();

	/**
	 * <p>Constructor for CreditorInformation.</p>
	 */
	public CreditorInformation() {		
	}

	/**
	 * <p>Constructor for CreditorInformation.</p>
	 *
	 * @param creditorAddress a {@link ch.swissqr.content.ch.Address} object
	 * @param iban a {@link java.lang.String} object
	 */
	public CreditorInformation(Address creditorAddress, String iban) {
		this.iban = iban;
		this.creditorAddress = creditorAddress;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.str(iban));
		sb.append(StringUtils.CRLF);
		sb.append(creditorAddress);
		return sb.toString();
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
	 * @return a {@link ch.swissqr.content.ch.CreditorInformation} object
	 */
	public CreditorInformation iban(String iban) {
		this.iban = iban;
		return this;
	}

	/**
	 * <p>Getter for the field <code>creditorAddress</code>.</p>
	 *
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address getCreditorAddress() {
		return creditorAddress;
	}

	/**
	 * <p>creditorAddress.</p>
	 *
	 * @param adr a {@link ch.swissqr.content.ch.Address} object
	 * @return a {@link ch.swissqr.content.ch.CreditorInformation} object
	 */
	public CreditorInformation creditorAddress(Address adr) {
		creditorAddress = adr;
		return this;
	}
	
	/**
	 * <p>check.</p>
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<Error> check() {
		List<Error> result =  new ArrayList();
		StringUtils.check("iban", true, 21, iban, result);
		if (iban != null && iban.length()>2) {
			StringUtils.check("iban", false, Arrays.asList("CH","LI"), iban.substring(0, 2), result);
		}
		result.addAll(this.getCreditorAddress().check("Creditor"));
		return result;
	}
	

}
