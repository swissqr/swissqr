package ch.swissqr.content.ch.formats;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import ch.swissqr.content.IContent;

/**
 * The xml serialization does not support lists. We create a wrapper object to
 * circumvent this restriction.
 * 
 * @author pschatzmann
 *
 */
@XmlRootElement(name = "root")
public class XMLList {
	private List<IContent> list;

	public XMLList() {
	}

	public XMLList(List list) {
		this.list = list;
	}

	public List<IContent> getList() {
		return list;
	}

	public void setList(List<IContent> list) {
		this.list = list;
	}
}
