package ch.swissqr.barcode;


/**
 * Generation of a Swiss QR Barcode
 * 
 * Rest Webservice for the Generation of Swiss QR barcodes Barcode dimensions
 * 46 x 46 mm Swiss cross logo measuring 7 x 7 mm.
 * 
 * The maximum Swiss QR Code data content permitted is 997 characters (including
 * the element separators). The version of the QR Code resulting with error
 * correction level "M" and binary coding is version 25 with 117 x 117 modules.
 * 
 * @author pschatzmann
 *
 */

public class QRSwissBarcode extends QRCombinedBarcode implements IBarcode {
	public QRSwissBarcode() {
		this(false);
	}
	
	public QRSwissBarcode(boolean isTest) {
		super(isTest? "/icons/test.png" : "/kreuz/CH-Kreuz_7mm.png", 46.0, ErrorCorrectionLevel.M);
	}
	
	public QRSwissBarcode(boolean isTest, Double size, ErrorCorrectionLevel level) {
		super(isTest? "/icons/test.png" : "/kreuz/CH-Kreuz_7mm.png",size, level);
	}
	
	

}
