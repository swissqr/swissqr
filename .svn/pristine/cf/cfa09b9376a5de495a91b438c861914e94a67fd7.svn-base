package ch.swissqr.barcode;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import ch.swissqr.errors.BarcodeException;
import ch.swissqr.utils.Convert;

/**
 * Generation of QR barcodes 
 * 
 * @author pschatzmann
 *
 */

public class QRBarcode implements IBarcode {
	private static final Logger LOG = Logger.getLogger(QRBarcode.class);
	private ErrorCorrectionLevel level = ErrorCorrectionLevel.M;
	private Convert cv = new Convert(120.0);
	private int dimensions = cv.mmToPixel(46.0) ; // in pixel e.g. 174

	public QRBarcode() {
	}

	public QRBarcode(double dimensionsMM, ErrorCorrectionLevel level) {
		this.dimensions = cv.mmToPixel(dimensionsMM);
		this.level = level;
	}

	/**
	 * Create the image for the indicated barcode String
	 */
	public BufferedImage createImage(String qrCodeText, String imageFormat) throws BarcodeException, IOException {
		byte[] barcode = create(qrCodeText, imageFormat);
		ByteArrayInputStream input = new ByteArrayInputStream(barcode);
		BufferedImage image = ImageIO.read(input);
		return image;
	}

	
	
	/**
	 * Create the image for the indicated barcode String
	 */
	public byte[] create(String qrCodeText, String imageFormat) throws BarcodeException, IOException {
		try {
			Hashtable hints = new Hashtable();
			com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.valueOf(level.toString());
			hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.valueOf(level.toString()));
		    hints.put(EncodeHintType.MARGIN, 1);
		    hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
		    
	
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			int size = getDimensions();
	
			BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hints);
			int matrixWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(matrixWidth, matrixWidth,BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
	
			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, matrixWidth, matrixWidth);
			// Paint and save the image using the ByteMatrix
			graphics.setColor(Color.BLACK);
	
			for (int i = 0; i < matrixWidth; i++) {
				for (int j = 0; j < matrixWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( image, imageFormat, baos );
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();
			return imageInByte;
		} catch(Exception ex) {
			throw new BarcodeException(ex);
		}
	}
	
	/**
	 * Reads the image from the Inputstream and returns the related decoded content string
	 * @throws IOException
	 * @throws BarcodeException
	 */

	public String readImage(InputStream is) throws  IOException, BarcodeException {
		String msg = "";
		try {
			BufferedImage img = ImageIO.read(is);
			msg = this.readImage(img);
		} catch(Exception ex) {
			throw new BarcodeException(ex);
		}
		return msg;
	}

	/**
	 * Reads the BufferedImage from  and returns the related decoded content string
	 * @param bi
	 * @return
	 * @throws IOException
	 * @throws BarcodeException
	 */
	public String readImage(BufferedImage bi)
			throws IOException, BarcodeException {
		try {
	
			LOG.info("Dimension: "+bi.getHeight()+"/"+bi.getWidth());
			Hashtable<DecodeHintType, Object> hintMap = new Hashtable<DecodeHintType, Object>();
			hintMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
	
			// Bild zu BinaryBitmap verwandeln
			BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bi);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	
			// QR Leser initialisieren...
			QRCodeReader reader = new QRCodeReader();
			// ...und lesen:
			Result result;
				result = reader.decode(bitmap, hintMap);
				
			return result.getText();
		} catch(Exception ex) {
			throw new BarcodeException(ex);
		}
	}
	
	protected int getDimensions() {
		return this.dimensions;
	}
	
}
