package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface ReaderSourceWrapper<T extends Reader, W extends ReaderSource<T>>
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