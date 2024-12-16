package ch.swissqr.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.rendering.PDFRenderer;

import ch.swissqr.barcode.QRBarcode;
import ch.swissqr.content.AllBarcodeTypes;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.IContent;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.pdf.parsing.PDFContent;
import ch.swissqr.pdf.parsing.Text;
import ch.swissqr.utils.StringUtils;

/**
 * Common functionality for all PDF documents
 * 
 * @author pschatzmann
 *
 */
public class DocumentBase {
	private static final Logger LOG = Logger.getLogger(DocumentBase.class);
	private PDDocument document;
	private List<String> errors = new ArrayList();

	/**
	 * Creates an empty PDF document
	 */
	public DocumentBase() {
		document = new PDDocument();
	}

	/**
	 * Opens the document from the indicated pdf file
	 * @param file
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public DocumentBase(File file) throws InvalidPasswordException, IOException {
		document = PDDocument.load(file);
	}

	/**
	 * Opens a document from the input stream which contains the PDF document
	 * 
	 * @param is
	 * @throws InvalidPasswordException
	 * @throws IOException
	 */
	public DocumentBase(InputStream is) throws InvalidPasswordException, IOException {
		document = PDDocument.load(is);
	}

	/**
	 * Returns the PDF document
	 * @return
	 */
	public PDDocument getDocument() {
		return this.document;
	}

	/**
	 * Defines the PDF document
	 * @param doc
	 * @throws BarcodeException
	 */
	public void setDocument(PDDocument doc) throws BarcodeException {
		if (document!=null) {
			try {
				document.close();
			} catch (IOException e) {
				throw new BarcodeException(e);
			}
		}
		this.document = doc;
	}


	/**
	 * Determines the string content of all QR barcodes
	 * @return
	 * @throws IOException
	 */
	public List<String> getBarcodeStrings() throws IOException {
		errors.clear();
		List<String> result = new ArrayList();
		QRBarcode bc = new QRBarcode();
		for (PDImage img : getImages()) {
			BufferedImage bi = img.getImage();
			try {
				String s = bc.readImage(bi);
				if (!StringUtils.isEmpty(s)) {
					result.add(s);
				}
			} catch (Exception ex) {
				LOG.error(ex,ex);
				//errors.add(ex.getMessage());
			}
		}
		return result;
	}
	
	public List<String> getErrors()  {
		return this.errors;
	}

	

	/**
	 * Returns all images from the pdf document
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<PDImage> getImages() throws IOException {
		List<PDImage> result = new ArrayList();
		Iterator<PDPage> iter = this.getDocument().getDocumentCatalog().getPages().iterator();
		while (iter.hasNext()) {
			PDPage page = iter.next();
			PDResources resources = page.getResources();
			for (COSName cosName : resources.getXObjectNames()) {
				PDXObject xobject = resources.getXObject(cosName);
				if (xobject instanceof PDImage) {
					result.add((PDImage) xobject);
				}
			}
		}
		return result;
	}

	/**
	 * Retrns the content of the Swiss QR Barcodes
	 * 
	 * @return
	 * @throws IOException
	 * @throws ch.swissqr.content.ch.formats.FormatException
	 * @throws BarcodeException
	 */
	public List<ContentBarcodeCH> getSwissBarcodeContent()
			throws IOException, ch.swissqr.content.ch.formats.FormatException, BarcodeException {
		List<ContentBarcodeCH> result = new ArrayList();
		for (String str : getBarcodeStrings()) {
			ContentBarcodeCH c = new ContentBarcodeCH(str);
			if (str.startsWith("SPC")) {
				result.add(c);
			}
		}
		return result;
	}
	
	/**
	 * Returns the content of all QR Barcodes
	 * @return
	 * @throws IOException
	 * @throws ch.swissqr.content.ch.formats.FormatException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BarcodeException 
	 */
	public List<IContent> getBarcodeContent()
			throws IOException, ch.swissqr.content.ch.formats.FormatException, InstantiationException, IllegalAccessException, BarcodeException {
		return AllBarcodeTypes.getIContentList(getBarcodeStrings());
	}

	/**
	 * Saves the current PDF document to a file
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		document.save(file);
	}

	/**
	 * Saves the current PDF document to a output stream
	 * 
	 * @param os
	 * @throws IOException
	 */
	public void write(OutputStream os) throws IOException {
		document.save(os);
	}

	/**
	 * Writes the image to the indicated output stream
	 * 
	 * @param format
	 * @param outputStream
	 * @throws IOException
	 */
	public void write(OutputStream outputStream, String format) throws IOException {
		PDFRenderer pdfRenderer = new PDFRenderer(this.getDocument());
		BufferedImage bi = pdfRenderer.renderImage(0, 10.0f);
		ImageIO.write(bi, format, outputStream);
	}
	
	/**
	 * Returns the picture or pdf as byte array
	 * @param pictureFormat
	 * @return
	 * @throws IOException
	 */
	public byte[] getBytes(String pictureFormat) throws IOException {
		byte[] result;
		if (StringUtils.isEmpty(pictureFormat) || pictureFormat.equalsIgnoreCase("pdf")) {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			write(bo);
			result = bo.toByteArray();
		} else {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			write(bo, pictureFormat);
			result = bo.toByteArray();
		}
		return result;
	}
	

	/**
	 * Parse the pdf document and returns the text with the corrsponding positions
	 * @return
	 * @throws IOException 
	 */
	public List<Text> getTextContent() throws IOException {
		PDFContent content = new PDFContent();
		return content.getContent(this.getDocument()); 
	}
		
	/**
	 * Parse the pdf document and returns the content as string
	 * @return
	 * @throws IOException 
	 */
	public String getText() throws IOException {
		PDFContent content = new PDFContent();
		return content.getText(this.getDocument());
	}

	/**
	 * Add the doc at the end of the current document
	 * 
	 * @param doc
	 * @throws IOException
	 */
	public void addPDF(PDDocument doc) throws IOException {
		PDFMergerUtility util = new PDFMergerUtility();
		util.appendDocument(this.getDocument(), doc);
	}

	/**
	 * Releases the pdf document
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.document.close();
	}

}
