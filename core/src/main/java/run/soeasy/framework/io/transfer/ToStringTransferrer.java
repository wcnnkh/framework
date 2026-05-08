package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.source.WriterFactory;

@FunctionalInterface
public interface ToStringTransferrer<S> {
	default CharSequence toString(S source) {
		StringBuilder sb = new StringBuilder();
		try {
			toString(source, sb);
		} catch (IOException e) {
			throw new IllegalStateException("Internal IO exception", e);
		}
		return sb;
	}

	default void toString(S source, Appendable target) throws IOException {
		toString(source, (e) -> target.append(e));
	}

	default <W extends Writer, E extends Throwable> void toString(S source, WriterFactory<W> target)
			throws IOException, E {
		try (Writer writer = target.getWriter()) {
			toString(source, target);
		}
	}

	default <E extends Throwable> void toString(S source, ThrowingConsumer<? super CharBuffer, ? extends E> target)
			throws E {
		toString(source, (a, b, c) -> target.accept(CharBuffer.wrap(a, b, c)));
	}

	<E extends Throwable> void toString(S source, BufferConsumer<? super char[], ? extends E> target) throws E;
}
