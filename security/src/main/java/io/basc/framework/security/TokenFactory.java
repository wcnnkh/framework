package io.basc.framework.security;

import io.basc.framework.util.Pipeline;
import io.basc.framework.util.retry.RetryOperations;
import io.basc.framework.util.retry.support.RetryTemplate;

public interface TokenFactory {

	default Token getToken() throws InvalidTokenException {
		return getToken(false);
	}

	Token getToken(boolean forceUpdate) throws InvalidTokenException;

	default <V, E extends Throwable> V process(Pipeline<? super Token, ? extends V, ? extends E> processor)
			throws E, InvalidTokenException {
		return process(RetryTemplate.DEFAULT, processor);
	}

	default <V, E extends Throwable> V process(RetryOperations retryOperations,
			Pipeline<? super Token, ? extends V, ? extends E> processor) throws E, InvalidTokenException {
		if (retryOperations == null) {
			return processor.process(getToken());
		}

		return retryOperations.execute((context) -> {
			try {
				return processor.process(getToken(context.getRetryCount() != 0));
			} catch (InvalidTokenException e) {
				// 只有无效的token才进行重试
				throw e;
			} catch (Throwable e) {
				context.setExhaustedOnly();
				throw e;
			}
		});
	}
}
