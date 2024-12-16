package ch.swissqr.content.ch.formats;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import ch.swissqr.content.IContent;

/**
 * Serialization and Deserialization of XML
 * 
 * @author pschatzmann
 *
 */
public class XmlFormat  implements IFormat {

	@Override
	public String write(List<IContent> c) throws FormatException {
		try {
			XMLList content = new XMLList(c);				
			JAXBContext context = JAXBContext.newInstance(XMLList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter writer = new StringWriter();
			m.marshal(content, writer);
			return writer.toString();
		} catch (Exception ex) {
			throw new FormatException(ex);
		}
	}

	@Override
	public List<IContent> read(String xmlString) throws FormatException {
		try {
			JAXBContext context = JAXBContext.newInstance(XMLList.class);
			Unmarshaller m = context.createUnmarshaller();
			XMLList c = (XMLList) m.unmarshal(new StringReader(xmlString));
			return c.getList();
		} catch (Exception ex) {
			throw new FormatException(ex);
		}
	}

}
