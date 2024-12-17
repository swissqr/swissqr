package ch.swissqr.content.ch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Address information for Creditor, Ultimate creditor and Ultimate debtor.
 * We support Structured and Unstructured Addresses.
 *
 * @author pschatzmann
 */
public class Address  {
	private static Map<String, String> countries = new HashMap<String, String>();
	private static IAddressParser addressParser = new AddressParser();
	private String name = "";
	private String street = "";
	private String houseNumber = "";
	private String postalCode = "";
	private String city = "";
	private String countryISO = "";
	private AddressType addressType = null; // Structured Address
	/**
	 * Empty Constructor.
	 */
	public Address() {
	}

	/**
	 * Defines the address by parsing the full address string
	 *
	 * @param addressString a {@link java.lang.String} object
	 */
	public Address(String addressString) {
		this.setAddressPrinted(addressString);
	}

	
	/**
	 * Defines the unstructured address
	 *
	 * @param name a {@link java.lang.String} object
	 * @param street a {@link java.lang.String} object
	 * @param nr a {@link java.lang.String} object
	 * @param postalCode a {@link java.lang.String} object
	 * @param city a {@link java.lang.String} object
	 * @param country a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address structured(String name, String street, String nr, String postalCode, String city, String country) {
		this.name(name);
		this.street(street);
		this.houseNumber(nr);
		this.postalCode(postalCode);
		this.city(city);
		this.country(country);
		return this;
	}

	/**
	 * Defines the structured address
	 *
	 * @param name a {@link java.lang.String} object
	 * @param line1 a {@link java.lang.String} object
	 * @param line2 a {@link java.lang.String} object
	 * @param country a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address unstructured(String name, String line1, String line2, String country) {
		this.name(name);
		this.addressLine1(line1);
		this.addressLine2(line2);
		this.country(country);
		return this;
	}

	/**
	 * Determines the Name
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getName() {
		return name;
	}

	/**
	 * Defines the Name
	 *
	 * @param name a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address name(String name) {
		this.name = name;
		return this;
	}
	
	/**
	 * Determines the address line 1  (Street + House number)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getAddressLine1() {
		if (getAddressType()==AddressType.U) {
			return this.getStreet();
		} else {
			return this.getStreet()+" "+this.getHouseNumber();
		}
	}

	/**
	 * Defines the address line 1 (for unstructured address only)
	 *
	 * @param line1 a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address addressLine1(String line1) {
		this.setAddressType(AddressType.U);
		this.street = line1;
		return this;
	}

	/**
	 * Determines the address line 2 (postal code + city)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getAddressLine2() {
		StringBuffer sb = new StringBuffer();
		if (getAddressType()==AddressType.U) {
			sb.append(this.getHouseNumber());
		} else {
			if (!StringUtils.isEmpty(this.getCountryISO())) {
				sb.append(StringUtils.str(this.getCountryISO()));
				sb.append("-");
			}
			sb.append(this.getPostalCode());
			sb.append(" ");
			sb.append(this.getCity());
		}
		return sb.toString();

	}

	/**
	 * Defines the address line 2 (for unstructured address only)
	 *
	 * @param line1 a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address addressLine2(String line1) {
		this.setAddressType(AddressType.U);
		this.houseNumber = line1;
		return this;
	}


	/**
	 * Determines the Street  (for structured address only)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Defines the street  (for structured address only)
	 *
	 * @param street a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address street(String street) {
		this.setAddressType(AddressType.S);
		this.street = street;
		return this;
	}

	/**
	 * Determines the house number  (for structured address only)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getHouseNumber() {
		return houseNumber;
	}

	/**
	 * Defines the building number  (for structured address only)
	 *
	 * @param houseNumber a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address houseNumber(String houseNumber) {
		this.setAddressType(AddressType.S);
		this.houseNumber = houseNumber;
		return this;
	}

	/**
	 * Determines the postal code  (for structured address only)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Defines the postal code (for structured address only)
	 *
	 * @param postalCode a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address postalCode(String postalCode) {
		this.setAddressType(AddressType.S);
		this.postalCode = postalCode;
		return this;
	}

	/**
	 * Determines the country ISO code
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getCountryISO() {
		return StringUtils.str(countryISO);
	}

	/**
	 * Defines the country ISO code
	 *
	 * @param countryISO a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address countryISO(String countryISO) {
		this.countryISO = countryISO;
		return this;
	}

	/**
	 * Defines the country by name. We will look up the related ISO code)
	 *
	 * @param country a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address country(String country) {
		this.countryISO = country.length()==2 ? country : AddressParser.getCountryMap().get(country.toLowerCase());;
		return this;
	}

	/**
	 * Determines the city (for structured address only)
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Defines the city (for structured address only)
	 *
	 * @param city a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address city(String city) {
		this.setAddressType(AddressType.S);
		this.city = city;
		return this;
	}

	/**
	 * Checks if the address is complete
	 *
	 * @param addressType a {@link java.lang.String} object
	 * @return a {@link java.util.List} object
	 */
	public List<Error> check(String addressType) {
		List<Error> result = new ArrayList();
		StringUtils.check(addressType + "Name", true, 70, name, result);
		if (getAddressType()==AddressType.S) {
			StringUtils.check(addressType + "Street", false, 70, street, result);
			StringUtils.check(addressType + "HouseNumber", false, 16, houseNumber, result);
			StringUtils.check(addressType + "PostalCode", true, 16, postalCode, result);
			StringUtils.check(addressType + "City", true, 35, city, result);
		} else {
			StringUtils.check(addressType + "AddressLine1", true, 70, street, result);
			StringUtils.check(addressType + "AddressLine2", true, 70, houseNumber, result);	
		}
		return result;
	}

