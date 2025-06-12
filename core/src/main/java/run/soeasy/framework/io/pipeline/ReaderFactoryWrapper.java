package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.NoSuchElementException;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface ReaderFactoryWrapper<T extends Reader, W extends ReaderFactory<T>>
		extends ReaderFactory<T>, Wrapper<W> {
	@Override
	default Pipeline<T, IOException> getReaderPipeline() {
		return getSource().getReaderPipeline();
	}

	@Override
	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getSource().readAllCharacters();
	}

	@Override
	default Elements<String> readAllLines() {
		return getSource().readAllLines();
	}

	@Override
	default <R extends Writer> void transferTo(@NonNull WriterFactory<? extends R> dest) throws IOException {
		getSource().transferTo(dest);
	}
}