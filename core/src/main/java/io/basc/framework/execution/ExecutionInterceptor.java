package io.basc.framework.execution;

import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

/**
 * 执行拦截器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutionInterceptor {
	Object intercept(Executable source, Executor executor, Elements<? extends Value> parameters)
			throws ExecutionException;
}