	/**
	 * <p>isDefined.</p>
	 *
	 * @return a boolean
	 */
	@JsonIgnore
	public boolean isDefined() {
		return !StringUtils.isEmpty(this.name);
	}

	/**
	 * {@inheritDoc}
	 *
	 * Returns information in IBAN format
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.str(getAddressType()));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(name));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(street));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(houseNumber));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(postalCode));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.str(city));
		sb.append(StringUtils.CRLF);
		sb.append(StringUtils.isEmpty(name) ? "" : StringUtils.str(countryISO));
		sb.append(StringUtils.CRLF);
		return sb.toString();
	}

	/**
	 * <p>toStringExt.</p>
	 *
	 * @param prefix a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public String toStringExt(String prefix) {
		StringBuffer sb = new StringBuffer();
		sb.append(prefix + "Name: ");
		sb.append(StringUtils.str(name));
		sb.append(StringUtils.CRLF);
		sb.append(prefix + "Street: ");
		sb.append(StringUtils.str(street));
		sb.append(StringUtils.CRLF);
		sb.append(prefix + "HouseNumber: ");
		sb.append(StringUtils.str(houseNumber));
		sb.append(StringUtils.CRLF);
		sb.append(prefix + "PostalCode: ");
		sb.append(StringUtils.str(postalCode));
		sb.append(StringUtils.CRLF);
		sb.append(prefix + "City: ");
		sb.append(StringUtils.str(city));
		sb.append(StringUtils.CRLF);
		sb.append(prefix + "Country: ");
		sb.append(StringUtils.str(countryISO));
		sb.append(StringUtils.CRLF);
		return sb.toString();
	}

	/**
	 * Parses a full address to fill the address information
	 *
	 * @param str a {@link java.lang.String} object
	 */
	public void setAddressPrinted(String str) {
		getAddressParser().parse(str, this);
	}

	/**
	 * Returns the full printable address separated by CRLF
	 *
	 * @return a {@link java.lang.String} object
	 */
	@JsonIgnore
	public String getAddressPrinted() {
		return getAddressPrinted(StringUtils.CRLF);
	}

