package run.soeasy.framework.core.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.NoSuchElementException;

import lombok.NonNull;
import run.soeasy.framework.core.function.Source;

public interface InputSource<I extends InputStream, R extends Reader>
		extends InputFactory<I, R>, InputStreamSource<I>, ReaderSource<R> {
	public static interface InputSourceWrapper<I extends InputStream, R extends Reader, W extends InputSource<I, R>>
			extends InputSource<I, R>, InputFactoryWrapper<I, R, W>, InputStreamSourceWrapper<I, W>,
			ReaderSourceWrapper<R, W> {
		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default String readAllCharacters() throws NoSuchElementException, IOException {
			return getSource().readAllCharacters();
		}

		@Override
		default @NonNull Source<I, IOException> getInputStreamPipeline() {
			return getSource().getInputStreamPipeline();
		}

		@Override
		default @NonNull Source<R, IOException> getReaderPipeline() {
			return getSource().getReaderPipeline();
		}
	}

	boolean isReadable();

	@Override
	default @NonNull Source<I, IOException> getInputStreamPipeline() {
		return isReadable() ? InputStreamSource.super.getInputStreamPipeline() : Source.empty();
	}

	@Override
	default @NonNull Source<R, IOException> getReaderPipeline() {
		return isReadable() ? ReaderSource.super.getReaderPipeline() : Source.empty();
	}

	@Override
	default String readAllCharacters() throws NoSuchElementException, IOException {
		return decode().readAllCharacters();
	}
}
