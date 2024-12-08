package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import lombok.NonNull;

/**
 * 拦截器
 * 
 * @author wcnnkh
 *
 */
@FunctionalInterface
public interface ExecutionInterceptor {
	Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable;
}
