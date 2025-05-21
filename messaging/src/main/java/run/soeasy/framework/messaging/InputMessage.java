package run.soeasy.framework.messaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.Wrapped;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.io.InputFactory;
import run.soeasy.framework.core.io.InputStreamSource;

public interface InputMessage extends Message, InputStreamSource<InputStream> {
	@FunctionalInterface
	public static interface InputMessageWrapper<W extends InputMessage>
			extends InputMessage, MessageWrapper<W>, InputStreamSourceWrapper<InputStream, W> {
		@Override
		default InputFactory<InputStream, Reader> decode() {
			return getSource().decode();
		}

		@Override
		default InputMessage buffered() {
			return getSource().buffered();
		}
	}

	@Override
	default InputFactory<InputStream, Reader> decode() {
		String charsetName = getCharsetName();
		if (StringUtils.isEmpty(charsetName)) {
			return InputStreamSource.super.decode();
		}
		return decode(charsetName);
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
