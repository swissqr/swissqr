package ch.swissqr.content;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.swissqr.utils.StringUtils;
import ch.swissqr.barcode.ErrorCorrectionLevel;


/**
 * Content which just exposes the content string
 * @author pschatzmann
 *
 */
public class ContentString extends ContentBase {
	
	public ContentString() {
		super();
	}
	
	public ContentString(String content) {
		super(content);
	}
	
	public String getContentValue() throws UnsupportedEncodingException {
		return super.getContent();
	}
	
	public void setContentValue(String str) {
		this.setContent(str);
	}
	
	@Override
	public void setDataMap(Map<String, Object> record)  {
		super.setDataMap(record);
		this.parse(StringUtils.str(record.get("contentValue")));
	}

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
