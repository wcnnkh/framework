package io.basc.framework.util.io;

import java.io.IOException;
import java.io.OutputStream;

import io.basc.framework.util.function.Pipeline;
import lombok.NonNull;

public interface OutputStreamSource<T extends OutputStream> extends OutputStreamFactory<T> {

	public static interface OutputStreamSourceWrapper<T extends OutputStream, W extends OutputStreamSource<T>>
			extends OutputStreamSource<T>, OutputStreamFactoryWrapper<T, W> {
		@Override
		default T getOutputStream() throws IOException {
			return getSource().getOutputStream();
		}

		@Override
		default @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
			return getSource().getOutputStreamPipeline();
		}
	}

	T getOutputStream() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getOutputStreamPipeline() {
		return Pipeline.forCloseable(this::getOutputStream);
	}
}
