package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

import scw.io.IOUtils;

public abstract class AbstractInputMessage extends AbstractMessage implements InputMessage {

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
		try {
			is = getBody();
			return IOUtils.readContent(getBody(), getCharset().name());
		} finally {
			IOUtils.close(is);
		}
	}
}
