package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

import scw.io.IOUtils;

public interface InputMessage extends Message {
	InputStream getBody() throws IOException;
	
	default byte[] getBytes() throws IOException {
		InputStream is = null;
		try {
			is = getBody();
			return IOUtils.toByteArray(is);
		} finally {
			IOUtils.close(is);
		}
	}
}
