package ch.swissqr.content.ch.formats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ContentBarcodeEU;
import ch.swissqr.content.ContentMail;
import ch.swissqr.content.ContentSMS;
import ch.swissqr.content.ContentString;
import ch.swissqr.content.ContentTel;
import ch.swissqr.content.ContentVCard;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.StringUtils;

/**
 * Serialization and Deserialization of Json
 *
 * @author pschatzmann
 */
public class JsonFormat implements IFormat  {
	private static final Logger LOG = Logger.getLogger(JsonFormat.class);
	private ObjectMapper mapper = null;

	/**
	 * <p>Constructor for JsonFormat.</p>
	 */
	public JsonFormat() {
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.swissqr.content.formats.IFormat#write(ch.swissqr.content.BarcodeContent)
	 */
	/** {@inheritDoc} */
	@Override
	@Test
	public String write(List<IContent> list) throws FormatException {
		// Object to JSON in String
		try {
			String jsonInString = mapper.writeValueAsString(list);
			return jsonInString;
		} catch (Exception e) {
			throw new FormatException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.swissqr.content.formats.IFormat#read(java.lang.String)
	 */
	/** {@inheritDoc} */
	@Override
	@Test
	public List<IContent> read(String jsonInString) throws FormatException {
		List<IContent> result= new ArrayList();
		try {
			List<Map> list = mapper.readValue(jsonInString, List.class);
			for (Map recordMap : list) {
				String typeString = (String) recordMap.get("contentType");
				// set default value
				if (StringUtils.isEmpty(typeString)) {
					throw new BarcodeException("The field 'contentType' must not be empty");
				}
				String recordJson = mapper.writeValueAsString(recordMap);
				if ("ContentBarcodeCH".equals(typeString)) {
					result.add(mapper.readValue(recordJson, ContentBarcodeCH.class));
				} else 	if ("ContentBarcodeEU".equals(typeString)) {
					result.add(mapper.readValue(recordJson, ContentBarcodeEU.class));
				} else 	if ("ContentTel".equals(typeString)) {
					result.add(mapper.readValue(recordJson, ContentTel.class));
				} else 	if ("ContentString".equals(typeString)) {
					result.add(mapper.readValue(recordJson, ContentString.class));
				} else 	if ("ContentSMS".equals(typeString)) {
					result.add(mapper.readValue(recordJson, ContentSMS.class));
				} else 	if ("ContentMail".equals(typeString)) {
					result.add(mapper.readValue(recordJson, ContentMail.class));
				} else 	if ("ContentVCard".equals(typeString))  {
					result.add(mapper.readValue(recordJson, ContentVCard.class));
				}
			}
			return result;
		} catch (Exception e) {
			throw new FormatException(e);
		}
	}


}
