package ch.swissqr.barcode;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Convert;

/**
 * Generation of a  QR Barcode with an image in the center. The image is loaded from the resources
 *
 * @author pschatzmann
 */
public class QRCombinedBarcode extends QRBarcode implements IBarcode {
	private Convert cv = new Convert(120.0);
	private String resourceLocation="";
	private double dimension;
	private ErrorCorrectionLevel errorLevel;
	
	/**
	 * <p>Constructor for QRCombinedBarcode.</p>
	 *
	 * @param resourceLocation a {@link java.lang.String} object
	 * @param dimensionsMM a double
	 * @param level a {@link ch.swissqr.barcode.ErrorCorrectionLevel} object
	 */
	public QRCombinedBarcode(String resourceLocation, double dimensionsMM, ErrorCorrectionLevel level) {		
		this.resourceLocation = resourceLocation;
		this.dimension = dimensionsMM;
		this.errorLevel = level;
	}
	
	/** {@inheritDoc} */
	@Override
	public byte[] create(String qrCodeText, String imageFormat) throws BarcodeException, IOException  {
		BufferedImage bi = createImage(qrCodeText, imageFormat);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(bi, imageFormat, baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		return imageInByte;
	}

	/** {@inheritDoc} */
	@Override
	public BufferedImage createImage(String qrCodeText, String imageFormat) throws BarcodeException, IOException  {
		IBarcode bc = new QRBarcode(cv.mmToPixel(dimension), this.errorLevel);
		BufferedImage barcode = bc.createImage(qrCodeText, imageFormat);
		BufferedImage overlay = ImageIO.read(this.getClass().getResourceAsStream(resourceLocation));

		return getQRCodeWithOverlay(barcode, overlay);
	}

	private BufferedImage getQRCodeWithOverlay(BufferedImage barcodeRow, BufferedImage overlay) {
		BufferedImage barcode = scale(barcodeRow, dimension);
		BufferedImage scaledOverlay = scale(overlay, dimension / 6.5714); // 7

		Integer deltaHeight = barcode.getHeight() - scaledOverlay.getHeight();
		Integer deltaWidth = barcode.getWidth() - scaledOverlay.getWidth();

		BufferedImage combined = new BufferedImage(barcode.getWidth(), barcode.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g2 = (Graphics2D) combined.getGraphics();
		g2.drawImage(barcode, 0, 0, null);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2.drawImage(scaledOverlay, Math.round(deltaWidth / 2), Math.round(deltaHeight / 2), null);
		return combined;
	}

	private BufferedImage scale(BufferedImage input, double mm) {
		Integer scaledWidth = cv.mmToPixel(mm);
		Integer scaledHeight = scaledWidth;

		BufferedImage imageBuff = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics g = imageBuff.createGraphics();
		g.drawImage(input.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH), 0, 0,
				new Color(0, 0, 0), null);
		g.dispose();
		return imageBuff;
	}

}
