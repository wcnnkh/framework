/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.soeasy.framework.util.concurrent;

import java.io.Serializable;
import java.util.concurrent.Executor;

import run.soeasy.framework.util.Assert;

/**
 * Mainly intended for testing scenarios.
 *
 * <p>
 * Execution in the calling thread does have the advantage of participating in
 * it's thread context, for example the thread context class loader or the
 * thread's current transaction association. That said, in many cases,
 * asynchronous execution will be preferable: choose an asynchronous
 * {@code TaskExecutor} instead for such scenarios.
 *
 * @see SimpleAsyncExecutor
 */
@SuppressWarnings("serial")
public class SyncExecutor implements Executor, Serializable {

	/**
	 * Executes the given {@code task} synchronously, through direct invocation of
	 * it's {@link Runnable#run() run()} method.
	 * 
	 * @throws IllegalArgumentException if the given {@code task} is {@code null}
	 */
	public void execute(Runnable task) {
		Assert.notNull(task, "Runnable must not be null");
		task.run();
	}

}
