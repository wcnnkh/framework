package io.basc.framework.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.basc.framework.env.Sys;
import io.basc.framework.event.Observable;

public class IgnoreDTDResolver implements EntityResolver {
	private static final Observable<Boolean> IGNORE_DTD = Sys.getEnv().getProperties()
			.getObservable("io.basc.framework.xml.ignore.dtd").map((e) -> e.or(true).getAsBoolean());

	public static final EntityResolver INSTANCE = new IgnoreDTDResolver();

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (IGNORE_DTD.get() && systemId != null && systemId.endsWith(".dtd")) {
			return new InputSource(new StringReader(""));
		}
		return null;
	}

}
