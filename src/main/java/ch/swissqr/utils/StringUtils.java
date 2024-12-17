package ch.swissqr.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * String utility functions
 *
 * @author pschatzmann
 */
public class StringUtils {
	private static Logger LOG = Logger.getLogger(StringUtils.class);
	/** Constant <code>CRLF="\r\n"</code> */
	public static String CRLF = "\r\n";

	/**
	 * Returns true if the string is null or empty or contains only spaces
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a boolean
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	/**
	 * Converts a null into an empty string
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String str(String str) {
		return str == null ? "" : str;
	}

	/**
	 * <p>str.</p>
	 *
	 * @param str a {@link java.lang.Object} object
	 * @return a {@link java.lang.String} object
	 */
	public static String str(Object str) {
		return str == null ? "" : str.toString();
	}

	/**
	 * Checks if the indicated field is not too long, empty and contains only valid
	 * entries
	 *
	 * @param fieldName a {@link java.lang.String} object
	 * @param mandatory a boolean
	 * @param maxLen a int
	 * @param value a {@link java.lang.String} object
	 * @param msgList a {@link java.util.List} object
	 */
	public static void check(String fieldName, boolean mandatory, int maxLen, String value, List<Error> msgList) {
		if (mandatory && StringUtils.isEmpty(value)) {
			String msg = "The field '" + fieldName + "' must not be empty";
			msgList.add(new Error(fieldName, msg));
		}
		if (value != null && value.length() > maxLen) {
			String msg = "The field '" + fieldName + "'(" + value + ") must not be longer then " + maxLen
					+ " characters";
			msgList.add(new Error(fieldName, msg));
		}
	}

	/**
	 * * Checks if the indicated field is not too long, empty
	 *
	 * @param fieldName a {@link java.lang.String} object
	 * @param mandatory a boolean
	 * @param asList a {@link java.util.List} object
	 * @param value a {@link java.lang.String} object
	 * @param msgList a {@link java.util.List} object
	 */
	public static void check(String fieldName, boolean mandatory, List<String> asList, String value,
			List<Error> msgList) {
		if (mandatory && StringUtils.isEmpty(value)) {
			String msg = "The field '" + fieldName + "' must not be empty";
			msgList.add(new Error(fieldName, msg));
		}
		if (!asList.contains(value)) {
			String msg = "The field '" + fieldName + "' (" + value + ") can have only one of  the following values "
					+ asList;
			msgList.add(new Error(fieldName, msg));
		}
	}

	/**
	 * Determines a the value of a system property or environment property
	 *
	 * @param name a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String getProperty(String name) {
		String str = System.getProperty(name);
		if (str == null) {
			str = System.getenv(name);
		}
		return str;
	}

	/**
	 * Determines a the value of a system property or environment property. If
	 * nothing is defined we use the default value
	 *
	 * @param name a {@link java.lang.String} object
	 * @param defaultValue a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String getProperty(String name, String defaultValue) {
		String str = System.getProperty(name);
		if (str == null || str.isEmpty()) {
			str = System.getenv(name);
		}
		return str == null ? defaultValue : str;
	}

	/**
	 * Determines a value from the properties. If it is not found we return the
	 * default value
	 *
	 * @param prop a {@link java.util.Map} object
	 * @param name a {@link java.lang.String} object
	 * @param defaultValue a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String getProperty(Map prop, String name, String defaultValue) {
		String str = (String) prop.get(name);
		return str == null || str.isEmpty() ? defaultValue : str;
	}

	/**
	 * Reads the content of the URL into a String
	 *
	 * @throws java.io.IOException
	 * @throws java.net.MalformedURLException
	 * @param url a {@link java.net.URL} object
	 * @return a {@link java.lang.String} object
	 */
	public static String urlToString(URL url) throws IOException, MalformedURLException {
		URLConnection con = url.openConnection();
		return inputStreamToString(con.getInputStream());
	}

