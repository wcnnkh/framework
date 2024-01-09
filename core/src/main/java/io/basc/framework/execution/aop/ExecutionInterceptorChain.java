package io.basc.framework.execution.aop;

import java.util.Iterator;

import io.basc.framework.execution.Function;
import io.basc.framework.util.element.Elements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ExecutionInterceptorChain implements ExecutionInterceptor {
	private final Iterator<? extends ExecutionInterceptor> iterator;
	private Function nextChain;

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		if (iterator.hasNext()) {
			return iterator.next().intercept(function, args);
		}
		return nextChain == null ? null : nextChain.execute(args);
	}
}
