package ch.swissqr.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


import ch.swissqr.errors.BarcodeException;

/**
 * Generic Interface to generate barcodes
 *
 * @author pschatzmann
 */
public interface IBarcode {

	/**
	 * <p>createImage.</p>
	 *
	 * @param qrCodeText a {@link java.lang.String} object
	 * @param type a {@link java.lang.String} object
	 * @return a {@link java.awt.image.BufferedImage} object
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	BufferedImage createImage(String qrCodeText, String type) throws BarcodeException, IOException;
	/**
	 * <p>create.</p>
	 *
	 * @param qrCodeText a {@link java.lang.String} object
	 * @param type a {@link java.lang.String} object
	 * @return an array of {@link byte} objects
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	byte[] create(String qrCodeText, String type) throws BarcodeException, IOException;
	/**
	 * <p>readImage.</p>
	 *
	 * @param is a {@link java.io.InputStream} object
	 * @return a {@link java.lang.String} object
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 * @throws java.io.IOException if any.
	 */
	public String readImage(InputStream is) throws BarcodeException, IOException; 
	/**
	 * <p>readImage.</p>
	 *
	 * @param bi a {@link java.awt.image.BufferedImage} object
	 * @return a {@link java.lang.String} object
	 * @throws java.io.IOException if any.
	 * @throws ch.swissqr.errors.BarcodeException if any.
	 */
	public String readImage(BufferedImage bi) throws IOException, BarcodeException;
}
