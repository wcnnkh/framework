package run.soeasy.framework.retry;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.exchange.Stage;
import run.soeasy.framework.core.function.ThrowingConsumer;
import run.soeasy.framework.core.function.ThrowingFunction;

public interface RetryExecutor{
	<T, E extends Throwable> Stage<T> submit(
			@NonNull ThrowingFunction<? super RetryContext, ? extends T, ? extends E> retryFunction);

	default <E extends Throwable> Signal execute(
			@NonNull ThrowingConsumer<? super RetryContext, ? extends E> retryConsumer) {
		return submit((retryContext) -> {
			retryConsumer.accept(retryContext);
			return null;
		});
	}
}
