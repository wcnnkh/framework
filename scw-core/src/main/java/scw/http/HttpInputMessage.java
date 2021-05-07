package scw.http;

import java.io.IOException;
import java.io.InputStream;

import scw.core.Constants;
import scw.io.IOUtils;
import scw.net.message.InputMessage;

public interface HttpInputMessage extends InputMessage, HttpMessage {
	
	@Override
	default long getContentLength() {
		return HttpMessage.super.getContentLength();
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