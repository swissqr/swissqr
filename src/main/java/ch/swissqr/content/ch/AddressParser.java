package ch.swissqr.content.ch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Parse a address string into an Structured Address
 *
 * @author pschatzmann
 */
public class AddressParser implements IAddressParser {
	private static Logger LOG = Logger.getLogger(AddressParser.class);
	private static Map<String, String> countryMap = null;
	private static List<String> salutations = new ArrayList();

	static {
		salutations.addAll(Arrays.asList("herr", "frau", "firma", "fräulein", "firma", "mr", "ms",
			"madame","mme","mmes","mlle","monsieur","m."));
	}

	/** {@inheritDoc} */
	public void parse(String str, Address adr) {
		String sa[] = str.split("[\\r\\n]+|;|,");
		List<String> lines = new ArrayList(Arrays.asList(sa));

		trim(lines);
		removeEmptyLines(lines);

		// Anreden entfernen von erster zeile
		String firstLine = lines.get(0);
		if (salutations.contains(firstLine.toLowerCase())) {
			lines.remove(0);
		}

		// 1ster eintrag ist name
		String name = lines.get(0);
		adr.name(name);
		lines.remove(0);

		// bestimme land
		if (!lines.isEmpty()) {
			String lastRow = lines.get(lines.size() - 1);
			String countryCode = lastRow.length() == 2 ? lastRow : getCountryMap().get(lastRow.toLowerCase());
			if (countryCode != null) {
				adr.countryISO(countryCode);
				lines.remove(lastRow);
			} else {
				adr.countryISO("CH");
			}

			// bestimme plz und ort
			if (!lines.isEmpty()) {
				lastRow = lines.get(lines.size() - 1);
				Split split = new Split(lastRow);
				adr.postalCode(split.numberPart);
				adr.city((split.strPart));
				lines.remove(lastRow);

				if (!lines.isEmpty()) {
					// betimme strasse and nummer
					lastRow = lines.get(lines.size() - 1);
					split = new Split(lastRow);
					adr.street(split.strPart);
					adr.houseNumber(split.numberPart);
					lines.remove(lastRow);

					if (!lines.isEmpty()) {
						LOG.warn("Unprocessed address lines:" + lines);
					}
				}
			}
		}
	}

	/**
	 * Adds additional salutations which will be ignored in the starting address.
	 * Currently we have "herr", "frau", "firma", "fräulein", "firma", "mr", "ms",
	 *			"madame","mme","mmes","mlle","monsieur","m."
	 *
	 * @param salutations a {@link java.util.Collection} object
	 */
	public void addSalutations(Collection<String> salutations) {
		salutations.forEach(s -> salutations.add(s.toLowerCase()));
	}
	
	/**
	 * Removes the indicated saluatations
	 *
	 * @param salutations a {@link java.util.Collection} object
	 */
	public void removeSalutations(Collection<String> salutations) {
		salutations.forEach(s -> salutations.remove(s.toLowerCase()));
	}
	
	/**
	 * Returns the currently defined salutations
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<String> getSalutations(){
		return this.salutations;
	}


	/**
	 * <p>Getter for the field <code>countryMap</code>.</p>
	 *
	 * @return a {@link java.util.Map} object
	 */
	protected static Map<String, String> getCountryMap() {
		if (countryMap == null) {
			countryMap = new HashMap();
			BufferedReader br = null;
			try {
				br = new BufferedReader(
						new InputStreamReader(AddressParser.class.getResourceAsStream("/countries.csv")));
				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					String sa[] = sCurrentLine.split(",");
					if (sa.length == 2) {
						countryMap.put(sa[0].trim().toLowerCase(), sa[1].trim());
					} else {
						LOG.warn("Entry has been ignored: " + sCurrentLine);
					}
				}
			} catch (Exception ex) {
				LOG.error(ex, ex);
			} finally {
				try {

					if (br != null)
						br.close();

				} catch (Exception ex) {
				}
			}
			LOG.info("Number of countries: " + countryMap.size());
		}
		return countryMap;
	}

	/**
	 * <p>trim.</p>
	 *
	 * @param lines a {@link java.util.List} object
	 */
	protected static void trim(List<String> lines) {
		for (int j = 0; j < lines.size(); j++) {
			lines.set(j, lines.get(j).trim());
		}
	}

	/**
	 * <p>removeEmptyLines.</p>
	 *
	 * @param lines a {@link java.util.List} object
	 */
	protected static void removeEmptyLines(List<String> lines) {
		boolean ok = false;
		do {
			ok = lines.remove("");
		} while (ok);
	}

	/**
	 * Splits a string into a numeric and non numeric part
	 * 
	 * @author pschatzmann
	 *
	 */
	static class Split {
		String strPart = "";
		String numberPart = "";

		public Split(String input) {
			int pos = input.indexOf(" ");
			if (pos > -1) {
				String left = input.substring(0, pos);
				String right = input.substring(pos + 1, input.length());

				if (containsNumer(left)) {
					numberPart = left.trim();
					strPart = right.trim();
				} else {
					pos = input.lastIndexOf(" ");
					left = input.substring(0, pos);
					right = input.substring(pos + 1, input.length());
					numberPart = right.trim();
					strPart = left.trim();
				}
			} else {
				strPart = input.trim();
				numberPart = "";
			}
		}

		boolean containsNumer(String input) {
			for (int character = 0; character < input.length(); character++) {
				if ((Character.isDigit(input.charAt(character)))) {
					return true;
				}
			}
			return false;
		}

	}

}
