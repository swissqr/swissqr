package ch.swissqr.service.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.core.StreamingOutput;

import org.apache.log4j.Logger;

import ch.swissqr.barcode.ErrorCorrectionLevel;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ContentBarcodeEU;
import ch.swissqr.content.ContentString;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.paymentslip.PaymentSlipPDF;
import ch.swissqr.paymentslip.PaymentSlipPDF.Format;
import ch.swissqr.utils.Error;
import ch.swissqr.utils.StringUtils;

/**
 * Top Level Java API for the writing pictures and pdfs to an output stream.
 * Multiple objects are returned as zip output stream.
 *
 * @author pschatzmann
 */
public class CommonServicesAPI {
	private static Logger LOG = Logger.getLogger(CommonServicesAPI.class);
	/** Constant <code>TEST_IBAN="CH4431999123000889012"</code> */
	public static String TEST_IBAN = "CH4431999123000889012";
	/**
	 * Converts a collection of strings into a stream of QR barcode images
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param result a {@link java.io.OutputStream} object
	 * @param input a {@link java.util.Collection} object
	 */
	public static void getBarcodes(OutputStream result, Collection<String> input)
			throws BarcodeException, IOException {
		Collection<IContent> content = new ArrayList();
		for (String str : input) {
			content.add(new ContentString(str));
		}
		getContent(result, content);
	}

	/**
	 * Converts a collection of BarcodeContentCH into a stream of barcodes
	 * images
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param result a {@link java.io.OutputStream} object
	 * @param input a {@link java.util.Collection} object
	 * @param pictureFormat a {@link java.lang.String} object
	 * @param zip a {@link java.lang.Boolean} object
	 */
	@SuppressWarnings("unchecked")
	public static void getBarcodes(OutputStream result, Collection<IContent> input, String pictureFormat,
			Boolean zip) throws BarcodeException, IOException {
		getContent(result, input);
	}

	/**
	 * Converts a collection of quick response code objects into a stream of QR
	 * barcodes
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param result a {@link java.io.OutputStream} object
	 * @param input a {@link java.util.Collection} object
	 * @param pictureFormat a {@link java.lang.String} object
	 * @param zip a {@link java.lang.Boolean} object
	 */

	@SuppressWarnings("unchecked")
	protected static void getQuickResponseCodes(OutputStream result, Collection<ContentBarcodeEU> input,
			String pictureFormat, Boolean zip) throws BarcodeException, IOException {
		@SuppressWarnings("rawtypes")
		Collection content = input;
		getContent(result, content);
	}

	
	/**
	 * <p>getContent.</p>
	 *
	 * @param result a {@link java.io.OutputStream} object
	 * @param input a {@link java.util.Collection} object
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	protected static void getContent(OutputStream result, Collection<IContent> input)
			throws BarcodeException, IOException {

		if (input.isEmpty()) {
			throw new BarcodeException("The barcode content must not be empty");
		}

		ZipOutputStream out = new ZipOutputStream(result);
		int count = 1;
		for (IContent code : input) {
			String fileName = StringUtils.getProperty(code.getProperties(), "filename", ""+count);			
			String pictureFormat = StringUtils.getProperty(code.getProperties(), "pictureFormat","png");
			fileName = fileName + "."+pictureFormat;

			ZipEntry e = new ZipEntry(fileName);
			out.putNextEntry(e);
			
			String mm = StringUtils.getProperty(code.getProperties(), "dimension", "46");			
			ErrorCorrectionLevel ec = ErrorCorrectionLevel.valueOf(StringUtils.getProperty(code.getProperties(), "errorCorrection", "M"));

			BufferedImage image = code.toBarcode(pictureFormat, Double.valueOf(mm), ec);
			ImageIO.write(image, pictureFormat, out);
			out.flush();
			out.closeEntry();
			count++;
		}
		out.flush();
		out.close();

	}

	/**
	 * Checks the completeness and correctness of the content for barcode objects
	 *
	 * @param inputCollection a {@link java.util.Collection} object
	 * @return a {@link java.util.List} object
	 */
	protected static List<Error> check(Collection<IContent> inputCollection) {
		List<Error> result = new ArrayList();
		int count = 0;
		for (IContent document : inputCollection) {
			String fileName = ++count + "";
			List<Error> messages = document.check();
			for (Error resultEntry : messages) {
				resultEntry.setFileName(fileName);
				result.add(resultEntry);
			}
		}
		return result;
	}

	/**
	 * Writes a collection of BarcodeContentCH as payment slip documents to the
	 * output stream
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param result a {@link java.io.OutputStream} object
	 * @param input a {@link java.util.Collection} object
	 */
	protected static void getPaymentSlips(OutputStream result, Collection<ContentBarcodeCH> input)
			throws BarcodeException, IOException {


		if (input.isEmpty()) {
			throw new BarcodeException("The barcode content must not be empty");
		}

		ZipOutputStream out = new ZipOutputStream(result);
		int count = 1;
		for (ContentBarcodeCH code : input) {
			String pictureFormat = StringUtils.getProperty(code.getProperties(), "pictureFormat","pdf");
			String language = StringUtils.getProperty(code.getProperties(), "language", "de");
			String fileName = StringUtils.getProperty(code.getProperties(), "filename", count+"");
			fileName = fileName+"."+pictureFormat;
			String strFormat = StringUtils.getProperty(code.getProperties(), "pageFormat", "A4");
			boolean printLines = "true".equals(StringUtils.getProperty(code.getProperties(), "printLines", "true"));
			boolean printReceip = !"false".equals(StringUtils.getProperty(code.getProperties(), "printReceipt", "true"));
			Format paperFormat = Format.valueOf(strFormat);
			PaymentSlipPDF slip = new PaymentSlipPDF(code, language, paperFormat, printLines,printReceip);

			ZipEntry e = new ZipEntry(fileName);
			out.putNextEntry(e);

			byte[] data = slip.getBytes(pictureFormat);
			out.write(data, 0, data.length);
			out.closeEntry();
			count++;
		}
		out.flush();
		out.close();

	}

	/**
	 * <p>getBarcodeStreamingOutput.</p>
	 *
	 * @param barcodeList a {@link java.util.List} object
	 * @return a {@link javax.ws.rs.core.StreamingOutput} object
	 */
	public static StreamingOutput getBarcodeStreamingOutput(List<IContent> barcodeList) {
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException {
				try {
					CommonServicesAPI.getBarcodes(os, barcodeList, null, null);
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		};
		return stream;
	}

	/**
	 * <p>getPaymentSlipStreamingOutput.</p>
	 *
	 * @param barcodeList a {@link java.util.List} object
	 * @return a {@link javax.ws.rs.core.StreamingOutput} object
	 */
	public static StreamingOutput getPaymentSlipStreamingOutput( List<ContentBarcodeCH> barcodeList) {
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException {
				try {
					CommonServicesAPI.getPaymentSlips(os, barcodeList);
				} catch (Exception e) {
					throw new IOException(e);
				}
			}
		};
		return stream;
	}

}
