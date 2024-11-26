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

package io.basc.framework.util.retry.policy;

import io.basc.framework.util.retry.RetryContext;
import io.basc.framework.util.retry.RetryPolicy;
import io.basc.framework.util.retry.context.DefaultRetryContext;

/**
 * A {@link RetryPolicy} that allows the first attempt but never permits a
 * retry. Also be used as a base class for other policies, e.g. for test
 * purposes as a stub.
 * 
 * @author wcnnkh
 * 
 */
public class NeverRetryPolicy implements RetryPolicy {

	public boolean canRetry(RetryContext context) {
		return !((NeverRetryContext) context).isFinished();
	}

	public void close(RetryContext context) {
		// no-op
	}

	public RetryContext open(RetryContext parent) {
		return new NeverRetryContext(parent);
	}

	public void registerThrowable(RetryContext context, Throwable throwable) {
		((NeverRetryContext) context).setFinished();
		((DefaultRetryContext) context).setLastThrowable(throwable);
	}

	private static class NeverRetryContext extends DefaultRetryContext {
		private boolean finished = false;

		public NeverRetryContext(RetryContext parent) {
			super(parent);
		}

		public boolean isFinished() {
			return finished;
		}

		public void setFinished() {
			this.finished = true;
		}
	}

}
