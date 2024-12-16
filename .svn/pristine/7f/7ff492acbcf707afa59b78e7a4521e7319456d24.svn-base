package ch.swissqr.service.web;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import ch.swissqr.utils.StringUtils;

/**
 * For streamlined multi file upload we return a collection of the objects which
 * are necessary
 * 
 * @author pschatzmann
 *
 */
public class StreamInfo {
	InputStream inputStream;
	String fileName;
	String extension;

	StreamInfo(InputStream inputStream, String fileName) {
		this.fileName = fileName;
		this.inputStream = inputStream;
		this.extension = StringUtils.getFileExtension(fileName);
	}

	public static List<StreamInfo> getStreams(FormDataMultiPart multiPart) {
		List<FormDataBodyPart> bodyParts = multiPart.getFields("file");
		List<StreamInfo> result = new ArrayList();
		/* Save multiple files */
		for (int i = 0; i < bodyParts.size(); i++) {
			BodyPartEntity bodyPartEntity = (BodyPartEntity) bodyParts.get(i).getEntity();
			String fileName = bodyParts.get(i).getContentDisposition().getFileName();
			result.add(new StreamInfo(bodyPartEntity.getInputStream(), fileName));
		}

		return result;
	}

}