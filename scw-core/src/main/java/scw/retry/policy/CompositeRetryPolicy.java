/*
 * Copyright 2006-2007 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scw.retry.RetryContext;
import scw.retry.RetryPolicy;
import scw.retry.context.DefaultRetryContext;

/**
 * A {@link RetryPolicy} that composes a list of other policies and delegates
 * calls to them in order.
 *
 * @author shuchaowen
 *
 */
public class CompositeRetryPolicy implements RetryPolicy {

	RetryPolicy[] policies = new RetryPolicy[0];
	private boolean optimistic = false;

	/**
	 * Setter for optimistic.
	 *
	 * @param optimistic
	 */
	public void setOptimistic(boolean optimistic) {
		this.optimistic = optimistic;
	}

	/**
	 * Setter for policies.
	 *
	 * @param policies
	 */
	public void setPolicies(RetryPolicy[] policies) {
		this.policies = Arrays.asList(policies).toArray(new RetryPolicy[policies.length]);
	}

	/**
	 * Delegate to the policies that were in operation when the context was
	 * created. If any of them cannot retry then return false, oetherwise return
	 * true.
	 *
	 */
	public boolean canRetry(RetryContext context) {
		RetryContext[] contexts = ((CompositeRetryContext) context).contexts;
		RetryPolicy[] policies = ((CompositeRetryContext) context).policies;

		boolean retryable = true;

		if(optimistic) {
			retryable = false;
			for (int i = 0; i < contexts.length; i++) {
				if (policies[i].canRetry(contexts[i])) {
					retryable = true;
				}
			}
		}
		else {
			for (int i = 0; i < contexts.length; i++) {
				if (!policies[i].canRetry(contexts[i])) {
					retryable = false;
				}
			}
		}

		return retryable;
	}

	/**
	 * Delegate to the policies that were in operation when the context was
	 * created. If any of them fails to close the exception is propagated (and
	 * those later in the chain are closed before re-throwing).
	 *
	 */
	public void close(RetryContext context) {
		RetryContext[] contexts = ((CompositeRetryContext) context).contexts;
		RetryPolicy[] policies = ((CompositeRetryContext) context).policies;
		RuntimeException exception = null;
		for (int i = 0; i < contexts.length; i++) {
			try {
				policies[i].close(contexts[i]);
			}
			catch (RuntimeException e) {
				if (exception == null) {
					exception = e;
				}
			}
		}
		if (exception != null) {
			throw exception;
		}
	}

	/**
	 * Creates a new context that copies the existing policies and keeps a list
	 * of the contexts from each one.
	 *
	 */
	public RetryContext open(RetryContext parent) {
		List<RetryContext> list = new ArrayList<RetryContext>();
		for (RetryPolicy policy : policies) {
			list.add(policy.open(parent));
		}
		return new CompositeRetryContext(parent, list);
	}

	/**
	 * Delegate to the policies that were in operation when the context was
	 * created.
	 *
	 */
	public void registerThrowable(RetryContext context, Throwable throwable) {
		RetryContext[] contexts = ((CompositeRetryContext) context).contexts;
		RetryPolicy[] policies = ((CompositeRetryContext) context).policies;
		for (int i = 0; i < contexts.length; i++) {
			policies[i].registerThrowable(contexts[i], throwable);
		}
		((DefaultRetryContext) context).setLastThrowable(throwable);
	}

	private class CompositeRetryContext extends DefaultRetryContext{
		RetryContext[] contexts;

		RetryPolicy[] policies;

		public CompositeRetryContext(RetryContext parent, List<RetryContext> contexts) {
			super(parent);
			this.contexts = contexts.toArray(new RetryContext[contexts.size()]);
			this.policies = CompositeRetryPolicy.this.policies;
		}

	}

}
