package ch.swissqr.content.ch;

/**
 * <p>IAddressParser interface.</p>
 *
 * @author pschatzmann
 */
public interface IAddressParser {
	/**
	 * <p>parse.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 * @param adr a {@link ch.swissqr.content.ch.Address} object
	 */
	public void parse(String str, Address adr);
}
