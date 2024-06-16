package io.basc.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import io.basc.framework.util.function.Processor;

public interface InputStreamProcessor<T> extends Processor<InputStream, T, IOException> {

	public static InputStreamProcessor<InputStreamReader> toInputStreamReader() {
		return (is) -> new InputStreamReader(is);
	}

	public static InputStreamProcessor<InputStreamReader> toInputStreamReader(Charset charset) {
		return (is) -> new InputStreamReader(is, charset);
	}

	public static InputStreamProcessor<InputStreamReader> toInputStreamReader(String charsetName) {
		return (is) -> new InputStreamReader(is, charsetName);
	}
}
