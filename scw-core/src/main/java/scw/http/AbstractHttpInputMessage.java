package scw.http;

import java.io.IOException;
import java.io.InputStream;

import scw.core.Constants;
import scw.io.IOUtils;

public abstract class AbstractHttpInputMessage extends AbstractHttpMessage implements HttpInputMessage {

	public byte[] getBytes() throws IOException {
		InputStream is = null;
		try {
			is = getBody();
			return IOUtils.toByteArray(is);
		} finally {
			IOUtils.close(is);
		}
	}

	public String getTextBody() throws IOException {
		InputStream is = null;
		String charsetName = getCharacterEncoding();
		try {
			is = getBody();
			return IOUtils.readContent(getBody(), 256, charsetName == null ? Constants.UTF_8.name() : charsetName);
		} finally {
			IOUtils.close(is);
		}
	}
}
