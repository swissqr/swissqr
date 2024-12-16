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
 *
 */
public class AlternativeSchema implements IAlternativeSchema {
	private String title = "";
	private String content = "";

	public AlternativeSchema() {
	}

	public AlternativeSchema(String str) {
		int pos = getEndOfTitle(str);
		this.content = getContent(str, pos);
		this.title = getTitle(str, pos);
	}

	public AlternativeSchema(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getContent() {
		return content;
	}
		
	public void setContent(String value) {
		this.content = value;
	}

	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public AlternativeSchema content(String alternativeSchema) {
		this.content = alternativeSchema;
		return this;
	}

	public AlternativeSchema content(String title, String alternativeSchema) {
		this.content = alternativeSchema;
		this.title = title;
		return this;
	}

	public AlternativeSchema title(String str) {
		this.title = title;
		return this;
	}


	@Override
	public String toString() {
		return StringUtils.str(title) + StringUtils.str(content);
	}

	public List<Error> check() {
		List<Error> result = new ArrayList();
		StringUtils.check("alternativeSchema", false, 100, content, result);
		return result;
	}

	@JsonIgnore
	public boolean isDefined() {
		return !content.isEmpty();
	}
	
    protected int getEndOfTitle(String str) {
    	for (int j=0;j<str.length();j++) {
    		char c = str.charAt(j);
    		if (!Character.isLetterOrDigit(c))
    			return j;
    	}
    	return str.length()-1;
    }
    
    protected String getTitle(String str, int pos) {
    	return str.substring(0, pos);
    }
    
    protected String getContent(String str, int pos) {
    	return str.substring(pos, str.length());
    }

    
}
