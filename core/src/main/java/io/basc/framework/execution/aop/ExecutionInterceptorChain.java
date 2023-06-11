package io.basc.framework.execution.aop;

import java.util.Iterator;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class ExecutionInterceptorChain implements ExecutionInterceptor {
	private final Iterator<? extends ExecutionInterceptor> iterator;
	private final Executor nextChain;

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next().intercept(executor, args);
		}
		return nextChain == null ? null : nextChain.execute(args);
	}
}