	/**
	 * Return the full printable address lines
	 *
	 * @return an array of {@link java.lang.String} objects
	 */
	@JsonIgnore
	public String[] getAddressLines() {
		return getAddressPrinted(System.lineSeparator()).split(System.lineSeparator());
	}

	/**
	 * Return the full printable address lines where the filess are separated by the inicated delimiter
	 *
	 * @param delim a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	@JsonIgnore
	public String getAddressPrinted(String delim) {
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.str(this.getName()));
		sb.append(delim);
		sb.append(StringUtils.str(this.getAddressLine1()));
		sb.append(delim);
		sb.append(StringUtils.str(this.getAddressLine2()));
		return sb.toString();
	}
	
	/**
	 * Creates an address from a formatted address string
	 *
	 * @param formattedAddress a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public static Address createAddress(String formattedAddress) {
		Address result = new Address();
		result.setAddressPrinted(formattedAddress);
		return result;
	}

	/**
	 * Creates an address from a formatted address string
	 *
	 * @return Address
	 * @param swissQRAddress a {@link java.util.List} object
	 */
	public static Address createAddress(List<String> swissQRAddress) {
		StringBuffer sb = new StringBuffer();
		for (String line : swissQRAddress) {
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		return createAddress(sb.toString());
	}
	
	/**
	 * Determines the address type (S for structured, U for unstructured)
	 *
	 * @return AddressType
	 */
	public AddressType getAddressType() {
		if (addressType==null) {
			// dynamically determine address type if it is not defined
			if (!StringUtils.isEmpty(this.getStreet())) {
				if (StringUtils.isEmpty(this.getCity())){
					addressType = AddressType.S;
				} else {
					addressType = AddressType.U;					
				}			
			}
		}
		return addressType;
	}

	/**
	 * <p>Setter for the field <code>addressType</code>.</p>
	 *
	 * @param addressType a {@link ch.swissqr.content.ch.AddressType} object
	 */
	protected void setAddressType(AddressType addressType) {
		this.addressType  = addressType;
	}

	/**
	 * Determines the address parser which is used to translate a string into an address
	 *
	 * @return a {@link ch.swissqr.content.ch.IAddressParser} object
	 */
	public static IAddressParser getAddressParser() {
		return addressParser;
	}

	/**
	 * Defines the address parser which is used to translate a string into an address
	 *
	 * @param addressParser a {@link ch.swissqr.content.ch.IAddressParser} object
	 */
	public static void setAddressParser(IAddressParser addressParser) {
		Address.addressParser = addressParser;
	}

	/**
	 * Standard Java bean setter to define the name
	 *
	 * @param name a {@link java.lang.String} object
	 */
	public void setName(String name) {
		this.name(name);
	}

	/**
	 * Standard Java bean setter to define the street
	 *
	 * @param street a {@link java.lang.String} object
	 */
	public void setStreet(String street) {
		this.street(street);
	}

	/**
	 * Standard Java bean setter to define the house number
	 *
	 * @param houseNumber a {@link java.lang.String} object
	 */
	public void setHouseNumber(String houseNumber) {
		this.houseNumber(houseNumber);
	}

	/**
	 * Standard Java bean setter to define the zip code
	 *
	 * @param postalCode a {@link java.lang.String} object
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode(postalCode);
	}

	/**
	 * Standard Java bean setter to define the location
	 *
	 * @param city a {@link java.lang.String} object
	 */
	public void setCity(String city) {
		this.city(city);
	}

	/**
	 * Standard Java bean setter to define the ISO code for the country of the address
	 *
	 * @param countryISO a {@link java.lang.String} object
	 */
	public void setCountryISO(String countryISO) {
		this.countryISO(countryISO);
	}

	/**
	 * Defines the address type. This is usually not necessary because it is
	 * determined by the context.
	 *
	 * @param type a {@link ch.swissqr.content.ch.AddressType} object
	 * @return a {@link ch.swissqr.content.ch.Address} object
	 */
	public Address addressType(AddressType type) {
		this.addressType = type;
		return this;
	}
	

}
