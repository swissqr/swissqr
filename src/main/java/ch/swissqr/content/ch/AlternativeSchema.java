package ch.swissqr.content.ch;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Alternative schemes Parameters and data of other supported schemes.Optional
 * data group with a variable number of elements
 *
 * @author pschatzmann
 */
public class AlternativeSchema implements IAlternativeSchema {
	private String title = "";
	private String content = "";

	/**
	 * <p>Constructor for AlternativeSchema.</p>
	 */
	public AlternativeSchema() {
	}

	/**
	 * <p>Constructor for AlternativeSchema.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 */
	public AlternativeSchema(String str) {
		int pos = getEndOfTitle(str);
		this.content = getContent(str, pos);
		this.title = getTitle(str, pos);
	}

	/**
	 * <p>Constructor for AlternativeSchema.</p>
	 *
	 * @param title a {@link java.lang.String} object
	 * @param content a {@link java.lang.String} object
	 */
	public AlternativeSchema(String title, String content) {
		this.title = title;
		this.content = content;
	}

	/**
	 * <p>Getter for the field <code>content</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getContent() {
		return content;
	}
		
	/**
	 * <p>Setter for the field <code>content</code>.</p>
	 *
	 * @param value a {@link java.lang.String} object
	 */
	public void setContent(String value) {
		this.content = value;
	}

	/**
	 * <p>Getter for the field <code>title</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * <p>Setter for the field <code>title</code>.</p>
	 *
	 * @param title a {@link java.lang.String} object
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * <p>content.</p>
	 *
	 * @param alternativeSchema a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.AlternativeSchema} object
	 */
	public AlternativeSchema content(String alternativeSchema) {
		this.content = alternativeSchema;
		return this;
	}

	/**
	 * <p>content.</p>
	 *
	 * @param title a {@link java.lang.String} object
	 * @param alternativeSchema a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.AlternativeSchema} object
	 */
	public AlternativeSchema content(String title, String alternativeSchema) {
		this.content = alternativeSchema;
		this.title = title;
		return this;
	}

	/**
	 * <p>title.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 * @return a {@link ch.swissqr.content.ch.AlternativeSchema} object
	 */
	public AlternativeSchema title(String str) {
		this.title = title;
		return this;
	}


	/** {@inheritDoc} */
	@Override
	public String toString() {
		return StringUtils.str(title) + StringUtils.str(content);
	}

	/**
	 * <p>check.</p>
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<Error> check() {
		List<Error> result = new ArrayList();
		StringUtils.check("alternativeSchema", false, 100, content, result);
		return result;
	}

	/**
	 * <p>isDefined.</p>
	 *
	 * @return a boolean
	 */
	@JsonIgnore
	public boolean isDefined() {
		return !content.isEmpty();
	}
	
    /**
     * <p>getEndOfTitle.</p>
     *
     * @param str a {@link java.lang.String} object
     * @return a int
     */
    protected int getEndOfTitle(String str) {
    	for (int j=0;j<str.length();j++) {
    		char c = str.charAt(j);
    		if (!Character.isLetterOrDigit(c))
    			return j;
    	}
    	return str.length()-1;
    }
    
    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @param str a {@link java.lang.String} object
     * @param pos a int
     * @return a {@link java.lang.String} object
     */
    protected String getTitle(String str, int pos) {
    	return str.substring(0, pos);
    }
    
    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @param str a {@link java.lang.String} object
     * @param pos a int
     * @return a {@link java.lang.String} object
     */
    protected String getContent(String str, int pos) {
    	return str.substring(pos, str.length());
    }

    
}
