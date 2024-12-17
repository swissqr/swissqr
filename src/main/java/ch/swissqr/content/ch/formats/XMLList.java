package ch.swissqr.content.ch.formats;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import ch.swissqr.content.IContent;

/**
 * The xml serialization does not support lists. We create a wrapper object to
 * circumvent this restriction.
 *
 * @author pschatzmann
 */
@XmlRootElement(name = "root")
public class XMLList {
	private List<IContent> list;

	/**
	 * <p>Constructor for XMLList.</p>
	 */
	public XMLList() {
	}

	/**
	 * <p>Constructor for XMLList.</p>
	 *
	 * @param list a {@link java.util.List} object
	 */
	public XMLList(List list) {
		this.list = list;
	}

	/**
	 * <p>Getter for the field <code>list</code>.</p>
	 *
	 * @return a {@link java.util.List} object
	 */
	public List<IContent> getList() {
		return list;
	}

	/**
	 * <p>Setter for the field <code>list</code>.</p>
	 *
	 * @param list a {@link java.util.List} object
	 */
	public void setList(List<IContent> list) {
		this.list = list;
	}
}
