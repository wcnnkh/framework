package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Pipeline;
import io.basc.framework.util.function.Wrapper;
import lombok.NonNull;

@FunctionalInterface
public interface ReaderSource<T extends Reader> {
	@FunctionalInterface
	public static interface ReaderSourceWrapper<T extends Reader, W extends ReaderSource<T>>
			extends ReaderSource<T>, Wrapper<W> {
		@Override
		default Pipeline<T, IOException> getReader() {
			return getSource().getReader();
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
		default <R extends Writer> void transferTo(@NonNull WriterSource<? extends R> dest) throws IOException {
			getSource().transferTo(dest);
		}
	}

	@NonNull
	Pipeline<T, IOException> getReader();

	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getReader().optional().map(IOUtils::read).get();
	}

	default Elements<String> readAllLines() {
		return Elements.of(() -> {
			try {
				Pipeline<T, IOException> channel = getReader();
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

	default <R extends Writer> void transferTo(@NonNull WriterSource<? extends R> dest) throws IOException {
		getReader().optional().ifPresent((r) -> dest.getWriter().optional().ifPresent((w) -> IOUtils.copy(r, w)));
	}
}
