package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.exe.Execution;

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
