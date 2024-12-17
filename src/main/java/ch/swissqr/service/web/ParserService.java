package ch.swissqr.service.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;


import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.content.AllBarcodeTypes;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.errors.LicenceError;
import ch.swissqr.errors.UsageViolationException;
import ch.swissqr.pdf.Document;
import ch.swissqr.service.web.usage.UsageValidator;

/**
 * Parsing of barcode files: We support zip files, pdf files and gif, jpeg and
 * png image files
 *
 * @author pschatzmann
 */

@Path("/service/parser")
public class ParserService {
	private static Logger LOG = Logger.getLogger(ParserService.class);
	private @Context HttpHeaders headers;

	/**
	 * Returns the raw text content of one or multiple image, pdf and zip files.
	 *
	 * @throws java.io.IOException
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws ch.swissqr.errors.LicenceError
	 * @param body a {@link org.glassfish.jersey.media.multipart.FormDataMultiPart} object
	 * @return a {@link java.util.List} object
	 */

	@POST
	@Path("/text")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> imagesToString(FormDataMultiPart body)
			throws IOException, BarcodeException, LicenceError {

		LOG.info("imageToText");
		if (body==null) {
			throw new BarcodeException("The form body was empty: we expected one or multiple files!");
		}
		
		UsageValidator.checkEx(headers, null, body.getBodyParts().size());

		List<String> barcodes = new ArrayList();
		for (StreamInfo si : StreamInfo.getStreams(body)) {
			if (si.extension.equals("zip")) {
				ZipInputStream zs = new ZipInputStream(si.inputStream);
				ZipEntry entry = null;
				while ((entry = zs.getNextEntry()) != null) {
					processInputStreamToString(barcodes, si, zs);
				}

			} else {
				processInputStreamToString(barcodes, si, si.inputStream);
			}
		}
		return barcodes;
	}

	private void processInputStreamToString(List<String> strings, StreamInfo si, InputStream is) throws BarcodeException, InvalidPasswordException {
		try {
			QRBarcode bc = new QRBarcode();
			String contentString = "";
			if (si.extension.matches("png|jpg|jpeg|gif")) {
				contentString = bc.readImage(is);
				contentString = URLDecoder.decode(contentString, "UTF8");
			} else if (si.extension.equals("pdf")) {
				// process all barcodes in a PDF
				for (String content : new Document(is).getBarcodeStrings()) {
					strings.add(contentString);
				}
			}
			strings.add(contentString);
		}catch(Exception ex) {
			throw new BarcodeException(ex);
		}
	}

	/**
	 * Returns the content objects of one or multiple image, pdf and zip files.
	 *
	 * @throws java.lang.Exception
	 * @param body a {@link org.glassfish.jersey.media.multipart.FormDataMultiPart} object
	 * @return a {@link java.util.List} object
	 */

	@POST
	@Path("/objects")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public List<IContent> imageToObjects(FormDataMultiPart body)
	throws Exception {
		LOG.info("imageToObjects");

		if (body==null) {
			throw new BarcodeException("The form body was empty: we expected one or multiple files!");
		}
		UsageValidator.checkEx(headers,null, body.getBodyParts().size());

		List<IContent> resultList = new ArrayList();
		for (StreamInfo si : StreamInfo.getStreams(body)) {
			String fileName = si.fileName;
			InputStream is = si.inputStream;
			if (si.extension.equals("zip")) {
				ZipInputStream zs = new ZipInputStream(is);
				ZipEntry entry = null;
				while ((entry = zs.getNextEntry()) != null) {
					fileName = entry.getName();
					processInputStreamToList(resultList, si, zs, fileName);
				}

			} else {
				processInputStreamToList(resultList, si, is, fileName);
			}
		}
		LOG.info(resultList);
		return resultList;
	}

	private void processInputStreamToList(List<IContent> list, StreamInfo si, InputStream is, String fileName)
			throws InstantiationException, IllegalAccessException, InvalidPasswordException, IOException,
			ch.swissqr.content.ch.formats.FormatException, BarcodeException {
		QRBarcode bc = new QRBarcode();
		String contentString = "";
		String extension = si.extension;
		if (extension.matches("png|jpg|jpeg|gif")) {
			contentString = bc.readImage(is);
			contentString = URLDecoder.decode(contentString, "UTF8");
			IContent content = AllBarcodeTypes.getIContent(contentString);
			content.getProperties().setProperty("fileName", fileName);
			list.add(content);
		} else if (extension.equals("pdf")) {
			// process all barcodes in a PDF
			for (IContent content : new Document(is).getBarcodeContent()) {
				content.getProperties().setProperty("fileName", fileName);
				list.add(content);
			}
		}
	}
	
}
