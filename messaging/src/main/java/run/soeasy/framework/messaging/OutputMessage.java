package run.soeasy.framework.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.function.Pipeline;
import run.soeasy.framework.util.function.Wrapped;
import run.soeasy.framework.util.io.OutputStreamSource;
import run.soeasy.framework.util.io.WriterFactory;

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

		@Override
		default OutputMessage buffered() {
			return getSource().buffered();
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

	@Setter
	public static class BufferingOutputMessage<W extends OutputMessage> extends Wrapped<W>
			implements OutputMessageWrapper<W> {
		private OutputStream outputStream;

		public BufferingOutputMessage(W source) {
			super(source);
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			if (outputStream == null) {
				outputStream = getSource().getOutputStream();
			}
			return outputStream;
		}

		@Override
		public @NonNull Pipeline<OutputStream, IOException> getOutputStreamPipeline() {
			return Pipeline.of(() -> getOutputStream());
		}

		@Override
		public OutputMessage buffered() {
			return this;
		}
	}

	default OutputMessage buffered() {
		return new BufferingOutputMessage<>(this);
	}
}
