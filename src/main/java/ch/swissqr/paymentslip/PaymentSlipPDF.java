package ch.swissqr.paymentslip;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import ch.swissqr.barcode.QRSwissBarcode;
import ch.swissqr.content.ContentBarcodeCH;
import ch.swissqr.content.ch.Address;
import ch.swissqr.content.ch.IAlternativeSchema;
import ch.swissqr.errors.BarcodeException;
import ch.swissqr.pdf.DocumentBase;
import ch.swissqr.utils.StringUtils;

/**
 * Payment Slip DPF document
 *
 * @author pschatzmann
 */
public class PaymentSlipPDF extends DocumentBase {
	public enum Format {
		A4, A5, A6, Others
	}

	private static final Logger LOG = Logger.getLogger(PaymentSlipPDF.class);
	private static final float TOP = 95; 
	private static final float LINEHEIGHT = 3.88f; // 3.53f;
	private static final float SECTIONMARGIN = 5.0f;
	/** Constant <code>POINT_TO_MM=0.352778f</code> */
	public static final float POINT_TO_MM = 0.352778f;
    private static final float POINTS_PER_INCH = 72;
	private static final float USER_UNIT = 2.83441891578f; // mm to point
	private int leftX;
	private int rightX;
	private DateFormat dateFormat;
	private PDPageContentStream contentStream;
	private PDFont fontBold;
	private PDFont font;
	private float currentTop = TOP;
	private boolean printLines = true;
	private PDPage blankPage;
	private boolean autoClose = true;
	private Properties messages;
	private boolean printReceipt;
	private float cutoffLimit = 0;
	private float cutoffLimitInformation = 9.0f - 0.2f;
	private float cutoffLimitButtom = 19.0f; // changed from 13.0f
	private float cutoffLimitReceipt = 5.3f + 1.8f;

	
	/**
	 * <p>Constructor for PaymentSlipPDF.</p>
	 */
	public PaymentSlipPDF() {	
	}
	
	/**
	 * Default constructor
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @param requestedLangauge a {@link java.lang.String} object
	 * @param format a {@link ch.swissqr.paymentslip.PaymentSlipPDF.Format} object
	 * @param printLines a boolean
	 * @param printReceipt a boolean
	 */
	public PaymentSlipPDF(ContentBarcodeCH content, String requestedLangauge, Format format, boolean printLines, boolean printReceipt)
			throws BarcodeException, IOException {
		print(content, requestedLangauge, format, printLines, printReceipt);
	}
	
	/**
	 * Constructor for creating an unformatted payment slip with just prints the relevant content
	 * w/o left margin and cut off line printing
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @param requestedLangauge a {@link java.lang.String} object
	 */
	public PaymentSlipPDF(ContentBarcodeCH content, String requestedLangauge) throws BarcodeException, IOException {
		this(content,requestedLangauge, Format.A4,true, true);
	}

	/**
	 * Constructor for existing PDDocument
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @param document a {@link org.apache.pdfbox.pdmodel.PDDocument} object
	 */
	public PaymentSlipPDF(PDDocument document) throws BarcodeException {
		dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
		setDocument(document);
	}

	/**
	 * Prints the payment slip
	 *
	 * @throws ch.swissqr.errors.BarcodeException
	 * @throws java.io.IOException
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @param requestedLangauge a {@link java.lang.String} object
	 * @param format a {@link ch.swissqr.paymentslip.PaymentSlipPDF.Format} object
	 * @param printLines a boolean
	 * @param printReceipt a boolean
	 * @return a {@link org.apache.pdfbox.pdmodel.PDPageContentStream} object
	 */
	public PDPageContentStream print(ContentBarcodeCH content, String requestedLangauge, Format format, boolean printLines, boolean printReceipt)
			throws BarcodeException, IOException {

		setup(format, content, requestedLangauge, printLines, printReceipt);

		createPDFDocument();
		printPerformationLines(getDocument());
		createPaymentSlip(content);
		createReceipt(content, requestedLangauge, format);

		if (autoClose) {
			contentStream.close();
		}
		return this.contentStream;
	}

