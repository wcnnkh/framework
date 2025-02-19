package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Reader;

import io.basc.framework.util.function.Pipeline;
import lombok.NonNull;

public interface ReaderSource<T extends Reader> extends ReaderFactory<T> {
	public static interface ReaderSourceWrapper<T extends Reader, W extends ReaderSource<T>>
			extends ReaderSource<T>, ReaderFactoryWrapper<T, W> {
		@Override
		default T getReader() throws IOException {
			return getSource().getReader();
		}

		@Override
		default @NonNull Pipeline<T, IOException> getReaderPipeline() {
			return getSource().getReaderPipeline();
		}
	}

	T getReader() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getReaderPipeline() {
		return Pipeline.forCloseable(this::getReader);
	}
}
