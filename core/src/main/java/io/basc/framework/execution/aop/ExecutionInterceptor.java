package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;

/**
 * 拦截器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutionInterceptor {
	Object intercept(Executor executor, Object[] args) throws Throwable;
}