	private void setup(Format format, ContentBarcodeCH content, String requestedLangauge,boolean printLines, boolean printReceipt) throws IOException {
		String language = getLangauge(content, requestedLangauge);
		Locale.setDefault(new Locale(language, content.getDebitor().getCountryISO()));
		dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
		this.messages = getProperties(language);
		
		setupPageFormat(format, printLines, printReceipt);
	}
	

	private String getLangauge(ContentBarcodeCH content, String lang) {
		String language = lang;
		if (lang==null) {
			language = content.getProperties().getProperty("language");
		}
		if (language == null) {
			language = "en";
		}
		language =  language.trim().toLowerCase();
		if (!language.matches("de|en|fr|it")) {
			LOG.info("Setting default language to english because of unsupported code: " + lang);
			language = "en";
		}
		return language;
	}

	private void setupPageFormat(Format format, boolean printLines, boolean printReceipts) {
		this.printLines = printLines;
		this.printReceipt = printReceipts;
		//float userUnit = 28.34646f; // 28.34008f
		
		switch (format) {
		case A4:
			blankPage = new PDPage(PDRectangle.A4);
			blankPage.setCropBox(new PDRectangle(210f*USER_UNIT, 297f*USER_UNIT));
			leftX = 65;
			rightX = 120; 
			break;
		case A5:
			blankPage = new PDPage(new PDRectangle(210f*USER_UNIT, 148f*USER_UNIT));
			blankPage.setCropBox(new PDRectangle(210f*USER_UNIT, 148.5f*USER_UNIT));
			leftX = 65;
			rightX = 120; 
			break;
		case A6:
			// support for others and A6
			this.printLines = false;
			this.printReceipt = false;
			leftX = 5;
			rightX = 60;
			blankPage = new PDPage();
			blankPage.setCropBox(new PDRectangle(148.0f*USER_UNIT, 105.0f*USER_UNIT));
			break;
		default:
			// support for others and A6
			leftX = 65;
			rightX = 120; 
			blankPage = new PDPage();
			blankPage.setCropBox(new PDRectangle(210f*USER_UNIT, 148f*USER_UNIT));
			break;
		}

	}

	/**
	 * Creates a payment slip PDF document
	 * 
	 * @throws Exception
	 */
	private PDDocument createPaymentSlip(ContentBarcodeCH content) throws BarcodeException {
		try {
			PDDocument document = this.getDocument();
			printBarcode(content, document);

			printPaymentSlipCol1(content, messages, document);
			printPaymentSlipCol2(content, messages);
			printPaymentSlipButtom(content, messages);
			
			return document;
		} catch (Exception ex) {
			throw new BarcodeException(ex);
		}
	}

