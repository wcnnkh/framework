package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Function;

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
