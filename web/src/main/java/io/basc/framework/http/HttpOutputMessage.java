package io.basc.framework.http;

import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;

public interface HttpOutputMessage extends OutputMessage, HttpMessage {
	public static interface HttpOutputMessageWrapper<W extends HttpOutputMessage>
			extends HttpOutputMessage, OutputMessageWrapper<W>, HttpMessageWrapper<W> {
		@Override
		default void setContentType(MediaType contentType) {
			getSource().setContentType(contentType);
		}

		@Override
		default void setContentType(MimeType contentType) {
			getSource().setContentType(contentType);
		}

		@Override
		default void setContentLength(long contentLength) {
			getSource().setContentLength(contentLength);
		}
	}

	default void setContentType(MediaType contentType) {
		String charsetName = contentType.getCharsetName();
		if (charsetName == null) {
			charsetName = getCharsetName();
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
}