	/**
	 * <p>createPDFDocument.</p>
	 *
	 * @return a {@link org.apache.pdfbox.pdmodel.PDDocument} object
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	public PDDocument createPDFDocument() throws BarcodeException, IOException {
		PDDocument document = new PDDocument();
		setDocument(document);
		document.addPage(blankPage);

		fontBold = PDType1Font.HELVETICA_BOLD;
		font = PDType1Font.HELVETICA;

		contentStream = new PDPageContentStream(document, blankPage);
		return document;
	}


	private void printPerformationLines(PDDocument document) throws IOException {
		if (this.printLines) {
			// print scissors
			contentStream.drawImage(getImageFromResource(document, "/icons/scissors.png", "Scissors"),
					2*USER_UNIT, 106*USER_UNIT, 12*USER_UNIT, 12*USER_UNIT);
			
			// print lines
			float x = 59f;
			contentStream.setLineDashPattern(new float[] { 1*USER_UNIT, 3*USER_UNIT }, 0);
			contentStream.setLineWidth(1f);

			contentStream.setStrokingColor(Color.GRAY);
			contentStream.moveTo(0f*USER_UNIT, 105f*USER_UNIT);
			contentStream.lineTo(210f*USER_UNIT, 105f*USER_UNIT);
			contentStream.stroke();

			contentStream.moveTo(x*USER_UNIT, 0);
			contentStream.lineTo(x*USER_UNIT, 105f*USER_UNIT);
			contentStream.stroke();
		}
	}

	private void printPaymentSlipCol1 (ContentBarcodeCH content, Properties messages, PDDocument document) throws IOException {
		currentTop = TOP;
	    this.setCutoffLimit(cutoffLimitReceipt);
		printTitle(messages.getProperty("T_TITLE"), leftX, Math.round(currentTop), 11);
		printAmount(content, messages, document, leftX, 32, 1.05f, 1.05f, 8, 14,  8f, 10f);
	}

	private void printPaymentSlipCol2(ContentBarcodeCH content, Properties messages) throws IOException {
		currentTop = TOP;
		contentStream.beginText();

	    this.setCutoffLimit(cutoffLimitInformation);
		
		contentStream.newLineAtOffset(rightX*USER_UNIT, currentTop*USER_UNIT);
		print(messages.getProperty("T_ACCOUNT"), getAccountInfo(content), LINEHEIGHT,8.0f,10.0f);
		//print(null,content.getCreditorInformation().getCreditorAddress().getAddressLines(), LINEHEIGHT, 8.0f,10.0f);
		print(messages.getProperty("T_REF"), content.getPaymentReference().getReferenceFormatted(), LINEHEIGHT);
		print(messages.getProperty("T_INFO"), getAdditionalInfoData(content), LINEHEIGHT,8.0f,10.0f);

		float yBox = printDebitor(content, messages,LINEHEIGHT, 8.0f,10.0f);
		contentStream.endText();
		
		printEmptyAddressBox(this.getDocument(), yBox-2, rightX, 1.05f);
		
	}

	/**
	 * <p>getAccountInfo.</p>
	 *
	 * @param content a {@link ch.swissqr.content.ContentBarcodeCH} object
	 * @return a {@link java.util.List} object
	 */
	public List<String> getAccountInfo(ContentBarcodeCH content) {
		List<String> accountLines = new ArrayList();
		accountLines.add(StringUtils.formatInGroups(4,content.getCreditorInformation().getIban()));
		accountLines.addAll(Arrays.asList(content.getCreditorInformation().getCreditorAddress().getAddressLines()));
		return accountLines;
	}

	private List<String> getAdditionalInfoData(ContentBarcodeCH content) {
		List<String> info = new ArrayList();
		int maxWordLen = 10;
		info.addAll(splitLines(content.getPaymentReference().getUnstructuredMessage(),getCutoffLimit(),maxWordLen));
		Object billingInfo = content.getPaymentReference().getBillInformation();
		if (billingInfo!=null) {
			info.addAll(splitLines(billingInfo.toString(),getCutoffLimit(),maxWordLen));
		}
		return info;
	}
		

	private float printDebitor(ContentBarcodeCH content, Properties messages, float spacing, float titleFontSize, float textFontSize ) throws IOException {
		float yBox = -10000.0f;
		Address debitor = content.getDebitor();
		if (debitor != null && debitor.isDefined()) {
			print(messages.getProperty("T_DEB"), Arrays.asList(debitor.getAddressLines()), spacing,titleFontSize, textFontSize);
		} else {
			printTitle(messages.getProperty("T_DEB")+" "+messages.getProperty("T_DEB1"));
			yBox = currentTop;
			contentStream.newLineAtOffset(0, -25);
			currentTop = currentTop - 30;
		}
		return yBox;
	}

	private void printAmount(ContentBarcodeCH content, Properties messages, PDDocument document, int leftX, int yPos, float scaleX, float scaleY, int boxXOffset, int boxYPos, float sizeTitle, float sizeText) throws IOException {
		print(messages.getProperty("T_CURRENCY"), content.getPaymentAmount().getCurrency(), leftX, yPos, sizeTitle, sizeText);

		String amount = content.getPaymentAmount().getAmountPrinted();
		if (!StringUtils.isEmpty(amount)) {
			print(messages.getProperty("T_AMOUNT"), amount,
					leftX + 20, yPos, sizeTitle, sizeText);  
		} else {
			// print title and amount box
			printTitle(messages.getProperty("T_AMOUNT"), leftX + 24, yPos, sizeTitle);  //20 -> 28
			contentStream.drawImage(getImageFromResource(document, "/feld/Feld_Betrag_40x15mm.png", "Amount"),
					(leftX + boxXOffset)*USER_UNIT, boxYPos*USER_UNIT, 40*scaleX*USER_UNIT, 15*scaleY*USER_UNIT); // 4 -> 12
		}
	}
	
