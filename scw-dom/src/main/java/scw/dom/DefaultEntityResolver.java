package scw.dom;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import scw.env.Sys;
import scw.event.Observable;

public class DefaultEntityResolver implements EntityResolver {
	private static final Observable<Boolean> IGNORE_DTD = Sys.env.getObservableValue("dom.ignore.dtd", boolean.class,
			true);

	public static final EntityResolver INSTANCE = new DefaultEntityResolver();

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (IGNORE_DTD.get() && systemId != null && systemId.endsWith(".dtd")) {
			return new InputSource(new StringReader(""));
		}
		return null;
	}

}
