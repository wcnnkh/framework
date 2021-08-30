package io.basc.framework.http;

import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.OutputMessage;

public interface HttpOutputMessage extends OutputMessage, HttpMessage{
	
	default void setContentType(MediaType contentType) {
		String charsetName = contentType.getCharsetName();
		if (charsetName == null) {
			charsetName = getCharacterEncoding();
			if (charsetName == null) {
				getHeaders().setContentType(contentType);
			} else {
				getHeaders().setContentType(new MediaType(contentType, charsetName));
			}
		} else {
			getHeaders().setContentType(contentType);
		}
	}

	default void setContentType(MimeType contentType) {
		setContentType(new MediaType(contentType));
	}

	default void setContentLength(long contentLength) {
		getHeaders().setContentLength(contentLength);
	}

	default void setCharacterEncoding(String charsetName) {
		MediaType mediaType = getContentType();
		if (mediaType == null) {
			return;
		}

		setContentType(new MediaType(mediaType, charsetName));
	}
}
