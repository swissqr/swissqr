package ch.swissqr.pdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.paymentslip.PaymentSlipPDF;
import ch.swissqr.paymentslip.PaymentSlipPDF.Format;
import ch.swissqr.pdf.parsing.PDFContent;
import ch.swissqr.pdf.parsing.Text;

/**
 * A Single PDF document which supports the creation of a payment slip at the
 * end of the document.
 *
 * @author pschatzmann
 */
public class Document extends DocumentBase {

	/**
	 * <p>Constructor for Document.</p>
	 */
	public Document() {
		super();
	}

	/**
	 * <p>Constructor for Document.</p>
	 *
	 * @param file a {@link java.io.File} object
	 * @throws org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException if any.
	 * @throws java.io.IOException if any.
	 */
	public Document(File file) throws InvalidPasswordException, IOException {
		super(file);
	}

	/**
	 * <p>Constructor for Document.</p>
	 *
	 * @param is a {@link java.io.InputStream} object
	 * @throws org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException if any.
	 * @throws java.io.IOException if any.
	 */
	public Document(InputStream is) throws InvalidPasswordException, IOException {
		super(is);
	}

	/**
	 * Adds a payment slit at the end of the document
	 *
	 * @throws java.lang.Exception
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @param language a {@link java.lang.String} object
	 */
	public void addPaymentSlip(ContentBarcodeCH content, String language) throws Exception {
		this.addPDF(new PaymentSlipPDF(content, language, PaymentSlipPDF.Format.A4, true,true).getDocument());
	}

}
