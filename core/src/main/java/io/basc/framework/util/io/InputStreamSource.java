package io.basc.framework.util.io;

import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.util.function.Pipeline;
import lombok.NonNull;

@FunctionalInterface
public interface InputStreamSource<T extends InputStream> extends InputStreamFactory<T> {
	public static interface InputStreamSourceWrapper<T extends InputStream, W extends InputStreamSource<T>>
			extends InputStreamSource<T>, InputStreamFactoryWrapper<T, W> {
		@Override
		default T getInputStream() throws IOException {
			return getSource().getInputStream();
		}

		@Override
		default @NonNull Pipeline<T, IOException> getInputStreamPipeline() {
			return getSource().getInputStreamPipeline();
		}
	}

	T getInputStream() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getInputStreamPipeline() {
		return Pipeline.forCloseable(this::getInputStream);
	}
}
