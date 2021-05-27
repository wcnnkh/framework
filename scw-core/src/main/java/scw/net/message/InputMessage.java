package scw.net.message;

import java.io.IOException;
import java.io.InputStream;

import scw.core.Constants;
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
	
	default String getTextBody() throws IOException {
		InputStream is = null;
		String charsetName = getCharacterEncoding();
		try {
			is = getBody();
			return IOUtils.readContent(getBody(), 256,
					charsetName == null ? Constants.UTF_8.name() : charsetName);
		} finally {
			IOUtils.close(is);
		}
	}
}
