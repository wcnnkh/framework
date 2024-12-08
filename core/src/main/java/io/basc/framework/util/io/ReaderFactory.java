package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import io.basc.framework.util.Channel;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;

@FunctionalInterface
public interface ReaderFactory<T extends Reader> {
	@FunctionalInterface
	public static interface ReaderFactoryWrapper<T extends Reader, W extends ReaderFactory<T>>
			extends ReaderFactory<T>, Wrapper<W> {
		@Override
		default Channel<T, IOException> getReader() {
			return getSource().getReader();
		}
	}

	@NonNull
	Channel<T, IOException> getReader();

	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getReader().export().map(IOUtils::read).get();
	}

	default Elements<String> readLines() {
		return Elements.of(() -> {
			try {
				Channel<T, IOException> channel = getReader();
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
