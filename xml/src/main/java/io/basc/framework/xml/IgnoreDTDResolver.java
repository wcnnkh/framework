package io.basc.framework.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.env.SystemProperties;

public class IgnoreDTDResolver implements EntityResolver {
	private static final ValueWrapper IGNORE_DTD = SystemProperties.getInstance().get("io.basc.framework.xml.ignore.dtd")
			.or(true);

	public static final EntityResolver INSTANCE = new IgnoreDTDResolver();

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (IGNORE_DTD.getAsBoolean() && systemId != null && systemId.endsWith(".dtd")) {
			return new InputSource(new StringReader(""));
		}
		return null;
	}

}
