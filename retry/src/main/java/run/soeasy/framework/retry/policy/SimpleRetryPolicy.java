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

package run.soeasy.framework.retry.policy;

import lombok.ToString;
import run.soeasy.framework.retry.RetryContext;
import run.soeasy.framework.retry.RetryPolicy;
import run.soeasy.framework.retry.context.DefaultRetryContext;

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
 * @author wcnnkh
 *
 */
@ToString
public class SimpleRetryPolicy implements RetryPolicy {

	/**
	 * The default limit to the number of attempts for a new policy.
	 */
	public final static int DEFAULT_MAX_ATTEMPTS = 3;

	private volatile int maxAttempts;

	/**
	 * Create a {@link SimpleRetryPolicy} with the default number of retry attempts.
	 */
	public SimpleRetryPolicy() {
		this(DEFAULT_MAX_ATTEMPTS);
	}

	public SimpleRetryPolicy(int maxAttempts) {
		super();
		this.maxAttempts = maxAttempts;
	}

	/**
	 * Setter for retry attempts.
	 *
	 * @param retryAttempts the number of attempts before a retry becomes
	 *                      impossible.
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

	public boolean canRetry(RetryContext context) {
		return context.getRetryCount() < maxAttempts;
	}

	public void close(RetryContext status) {
	}

	public void registerThrowable(RetryContext context, Throwable throwable) {
		SimpleRetryContext simpleContext = ((SimpleRetryContext) context);
		simpleContext.setLastThrowable(throwable);
	}

	public RetryContext open(RetryContext parent) {
		return new SimpleRetryContext(parent);
	}

	private static class SimpleRetryContext extends DefaultRetryContext {
		public SimpleRetryContext(RetryContext parent) {
			super(parent);
		}
	}
}
