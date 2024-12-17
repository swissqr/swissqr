package ch.swissqr.pdf.parsing;

/**
 * X,Y position
 *
 * @author pschatzmann
 */
public class Pos {
	float x,y,width,heigth,pageWidth,pageHeight;
	/**
	 * <p>Constructor for Pos.</p>
	 *
	 * @param x a float
	 * @param y a float
	 * @param width a float
	 * @param pageWidth a float
	 * @param pageHeight a float
	 */
	public Pos(float x,float y,float width,float pageWidth, float pageHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
	}
	
	/**
	 * <p>Getter for the field <code>x</code>.</p>
	 *
	 * @return a {@link java.lang.Float} object
	 */
	public Float getX() {
		return x;
	}
	/**
	 * <p>Getter for the field <code>y</code>.</p>
	 *
	 * @return a {@link java.lang.Float} object
	 */
	public Float getY() {
		return y;
	}
	/**
	 * <p>Getter for the field <code>width</code>.</p>
	 *
	 * @return a {@link java.lang.Float} object
	 */
	public Float getWidth() {
		return width;
	}

	/**
	 * <p>Getter for the field <code>pageWidth</code>.</p>
	 *
	 * @return a {@link java.lang.Float} object
	 */
	public Float getPageWidth() {
		return pageWidth;
	}

	/**
	 * <p>Getter for the field <code>pageHeight</code>.</p>
	 *
	 * @return a {@link java.lang.Float} object
	 */
	public Float getPageHeight() {
		return pageHeight;
	}

}
