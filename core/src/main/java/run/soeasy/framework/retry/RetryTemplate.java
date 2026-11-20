package run.soeasy.framework.retry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.Promise;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.exchange.Stage;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class RetryTemplate implements RetryExecutor {
	@NonNull
	private final RetryPolicy retryPolicy;

	@Override
	public <T, E extends Throwable> Stage<T> submit(
			@NonNull ThrowingFunction<? super RetryContext, ? extends T, ? extends E> retryFunction) {
		RetryContext retryContext = retryPolicy.open(RetrySynchronizationManager.getContext());
		RetrySynchronizationManager.register(retryContext);
		Promise<T> promise = Signal.promise();
		try {
			while (retryPolicy.canRetry(retryContext) && !retryContext.isExhaustedOnly()) {
				try {
					T value = retryFunction.apply(retryContext);
					promise.complete(value);
					break;
				} catch (Throwable e) {
					retryPolicy.registerThrowable(retryContext, e);
				}
			}

			if (!promise.isDone()) {
				promise.completeExceptionally(retryContext.getLastThrowable());
			}
		} finally {
			retryPolicy.close(retryContext);
			RetrySynchronizationManager.clear();
		}
		return promise;
	}
}
