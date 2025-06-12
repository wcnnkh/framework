package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface ReaderSource<T extends Reader> extends ReaderFactory<T> {
	T getReader() throws IOException;

	@Override
	default @NonNull Pipeline<T, IOException> getReaderPipeline() {
		return Pipeline.forCloseable(this::getReader);
	}
}
