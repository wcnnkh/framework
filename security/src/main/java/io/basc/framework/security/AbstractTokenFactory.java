package io.basc.framework.security;

import java.util.concurrent.TimeUnit;

import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Processor;

public abstract class AbstractTokenFactory implements TokenFactory {
	private RetryOperations retryOperations = RetryTemplate.DEFAULT;
	private long expireAheadTime = 60;// token提前过期时间
	private TimeUnit expireAheadTimeUnit = TimeUnit.SECONDS;
	private volatile Token token;

	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		Assert.requiredArgument(retryOperations != null, "retryOperations");
		this.retryOperations = retryOperations;
	}

	public long getExpireAheadTime() {
		return expireAheadTime;
	}

	public void setExpireAheadTime(long expireAheadTime) {
		this.expireAheadTime = expireAheadTime;
	}

	public TimeUnit getExpireAheadTimeUnit() {
		return expireAheadTimeUnit;
	}

	public void setExpireAheadTimeUnit(TimeUnit expireAheadTimeUnit) {
		this.expireAheadTimeUnit = expireAheadTimeUnit;
	}

	public void setTokenExpireAheadTime(int expireAheadTime, TimeUnit expireAheadTimeUnit) {
		Assert.requiredArgument(expireAheadTimeUnit != null, "expireAheadTimeUnit");
		this.expireAheadTime = expireAheadTime;
		this.expireAheadTimeUnit = expireAheadTimeUnit;
	}

	protected boolean isInvalid(Token token) {
		return token == null || token.isExpired(expireAheadTime, expireAheadTimeUnit);
	}

	@Override
	public final <V, E extends Throwable> V process(Processor<? super Token, ? extends V, ? extends E> processor)
			throws E, InvalidTokenException {
		return process(getRetryOperations(), processor);
	}

	@Override
	public final Token getToken() throws InvalidTokenException {
		return getToken(false);
	}

	@Override
	public Token getToken(boolean forceUpdate) throws InvalidTokenException {
		if (forceUpdate || isInvalid(this.token)) {
			synchronized (this) {
				if (forceUpdate || isInvalid(this.token)) {
					this.token = getNewToken();
				}
			}
		}
		return this.token;
	}

	protected abstract Token getNewToken() throws InvalidTokenException;
}
