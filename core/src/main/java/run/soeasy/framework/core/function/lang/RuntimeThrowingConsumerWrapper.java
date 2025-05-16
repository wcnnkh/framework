package run.soeasy.framework.core.function.lang;

public interface RuntimeThrowingConsumerWrapper<S, E extends RuntimeException, W extends RuntimeThrowingConsumer<S, E>>
		extends RuntimeThrowingConsumer<S, E>, ThrowingConsumerWrapper<S, E, W> {
}