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
 *
 */

public class Document extends DocumentBase {

	public Document() {
		super();
	}

	public Document(File file) throws InvalidPasswordException, IOException {
		super(file);
	}

	public Document(InputStream is) throws InvalidPasswordException, IOException {
		super(is);
	}

	/**
	 * Adds a payment slit at the end of the document
	 * 
	 * @param content
	 * @throws Exception
	 */
	public void addPaymentSlip(ContentBarcodeCH content, String language) throws Exception {
		this.addPDF(new PaymentSlipPDF(content, language, PaymentSlipPDF.Format.A4, true,true).getDocument());
	}

}
