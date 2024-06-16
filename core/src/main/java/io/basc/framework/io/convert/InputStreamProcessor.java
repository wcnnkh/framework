package io.basc.framework.io.convert;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.basc.framework.util.function.Processor;

public interface InputStreamProcessor<T> extends Processor<InputStream, T, IOException> {

	public static InputStreamProcessor<InputStreamReader> toInputStreamReader() {
		return (is) -> new InputStreamReader(is);
	}
}
