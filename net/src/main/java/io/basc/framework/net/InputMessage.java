package io.basc.framework.net;

import java.io.InputStream;
import java.io.Reader;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.io.InputStreamSource;
import io.basc.framework.util.io.ReaderFactory;

public interface InputMessage extends Message, InputStreamSource<InputStream> {
	@FunctionalInterface
	public static interface InputMessageWrapper<W extends InputMessage>
			extends InputMessage, MessageWrapper<W>, InputStreamSourceWrapper<InputStream, W> {
		@Override
		default ReaderFactory<Reader> toReaderFactory() {
			return getSource().toReaderFactory();
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
}
