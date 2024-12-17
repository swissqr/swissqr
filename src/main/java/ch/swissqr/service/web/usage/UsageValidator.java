package ch.swissqr.service.web.usage;

import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;

import ch.swissqr.errors.LicenceError;
import ch.swissqr.errors.UsageViolationException;
import ch.swissqr.utils.StringUtils;

/**
 * We allow the registration of a usage monitoring or license checking functionality.
 *
 * @author pschatzmann
 */
public class UsageValidator  {
	private static final Logger LOG = Logger.getLogger(UsageValidator.class);
	private static ILicenseCheck check;
	
	/**
	 * In the system environment or system property 'licenseCheckClass' you can indicate
	 * a java class which is used to perform the check. This class needs to implement
	 * the interface ILicenseCheck. Please indicate the class with the package names:
	 * e.g. ch.swissqr.license.LicenseCheck
	 *
	 * @throws ch.swissqr.errors.LicenceError
	 * @param headers a {@link javax.ws.rs.core.HttpHeaders} object
	 * @param licenseKey a {@link java.lang.String} object
	 * @param numberOfBarcodes a int
	 */
	public static void checkEx(HttpHeaders headers, String licenseKey, int numberOfBarcodes) throws LicenceError {
		String requestorIP = getRemoteIP(headers);
		String apiKey = !StringUtils.isEmpty(licenseKey) ? licenseKey : getApiKey(headers);
		LOG.info(requestorIP+" / key = "+apiKey);
		if (check==null) {
			String className = getLicenseCheckClassName();
			LOG.info("Using license check class "+className);
			if (className!=null) {
				try {
					check = (ILicenseCheck) Class.forName(className).newInstance();
				} catch(Exception ex) {
					LOG.error("Could not laod license class", ex);
				}
			}
		}
		if (check!=null) {
			check.checkLicence(StringUtils.str(requestorIP), StringUtils.str(apiKey), numberOfBarcodes);
		}
	}
	
	/**
	 * If no license is indicated we return false. If a license is given we perform a check which
	 * gives an exception of it fails. This allows the generation of test barcode if no license
	 * is available.
	 *
	 * @throws ch.swissqr.errors.LicenceError
	 * @param headers a {@link javax.ws.rs.core.HttpHeaders} object
	 * @param licenseKey a {@link java.lang.String} object
	 * @param numberOfBarcodes a int
	 * @return a boolean
	 */
	public static boolean check(HttpHeaders headers, String licenseKey, int numberOfBarcodes) throws LicenceError  {
		boolean ok = true;
		if (getLicenseCheckClassName()!=null) {
			String apiKey = !StringUtils.isEmpty(licenseKey) ? licenseKey : getApiKey(headers);
			LOG.info(apiKey);
			if (StringUtils.isEmpty(apiKey)) {
				ok = false;
			} else {
				checkEx(headers,licenseKey,numberOfBarcodes);
			}
		}
		return ok;
	}
	
	/**
	 * Determines the class name which is used for the license check.
	 *
	 * @return a {@link java.lang.String} object
	 */
	public static String getLicenseCheckClassName() {
		return StringUtils.getProperty("licenseCheckClass");
	}

	
	/**
	 * Determines the remote IP address
	 */
	private static String getRemoteIP(HttpHeaders headers) {
		String result = headers.getHeaderString("REMOTE_ADDR");
		LOG.debug("REMOTE_ADDR:"+result);
		if (StringUtils.isEmpty(result)) {
			result = headers.getHeaderString("HTTP_X_FORWARDED_FOR");
			LOG.debug("HTTP_X_FORWARDED_FOR:"+result);
			if (StringUtils.isEmpty(result)) {
				result = headers.getHeaderString("HTTP_CLIENT_IP");
				LOG.debug("HTTP_CLIENT_IP:"+result);
				if (StringUtils.isEmpty(result)) {
					result = headers.getHeaderString("HTTP_X_FORWARDED");
					LOG.debug("HTTP_X_FORWARDED"+result);
					if (StringUtils.isEmpty(result)) {
						result = headers.getHeaderString("HTTP_X_CLUSTER_CLIENT_IP");
						LOG.debug("HTTP_X_CLUSTER_CLIENT_IP"+result);
						if (StringUtils.isEmpty(result)) {
							result = headers.getHeaderString("HTTP_FORWARDED_FOR");
							LOG.debug("HTTP_FORWARDED_FOR"+result);
							if (StringUtils.isEmpty(result)) {
								result = headers.getHeaderString("HTTP_FORWARDED");
								LOG.debug("HTTP_FORWARDED"+result);
								if (StringUtils.isEmpty(result)) {
									result = headers.getHeaderString("X-Real-IP");
									LOG.debug("X-Real-IP"+result);
									if (StringUtils.isEmpty(result)) {
										result = headers.getHeaderString("X-Forwarded-For");
										LOG.debug("X-Forwarded-For"+result);
									}
								}
							}
						}
					}
				}
			}
		}
		
		return result;
	}

	/**
	 * Determines the apiKey from the header
	 */
	private static String getApiKey(HttpHeaders headers) {
		return headers.getHeaderString("licenseKey");
	}
}
