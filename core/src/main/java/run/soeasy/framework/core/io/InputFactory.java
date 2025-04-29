package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.function.Source;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface InputFactory<I extends InputStream, R extends Reader> extends InputStreamFactory<I>, ReaderFactory<R> {
	public static interface InputFactoryWrapper<I extends InputStream, R extends Reader, W extends InputFactory<I, R>>
			extends InputFactory<I, R>, InputStreamFactoryWrapper<I, W>, ReaderFactoryWrapper<R, W> {

		@Override
		default boolean isDecoded() {
			return getSource().isDecoded();
		}

	}
	
	@RequiredArgsConstructor
	@Getter
	public static class DefaultInputFactory<I extends InputStream, S extends InputStreamFactory<? extends I>, R extends Reader, T extends ReaderFactory<? extends R>>
			implements InputFactory<I, R> {
		protected final S inputStreamFactory;
		protected final T readerFactory;

		@Override
		public boolean isDecoded() {
			return readerFactory != null;
		}

		@Override
		public @NonNull Source<I, IOException> getInputStreamPipeline() {
			if (inputStreamFactory == null) {
				return Source.empty();
			}
			return inputStreamFactory.getInputStreamPipeline().map(ThrowingFunction.identity());
		}

		@Override
		public @NonNull Source<R, IOException> getReaderPipeline() {
			if (readerFactory == null) {
				return Source.empty();
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

	@Override
	boolean isDecoded();

	public static <I extends InputStream, R extends Reader> InputFactory<I, R> forFactory(
			InputStreamFactory<? extends I> inputStreamFactory, ReaderFactory<? extends R> readerFactory) {
		return new DefaultInputFactory<>(inputStreamFactory, readerFactory);
	}
}
