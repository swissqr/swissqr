package ch.swissqr.pdf.parsing;

/**
 * String with information of page and position
 * 
 * @author pschatzmann
 *
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
	
	public Integer getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public Pos getPosition() {
		return position;
	}
	public void setPosition(Pos position) {
		this.position = position;
	}
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	
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
