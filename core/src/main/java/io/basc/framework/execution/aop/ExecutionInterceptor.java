package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;

/**
 * 拦截器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutionInterceptor {
	Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable;
}