	/**
	 * Creates the Further information section
	 * @throws IOException
	 */
	private void printPaymentSlipButtom(ContentBarcodeCH content, Properties messages) throws IOException {
		int y = 8;
		
		this.setCutoffLimit(cutoffLimitButtom);
		
		// print ultimate creditor
		if (content.getUltimateCreditor().isDefined()) {
			String title = messages.getProperty("T_ULTIMATECREDITOR");
			String text = content.getUltimateCreditor().getAddressPrinted(", ");
			text = limitLength(text,this.getCutoffLimit());
			printFurtherInformationLine(title, text,leftX, y);
			y -= LINEHEIGHT;
		}
		
		// print alt procedures
		List<IAlternativeSchema> altProcudure = content.getAlternativeSchema();
		for (IAlternativeSchema as : altProcudure) {
			printFurtherInformationLine(as.getTitle(),limitLength(as.getContent(),this.getCutoffLimit()) ,leftX, y);
			y -= LINEHEIGHT;
		}
	}
	
	/**
	 * Prints the receipt section
	 * @throws IOException
	 */
	private void createReceipt(ContentBarcodeCH content, String requestedLangauge, Format format) throws IOException {
		if (this.printReceipt) {
			this.setCutoffLimit(cutoffLimitReceipt);
			
			int x = 2;
			this.currentTop = TOP;
			float lineHeight = 3.175002f;//2.8f;
	
			this.printTitle(messages.getProperty("T_RECEIPT"),  x, (int)currentTop, 11);
			currentTop-=5;
			
			contentStream.beginText();
			contentStream.newLineAtOffset(x*USER_UNIT, currentTop*USER_UNIT);
			float fontSize = 8f;
			float fontSizeTitle = 6f;
			print(messages.getProperty("T_ACCOUNT"), getAccountInfo(content), lineHeight,fontSizeTitle,fontSize );
			print(messages.getProperty("T_REF"), content.getPaymentReference().getReferenceFormatted(), lineHeight,fontSizeTitle,fontSize);
			
			float yBox = printDebitor(content, messages, lineHeight, fontSizeTitle, fontSize);
			contentStream.endText();		
			
			printEmptyAddressBox(this.getDocument(), yBox+3, x, 0.84f);
			
			printAmount(content, messages, this.getDocument(), x, 33, 0.8f,0.7f, 15, 21, fontSizeTitle, fontSize);
			
			this.printTitle(messages.getProperty("T_ACCEPENCE"),  34, 16, fontSizeTitle);
		}

	}
	

	private List<String> splitLines(String string, float cutoffLimit, int maxWordLen) {
		List<String> result = new ArrayList();
		String tail = string;
		while(tail.length()>0) {
			String head = limitLength(tail, cutoffLimit);
			String tmptail = tail.substring(head.length(),tail.length());
			if (!tmptail.isEmpty() && !tmptail.startsWith(" ")) {				
				int lastSpace = head.lastIndexOf(" ");
 				head = head.substring(0,lastSpace);
 				tail = tail.substring(head.length(),tail.length());
			} else {
				tail = tmptail;
			}
			tail = tail.trim();
			result.add(head.trim());
		}
		
		return result;
	}


	private String cutOff(String str) {
		return limitLength(str, cutoffLimit);
	}

	
	private String limitLength(String str,float len) {
		String result = str;
		while(isTooLong(result, len)) {
			result = result.substring(0,result.length()-1);
		}
		return result;
	}

	private boolean isTooLong(String str, float limit) {
		boolean result = getStringWidth(str)  / (1000.0 * USER_UNIT)>limit;
		//LOG.debug(str+" -> "+ (result?"too long":"ok"));
		return result;
	}
	
	
	private float getStringWidth(String str) {
		float result = 0;
		try {
			result = font.getStringWidth(str);
		} catch (IOException e) {
			LOG.error(e,e);
		}
		return result;
	}
	private Properties getProperties(String lang) throws IOException {
		String resourceName = "language_" + lang;
		messages = StringUtils.loadProperties(resourceName);
		return messages;
	}

