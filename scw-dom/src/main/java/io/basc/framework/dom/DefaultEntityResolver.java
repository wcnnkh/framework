package io.basc.framework.dom;

import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
