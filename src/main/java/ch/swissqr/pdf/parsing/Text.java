package ch.swissqr.pdf.parsing;

/**
 * String with information of page and position
 *
 * @author pschatzmann
 */
public class Text implements Comparable<Text>{
	private int page;
	private Pos position;
	private String string;
	
	Text(int page, String text, Pos pos) {
		this.page = page;
		this.position = pos;
		this.string = text;
	}
	
	/**
	 * <p>Getter for the field <code>page</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object
	 */
	public Integer getPage() {
		return page;
	}
	/**
	 * <p>Setter for the field <code>page</code>.</p>
	 *
	 * @param page a int
	 */
	public void setPage(int page) {
		this.page = page;
	}
	/**
	 * <p>Getter for the field <code>position</code>.</p>
	 *
	 * @return a {@link ch.swissqr.pdf.parsing.Pos} object
	 */
	public Pos getPosition() {
		return position;
	}
	/**
	 * <p>Setter for the field <code>position</code>.</p>
	 *
	 * @param position a {@link ch.swissqr.pdf.parsing.Pos} object
	 */
	public void setPosition(Pos position) {
		this.position = position;
	}
	/**
	 * <p>Getter for the field <code>string</code>.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String getString() {
		return string;
	}
	/**
	 * <p>Setter for the field <code>string</code>.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 */
	public void setString(String string) {
		this.string = string;
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(string);
		sb.append(" [");
		sb.append(page);
		sb.append("] (");
		sb.append(position.getX());
		sb.append("/");
		sb.append(position.getY());
		sb.append(":");
		sb.append(position.getWidth());
		sb.append(")");
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(Text o) {
		int result = this.getPage().compareTo(o.getPage());
		if (result==0) {
			 result = o.getPosition().getY().compareTo(this.getPosition().getY());
		}
		if (result==0) {
			 result = this.getPosition().getX().compareTo(o.getPosition().getX());
		}
		return result;
	}
	
	
}
