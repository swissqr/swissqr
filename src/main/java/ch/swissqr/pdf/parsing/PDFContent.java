package ch.swissqr.pdf.parsing;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * PDFTextStripper which records the position of the text. The result is returned as list of Text objects
 *
 * @author pschatzmann
 */
public class PDFContent extends PDFTextStripper {
	private List<Text> text = new ArrayList();

	/**
	 * <p>Constructor for PDFContent.</p>
	 *
	 * @throws java.io.IOException if any.
	 */
	public PDFContent() throws IOException {
		super();
	}

	/**
	 * {@inheritDoc}
	 *
	 * Override the default functionality of PDFTextStripper.writeString()
	 */
	@Override
	protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
		super.writeString(string, textPositions);
		text.add(new Text(this.getCurrentPageNo(), string, getPosition(textPositions)));
	}

	private Pos getPosition(List<TextPosition> textPositions) {
		Float minX = null;
		Float maxX = null;
		Float maxY = null;
		for (TextPosition pos : textPositions) {
			if (minX == null || pos.getX() < minX) {
				minX = pos.getX();
			}			
			if (maxX == null || pos.getEndX() > maxX) {
				maxX = pos.getEndX();
			}						
		}
		TextPosition first = textPositions.get(0);
		maxY = first.getEndY();
		
		return new Pos(minX, maxY, maxX - minX,  first.getPageWidth(), first.getPageWidth());

	}

	/**
	 * <p>parse.</p>
	 *
	 * @param doc a {@link org.apache.pdfbox.pdmodel.PDDocument} object
	 * @throws java.io.IOException if any.
	 */
	public void parse(PDDocument doc) throws IOException {
		text.clear();
		StringWriter outputStream = new StringWriter();
		writeText(doc, outputStream);
		Collections.sort(text);
	}
	
	/**
	 * <p>getContent.</p>
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<Text> getContent()  {
		return text;
	}
	
	/**
	 * <p>getContent.</p>
	 *
	 * @param doc a {@link org.apache.pdfbox.pdmodel.PDDocument} object
	 * @return a {@link java.util.List} object
	 * @throws java.io.IOException if any.
	 */
	public List<Text> getContent(PDDocument doc) throws IOException {
		parse(doc);
		return text;
	}

	
}
