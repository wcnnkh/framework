package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Reader;
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
	}

	@NonNull
	Pipeline<T, IOException> getReader();

	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getReader().export().map(IOUtils::read).get();
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
}