	private void printBarcode(ContentBarcodeCH content, PDDocument document) throws Exception, IOException {
		byte barcodeByteArray[] = new QRSwissBarcode(content.isTest()).create(content.getContent(), "png");
		PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, barcodeByteArray, "swiss-qr");
		float scaling = 1.10f;
		contentStream.drawImage(pdImage, (leftX-2)*USER_UNIT, 39*USER_UNIT, 46*scaling*USER_UNIT, 46*scaling*USER_UNIT); // 28
	}

	private void printEmptyAddressBox(PDDocument document, float yBox, int x, float factor) throws IOException {
		if (yBox > 0) {
			yBox = yBox - 25 + 3;
			contentStream.drawImage(getImageFromResource(document, "/feld/Feld_Zahlungspflichtiger_65x25mm.png", "Debitor"), 
			x*USER_UNIT, yBox*USER_UNIT, 65*factor*USER_UNIT, 25*factor*USER_UNIT);
		}
	}

	private PDImageXObject getImageFromResource(PDDocument document, String path, String name) throws IOException {
		BufferedImage img = ImageIO.read(this.getClass().getResourceAsStream(path));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baos);
		baos.flush();
		byte[] amountByteArray = baos.toByteArray();
		baos.close();
		PDImageXObject amountImage = PDImageXObject.createFromByteArray(document, amountByteArray, name);
		return amountImage;
	}

	private void print(String title, String content, float y) throws IOException {
		print(title, content, y, 8.0f,10.0f);
	}
	
	private void print(String title, String content, float y, float titleFontSize, float textFontSize) throws IOException {
		if (!StringUtils.isEmpty(content)) {
			print(title, Arrays.asList(content), y,titleFontSize, textFontSize);
		}
	}

	private void printTitle(String title) throws IOException {
		printX(title, new ArrayList(), 0);
	}

	
	private void print(String title, List<String> contentArray, float y, float titleFontSize, float textFontSize) throws IOException {
		if (contentArray.size() > 0 && !StringUtils.isEmpty(contentArray.get(0))) {
			printX(title, contentArray, y, titleFontSize, textFontSize);
		}
	}

	private void printX(String title, List<String>  contentArray, float y) throws IOException {
		printX(title, contentArray,y, 8.0f, 10.0f);
	}

	private void printX(String title, List<String>  contentArray, float y, float titleFontSize, float textFontSize) throws IOException {
		if (title!=null) {
			contentStream.setFont(fontBold, titleFontSize * POINT_TO_MM * USER_UNIT);
			contentStream.showText(title);
		}
		contentStream.setFont(font, textFontSize * POINT_TO_MM * USER_UNIT);
		for (String content : contentArray) {
			contentStream.newLineAtOffset(0, -y*USER_UNIT);
			String strOut = cutOff(content);
			contentStream.showText(strOut);
			currentTop = currentTop - y;
		}
		currentTop = currentTop - SECTIONMARGIN;
		contentStream.newLineAtOffset(0, -SECTIONMARGIN*USER_UNIT);
	}
	


	private void printTitle(String title, int x, int y, float fontSize) throws IOException {
		contentStream.beginText();
		// Setting the position for the line
		contentStream.newLineAtOffset(x*USER_UNIT, y*USER_UNIT);
		contentStream.setFont(fontBold, fontSize * POINT_TO_MM * USER_UNIT);
		contentStream.showText(title);
		contentStream.endText();
	}

	private void print(String title, String content, int x, int y, float sizeTitle, float sizeText) throws IOException {
		if (!StringUtils.isEmpty(content)) {
			contentStream.beginText();
			// Setting the position for the line
			contentStream.newLineAtOffset(x*USER_UNIT, y*USER_UNIT);
			contentStream.setFont(fontBold, sizeTitle * POINT_TO_MM * USER_UNIT);
			contentStream.showText(title);
			contentStream.newLineAtOffset(0, -LINEHEIGHT*USER_UNIT);
			contentStream.setFont(font, sizeText * POINT_TO_MM * USER_UNIT);
			contentStream.showText(cutOff(content));
			contentStream.endText();
		}
	}

	/**
	 * Prints the information on one line
	 * @throws IOException
	 */
	private void printFurtherInformationLine(String title, String content, int x, int y) throws IOException {
		contentStream.beginText();
		contentStream.newLineAtOffset(x*USER_UNIT, y*USER_UNIT);
		if (!title.isEmpty()) {
			contentStream.setFont(fontBold, 7.0f * POINT_TO_MM * USER_UNIT);
			contentStream.showText(title);
			contentStream.showText(" ");
		}
		contentStream.setFont(font, 7.0f * POINT_TO_MM * USER_UNIT);
		contentStream.showText(content);
		contentStream.endText();
			
	}

	/**
	 * Returns a ',' delimiter if the expression is true
	 */
	private String delim(boolean b) {
		return b ? "," : "";
	}
	
	/**
	 * <p>getPDPageContentStream.</p>
	 *
	 * @return a {@link org.apache.pdfbox.pdmodel.PDPageContentStream} object
	 */
	public PDPageContentStream getPDPageContentStream() {
		return this.contentStream;
	}

	/**
	 * <p>isAutoClose.</p>
	 *
	 * @return a boolean
	 */
	public boolean isAutoClose() {
		return autoClose;
	}

	/**
	 * <p>Setter for the field <code>autoClose</code>.</p>
	 *
	 * @param autoClose a boolean
	 */
	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	/**
	 * <p>isPrintReceipt.</p>
	 *
	 * @return a boolean
	 */
	public boolean isPrintReceipt() {
		return printReceipt;
	}

	/**
	 * <p>Setter for the field <code>printReceipt</code>.</p>
	 *
	 * @param printReceipt a boolean
	 */
	public void setPrintReceipt(boolean printReceipt) {
		this.printReceipt = printReceipt;
	}

	/**
	 * <p>Getter for the field <code>cutoffLimit</code>.</p>
	 *
	 * @return a float
	 */
	public float getCutoffLimit() {
		return cutoffLimit;
	}

	/**
	 * <p>Setter for the field <code>cutoffLimit</code>.</p>
	 *
	 * @param cutoffLimit a float
	 */
	public void setCutoffLimit(float cutoffLimit) {
		this.cutoffLimit = cutoffLimit;
	}

	/**
	 * <p>Getter for the field <code>cutoffLimitInformation</code>.</p>
	 *
	 * @return a float
	 */
	public float getCutoffLimitInformation() {
		return cutoffLimitInformation;
	}

	/**
	 * <p>Setter for the field <code>cutoffLimitInformation</code>.</p>
	 *
	 * @param cutoffLimitInformation a float
	 */
	public void setCutoffLimitInformation(float cutoffLimitInformation) {
		this.cutoffLimitInformation = cutoffLimitInformation;
	}

	/**
	 * <p>Getter for the field <code>cutoffLimitButtom</code>.</p>
	 *
	 * @return a float
	 */
	public float getCutoffLimitButtom() {
		return cutoffLimitButtom;
	}

	/**
	 * <p>Setter for the field <code>cutoffLimitButtom</code>.</p>
	 *
	 * @param cutoffLimitButton a float
	 */
	public void setCutoffLimitButtom(float cutoffLimitButton) {
		this.cutoffLimitButtom = cutoffLimitButton;
	}

	/**
	 * <p>Getter for the field <code>cutoffLimitReceipt</code>.</p>
	 *
	 * @return a float
	 */
	public float getCutoffLimitReceipt() {
		return cutoffLimitReceipt;
	}

	/**
	 * <p>Setter for the field <code>cutoffLimitReceipt</code>.</p>
	 *
	 * @param cutoffLimitReceipt a float
	 */
	public void setCutoffLimitReceipt(float cutoffLimitReceipt) {
		this.cutoffLimitReceipt = cutoffLimitReceipt;
	}

}
