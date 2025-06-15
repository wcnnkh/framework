package run.soeasy.framework.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.Pipeline;

@FunctionalInterface
public interface ReaderFactoryWrapper<R extends Reader, W extends ReaderFactory<R>>
		extends ReaderFactory<R>, Wrapper<W> {

	@Override
	default Reader getReader() throws IOException {
		return getSource().getReader();
	}

	@Override
	default Pipeline<R, IOException> getReaderPipeline() {
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

	@Override
	default <T extends Writer> long transferTo(@NonNull WriterFactory<? extends T> dest) throws IOException {
		return getSource().transferTo(dest);
	}
}