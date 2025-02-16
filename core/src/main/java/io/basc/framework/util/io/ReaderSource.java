package io.basc.framework.util.io;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.function.Consumer;
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

		@Override
		default <E extends Throwable> void exportCharBuffer(Consumer<? super CharBuffer, ? extends E> consumer)
				throws IOException, E {
			getSource().exportCharBuffer(consumer);
		}
	}

	@NonNull
	Pipeline<T, IOException> getReader();

	default String readAllCharacters() throws NoSuchElementException, IOException {
		return getReader().option().map(IOUtils::read).get();
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
		getReader().option().ifPresent((r) -> dest.getWriter().option().ifPresent((w) -> IOUtils.copy(r, w)));
	}

	default <E extends Throwable> void exportCharBuffer(Consumer<? super CharBuffer, ? extends E> consumer)
			throws IOException, E {

	}
}
