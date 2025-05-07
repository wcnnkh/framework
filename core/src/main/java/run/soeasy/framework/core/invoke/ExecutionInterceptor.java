package run.soeasy.framework.core.invoke;

import lombok.NonNull;

/**
 * 拦截器
 * 
 * @author wcnnkh
 *
 */
@FunctionalInterface
public interface ExecutionInterceptor {
	Object intercept(@NonNull Execution function, @NonNull Object... args) throws Throwable;
}
