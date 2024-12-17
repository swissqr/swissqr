package ch.swissqr.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import ch.swissqr.barcode.ErrorCorrectionLevel;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.swissqr.barcode.QRCombinedBarcode;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.property.Address;
import ezvcard.property.Email;
import ezvcard.property.Telephone;
import ezvcard.property.Title;
import ezvcard.property.Url;

/**
 * QR barcode which contains a VCARD address information
 *
 * @author pschatzmann
 */
// BEGIN:VCARD
// VERSION:4.0
// N:Forrest;Gump;;Mr.;
// FN:Forrest Gump
// ORG:Bubba Gump Shrimp Co.
// TITLE:Shrimp Man
// PHOTO;MEDIATYPE=image/gif:http://www.example.com/dir_photos/my_photo.gif
// TEL;TYPE=work,voice;VALUE=uri:tel:+1-111-555-1212
// TEL;TYPE=home,voice;VALUE=uri:tel:+1-404-555-1212
// ADR;TYPE=WORK,PREF:;;100 Waters Edge;Baytown;LA;30314;United States of
// America
// LABEL;TYPE=WORK,PREF:100 Waters Edge\nBaytown\, LA 30314\nUnited States of
// America
// ADR;TYPE=HOME:;;42 Plantation St.;Baytown;LA;30314;United States of America
// LABEL;TYPE=HOME:42 Plantation St.\nBaytown\, LA 30314\nUnited States of
// America
// EMAIL:forrestgump@example.com
// REV:20080424T195243Z
// x-qq:21588891
// END:VCARD
public class ContentVCard extends ContentBase implements IContent {
	private static final Logger LOG = Logger.getLogger(ContentVCard.class);
	private VCard vcard;

	/**
	 * <p>Constructor for ContentVCard.</p>
	 */
	public ContentVCard() {
		vcard = new VCard();
	}

	/**
	 * <p>Constructor for ContentVCard.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 */
	public ContentVCard(String str) {
		parse(str);
	}

	/**
	 * <p>Constructor for ContentVCard.</p>
	 *
	 * @param vcardPar a {@link ezvcard.VCard} object
	 */
	public ContentVCard(VCard vcardPar) {
		this.vcard = vcardPar;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isOK() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String getContent() throws UnsupportedEncodingException {
		String content = Ezvcard.write(vcard).version(VCardVersion.V3_0).go();
		LOG.info(content);
		return content;

	}

	/** {@inheritDoc} */
	@Override
	public IContent parse(String str) {
		vcard = Ezvcard.parse(str).first();
		return this;
	}

	// @JsonIgnore
	// public VCard getVcard() {
	// return vcard;
	// }
	//
	// public void setVcard(VCard vcard) {
	// this.vcard = vcard;
	// }

	/** {@inheritDoc} */
	@Override
	public void clean() {
		vcard = new VCard();

	}

	/** {@inheritDoc} */
	@Override
	public List<Error> check() {
		return Arrays.asList();
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Properties getProperties() {
		return new Properties();
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Collection<String> getPrefix() {
		return Arrays.asList("BEGIN:VCARD");
	}

	/** {@inheritDoc} */
	@Override
	@JsonIgnore
	public BufferedImage toBarcode(String format, Double mm, ErrorCorrectionLevel errorCorrectionLevel)
			throws UnsupportedEncodingException, BarcodeException, IOException {
		QRCombinedBarcode barcode = new QRCombinedBarcode(this.isTest()? "/icons/test.png":"/icons/address.png", mm, errorCorrectionLevel);
		return barcode.createImage(this.getContent(), format);
	}

	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String, Object> record) {
		super.setDataMap(record);

		this.setFormattedName(StringUtils.str(record.get("formattedName")));
		this.setOrganization(StringUtils.str(record.get("organization")));
		this.setEmail(StringUtils.str(record.get("email")));
		this.setTelephoneNumber(StringUtils.str(record.get("telephoneNumber")));
		this.setTitle(StringUtils.str(record.get("title")));
		this.setUrl(StringUtils.str(record.get("url")));
		this.setAddress(StringUtils.str(record.get("address")));
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Map<String, Object> getDataMap() {
		try {
			Map result = new HashMap();
			result.put("formattedName", this.getFormattedName());
			result.put("organization", this.getOrganization());
			result.put("email", this.getEmail());
			result.put("telephoneNumber", this.getTelephoneNumber());
			result.put("title", this.getTitle());
			result.put("url", this.getUrl());
			result.put("address", this.getAddress());
			result.put("contentType", this.getContentType());
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * <p>setFormattedName.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 */
	public void setFormattedName(String name) {
		this.vcard.setFormattedName(name);
	}

	/**
	 * <p>getFormattedName.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getFormattedName() {
		return this.vcard.getFormattedName().getValue();
	}

	/**
	 * <p>setOrganization.</p>
	 *
	 * @param company a {@link java.lang.String} object
	 */
	public void setOrganization(String company) {
		this.vcard.setOrganization(company);
	}

	/**
	 * <p>getOrganization.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getOrganization() {
		if (this.vcard.getOrganization() != null) {
			for (String org : this.vcard.getOrganization().getValues()) {
				return org;
			}
		}
		return "";
	}

	/**
	 * <p>setEmail.</p>
	 *
	 * @param email a {@link java.lang.String} object
	 */
	public void setEmail(String email) {
		this.vcard.addEmail(email);
	}

	/**
	 * <p>getEmail.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getEmail() {
		for (Email mail : this.vcard.getEmails()) {
			return mail.getValue();
		}
		return "";
	}

	/**
	 * <p>setTelephoneNumber.</p>
	 *
	 * @param phoneNumber a {@link java.lang.String} object
	 */
	public void setTelephoneNumber(String phoneNumber) {
		this.vcard.addTelephoneNumber(phoneNumber);
	}

	/**
	 * <p>getTelephoneNumber.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getTelephoneNumber() {
		for (Telephone tel : this.vcard.getTelephoneNumbers()) {
			return tel.getText();
		}
		return "";
	}

	/**
	 * <p>setTitle.</p>
	 *
	 * @param title a {@link java.lang.String} object
	 */
	public void setTitle(String title) {
		this.vcard.addTitle(title);
	}

	/**
	 * <p>getTitle.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getTitle() {
		for (Title title : this.vcard.getTitles()) {
			return title.getValue();
		}
		return "";
	}

	/**
	 * <p>setUrl.</p>
	 *
	 * @param url a {@link java.lang.String} object
	 */
	public void setUrl(String url) {
		this.vcard.addUrl(url);
	}

	/**
	 * <p>getUrl.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getUrl() {
		for (Url url : this.vcard.getUrls()) {
			return url.getValue();
		}
		return "";
	}

	/**
	 * <p>setAddress.</p>
	 *
	 * @param address a {@link java.lang.String} object
	 */
	public void setAddress(String address) {
		Address adr = new Address();
		adr.setStreetAddress(address);
		
		this.vcard.addAddress(adr);
	}

	/**
	 * <p>getAddress.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getAddress() {
		for (Address a : this.vcard.getAddresses()) {
			return a.getStreetAddressFull();
		}
		return "";
	}

}
