package io.basc.framework.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapped;
import io.basc.framework.util.io.InputStreamSource;
import io.basc.framework.util.io.ReaderFactory;
import lombok.NonNull;
import lombok.Setter;

public interface InputMessage extends Message, InputStreamSource<InputStream> {
	@FunctionalInterface
	public static interface InputMessageWrapper<W extends InputMessage>
			extends InputMessage, MessageWrapper<W>, InputStreamSourceWrapper<InputStream, W> {
		@Override
		default ReaderFactory<Reader> toReaderFactory() {
			return getSource().toReaderFactory();
		}

		@Override
		default InputMessage buffered() {
			return getSource().buffered();
		}
	}
	
	@Override
	default ReaderFactory<Reader> toReaderFactory() {
		String charsetName = getCharsetName();
		if (StringUtils.isEmpty(charsetName)) {
			return InputStreamSource.super.toReaderFactory();
		}
		return toReaderFactory(charsetName);
	}

	@Setter
	public static class BufferingInputMessage<W extends InputMessage> extends Wrapped<W>
			implements InputMessageWrapper<W> {
		private InputStream inputStream;

		public BufferingInputMessage(W source) {
			super(source);
		}

		@Override
		public InputStream getInputStream() throws IOException {
			if (inputStream == null) {
				byte[] data = getSource().readAllBytes();
				inputStream = new ByteArrayInputStream(data);
			}
			return inputStream;
		}

		@Override
		public @NonNull Pipeline<InputStream, IOException> getInputStreamPipeline() {
			return Pipeline.of(() -> getInputStream());
		}
		
		@Override
		public InputMessage buffered() {
			return this;
		}
	}

	default InputMessage buffered() {
		return new BufferingInputMessage<>(this);
	}
}
