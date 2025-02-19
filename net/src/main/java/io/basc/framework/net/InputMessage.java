package io.basc.framework.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.io.InputStreamFactory;
import io.basc.framework.util.io.ReaderFactory;
import lombok.NonNull;

public interface InputMessage extends Message, InputStreamFactory<InputStream>, ReaderFactory<Reader> {
	@FunctionalInterface
	public static interface InputMessageWrapper<W extends InputMessage> extends InputMessage, MessageWrapper<W>,
			InputStreamSourceWrapper<InputStream, W>, ReaderSourceWrapper<Reader, W> {
		@Override
		default @NonNull Pipeline<Reader, IOException> getReader() {
			return getSource().getReader();
		}
	}

	@Override
	default @NonNull Pipeline<Reader, IOException> getReader() {
		String charsetName = getCharsetName();
		return StringUtils.isEmpty(charsetName) ? toReaderSource().getReader()
				: toReaderSource(charsetName).getReader();
	}
}
