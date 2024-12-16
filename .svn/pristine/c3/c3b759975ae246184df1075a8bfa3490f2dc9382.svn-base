package ch.swissqr.utils;

/**
 * Conversion between pixcels and mm
 * 1 in = 25,4 mm
 * lazer printer has 2400 dpi
 * screen has 96 or 120 dpi
 * @author pschatzmann
 *
 */

public class Convert {
	double dpi = 96.0;
	
	Convert() {}
	
	public Convert(double dpi) {
		this.dpi = dpi;
	}
	
	public  int pixelTomm(double pix) {
		return Double.valueOf( pix * 25.4 / dpi).intValue();
	}
	
	public  int mmToPixel(double mm) {
		return Double.valueOf(mm / 25.4 * dpi).intValue();
	}

}
