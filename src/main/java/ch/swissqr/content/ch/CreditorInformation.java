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
 *
 */

public class CreditorInformation {
 	private String iban = "";
 	private Address creditorAddress = new Address();

	public CreditorInformation() {		
	}

	public CreditorInformation(Address creditorAddress, String iban) {
		this.iban = iban;
		this.creditorAddress = creditorAddress;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.str(iban));
		sb.append(StringUtils.CRLF);
		sb.append(creditorAddress);
		return sb.toString();
	}

	public String getIban() {
		return iban;
	}

	public CreditorInformation iban(String iban) {
		this.iban = iban;
		return this;
	}

	public Address getCreditorAddress() {
		return creditorAddress;
	}

	public CreditorInformation creditorAddress(Address adr) {
		creditorAddress = adr;
		return this;
	}
	
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
