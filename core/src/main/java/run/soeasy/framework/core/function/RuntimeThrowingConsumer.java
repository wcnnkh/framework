package run.soeasy.framework.core.function;

import java.util.function.Consumer;

public interface RuntimeThrowingConsumer<S, E extends RuntimeException> extends ThrowingConsumer<S, E>, Consumer<S> {
	public static interface RuntimeThrowingConsumerWrapper<S, E extends RuntimeException, W extends RuntimeThrowingConsumer<S, E>>
			extends RuntimeThrowingConsumer<S, E>, ThrowingConsumerWrapper<S, E, W> {
	}
}
