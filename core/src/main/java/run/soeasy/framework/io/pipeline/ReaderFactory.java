package run.soeasy.framework.io.pipeline;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.function.Pipeline;
import run.soeasy.framework.io.IOUtils;

@FunctionalInterface
public interface ReaderFactory<T extends Reader> {
	@NonNull
	Pipeline<T, IOException> getReaderPipeline();

	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getReaderPipeline().optional().map(IOUtils::read).get();
	}

	default Elements<String> readAllLines() {
		return Elements.of(() -> {
			try {
				Pipeline<T, IOException> channel = getReaderPipeline();
				return IOUtils.readLines(channel.get()).onClose(() -> {
					try {
						channel.close();
					} catch (IOException e) {
						// ignore
					}
				});
			} catch (IOException ignore) {
			}
			return Stream.empty();
		});
	}

	default <R extends Writer> void transferTo(@NonNull WriterFactory<? extends R> dest) throws IOException {
		getReaderPipeline().optional()
				.ifPresent((r) -> dest.getWriterPipeline().optional().ifPresent((w) -> IOUtils.copy(r, w)));
	}
}
