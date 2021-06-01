/*
 * Copyright 2006-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.retry.policy;

import scw.core.utils.ClassUtils;
import scw.retry.RetryContext;
import scw.retry.RetryPolicy;
import scw.retry.context.DefaultRetryContext;

/**
 *
 * Simple retry policy that retries a fixed number of times for a set of named
 * exceptions (and subclasses). The number of attempts includes the initial try,
 * so e.g.
 *
 * <pre>
 * retryTemplate = new RetryTemplate(new SimpleRetryPolicy(3));
 * retryTemplate.execute(callback);
 * </pre>
 *
 * will execute the callback at least once, and as many as 3 times.
 *
 * @author shuchaowen
 *
 */
public class SimpleRetryPolicy implements RetryPolicy {

	/**
	 * The default limit to the number of attempts for a new policy.
	 */
	public final static int DEFAULT_MAX_ATTEMPTS = 3;

	private volatile int maxAttempts;

	/**
	 * Create a {@link SimpleRetryPolicy} with the default number of retry
	 * attempts.
	 */
	public SimpleRetryPolicy() {
		this(DEFAULT_MAX_ATTEMPTS);
	}

	/**
	 * Create a {@link SimpleRetryPolicy} with the specified number of retry
	 * attempts. If traverseCauses is true, the exception causes will be traversed until
	 * a match is found.
	 *
	 * @param maxAttempts
	 */
	public SimpleRetryPolicy(int maxAttempts) {
		super();
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Setter for retry attempts.
	 *
	 * @param retryAttempts the number of attempts before a retry becomes
	 * impossible.
	 */
	public void setMaxAttempts(int retryAttempts) {
		this.maxAttempts = retryAttempts;
	}

	/**
	 * The maximum number of retry attempts before failure.
	 *
	 * @return the maximum number of attempts
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * Test for retryable operation based on the status.
	 *
	 * @return true if the last exception was retryable and the number of
	 * attempts so far is less than the limit.
	 */
	public boolean canRetry(RetryContext context) {
		return context.getRetryCount() < maxAttempts;
	}

	public void close(RetryContext status) {
	}

	/**
	 * Update the status with another attempted retry and the latest exception.
	 *
	 * @see RetryPolicy#registerThrowable(RetryContext, Throwable)
	 */
	public void registerThrowable(RetryContext context, Throwable throwable) {
		SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
		simpleContext.setLastThrowable(throwable);
	}

	/**
	 * Get a status object that can be used to track the current operation
	 * according to this policy. Has to be aware of the latest exception and the
	 * number of attempts.
	 *
	 */
	public RetryContext open(RetryContext parent) {
		return new SimpleRetryContext(parent);
	}

	private static class SimpleRetryContext extends DefaultRetryContext {
		public SimpleRetryContext(RetryContext parent) {
			super(parent);
		}
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + "[maxAttempts=" + maxAttempts + "]";
	}
}
