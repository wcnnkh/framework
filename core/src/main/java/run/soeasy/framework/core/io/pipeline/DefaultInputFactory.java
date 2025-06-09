package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class DefaultInputFactory<I extends InputStream, S extends InputStreamFactory<? extends I>, R extends Reader, T extends ReaderFactory<? extends R>>
		implements InputFactory<I, R> {
	protected final S inputStreamFactory;
	protected final T readerFactory;

	@Override
	public boolean isDecoded() {
		return readerFactory != null;
	}

	@Override
	public @NonNull Pipeline<I, IOException> getInputStreamPipeline() {
		if (inputStreamFactory == null) {
			return Pipeline.empty();
		}
		return inputStreamFactory.getInputStreamPipeline().map(ThrowingFunction.identity());
	}

	@Override
	public @NonNull Pipeline<R, IOException> getReaderPipeline() {
		if (readerFactory == null) {
			return Pipeline.empty();
		}
		return readerFactory.getReaderPipeline().map(ThrowingFunction.identity());
	}

	@Override
	public InputFactory<I, Reader> decode() {
		if (readerFactory != null) {
			return new DefaultInputFactory<>(inputStreamFactory, readerFactory);
		}
		return InputFactory.super.decode();
	}
}