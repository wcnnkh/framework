package io.basc.framework.net;

import java.io.OutputStream;
import java.io.Writer;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.io.OutputStreamSource;
import io.basc.framework.util.io.WriterFactory;

public interface OutputMessage extends Message, OutputStreamSource<OutputStream> {
	@FunctionalInterface
	public static interface OutputMessageWrapper<W extends OutputMessage>
			extends OutputMessage, MessageWrapper<W>, OutputStreamSourceWrapper<OutputStream, W> {

		@Override
		default WriterFactory<Writer> toWriterFactory() {
			return getSource().toWriterFactory();
		}

		@Override
		default void setContentType(MediaType contentType) {
			getSource().setContentType(contentType);
		}

		@Override
		default void setContentLength(long contentLength) {
			getSource().setContentLength(contentLength);
		}

		@Override
		default void setCharsetName(String charsetName) {
			getSource().setCharsetName(charsetName);
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

	default void setContentLength(long contentLength) {
		getHeaders().setContentLength(contentLength);
	}

	default void setCharsetName(String charsetName) {
		MediaType mediaType = getContentType();
		if (mediaType == null) {
			mediaType = MediaType.ALL;
			return;
		}

		setContentType(new MediaType(mediaType, charsetName));
	}

	@Override
	default WriterFactory<Writer> toWriterFactory() {
		String charsetName = getCharsetName();
		if (StringUtils.isEmpty(charsetName)) {
			return OutputStreamSource.super.toWriterFactory();
		}
		return toWriterFactory(charsetName);
	}
}
