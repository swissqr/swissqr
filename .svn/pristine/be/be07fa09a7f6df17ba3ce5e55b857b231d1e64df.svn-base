package ch.swissqr.barcode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;


import ch.swissqr.errors.BarcodeException;

/**
 * Generic Interface to generate barcodes
 * @author pschatzmann
 *
 */

public interface IBarcode {

	BufferedImage createImage(String qrCodeText, String type) throws BarcodeException, IOException;
	byte[] create(String qrCodeText, String type) throws BarcodeException, IOException;
	public String readImage(InputStream is) throws BarcodeException, IOException; 
	public String readImage(BufferedImage bi) throws IOException, BarcodeException;
}