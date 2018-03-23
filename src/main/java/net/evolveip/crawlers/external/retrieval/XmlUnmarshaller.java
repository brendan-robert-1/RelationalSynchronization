/**
 *
 */
package net.evolveip.crawlers.external.retrieval;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

/**
 * @author brobert
 *
 */
public class XmlUnmarshaller {

	/**
	 * Wrapper on unmarshalling
	 *
	 * @param t
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T unmarshall(Class<T> t, String xml) {
		T t1 = null;
		try {
			JAXBContext jb = JAXBContext.newInstance(t);
			Unmarshaller um = jb.createUnmarshaller();
			um.setEventHandler(event -> {
				throw new RuntimeException("Class: " + t.getSimpleName() + " " + event.getMessage(), event.getLinkedException());
			});
			StringReader reader = new StringReader(xml);
			t1 = (T) um.unmarshal(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t1;
	}
}
