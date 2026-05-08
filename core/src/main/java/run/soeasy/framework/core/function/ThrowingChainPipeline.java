package run.soeasy.framework.core.function;

import java.util.function.Function;

final class ThrowingChainPipeline<S, E extends Exception, T extends Exception, W extends Pipeline<S, E>>
		extends ChainPipeline<S, S, E, T, W> {

	public ThrowingChainPipeline(W source, Function<? super E, ? extends T> throwingMapper,
			ThrowingRunnable<? extends E> closeable) {
		super(source, ThrowingFunction.identity(), throwingMapper, null, closeable.throwing(throwingMapper));
	}
}
