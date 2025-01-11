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
public interface ReaderFactory<T extends Reader> {
	@FunctionalInterface
	public static interface ReaderFactoryWrapper<T extends Reader, W extends ReaderFactory<T>>
			extends ReaderFactory<T>, Wrapper<W> {
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
