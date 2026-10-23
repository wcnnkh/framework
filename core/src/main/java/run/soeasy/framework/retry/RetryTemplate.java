package run.soeasy.framework.retry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.exchange.ListenableFuture;
import run.soeasy.framework.core.exchange.SettableListenableFuture;
import run.soeasy.framework.core.function.ThrowingFunction;

@RequiredArgsConstructor
@Getter
public class RetryTemplate implements RetryExecutor {
	@NonNull
	private final RetryPolicy retryPolicy;

	@Override
	public <T, E extends Throwable> ListenableFuture<T> submit(
			@NonNull ThrowingFunction<? super RetryContext, ? extends T, ? extends E> retryFunction) {
		RetryContext retryContext = retryPolicy.open(RetrySynchronizationManager.getContext());
		RetrySynchronizationManager.register(retryContext);
		SettableListenableFuture<T> listenableFuture = new SettableListenableFuture<>();
		listenableFuture.setUncancellable();
		try {
			while (retryPolicy.canRetry(retryContext) && !retryContext.isExhaustedOnly()) {
				try {
					T value = retryFunction.apply(retryContext);
					listenableFuture.trySuccess(value);
					break;
				} catch (Throwable e) {
					retryPolicy.registerThrowable(retryContext, e);
				}
			}

			if (!listenableFuture.isDone()) {
				listenableFuture.setFailure(retryContext.getLastThrowable());
			}
		} finally {
			retryPolicy.close(retryContext);
			RetrySynchronizationManager.clear();
		}
		return listenableFuture;
	}
}
