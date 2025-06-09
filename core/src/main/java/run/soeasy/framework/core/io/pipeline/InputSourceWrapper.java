package run.soeasy.framework.core.io.pipeline;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.NoSuchElementException;

import lombok.NonNull;
import run.soeasy.framework.core.function.Pipeline;

public interface InputSourceWrapper<I extends InputStream, R extends Reader, W extends InputSource<I, R>> extends
		InputSource<I, R>, InputFactoryWrapper<I, R, W>, InputStreamSourceWrapper<I, W>, ReaderSourceWrapper<R, W> {
	@Override
	default boolean isReadable() {
		return getSource().isReadable();
	}

	@Override
	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getSource().readAllCharacters();
	}

	@Override
	default @NonNull Pipeline<I, IOException> getInputStreamPipeline() {
		return getSource().getInputStreamPipeline();
	}

	@Override
	default @NonNull Pipeline<R, IOException> getReaderPipeline() {
		return getSource().getReaderPipeline();
	}
}