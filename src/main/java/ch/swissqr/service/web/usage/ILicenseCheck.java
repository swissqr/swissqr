package ch.swissqr.service.web.usage;

import ch.swissqr.errors.LicenceError;

/**
 * <p>ILicenseCheck interface.</p>
 *
 * @author pschatzmann
 */
public interface ILicenseCheck {
	/**
	 * <p>checkLicence.</p>
	 *
	 * @param requestorIP a {@link java.lang.String} object
	 * @param licence a {@link java.lang.String} object
	 * @param count a int
	 * @throws ch.swissqr.errors.LicenceError if any.
	 */
	public void checkLicence(String requestorIP, String licence, int count) throws LicenceError;

}
