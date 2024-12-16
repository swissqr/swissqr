package ch.swissqr.pdf.parsing;

/**
 * X,Y position
 * @author pschatzmann
 *
 */
public class Pos {
	float x,y,width,heigth,pageWidth,pageHeight;
	public Pos(float x,float y,float width,float pageWidth, float pageHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
	}
	
	public Float getX() {
		return x;
	}
	public Float getY() {
		return y;
	}
	public Float getWidth() {
		return width;
	}

	public Float getPageWidth() {
		return pageWidth;
	}

	public Float getPageHeight() {
		return pageHeight;
	}

}
