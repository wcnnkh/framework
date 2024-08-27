package io.basc.framework.execution.aop;

import io.basc.framework.execution.Function;
import io.basc.framework.util.Elements;

/**
 * 拦截器
 * 
 * @author wcnnkh
 *
 */
public interface ExecutionInterceptor {
	Object intercept(Function function, Elements<? extends Object> args) throws Throwable;
}
