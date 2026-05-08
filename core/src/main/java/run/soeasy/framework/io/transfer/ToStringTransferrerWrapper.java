package run.soeasy.framework.io.transfer;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

import run.soeasy.framework.core.domain.Wrapper;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.source.WriterFactory;

@FunctionalInterface
public interface ToStringTransferrerWrapper<S, W extends ToStringTransferrer<S>>
		extends ToStringTransferrer<S>, Wrapper<W> {
	@Override
	default <E extends Throwable> void toString(S source, BufferConsumer<? super char[], ? extends E> target) throws E {
		getSource().toString(source, target);
	}

	@Override
	default CharSequence toString(S source) {
		return getSource().toString(source);
	}

	@Override
	default void toString(S source, Appendable target) throws IOException {
		getSource().toString(source, target);
	}

	@Override
	default <E extends Throwable> void toString(S source, ThrowingConsumer<? super CharBuffer, ? extends E> target)
			throws E {
		getSource().toString(source, target);
	}

	@Override
	default <R extends Writer, E extends Throwable> void toString(S source, WriterFactory<R> target)
			throws IOException, E {
		getSource().toString(source, target);
	}
}
