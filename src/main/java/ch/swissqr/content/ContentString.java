package ch.swissqr.content;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.swissqr.utils.StringUtils;
import ch.swissqr.barcode.ErrorCorrectionLevel;


/**
 * Content which just exposes the content string
 *
 * @author pschatzmann
 */
public class ContentString extends ContentBase {
	
	/**
	 * <p>Constructor for ContentString.</p>
	 */
	public ContentString() {
		super();
	}
	
	/**
	 * <p>Constructor for ContentString.</p>
	 *
	 * @param content a {@link java.lang.String} object
	 */
	public ContentString(String content) {
		super(content);
	}
	
	/**
	 * <p>getContentValue.</p>
	 *
	 * @return a {@link java.lang.String} object
	 * @throws java.io.UnsupportedEncodingException if any.
	 */
	public String getContentValue() throws UnsupportedEncodingException {
		return super.getContent();
	}
	
	/**
	 * <p>setContentValue.</p>
	 *
	 * @param str a {@link java.lang.String} object
	 */
	public void setContentValue(String str) {
		this.setContent(str);
	}
	
	/** {@inheritDoc} */
	@Override
	public void setDataMap(Map<String, Object> record)  {
		super.setDataMap(record);
		this.parse(StringUtils.str(record.get("contentValue")));
	}

	/** {@inheritDoc} */
	@JsonIgnore
	@Override
	public Map<String, Object> getDataMap() {
		try {
			Map result = new HashMap();
			result.put("contentValue", this.getContent());
			result.put("contentType", this.getContentType());
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
