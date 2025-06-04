package run.soeasy.framework.xml;

import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IgnoreDTDResolver implements EntityResolver {
	private boolean ignore = true;

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (ignore && systemId != null && systemId.endsWith(".dtd")) {
			return new InputSource(new StringReader(""));
		}
		return null;
	}

}
