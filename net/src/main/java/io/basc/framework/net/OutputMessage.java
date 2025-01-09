package io.basc.framework.net;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.io.OutputStreamFactory;
import io.basc.framework.util.io.WriterFactory;
import lombok.NonNull;

public interface OutputMessage extends Message, OutputStreamFactory<OutputStream>, WriterFactory<Writer> {
	@FunctionalInterface
	public static interface OutputMessageWrapper<W extends OutputMessage> extends OutputMessage, MessageWrapper<W>,
			OutputStreamFactoryWrapper<OutputStream, W>, WriterFactoryWrapper<Writer, W> {

		@Override
		default Pipeline<Writer, IOException> getWriter() {
			return getSource().getWriter();
		}

		@Override
		default void setContentType(MimeType contentType) {
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

	void setContentType(MimeType contentType);

	void setContentLength(long contentLength);

	default void setCharsetName(String charsetName) {
		MimeType mimeType = getContentType();
		if (mimeType == null) {
			return;
		}

		setContentType(new MimeType(mimeType, charsetName));
	}

	@Override
	default @NonNull Pipeline<Writer, IOException> getWriter() {
		String charsetName = getCharsetName();
		return StringUtils.isEmpty(charsetName) ? toWriterFactory().getWriter()
				: toWriterFactory(charsetName).getWriter();
	}
}
