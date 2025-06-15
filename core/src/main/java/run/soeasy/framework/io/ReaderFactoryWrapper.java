package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface ReaderFactoryWrapper<T extends Reader, W extends ReaderFactory<T>>
		extends ReaderFactory<T>, Wrapper<W> {

	@Override
	default Reader getReader() throws IOException {
		return getSource().getReader();
	}

	@Override
	default Pipeline<T, IOException> getReaderPipeline() {
		return getSource().getReaderPipeline();
	}

	@Override
	default CharSequence toCharSequence() throws IOException {
		return getSource().toCharSequence();
	}

	@Override
	default Elements<String> readLines() {
		return getSource().readLines();
	}
}