	/**
	 * <p>inputStreamToString.</p>
	 *
	 * @param is a {@link java.io.InputStream} object
	 * @return a {@link java.lang.String} object
	 * @throws java.io.IOException if any.
	 * @throws java.net.MalformedURLException if any.
	 */
	public static String inputStreamToString(InputStream is) throws IOException, MalformedURLException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
		String barcodeContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
		return barcodeContent;
	}

	/**
	 * <p>getDateDelimiter.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a char
	 */
	public static char getDateDelimiter(String str) {
		for (char c : str.toCharArray()) {
			if (c != ' ' && !Character.isDigit(c)) {
				return c;
			}
		}
		return '.';
	}

	/**
	 * <p>parseDate.</p>
	 *
	 * @param dueDateString a {@link java.lang.String} object
	 * @return a {@link java.util.Date} object
	 * @throws java.text.ParseException if any.
	 */
	public static Date parseDate(String dueDateString) throws ParseException {
		Date dueDate = null;
		if (!StringUtils.isEmpty(dueDateString)) {
			try {
				char delim = StringUtils.getDateDelimiter(dueDateString);
				int pos = dueDateString.trim().indexOf(delim);
				DateFormat df;
				if (pos < 3) {
					df = new SimpleDateFormat("dd" + delim + "MM" + delim + "yyyy");
				} else {
					df = new SimpleDateFormat("yyyy" + delim + "MM" + delim + "dd");
				}
				dueDate = df.parse(dueDateString);
			} catch (Exception ex) {
				LOG.error(ex, ex);
			}
		}
		return dueDate;
	}

	/**
	 * <p>loadProperties.</p>
	 *
	 * @param resourceName a {@link java.lang.String} object
	 * @return a {@link java.util.Properties} object
	 * @throws java.io.IOException if any.
	 */
	public static Properties loadProperties(String resourceName) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		InputStream resourceStream = loader.getResourceAsStream(resourceName);
		InputStreamReader isr = new InputStreamReader(resourceStream, "UTF-8");
		props.load(isr);
		return props;
	}

	/**
	 * <p>loadResource.</p>
	 *
	 * @param cls a {@link java.lang.Class} object
	 * @param fileName a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String loadResource(Class cls, String fileName) {
		InputStream is = cls.getResourceAsStream(fileName.startsWith("/") ? fileName : "/" + fileName);
		Scanner s = new Scanner(is, "UTF8").useDelimiter("\\A");
		String content = s.hasNext() ? s.next() : "";
		return content;
	}

	/**
	 * Determines the file extension from a file name
	 *
	 * @param fileName a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String getFileExtension(String fileName) {
		String extension = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		return extension;
	}

	/**
	 * We determie the file format (file extension) from the indicated mime string
	 * E.g for image/jpeg we return jpeg
	 *
	 * @param accept a {@link java.lang.String} object
	 * @param mime a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String getFormatFromMime(String accept, String mime) {
		String result = mime;
		if (accept != null) {
			if (accept.startsWith("image/")) {
				result = accept.replace("image/", "");
			} else if (accept.startsWith("application/")) {
				result = accept.replace("application/", "");
			}
		}
		return result;
	}

	/**
	 * Returns the first line up to the first line separator
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String firstLine(String str) {
		int pos = str.indexOf(System.lineSeparator());
		String result = "";
		if (pos > 0) {
			result = str.substring(0, pos);
		}
		return result;
	}

	/**
	 * Compares two strings. null and "" are treated as equal
	 *
	 * @param s1 a {@link java.lang.String} object
	 * @param s2 a {@link java.lang.String} object
	 * @return a boolean
	 */
	public static boolean equals(String s1, String s2) {
		return str(s1).equals(str(s2));
	}

	/**
	 * Formats the string in groups
	 *
	 * @param groups a int
	 * @param str a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String formatInGroups(int groups, String str) {
		String str1 = str.replaceAll(" ", "");
		StringBuffer sb = new StringBuffer();
		String sa[] = split(str1, groups);
		sb.append(sa[0]);
		sb.append(" ");
		while (!sa[1].isEmpty()) {
			str1 = sa[1];
			sa = split(str1, groups);
			sb.append(sa[0]);
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	/**
	 * Formats a string into groups of n characters separated by space
	 *
	 * @param n a int
	 * @param str a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String formatInGroupsRight(int n, String str) {
		String result = StringUtils.reverse(str);
		result = StringUtils.formatInGroups(n, result);
		result = StringUtils.reverse(result);
		return result.trim();
	}

	/**
	 * Splits a string into a head and tail at the indicated position
	 *
	 * @param str a {@link java.lang.String} object
	 * @param pos a int
	 * @return an array of {@link java.lang.String} objects
	 */
	public static String[] split(String str, int pos) {
		String head = "";
		String tail = "";
		if (!str.isEmpty()) {
			int maxPos = Math.min(str.length(), pos);
			head = str.substring(0, maxPos);
			if (str.length() >= maxPos) {
				tail = str.substring(maxPos, str.length());
			}
		}
		String[] result = { head, tail };
		return result;
	}

	/**
	 * Reverses a String
	 *
	 * @param a a {@link java.lang.String} object
	 * @return a {@link java.lang.String} object
	 */
	public static String reverse(String a) {
		int j = a.length();
		char[] newWord = new char[j];
		for (int i = 0; i < a.length(); i++) {
			newWord[--j] = a.charAt(i);
		}
		return new String(newWord);
	}

	/**
	 * Lines which are too long are split into multiple printed lines
	 *
	 * @param in a {@link java.lang.String} object
	 * @param limit a int
	 * @param wrapLimit a int
	 * @return a {@link java.util.List} object
	 */
	public static List<String> splitLines(String in, int limit, int wrapLimit) {
		List<String> result = new ArrayList();
		String str = in;
		while (str.length() > 0) {
			String sa[] = split(str, limit);

			int spacePos = sa[0].lastIndexOf(' ');
			if (spacePos > 0 && spacePos>=wrapLimit) {
				String sa1[] = split(sa[0], spacePos);
				result.add(sa1[0].trim());
				str = (sa1[1] + sa[1]).trim();
			} else {
				result.add(sa[0].trim().trim());
				str = sa[1].trim();
			}
		}
		return result;
	}

}
